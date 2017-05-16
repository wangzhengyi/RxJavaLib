package com.wzy.rxdownload.entity;

import com.wzy.rxdownload.RxDownload;
import com.wzy.rxdownload.db.DataBaseHelper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

public class DownloadMission {
    public boolean canceled = false;

    private RxDownload rxDownload;
    private String url;
    private String saveName;
    private String savePath;
    private DownloadStatus mStatus;
    private Subscription subscription;

    public String getUrl() {
        return url;
    }

    public String getSaveName() {
        return saveName;
    }

    public String getSavePath() {
        return savePath;
    }

    public DownloadStatus getStatus() {
        return mStatus;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void start(final Map<String, DownloadMission> nowDownloadMap, final AtomicInteger count,
                      final DataBaseHelper helper,
                      final Map<String, Subject<DownloadEvent, DownloadEvent>> subjectPool) {
        nowDownloadMap.put(url, this);
        count.incrementAndGet();
        final DownloadEventFactory eventFactory = DownloadEventFactory.getSingleton();
        subscription = rxDownload.download(url, saveName, savePath)
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .subscribe(new Subscriber<DownloadStatus>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        helper.updateRecord(url, DownloadFlag.STARTED);
                    }

                    @Override
                    public void onCompleted() {
                        subjectPool.get(url).onNext(
                                eventFactory.create(url, DownloadFlag.COMPLETED, mStatus));
                        helper.updateRecord(url, DownloadFlag.COMPLETED);
                        count.decrementAndGet();
                        nowDownloadMap.remove(url);

                    }

                    @Override
                    public void onError(Throwable e) {
                        subjectPool.get(url).onNext(
                                eventFactory.create(url, DownloadFlag.FAILED, mStatus, e));
                        helper.updateRecord(url, DownloadFlag.FAILED);
                        count.decrementAndGet();
                        nowDownloadMap.remove(url);
                    }

                    @Override
                    public void onNext(DownloadStatus status) {
                        subjectPool.get(url).onNext(
                                eventFactory.create(url, DownloadFlag.STARTED, status));
                        helper.updateRecord(url, status);
                        mStatus = status;
                    }
                });
    }


    public static class Builder {
        RxDownload rxDownload;
        String url;
        String saveName;
        String savePath;

        public Builder setRxDownload(RxDownload rxDownload) {
            this.rxDownload = rxDownload;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSaveName(String saveName) {
            this.saveName = saveName;
            return this;
        }

        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public DownloadMission build() {
            DownloadMission mission = new DownloadMission();
            mission.rxDownload = rxDownload;
            mission.url = url;
            mission.saveName = saveName;
            mission.savePath = savePath;
            return mission;
        }
    }
}
