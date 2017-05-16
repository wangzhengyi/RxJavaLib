package com.wzy.rxdownload.entity;

import java.util.HashMap;
import java.util.Map;

public class DownloadEventFactory {
    private static volatile DownloadEventFactory singleton;
    private Map<String, DownloadEvent> map = new HashMap<>();

    private DownloadEventFactory() {

    }

    public static DownloadEventFactory getSingleton() {
        if (singleton == null) {
            synchronized (DownloadEventFactory.class) {
                if (singleton == null) {
                    singleton = new DownloadEventFactory();
                }
            }
        }

        return singleton;
    }

    public DownloadEvent create(String url, int flag, DownloadStatus status) {
        return createEvent(url, flag, status, null);
    }

    public DownloadEvent create(String url, int flag, DownloadStatus status, Throwable error) {
        return createEvent(url, flag, status, error);
    }

    private DownloadEvent createEvent(String url, int flag, DownloadStatus status, Throwable error) {
        DownloadEvent event = map.get(url);
        if (event == null) {
            event = new DownloadEvent();
            map.put(url, event);
        }
        event.setFlag(flag);
        event.setDownloadStatus(status == null ? new DownloadStatus() : status);
        event.setError(error);

        return event;
    }
}
