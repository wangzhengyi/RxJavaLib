package com.android.rxjava;


public class Observable<T> {
    public interface OnSubscribe<T> {
        void call(Subscriber<? super T> subscriber);
    }

    public interface Transformer<T, R> {
        R call(T from);
    }

    final OnSubscribe<T> onSubscribe;

    private Observable(OnSubscribe<T> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <T> Observable<T> create(OnSubscribe<T> onSubscribe) {
        return new Observable<T>(onSubscribe);
    }

    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onStart();
        onSubscribe.call(subscriber);
    }

    public <R> Observable<R> map(final Transformer<? super T, ? extends R> transformer) {
        return Observable.create(new MapOnSubscribe<T, R>(Observable.this, transformer));
    }

    public Observable<T> subscribeOn(final Scheduler scheduler) {
        return Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                subscriber.onStart();
                scheduler.createWorker().schedule(new Runnable() {
                    @Override
                    public void run() {
                        Observable.this.onSubscribe.call(subscriber);
                    }
                });
            }
        });
    }

    public Observable<T> observeOn(final Scheduler scheduler) {
        return Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                subscriber.onStart();
                final Scheduler.Worker worker = scheduler.createWorker();
                Observable.this.onSubscribe.call(new Subscriber<T>() {
                    @Override
                    public void onNext(final T value) {
                        worker.schedule(new Runnable() {
                            @Override
                            public void run() {
                                subscriber.onNext(value);
                            }
                        });
                    }

                    @Override
                    public void onCompleted() {
                        worker.schedule(new Runnable() {
                            @Override
                            public void run() {
                                subscriber.onCompleted();
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable t) {
                        worker.schedule(new Runnable() {
                            @Override
                            public void run() {
                                subscriber.onError(t);
                            }
                        });
                    }
                });
            }
        });
    }
}
