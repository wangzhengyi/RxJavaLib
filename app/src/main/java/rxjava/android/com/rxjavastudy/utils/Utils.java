package rxjava.android.com.rxjavastudy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static final String SP_FILE_NAME = "apps";
    public static final String SP_APPS_KEY = "APPS";

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void storeBitmap(final Context context, final Bitmap bitmap, final String filename) {
        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                blockingStoreBitmap(context, bitmap, filename);
            }
        });
    }

    private static void blockingStoreBitmap(Context context, Bitmap bitmap, String filename) {
        FileOutputStream fout = null;
        try {
            fout =  new FileOutputStream(new File(filename), true);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
        } catch (Exception e) {
            Log.e(TAG, "blockingStoreBitmap: " + e.getMessage());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void testMain() {
        int[] arr = new int[10];
    }
}
