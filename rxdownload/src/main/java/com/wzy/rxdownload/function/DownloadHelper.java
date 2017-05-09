package com.wzy.rxdownload.function;


import android.content.Context;
import android.util.Log;

import com.wzy.rxdownload.entity.DownloadRange;
import com.wzy.rxdownload.entity.DownloadStatus;
import com.wzy.rxdownload.entity.DownloadType;
import com.wzy.rxdownload.entity.DownloadTypeFactory;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class DownloadHelper {
    private static final String TAG = DownloadHelper.class.getSimpleName();
    private static final String TEST_RANGE_SUPPORT = "bytes=0-";
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

    public void setRetrofit(Retrofit retrofit) {
        mDownloadApi = retrofit.create(DownloadApi.class);
    }

    public void setDefaultSavePath(String defaultSavePath) {
        mFileHelper.setDefaultSavePath(defaultSavePath);
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.MAX_RETRY_COUNT = maxRetryCount;
    }

    public String[] getFileSavePaths(String savePath) {
        return mFileHelper.getRealDirectoryPaths(savePath);
    }

    public String[] getRealFilePaths(String saveName, String savePath) {
        return mFileHelper.getRealFilePaths(saveName, savePath);
    }

    public DownloadApi getDownloadApi() {
        return mDownloadApi;
    }

    public int getMaxThreads() {
        return mFileHelper.getMaxThreads();
    }

    public void setMaxThreads(int maxThreads) {
        mFileHelper.setMaxThreads(maxThreads);
    }

    public void prepareNormalDownload(String url, long fileLength, String lastModify)
            throws IOException, ParseException {
        mFileHelper.prepareDownload(getLastModifyFile(url), getFile(url), fileLength, lastModify);
    }

    public void saveNormalFile(Subscriber<? super DownloadStatus> subscriber,
                               String url,
                               Response<ResponseBody> resp) {
        mFileHelper.saveFile(subscriber, getFile(url), resp);
    }

    public DownloadRange readDownloadRange(String url, int i) throws IOException {
        return mFileHelper.readDownloadRange(getTempFile(url), i);
    }

    public void prepareMultiThreadDownload(String url, long fileLength, String lastModify)
            throws IOException, ParseException {
        mFileHelper.prepareDownload(
                getLastModifyFile(url), getTempFile(url), getFile(url), fileLength, lastModify);
    }

    public void saveRangeFile(Subscriber<? super DownloadStatus> subscriber,
                              int i, long start, long end, String url, ResponseBody responseBody) {
        mFileHelper.saveFile(
                subscriber, i, start, end, getTempFile(url), getFile(url), responseBody);
    }

    public Boolean retry(Integer integer, Throwable throwable) {
        if (throwable instanceof ProtocolException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(TAG, "retry: " + Thread.currentThread().getName() +
                    " we got an error in the underlying protocol, such as a TCP error, " +
                        "retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof SocketTimeoutException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(TAG, "retry: " + Thread.currentThread().getName() +
                        " socket time out, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof ConnectTimeoutException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(TAG, "retry: " + Thread.currentThread().getName() +
                        " connect time out, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof UnknownHostException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(TAG, "retry: " + Thread.currentThread().getName() +
                        " no network, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof HttpException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(FileHelper.TAG, Thread.currentThread().getName() +
                        " had non-2XX http error, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof ConnectException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(FileHelper.TAG, Thread.currentThread().getName() +  " "
                                + throwable.getMessage() + ". retry to connect "
                                + String.valueOf(integer) + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof SocketException) {
            if (integer <= MAX_RETRY_COUNT) {
                Log.w(FileHelper.TAG, Thread.currentThread().getName() +
                        " a network or conversion error happened, retry to connect "
                        + integer + " times");
                return true;
            }
            return false;
        } else {
            Log.w(TAG, "retry: " + throwable.getMessage());
            return false;
        }
    }

    public Observable<DownloadStatus> downloadDispatcher(final String url,
                                                         final String saveName,
                                                         final String savePath,
                                                         final Context context,
                                                         final boolean autoInstall) {
        if (isRecordExists(url)) {
            return Observable.error(new Throwable(
                    "This url download task already exists, so do nothing"));
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

    public Observable<DownloadType> requestHeaderWidthIfRangeByGet(final String url)
            throws IOException {
        return getDownloadApi()
                .requestWithIfRange(TEST_RANGE_SUPPORT, getLastModify(url), url)
                .map(new Func1<Response<Void>, DownloadType>() {
                    @Override
                    public DownloadType call(Response<Void> resp) {
                        if (Utils.serverFileNotChange(resp)) {
                            return getWhenServerFileNotChange(resp, url);
                        } else if (Utils.serverFileChanged(resp)) {
                            return getWhenServerFileChange(resp, url);
                        } else {
                            throw new RuntimeException("unknown error");
                        }
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return retry(integer, throwable);
                    }
                });
    }

    private boolean isRecordExists(String url) {
        return mDownloadRecord.get(url) != null;
    }

    private void addDownloadRecord(String url, String saveName, String savePath)
            throws IOException {
        mFileHelper.createDirectories(savePath);
        mDownloadRecord.put(url, getRealFilePaths(saveName, savePath));
    }

    private void deleteDownloadRecord(String url) {
        mDownloadRecord.remove(url);
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

    private String getLastModify(String url) throws IOException {
        return mFileHelper.getLastModify(getLastModifyFile(url));
    }

    private boolean downloadFileExists(String url) {
        return getFile(url).exists();
    }

    private Observable<DownloadType> getDownloadType(String url) {
        if (downloadFileExists(url)) {
            try {
                return getWhenFileExists(url);
            } catch (IOException e) {
                return getWhenFileNotExists(url);
            }
        } else {
            return getWhenFileNotExists(url);
        }
    }

    private Observable<DownloadType> getWhenFileNotExists(final String url) {
        return getDownloadApi()
                .getHttpHeader(TEST_RANGE_SUPPORT, url)
                .map(new Func1<Response<Void>, DownloadType>() {
                    @Override
                    public DownloadType call(Response<Void> response) {
                        Log.d(TAG, "call: response=" + response);
                        if (Utils.notSupportRange(response)) {
                            return mFactory.url(url)
                                    .fileLength(Utils.contentLength(response))
                                    .lastModify(Utils.lastModify(response))
                                    .buildNormalDownload();
                        } else {
                            return mFactory.url(url)
                                    .fileLength(Utils.contentLength(response))
                                    .lastModify(Utils.lastModify(response))
                                    .buildMultiDownload();
                        }
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return retry(integer, throwable);
                    }
                });
    }

    private Observable<DownloadType> getWhenFileExists(final String url) throws IOException {
        return getDownloadApi()
                .getHttpHeaderWithIfRange(TEST_RANGE_SUPPORT, getLastModify(url), url)
                .map(new Func1<Response<Void>, DownloadType>() {
                    @Override
                    public DownloadType call(Response<Void> response) {
                        Log.d(TAG, "call: response=" + response);
                        if (Utils.serverFileNotChange(response)) {
                            return getWhenServerFileNotChange(response, url);
                        } else if (Utils.serverFileChanged(response)) {
                            return getWhenServerFileChange(response, url);
                        } else if (Utils.requestRangeNotSatisfiable(response)) {
                            return mFactory.url(url)
                                    .fileLength(Utils.contentLength(response))
                                    .lastModify(Utils.lastModify(response))
                                    .buildRequestRangeNotSatisfiable();
                        } else {
                            throw new RuntimeException("unknown error");
                        }
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                    return retry(integer, throwable);
                    }
                });
    }

    private DownloadType getWhenServerFileChange(Response<Void> resp, String url) {
        if (Utils.notSupportRange(resp)) {
            return mFactory.url(url)
                    .fileLength(Utils.contentLength(resp))
                    .lastModify(Utils.lastModify(resp))
                    .buildNormalDownload();
        } else {
            return mFactory.url(url)
                    .fileLength(Utils.contentLength(resp))
                    .lastModify(Utils.lastModify(resp))
                    .buildMultiDownload();
        }
    }

    private DownloadType getWhenServerFileNotChange(Response<Void> resp, String url) {
        if (Utils.notSupportRange(resp)) {
            return getWhenNotSupportRange(resp, url);
        } else {
            return getWhenSupportRange(resp, url);
        }
    }

    private DownloadType getWhenNotSupportRange(Response<Void> resp, String url) {
        long contentLength = Utils.contentLength(resp);
        if (downloadNotCompleted(url, contentLength)) {
            return mFactory.url(url)
                    .fileLength(contentLength)
                    .lastModify(Utils.lastModify(resp))
                    .buildNormalDownload();
        } else {
            return mFactory.url(url)
                    .fileLength(contentLength)
                    .lastModify(Utils.lastModify(resp))
                    .buildAlreadyDownload();
        }
    }

    private DownloadType getWhenSupportRange(Response<Void> resp, String url) {
        long contentLength = Utils.contentLength(resp);
        try {
            if (needReDownload(url, contentLength)) {
                return mFactory.url(url)
                        .fileLength(contentLength)
                        .lastModify(Utils.lastModify(resp))
                        .buildMultiDownload();
            }
            if (downloadNotCompleted(url)) {
                return mFactory.url(url)
                        .fileLength(contentLength)
                        .lastModify(Utils.lastModify(resp))
                        .buildContinueDownload();
            }

        } catch (IOException e) {
            return mFactory.url(url)
                    .fileLength(contentLength)
                    .lastModify(Utils.lastModify(resp))
                    .buildMultiDownload();
        }
        return mFactory.url(url)
                .fileLength(contentLength)
                .buildAlreadyDownload();
    }

    private boolean downloadNotCompleted(String url) throws IOException {
        return mFileHelper.downloadNotComplete(getTempFile(url));
    }

    private boolean downloadNotCompleted(String url, long contentLength) {
        return getFile(url).length() != contentLength;
    }

    private boolean needReDownload(String url, long contentLength) throws IOException {
        return tempFileNotExists(url) || tempFileDamaged(url, contentLength);
    }

    private boolean tempFileDamaged(String url, long fileLength) throws IOException {
        return mFileHelper.tempFileDamaged(getTempFile(url), fileLength);
    }

    private boolean tempFileNotExists(String url) {
        return !getTempFile(url).exists();
    }
}