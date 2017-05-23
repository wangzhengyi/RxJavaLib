package com.android.rxjava;

public interface Observer<T> {
    void onNext(T value);
    void onCompleted();
    void onError(Throwable t);
}
