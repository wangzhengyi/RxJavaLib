package com.wzy.rxdownload;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.wzy.rxdownload.entity.DownloadEvent;
import com.wzy.rxdownload.entity.DownloadMission;
import com.wzy.rxdownload.entity.DownloadStatus;
import com.wzy.rxdownload.function.DownloadHelper;
import com.wzy.rxdownload.function.DownloadService;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;


public class RxDownload {
    private static final String TAG = RxDownload.class.getSimpleName();
    private static DownloadService mDownloadService;
    private static boolean bound = false;

    private DownloadHelper mDownloadHelper;

    private Context mContext;

    private boolean mAutoInstall;
    private int MAX_DOWNLOAD_NUMBER = 5;

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

    public RxDownload maxRetryCount(int max) {
        mDownloadHelper.setMaxRetryCount(max);
        return this;
    }

    public RxDownload setDownloadNumber(int max) {
        this.MAX_DOWNLOAD_NUMBER = max;
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

    public Observable<DownloadEvent> receiveDownloadStatus(final String url) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            subscriber.onNext(null);
                        }
                    });
                } else {
                    subscriber.onNext(null);
                }
            }
        }).flatMap(new Func1<Object, Observable<DownloadEvent>>() {
            @Override
            public Observable<DownloadEvent> call(Object obj) {
                return mDownloadService.getSubject(RxDownload.this, url).asObservable().onBackpressureLatest();
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<?> pauseServiceDownload(final String url) {
        return Observable.just(null).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            mDownloadService.pauseDownload(url);
                        }
                    });
                } else {
                    mDownloadService.pauseDownload(url);
                }
            }
        });
    }

    public Observable<?> cancelServiceDownload(final String url) {
        return Observable.just(null).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            mDownloadService.cancelDownload(url);
                        }
                    });
                } else {
                    mDownloadService.cancelDownload(url);
                }
            }
        });
    }

    public <T> Observable.Transformer<T, DownloadStatus> transform(final String url,
                                                                   final String saveName,
                                                                   final String savePath) {
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

    public <T> Observable.Transformer<T, Object> transformService(final String url,
                                                                  final String saveName,
                                                                  final String savePath) {
        return new Observable.Transformer<T, Object>() {

            @Override
            public Observable<Object> call(Observable<T> observable) {
                return observable.flatMap(new Func1<T, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(T t) {
                        return serviceDownload(url, saveName, savePath);
                    }
                });
            }
        };
    }

    /**
     * Normal Download..
     * <p>
     * UnSubscribe will pause download.
     * <p>
     * Do not save the download record in the database.
     *
     * @param url      Download url
     * @param saveName Download file name
     * @param savePath Download file save path. If null, using default save path {@code /storage/emulated/0/Download/}
     * @return Observable<DownloadState>
     */
    public Observable<DownloadStatus> download(final String url, final String saveName,
                                               final String savePath) {
        return mDownloadHelper.downloadDispatcher(url, saveName, savePath, mContext, mAutoInstall);
    }

    private void addDownloadTask(String url, String saveName, String savePath) throws IOException {
        Log.w(TAG, "addDownloadTask: begin!");
        mDownloadService.addDownloadMission(new DownloadMission.Builder()
                .setRxDownload(RxDownload.this)
                .setUrl(url)
                .setSaveName(saveName)
                .setSavePath(savePath)
                .build()
        );
    }

    public Observable<Object> serviceDownload(final String url, final String saveName,
                                              final String savePath) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            try {
                                Log.w(TAG, "call: addDownloadTask");
                                addDownloadTask(url, saveName, savePath);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            } catch (IOException e) {
                                subscriber.onError(e);
                            }
                        }
                    });
                } else {
                    try {
                        addDownloadTask(url, saveName, savePath);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void startBindServiceAndDo(final ServiceConnectedCallback callback) {
        if (mContext == null) {
            throw new RuntimeException("Context is NULL! You should call " +
                    "#RxDownload.context(Context context) first!");
        }
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(DownloadService.INTENT_KEY, MAX_DOWNLOAD_NUMBER);
        mContext.startService(intent);
        mContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.DownloadBinder downloadBinder =
                        (DownloadService.DownloadBinder) service;
                mDownloadService = downloadBinder.getService();
                mContext.unbindService(this);
                bound = true;
                callback.call();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    public File[] getRealFiles(String saveName, String savePath) {
        String[] filePaths = mDownloadHelper.getRealFilePaths(saveName, savePath);
        return new File[]{new File(filePaths[0]), new File(filePaths[1]), new File(filePaths[2])};
    }

    private interface ServiceConnectedCallback {
        void call();
    }
}
