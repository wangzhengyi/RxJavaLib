package com.wzy.retrofit3;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

class Platform {
    private static final Platform PLATFORM = findPlatform();

    private static Platform findPlatform() {
        return new Android();
    }

    public static Platform get() {
        return PLATFORM;
    }

    Executor defaultCallbackExecutor() {
        return null;
    }

    CallAdapter.Factory defaultCallAdapterFactory(Executor callbackExecutor) {
        if (callbackExecutor != null) {
            return new ExecutorCallAdapterFactory(callbackExecutor);
        }
        return DefaultCallAdapterFactory.INSTANCE;
    }

    static class Android extends Platform {
        @Override
        Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        @Override
        CallAdapter.Factory defaultCallAdapterFactory(Executor callbackExecutor) {
            if (callbackExecutor == null) throw new AssertionError();
            return new ExecutorCallAdapterFactory(callbackExecutor);
        }

        static class MainThreadExecutor implements Executor {
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(Runnable r) {
                handler.post(r);
            }
        }
    }
}
