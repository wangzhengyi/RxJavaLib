package rxjava.android.com.rxjavastudy.bean;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class AppInfoRich implements Comparable<Object>{
    private static final String TAG = AppInfoRich.class.getSimpleName();
    private String name;
    private PackageInfo pi = null;
    private Drawable icon = null;
    private Intent launchIntent;
    private String packageName;

    public AppInfoRich(Context context, PackageInfo pi) {
        this.pi = pi;
        PackageManager pm = context.getPackageManager();
        this.name = (String) pi.applicationInfo.loadLabel(pm);
        this.icon = pi.applicationInfo.loadIcon(pm);
        this.packageName = pi.packageName;
        this.launchIntent = pm.getLaunchIntentForPackage(pi.packageName);
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Intent getLaunchIntent() {
        return this.launchIntent;
    }

    public PackageInfo getPackageInfo() {
        return pi;
    }

    public String getVersionName() {
        if (pi != null) {
            return pi.versionName;
        } else {
            return "";
        }
    }

    public int getVersionCode() {
        if (pi != null) {
            return pi.versionCode;
        } else {
            return 0;
        }
    }

    public Drawable getIcon() {
        return icon;
    }

    public long getFristInstallTime() {
        if (pi != null) {
            return pi.firstInstallTime;
        } else {
            return 0;
        }
    }

    public long getLastUpdateTime() {
        if (pi != null) {
            return pi.lastUpdateTime;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(@NonNull Object o) {
        AppInfoRich appInfoRich = (AppInfoRich) o;
        return getName().compareTo(appInfoRich.getName());
    }
}
