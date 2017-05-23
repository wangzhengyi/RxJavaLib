package com.android.rxjava;


public class Main {
    public static void main(String[] args) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("Current Thread:" + Thread.currentThread().getName());
                for (int i = 0; i < 10; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).map(new Observable.Transformer<Integer, String>() {
            @Override
            public String call(Integer from) {
                return "==haha:" + from;
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<String>() {
            @Override
            public void onNext(String value) {
                System.out.println("value=" + value);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted!");
                System.out.println("Current Thread:" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError!");
            }
        });
    }
}
