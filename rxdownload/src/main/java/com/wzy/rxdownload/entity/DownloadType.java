package com.wzy.rxdownload.entity;


import android.util.Log;

import com.wzy.rxdownload.function.DownloadHelper;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public abstract class DownloadType {
    private static final String TAG = DownloadType.class.getSimpleName();
    String mUrl;
    long mFileLength;
    String mLastModify;
    DownloadHelper mDownloadHelper;

    public abstract void prepareDownload() throws IOException, ParseException;
    public abstract Observable<DownloadStatus> startDownload() throws IOException;

    static class ContinueDownload extends DownloadType {

        private Observable<DownloadStatus> rangeDownloadTask(final int i) {
            return Observable.create(new Observable.OnSubscribe<DownloadRange>() {
                @Override
                public void call(Subscriber<? super DownloadRange> subscriber) {
                    try {
                        DownloadRange range = mDownloadHelper.readDownloadRange(mUrl, i);
                        if (range.start < range.end) {
                            subscriber.onNext(range);
                        }
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }).flatMap(new Func1<DownloadRange, Observable<DownloadStatus>>() {

                @Override
                public Observable<DownloadStatus> call(final DownloadRange downloadRange) {
                    String range = "bytes=" + downloadRange.start + "-" + downloadRange.end;
                    return mDownloadHelper.getDownloadApi().download(range, mUrl)
                            .flatMap(new Func1<Response<ResponseBody>, Observable<DownloadStatus>>() {

                                @Override
                                public Observable<DownloadStatus> call(Response<ResponseBody> response) {
                                    return rangeSave(downloadRange.start, downloadRange.end, i, response.body());
                                }
                            });
                }
            }).subscribeOn(Schedulers.io()).onBackpressureLatest().retry(new Func2<Integer, Throwable, Boolean>() {
                @Override
                public Boolean call(Integer integer, Throwable throwable) {
                    //// TODO: 2017/5/8
                    return null;
                }
            });
        }

        private Observable<DownloadStatus> rangeSave(final long start, final long end, final int i, final ResponseBody response) {
            return Observable.create(new Observable.OnSubscribe<DownloadStatus>() {
                @Override
                public void call(Subscriber<? super DownloadStatus> subscriber) {
                    mDownloadHelper.saveRangeFile(subscriber, i, start, end, mUrl, response);
                }
            });
        }

        @Override
        public void prepareDownload() throws IOException, ParseException {
            Log.d(TAG, "prepareDownload: Continue download start!!");
        }

        @Override
        public Observable<DownloadStatus> startDownload() throws IOException {
            List<Observable<DownloadStatus>> tasks = new ArrayList<>();
            for (int i = 0; i < mDownloadHelper.getMaxThreads(); i++) {
                tasks.add(rangeDownloadTask(i));
            }
            return Observable.mergeDelayError(tasks);
        }
    }

    static class MultiThreadDownload extends ContinueDownload {
        @Override
        public void prepareDownload() throws IOException, ParseException {
            super.prepareDownload();
            mDownloadHelper.prepareMultiThreadDownload(mUrl, mFileLength, mLastModify);
        }

        @Override
        public Observable<DownloadStatus> startDownload() throws IOException {
            return super.startDownload();
        }
    }
}
