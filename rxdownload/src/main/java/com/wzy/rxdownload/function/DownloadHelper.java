package com.wzy.rxdownload.function;


import android.content.Context;
import android.util.Log;

import com.wzy.rxdownload.entity.DownloadRange;
import com.wzy.rxdownload.entity.DownloadStatus;
import com.wzy.rxdownload.entity.DownloadType;
import com.wzy.rxdownload.entity.DownloadTypeFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class DownloadHelper {
    private static final String TEST_RANGE_SUPPORT = "bytes=0-";
    private static final String TAG = DownloadHelper.class.getSimpleName();
    private int MAX_RETRY_COUNT = 3;

    private DownloadApi mDownloadApi;
    private FileHelper mFileHelper;
    private DownloadTypeFactory mFactory;

    // Record: {"url" : new String[] { "file path", "temp file path", "last modify file path"}}
    private Map<String, String[]> mDownloadRecord;

    public DownloadHelper() {
        mDownloadRecord = new HashMap<>();
        mFileHelper = new FileHelper();
        mDownloadApi = RetrofitProvider.getInstance().create(DownloadApi.class);
        mFactory = new DownloadTypeFactory(this);
    }

    public Observable<DownloadStatus> downloadDispatcher(final String url, final String saveName,
                                                         final String savePath, final Context context,
                                                         final boolean autoInstall) {
        if (isRecordExists(url)) {
            return Observable.error(new Throwable("This url download task already exists, so do nothing"));
        }

        try {
            addDownloadRecord(url, saveName, savePath);
        } catch (IOException e) {
            return Observable.error(e);
        }

        return getDownloadType(url)
                .flatMap(new Func1<DownloadType, Observable<DownloadStatus>>() {

                    @Override
                    public Observable<DownloadStatus> call(DownloadType downloadType) {
                        try {
                            downloadType.prepareDownload();
                            return downloadType.startDownload();
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                            return Observable.error(e);
                        }
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        deleteDownloadRecord(url);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        deleteDownloadRecord(url);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        deleteDownloadRecord(url);
                    }
                });
    }

    private boolean isRecordExists(String url) {
        return mDownloadRecord.get(url) != null;
    }

    private void addDownloadRecord(String url, String saveName, String savePath) throws IOException {
        mFileHelper.createDirectories(savePath);
        mDownloadRecord.put(url, getRealFilePaths(saveName, savePath));
    }

    private void deleteDownloadRecord(String url) {
        mDownloadRecord.remove(url);
    }

    private String[] getRealFilePaths(String saveName, String savePath) {
        return mFileHelper.getRealFilePaths(saveName, savePath);
    }


    public DownloadRange readDownloadRange(String url, int i) throws IOException {
        return mFileHelper.readDownloadRange(getTempFile(url), i);
    }

    private File getFile(String url) {
        return new File(mDownloadRecord.get(url)[0]);
    }

    private File getTempFile(String url) {
        return new File(mDownloadRecord.get(url)[1]);
    }

    private File getLastModifyFile(String url) {
        return new File(mDownloadRecord.get(url)[2]);
    }

    private Observable<DownloadType> getDownloadType(String url) {
        return getWhenFileNotExists(url);
    }

    private Observable<DownloadType> getWhenFileNotExists(final String url) {
        return getDownloadApi()
                .getHttpHeader(TEST_RANGE_SUPPORT, url)
                .map(new Func1<Response<Void>, DownloadType>() {
                    @Override
                    public DownloadType call(Response<Void> response) {
                        Log.d(TAG, "call: response=" + response);
                        return mFactory.url(url)
                                .fileLength(Utils.contentLength(response))
                                .lastModify(Utils.lastModify(response))
                                .buildMultiDownload();
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        // TODO: 2017/5/8
                        return null;
                    }
                });
    }


    public DownloadApi getDownloadApi() {
        return mDownloadApi;
    }

    public void saveRangeFile(Subscriber<? super DownloadStatus> subscriber, int i, long start, long end, String url, ResponseBody responseBody) {
        mFileHelper.saveFile(subscriber, i, start, end, getTempFile(url), getFile(url), responseBody);
    }

    public int getMaxThreads() {
        return mFileHelper.getMaxThreads();
    }

    public void setMaxThreads(int maxThreads) {
        mFileHelper.setMaxThreads(maxThreads);
    }

    public void prepareMultiThreadDownload(String url, long fileLength, String lastModify) throws IOException, ParseException {
        mFileHelper.prepareDownload(getLastModifyFile(url), getTempFile(url), getFile(url), fileLength, lastModify);
    }
}