package com.wzy.rxdownload.function;


import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wzy.rxdownload.BuildConfig;
import com.wzy.rxdownload.entity.DownloadRange;
import com.wzy.rxdownload.entity.DownloadStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;

import okhttp3.ResponseBody;
import rx.Subscriber;

public class FileHelper {
    public static final String TAG = "RxDownload";

    private static final String TMP_SUFFIX = ".tmp";
    private static final String LMF_SUFFIX = ".lmf";
    private static final String CACHE = ".cache";

    private static final int EACH_RECORD_SIZE = 16;
    private int RECORD_FILE_TOTAL_SIZE;
    private int MAX_THREADS = 3;

    private String mDefaultSavePath;
    private String mDefaultCachePath;

    FileHelper() {
        RECORD_FILE_TOTAL_SIZE = EACH_RECORD_SIZE * MAX_THREADS;
        setDefaultSavePath(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath());
    }

    int getMaxThreads() {
        return MAX_THREADS;
    }

    void setMaxThreads(int maxThreads) {
        this.MAX_THREADS = maxThreads;
        RECORD_FILE_TOTAL_SIZE = EACH_RECORD_SIZE * maxThreads;
    }

    void setDefaultSavePath(String defaultSavePath) {
        mDefaultSavePath = defaultSavePath;
        mDefaultCachePath = (String) TextUtils.concat(defaultSavePath, File.separator, CACHE);
    }

    void createDirectories(String savePath) throws IOException {
        createDirectories(getRealDirectoryPaths(savePath));
    }

