package com.wzy.rxdownload.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.wzy.rxdownload.function.Utils;

import java.text.NumberFormat;

public class DownloadStatus implements Parcelable{
    public boolean isChunked = false;
    private long totalSize;
    private long downloadSize;

    public DownloadStatus() {

    }

    public DownloadStatus(long downloadSize, long totalSize) {
        this.downloadSize = downloadSize;
        this.totalSize = totalSize;
    }

    public DownloadStatus(boolean isChunked, long downloadSize, long totalSize) {
        this.isChunked = isChunked;
        this.downloadSize = downloadSize;
        this.totalSize = totalSize;
    }

    protected DownloadStatus(Parcel in) {
        isChunked = in.readByte() != 0;
        totalSize = in.readLong();
        downloadSize = in.readLong();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    /**
     * 获取格式化的总Size
     * @return example: 2KB, 10MB, 1GB
     */
    public String getFormatTotalSize() {
        return Utils.formatSize(totalSize);
    }

    /**
     * 获取格式化的下载总量
     * @return example: 1KB
     */
    public String getFormatDownloadSize() {
        return Utils.formatSize(downloadSize);
    }

    /**
     * 获取格式化的状态字符串
     * @return example: 2MB/100MB
     */
    public String getFormatStatusString() {
        return getFormatDownloadSize() + "/" + getFormatTotalSize();
    }

    /**
     * 获取下载的百分比，保留两位小数
     * @return example: 23.23%
     */
    public String getPercent() {
        String percent;
        double result;

        if (totalSize == 0L) {
            result = 0.00;
        } else {
            result = downloadSize * 1.0 / totalSize;
        }

        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        percent = nf.format(result);
        return percent;
    }

    public static final Creator<DownloadStatus> CREATOR = new Creator<DownloadStatus>() {
        @Override
        public DownloadStatus createFromParcel(Parcel in) {
            return new DownloadStatus(in);
        }

        @Override
        public DownloadStatus[] newArray(int size) {
            return new DownloadStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isChunked ? 1 : 0));
        dest.writeLong(totalSize);
        dest.writeLong(downloadSize);
    }
}
