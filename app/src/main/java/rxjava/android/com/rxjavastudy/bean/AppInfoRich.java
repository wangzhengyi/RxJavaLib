package rxjava.android.com.rxjavastudy.bean;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;


/**
 * Created by zhengyi.wzy on 2017/4/11.
 */

public class AppInfoRich implements Comparable<Object>{
    private String name;
    private Context context;
    private ResolveInfo resolveInfo;
    private ComponentName componentName = null;
    private PackageInfo pi = null;
    private Drawable icon = null;

    public AppInfoRich(Context context, ResolveInfo ri) {
        this.context = context;
        this.resolveInfo = ri;
        this.componentName = new ComponentName(ri.activityInfo.applicationInfo.packageName,
                ri.activityInfo.name);
        try {
            this.pi = context.getPackageManager().getPackageInfo(ri.activityInfo.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            try {
                return getNameFromResolveInfo();
            } catch (PackageManager.NameNotFoundException e) {
                return getPackageName();
            }
        }
    }

    public String getActivityName() {
        return resolveInfo.activityInfo.name;
    }

    public String getPackageName() {
        return resolveInfo.activityInfo.packageName;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public String getComponentInfo() {
        if (getComponentName() != null) {
            return getComponentName().toString();
        } else {
            return "";
        }
    }

    public ResolveInfo getResolveInfo() {
        return resolveInfo;
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
        if (icon == null) {
            icon = getResolveInfo().loadIcon(context.getPackageManager());
        }
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

    public String getNameFromResolveInfo() throws PackageManager.NameNotFoundException {
        String name = resolveInfo.resolvePackageName;
        if (resolveInfo.activityInfo != null) {
            Resources res = context.getPackageManager().getResourcesForApplication(
                    resolveInfo.activityInfo.applicationInfo);
            Resources engRes = getEnglishResources(res);

            if (resolveInfo.activityInfo.labelRes != 0) {
                name = engRes.getString(resolveInfo.activityInfo.labelRes);
                if (TextUtils.isEmpty(name)) {
                    name = res.getString(resolveInfo.activityInfo.labelRes);
                }
            } else {
                name = resolveInfo.activityInfo.applicationInfo.
                        loadLogo(context.getPackageManager()).toString();
            }
        }

        return name;
    }

    private Resources getEnglishResources(Resources res) {
        AssetManager assetManager = res.getAssets();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration configuration = new Configuration(res.getConfiguration());
        configuration.locale = Locale.US;
        return new Resources(assetManager, metrics, configuration);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        AppInfoRich appInfoRich = (AppInfoRich) o;
        return getName().compareTo(appInfoRich.getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
