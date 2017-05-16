package com.wzy.rxdownload.entity;

public class DownloadEvent {
    private int flag = DownloadFlag.NORMAL;
    private DownloadStatus downloadStatus = new DownloadStatus();
    private Throwable error;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
