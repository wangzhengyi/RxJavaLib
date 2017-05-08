package com.wzy.rxdownload;


import android.content.Context;

import com.wzy.rxdownload.entity.DownloadStatus;
import com.wzy.rxdownload.function.DownloadHelper;

import rx.Observable;
import rx.functions.Func1;


public class RxDownload {

    private DownloadHelper mDownloadHelper;

    private Context mContext;

    private boolean mAutoInstall;

    private RxDownload() {
        mDownloadHelper = new DownloadHelper();
    }

    public static RxDownload getInstance() {
        return new RxDownload();
    }

    public RxDownload maxThread(int max) {
        mDownloadHelper.setMaxThreads(max);
        return this;
    }

    public RxDownload context(Context context) {
        this.mContext = context;
        return this;
    }

    public RxDownload autoInstall(boolean flag) {
        this.mAutoInstall = flag;
        return this;
    }

    public <T> Observable.Transformer<T, DownloadStatus> transform(final String url, final String saveName, final String savePath) {
        return new Observable.Transformer<T, DownloadStatus>() {
            @Override
            public Observable<DownloadStatus> call(Observable<T> tObservable) {
                return tObservable.flatMap(new Func1<T, Observable<DownloadStatus>>() {
                    @Override
                    public Observable<DownloadStatus> call(T t) {
                        return download(url, saveName, savePath);
                    }
                });
            }
        };
    }


    public Observable<DownloadStatus> download(final String url, final String saveName,
                                               final String savePath) {
        return mDownloadHelper.downloadDispatcher(url, saveName, savePath, mContext, mAutoInstall);
    }
}
