package com.wzy.rxdownload.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wzy.rxdownload.entity.DownloadFlag;
import com.wzy.rxdownload.entity.DownloadMission;
import com.wzy.rxdownload.entity.DownloadRecord;
import com.wzy.rxdownload.entity.DownloadStatus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wzy.rxdownload.db.Db.RecordTable.COLUMN_DOWNLOAD_FLAG;
import static com.wzy.rxdownload.db.Db.RecordTable.COLUMN_DOWNLOAD_SIZE;
import static com.wzy.rxdownload.db.Db.RecordTable.COLUMN_ID;
import static com.wzy.rxdownload.db.Db.RecordTable.COLUMN_IS_CHUNKED;
import static com.wzy.rxdownload.db.Db.RecordTable.COLUMN_TOTAL_SIZE;
import static com.wzy.rxdownload.db.Db.RecordTable.COLUMN_URL;
import static com.wzy.rxdownload.db.Db.RecordTable.TABLE_NAME;
import static com.wzy.rxdownload.db.Db.RecordTable.insert;
import static com.wzy.rxdownload.db.Db.RecordTable.update;

public class DataBaseHelper {
    private volatile static DataBaseHelper singleInstance;
    private volatile SQLiteDatabase readableDatabase;
    private volatile SQLiteDatabase writeableDatabase;
    private final Object dbLock = new Object();
    private DbOpenHelper dbOpenHelper;

    private DataBaseHelper(Context context) {
        dbOpenHelper = new DbOpenHelper(context);
    }

    public static DataBaseHelper getSingleInstance(Context context) {
        if (singleInstance == null) {
            synchronized (DataBaseHelper.class) {
                if (singleInstance == null) {
                    singleInstance = new DataBaseHelper(context);
                }
            }
        }

        return singleInstance;
    }

    private SQLiteDatabase getWriteableDatabase() {
        SQLiteDatabase db = writeableDatabase;
        if (db == null) {
            synchronized (dbLock) {
                //noinspection ConstantConditions
                if (db == null) {
                    db = writeableDatabase = dbOpenHelper.getWritableDatabase();
                }
            }
        }
        return db;
    }

    private SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = readableDatabase;
        if (db == null) {
            synchronized (dbLock) {
                //noinspection ConstantConditions
                if (db == null) {
                    db = readableDatabase = dbOpenHelper.getReadableDatabase();
                }
            }
        }

        return db;
    }

    public boolean recordNotExists(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, new String[]{COLUMN_ID},
                    COLUMN_URL + "=?", new String[]{url}, null, null, null);
            return cursor.getCount() == 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long insertRecord(DownloadMission mission) {
        return getWriteableDatabase().insert(TABLE_NAME, null, insert(mission));
    }

    public long updateRecord(String url, DownloadStatus status) {
        return getWriteableDatabase().update(TABLE_NAME, update(status), COLUMN_URL + "=?",
                new String[]{url});
    }

    public long updateRecord(String url, int flag) {
        return getWriteableDatabase().update(TABLE_NAME, update(flag), COLUMN_URL + "=?",
                new String[]{url});
    }

    public int deleteRecord(String url) {
        return getWriteableDatabase().delete(TABLE_NAME, COLUMN_URL + "=?", new String[]{url});
    }

    public DownloadRecord readSingleRecord(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, null, COLUMN_URL + "=?",
                    new String[]{url}, null, null, null);
            if (cursor.moveToFirst()) {
                return Db.RecordTable.read(cursor);
            } else {
                return new DownloadRecord();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public DownloadStatus readDownloadStatus(String url) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, new String[]{
                            COLUMN_DOWNLOAD_SIZE, COLUMN_TOTAL_SIZE, COLUMN_IS_CHUNKED}, COLUMN_URL + "=?",
                    new String[]{url}, null, null, null);
            if (cursor.moveToFirst()) {
                return Db.RecordTable.readStatus(cursor);
            } else {
                return new DownloadStatus();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void closeDatabase() {
        synchronized (dbLock) {
            readableDatabase = null;
            writeableDatabase = null;
            dbOpenHelper.close();
        }
    }

    public Observable<List<DownloadRecord>> readAllRecords() {
        return Observable.create(new Observable.OnSubscribe<List<DownloadRecord>>() {
            @Override
            public void call(Subscriber<? super List<DownloadRecord>> subscriber) {
                Cursor cursor = null;
                List<DownloadRecord> result = new ArrayList<DownloadRecord>();
                try {
                    cursor = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            result.add(Db.RecordTable.read(cursor));
                        } while (cursor.moveToNext());
                    }
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DownloadRecord> readRecord(final String url) {
        return Observable.create(new Observable.OnSubscribe<DownloadRecord>() {
            @Override
            public void call(Subscriber<? super DownloadRecord> subscriber) {
                Cursor cursor = null;
                DownloadRecord record;
                try {
                    cursor = getReadableDatabase().query(TABLE_NAME, null, COLUMN_URL + "=?",
                            new String[]{url}, null, null, null);
                    if (cursor.moveToFirst()) {
                        record = Db.RecordTable.read(cursor);
                    } else {
                        record = new DownloadRecord();
                    }
                    subscriber.onNext(record);
                    subscriber.onCompleted();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public long repaireErrorFlag() {
        return getWriteableDatabase().update(TABLE_NAME, Db.RecordTable.update(DownloadFlag.PAUSED),
                COLUMN_DOWNLOAD_FLAG + "=? or " + COLUMN_DOWNLOAD_FLAG + "=?", new String[]{
                        String.valueOf(DownloadFlag.WAITING), String.valueOf(DownloadFlag.STARTED)
                });
    }
}
