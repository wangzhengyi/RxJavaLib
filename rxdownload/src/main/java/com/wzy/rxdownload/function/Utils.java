package com.wzy.rxdownload.function;


import android.text.TextUtils;

import java.io.Closeable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Response;

public class Utils {
    public static String formatSize(long size) {
        String hrSize;

        double b = size;
        double k = size / 1024.0;
        double m = (size / 1024.0) / 1024.0;
        double g = ((size / 1024.0) / 1024.0) / 1024.0;
        double t = (((size / 1024.0) / 1024.0) / 1024.0) / 1024.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" B");
        }

        return hrSize;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static long GMTToLong(String GMT) throws ParseException {
        if (TextUtils.isEmpty(GMT)) {
            return new Date().getTime();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = sdf.parse(GMT);
        return date.getTime();
    }

    public static String lastModify(Response<?> response) {
        return response.headers().get("Last-Modified");
    }

    public static long contentLength(Response<?> response) {
        String contentLength = response.headers().get("Content-Length");
        if (TextUtils.isEmpty(contentLength)) {
            return -1;
        }
        try {
            return Long.parseLong(contentLength);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}