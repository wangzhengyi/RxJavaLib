package com.wzy.rxdownload.function;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.wzy.rxdownload.RxDownload;
import com.wzy.rxdownload.db.DataBaseHelper;
import com.wzy.rxdownload.entity.DownloadEvent;
import com.wzy.rxdownload.entity.DownloadEventFactory;
import com.wzy.rxdownload.entity.DownloadFlag;
import com.wzy.rxdownload.entity.DownloadMission;
import com.wzy.rxdownload.entity.DownloadRecord;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class DownloadService extends Service {
    public static final String INTENT_KEY = "wzy_rxdownload_max_download_number";

    private DownloadBinder mDownloadBinder;
    private DataBaseHelper mDataBaseHelper;
    private DownloadEventFactory mDownloadEventFactory;

    private volatile AtomicInteger atomicInteger = new AtomicInteger(0);
    private volatile Map<String, Subject<DownloadEvent, DownloadEvent>> mSubjectPool;

    private Map<String, DownloadMission> mNowDownloading;
    private LinkedBlockingQueue<DownloadMission> mWaitingForDownload;
    private Map<String, DownloadMission> mWaitingForDownloadLookupMap;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private int MAX_DOWNLOAD_NUMBER = 5;

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDownloadBinder = new DownloadBinder();
        mSubjectPool = new ConcurrentHashMap<>();
        mWaitingForDownload = new LinkedBlockingQueue<>();
        mWaitingForDownloadLookupMap = new ConcurrentHashMap<>();
        mNowDownloading = new HashMap<>();

        mDataBaseHelper = DataBaseHelper.getSingleInstance(this);
        mDownloadEventFactory = DownloadEventFactory.getSingleton();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDataBaseHelper.repaireErrorFlag();
        if (intent != null) {
            MAX_DOWNLOAD_NUMBER = intent.getIntExtra(INTENT_KEY, MAX_DOWNLOAD_NUMBER);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataBaseHelper.closeDatabase();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mExecutor.execute(new DownloadMissionDispatchRunnable());
        return mDownloadBinder;
    }

    public Subject<DownloadEvent, DownloadEvent> createAndGet(String url) {
        if (mSubjectPool.get(url) == null) {
            Subject<DownloadEvent, DownloadEvent> subject = new SerializedSubject<>(
                    BehaviorSubject.create(
                            mDownloadEventFactory.create(url, DownloadFlag.NORMAL, null)
                    ));
            mSubjectPool.put(url, subject);
        }
        return mSubjectPool.get(url);
    }

    public void addDownloadMission(DownloadMission mission) throws IOException {
        String url = mission.getUrl();
        if (mWaitingForDownloadLookupMap.get(url) != null || mNowDownloading.get(url) != null) {
            throw new IOException("This download mission is exists.");
        } else {
            if (mDataBaseHelper.recordNotExists(url)) {
                mDataBaseHelper.insertRecord(mission);
                createAndGet(url).onNext(mDownloadEventFactory.create(url, DownloadFlag.WAITING, null));
            } else {
                mDataBaseHelper.updateRecord(url, DownloadFlag.WAITING);
                createAndGet(url).onNext(mDownloadEventFactory.create(url, DownloadFlag.WAITING,
                        mDataBaseHelper.readDownloadStatus(url)));
            }
            mWaitingForDownload.offer(mission);
            mWaitingForDownloadLookupMap.put(url, mission);
        }
    }

    public void pauseDownload(String url) {
        suspendDownloadAndSendEvent(url, DownloadFlag.PAUSED);
        mDataBaseHelper.updateRecord(url, DownloadFlag.PAUSED);
    }

    public void cancelDownload(String url) {
        suspendDownloadAndSendEvent(url, DownloadFlag.CANCELED);
        mDataBaseHelper.updateRecord(url, DownloadFlag.CANCELED);
    }

    public void deleteDownload(String url) {
        suspendDownloadAndSendEvent(url, DownloadFlag.DELETED);
        mDataBaseHelper.deleteRecord(url);
    }

    private void suspendDownloadAndSendEvent(String url, int flag) {
        if (mWaitingForDownloadLookupMap.get(url) != null) {
            mWaitingForDownloadLookupMap.get(url).canceled = true;
        }
        if (mNowDownloading.get(url) != null) {
            Utils.unSubscribe(mNowDownloading.get(url).getSubscription());
            createAndGet(url).onNext(mDownloadEventFactory.create(url, flag,
                    mNowDownloading.get(url).getStatus()));
            atomicInteger.decrementAndGet();
            mNowDownloading.remove(url);
        } else {
            createAndGet(url).onNext(mDownloadEventFactory.create(url, flag,
                    mDataBaseHelper.readDownloadStatus(url)));
        }
    }

    public Subject<DownloadEvent, DownloadEvent> getSubject(RxDownload rxDownload, String url) {
        Subject<DownloadEvent, DownloadEvent> subject = createAndGet(url);
        if (mDataBaseHelper.recordNotExists(url)) {
            DownloadRecord record = mDataBaseHelper.readSingleRecord(url);
            File file = rxDownload.getRealFiles(record.getSaveName(), record.getSavePath())[0];
            if (file.exists()) {
                subject.onNext(mDownloadEventFactory.create(url, record.getFlag(), record.getStatus()));
            }
        }

        return subject;
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private class DownloadMissionDispatchRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                DownloadMission mission;
                try {
                    mission = mWaitingForDownload.take();
                } catch (InterruptedException e) {
                    continue;
                }
                if (mission != null) {
                    String url = mission.getUrl();
                    if (mission.canceled) {
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookupMap.remove(url);
                        continue;
                    }
                    if (mNowDownloading.get(url) != null) {
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookupMap.remove(url);
                        continue;
                    }
                    if (atomicInteger.get() < MAX_DOWNLOAD_NUMBER) {
                        mission.start(mNowDownloading, atomicInteger, mDataBaseHelper, mSubjectPool);
                        mWaitingForDownload.remove(mission);
                        mWaitingForDownloadLookupMap.remove(url);
                    }
                }
            }
        }
    }
}
