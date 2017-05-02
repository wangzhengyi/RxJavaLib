package rxjava.android.com.rxjavastudy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();
    private static ImageLoader sInstance;

    private static final int MSG_POST_RESULT = 1;
    private static final String DISK_CACHE_DIR_NAME = "photowall";


    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POLL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int TIME_OUT = 5000;
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mAtomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ImageLoader#" + mAtomicInteger.getAndIncrement());
        }
    };
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POLL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), THREAD_FACTORY);


    private static final int MAX_MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);
    private static final int IO_BUFFER_SIZE = 4096;
    private static final int MAX_DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int DISK_CACHE_INDEX = 0;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private boolean mIsDiskLruCacheCreated = false;
    private Handler mMainHandler;

    private ImageLoader(Context context) {
        mMemoryCache = new LruCache<String, Bitmap>(MAX_MEMORY_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        File file = getDiskCacheDir(context, DISK_CACHE_DIR_NAME);
        Log.i(TAG, "ImageLoader: file path=" + file.getAbsolutePath());
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        if (getUsableSpace(file) >= MAX_DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(file, 1, 1, MAX_DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                mDiskLruCache = null;
            }
        }

        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_POST_RESULT) {
                    ImageLoaderResult result = (ImageLoaderResult) msg.obj;
                    if (result.imageView.getTag() == result.url) {
                        result.imageView.setImageBitmap(result.bitmap);
                    }
                }
            }
        };
    }

    public static ImageLoader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(context);
                }
            }
        }

        return sInstance;
    }

    @SuppressWarnings("WeakerAccess")
    public static File getDiskCacheDir(Context context, String dirName) {
        String diskCachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //noinspection ConstantConditions
            diskCachePath = context.getExternalCacheDir().getPath();
        } else {
            diskCachePath = context.getCacheDir().getPath();
        }

        return new File(diskCachePath + File.separator + dirName);
    }

    @SuppressWarnings("WeakerAccess")
    public static String hashKeyFromUri(String url) {
        String md5 = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            md5 = getMd5(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    @SuppressWarnings("WeakerAccess")
    public static String getMd5(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private long getUsableSpace(File path) {
        return path.getUsableSpace();
    }

    public void bindImageView(final String uri, final ImageView imageView) {
        bindImageView(uri, imageView, 0, 0);
    }

    @SuppressWarnings("WeakerAccess")
    public void bindImageView(final String uri, final ImageView imageView,
                              final int reqWidth, final int reqHeight) {
        imageView.setTag(uri);
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                    ImageLoaderResult result = new ImageLoaderResult(imageView, uri, bitmap);
                    mMainHandler.obtainMessage(MSG_POST_RESULT, result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    private Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemoryCache(uri);
        if (bitmap != null) {
            return bitmap;
        }

        try {
            bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
            if (bitmap != null) {
                return bitmap;
            }
            bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadBitmapFromHttp(uri, reqWidth, reqHeight);
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromHttp(String uri, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("Can't visit network from UI thread!");
        }

        if (mDiskLruCache == null) {
            return null;
        }

        String hash = hashKeyFromUri(uri);
        DiskLruCache.Editor editor = mDiskLruCache.edit(hash);
        if (editor != null) {
            boolean res = downloadUriToDisk(uri, editor);
            if (res) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
    }

    private Bitmap downloadBitmapFromHttp(String uri, int reqWidth, int reqHeight) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(uri);
            if (url.getProtocol().equals("https")) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.connect();
            bitmap = ImageResizer.decodeBitmapFromInputStream(connection.getInputStream(), reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromDiskCache(String uri, int reqWidth, int reqHeight)
            throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.w(TAG, "loadBitmapFromDiskCache: load bitmap from UI thread, it's not recommend!");
        }

        if (mDiskLruCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        String hash = hashKeyFromUri(uri);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hash);
        if (snapshot != null) {
            FileInputStream fin = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fd = fin.getFD();
            bitmap = ImageResizer.decodeBitmapFromFileDescriptor(fd, reqWidth, reqHeight);
            if (bitmap != null) {
                putBitmapToMemoryCache(hash, bitmap);
            }
        }

        return bitmap;
    }

    private boolean downloadUriToDisk(String uri, DiskLruCache.Editor editor) {
        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;

        try {
            URL url = new URL(uri);
            if (url.getProtocol().equals("https")) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            bin = new BufferedInputStream(connection.getInputStream());
            bout = new BufferedOutputStream(editor.newOutputStream(0));
            byte[] buffer = new byte[IO_BUFFER_SIZE];
            int count;
            while ((count = bin.read(buffer)) != -1) {
                bout.write(buffer, 0, count);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (bin != null) {
                try {
                    bin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private Bitmap loadBitmapFromMemoryCache(String uri) {
        String key = hashKeyFromUri(uri);
        return getBitmapFromMemoryCache(key);
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private void putBitmapToMemoryCache(String key, Bitmap value) {
        mMemoryCache.put(key, value);
    }

    private static class ImageLoaderResult {
        ImageView imageView;
        String url;
        Bitmap bitmap;

        ImageLoaderResult(ImageView iv, String url, Bitmap bitmap) {
            this.imageView = iv;
            this.url = url;
            this.bitmap = bitmap;
        }
    }
}
