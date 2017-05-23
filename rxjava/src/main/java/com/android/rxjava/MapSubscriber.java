package com.android.rxjava;


public class MapSubscriber<R, T> extends Subscriber<T> {
    final Subscriber<? super R> actual;
    final Observable.Transformer<? super T, ? extends R> transformer;

    public MapSubscriber(Subscriber<? super R> actual, Observable.Transformer<? super T, ? extends R> transformer) {
        this.actual = actual;
        this.transformer = transformer;
    }

    @Override
    public void onNext(T value) {
        actual.onNext(transformer.call(value));
    }

    @Override
    public void onCompleted() {
        actual.onCompleted();
    }

    @Override
    public void onError(Throwable t) {
        actual.onError(t);
    }
}
