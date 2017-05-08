package com.wzy.rxdownload.entity;

import com.wzy.rxdownload.function.DownloadHelper;

public class DownloadTypeFactory {
    private String mUrl;
    private long mFileLength;
    private String mLastModify;
    private DownloadHelper mDownloadHelper;

    public DownloadTypeFactory(DownloadHelper downloadHelper) {
        this.mDownloadHelper = downloadHelper;
    }

    public DownloadTypeFactory url(String url) {
        this.mUrl = url;
        return this;
    }

    public DownloadTypeFactory fileLength(long fileLength) {
        this.mFileLength = fileLength;
        return this;
    }

    public DownloadTypeFactory lastModify(String lastModify) {
        this.mLastModify = lastModify;
        return this;
    }

    public DownloadType buildMultiDownload() {
        DownloadType type = new DownloadType.MultiThreadDownload();
        type.mUrl = mUrl;
        type.mFileLength = mFileLength;
        type.mLastModify = mLastModify;
        type.mDownloadHelper = mDownloadHelper;
        return type;
    }
}