    private void createDirectories(String... directoryPaths) throws IOException {
        for (String each : directoryPaths) {
            File file = new File(each);
            if (file.exists() && file.isDirectory()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "createDirectories: Directory exists. Don't need created. Path =" + each);
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "createDirectories: Directory is not exists. So we need create. Path = " + each);
                }
                boolean flag = file.mkdirs();
                if (flag) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "createDirectories: Directory create succeed! Path = " + each);
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "createDirectories: Directory create failed! Path = " + each);
                        throw new IOException("Directory create failed! Path = " + each);
                    }
                }
            }
        }
    }

    String[] getRealDirectoryPaths(String savePath) {
        String fileDirectory;
        String cacheDirectory;

        if (!TextUtils.isEmpty(savePath)) {
            fileDirectory = savePath;
            cacheDirectory = (String) TextUtils.concat(savePath, File.separator, CACHE);
        } else {
            fileDirectory = mDefaultSavePath;
            cacheDirectory = mDefaultCachePath;
        }

        return new String[] {fileDirectory, cacheDirectory};
    }

    String[] getRealFilePaths(String saveName, String savePath) {
        String[] directories = getRealDirectoryPaths(savePath);
        String filePath = (String) TextUtils.concat(directories[0], File.separator, saveName);
        String tempPath = (String) TextUtils.concat(directories[1], File.separator, saveName, TMP_SUFFIX);
        String lmfPath = (String) TextUtils.concat(directories[1], File.separator, saveName, LMF_SUFFIX);
        return new String[] {filePath, tempPath, lmfPath};
    }

    DownloadRange readDownloadRange(File tempFile, int i) throws IOException {
        RandomAccessFile record = null;
        FileChannel channel = null;

        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE,
                    i * EACH_RECORD_SIZE, (i + 1) * EACH_RECORD_SIZE);
            long startByte = buffer.getLong();
            long endByte = buffer.getLong();
            return new DownloadRange(startByte, endByte);
        } finally {
            Utils.closeQuietly(record);
            Utils.closeQuietly(channel);
        }
    }

    void saveFile(Subscriber<? super DownloadStatus> subscriber, int i, long start, long end,
                  File tempFile, File saveFile, ResponseBody responseBody) {
        RandomAccessFile record = null;
        FileChannel recordChannel = null;

        RandomAccessFile save = null;
        FileChannel saveChannel = null;

        InputStream in = null;

        try {
            Log.d(TAG, "saveFile: " + Thread.currentThread().getName() + " start download from " + start + " to " + end);
            int readLen;
            byte[] buffer = new byte[8192];
            DownloadStatus status = new DownloadStatus();

            record = new RandomAccessFile(tempFile, "rws");
            recordChannel = record.getChannel();
            MappedByteBuffer recordBuffer = recordChannel.map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);
            long totalSize = recordBuffer.getLong(RECORD_FILE_TOTAL_SIZE - 8) + 1;
            status.setTotalSize(totalSize);

            save = new RandomAccessFile(saveFile, "rws");
            saveChannel = save.getChannel();
            MappedByteBuffer saveBuffer = saveChannel.map(FileChannel.MapMode.READ_WRITE, start, end - start + 1);

            in = responseBody.byteStream();
            while ((readLen = in.read(buffer)) != -1) {
                saveBuffer.put(buffer, 0, readLen);
                recordBuffer.putLong(i * EACH_RECORD_SIZE, recordBuffer.getLong(i * EACH_RECORD_SIZE) + readLen);

                status.setDownloadSize(totalSize - getResidue(recordBuffer));
                subscriber.onNext(status);
            }
            Log.d(TAG, "saveFile: " + Thread.currentThread().getName() + " complete download! Download size is " + responseBody.contentLength() + " bytes");
            subscriber.onCompleted();
        } catch (IOException e) {
            subscriber.onError(e);
        } finally {
            Utils.closeQuietly(record);
            Utils.closeQuietly(recordChannel);
            Utils.closeQuietly(save);
            Utils.closeQuietly(saveChannel);
            Utils.closeQuietly(in);
        }
    }

    void prepareDownload(File lastModifyFile, File tempFile, File saveFile, long fileLength, String lastModify) throws IOException, ParseException {
        writeLastModify(lastModifyFile, lastModify);
        RandomAccessFile save = null;
        RandomAccessFile record = null;
        FileChannel recordChannle = null;

        try {
            save = new RandomAccessFile(saveFile, "rws");
            save.setLength(fileLength);

            record = new RandomAccessFile(tempFile, "rws");
            record.setLength(RECORD_FILE_TOTAL_SIZE);
            recordChannle = record.getChannel();
            MappedByteBuffer buffer = recordChannle.map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);

            long start;
            long end;
            int eachSize = (int) (fileLength / MAX_THREADS);
            for (int i = 0; i < MAX_THREADS; i++) {
                if (i == MAX_THREADS - 1) {
                    start = i * eachSize;
                    end = fileLength - 1;
                } else {
                    start = i * eachSize;
                    end = (i + 1) * eachSize - 1;
                }
                buffer.putLong(start);
                buffer.putLong(end);
            }
        } finally {
            Utils.closeQuietly(save);
            Utils.closeQuietly(recordChannle);
            Utils.closeQuietly(record);
        }
    }

    private void writeLastModify(File lmfFile, String lastModify) throws IOException, ParseException {
        RandomAccessFile lmf = null;
        FileChannel lmfChannel = null;
        try {
            lmf = new RandomAccessFile(lmfFile, "rws");
            lmfChannel = lmf.getChannel();
            MappedByteBuffer lmfBuffer = lmfChannel.map(FileChannel.MapMode.READ_WRITE, 0, 8);
            lmfBuffer.putLong(Utils.GMTToLong(lastModify));
        } finally {
            Utils.closeQuietly(lmfChannel);
            Utils.closeQuietly(lmf);
        }
    }

    private long getResidue(MappedByteBuffer recordBuffer) {
        long residue = 0;
        for (int i = 0; i < MAX_THREADS; i ++) {
            long startTemp = recordBuffer.getLong(i * EACH_RECORD_SIZE);
            long endTemp = recordBuffer.getLong(i * EACH_RECORD_SIZE + 8);
            long temp = endTemp - startTemp + 1;
            residue += temp;
        }
        return residue;
    }
}
