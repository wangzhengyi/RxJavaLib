package rxjava.android.com.rxjavastudy.bean;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rxjava.android.com.rxjavastudy.App;
import rxjava.android.com.rxjavastudy.utils.Utils;

public class ApplicationsList {

    private static class SingletonHolder {
        public static ApplicationsList sInstance = new ApplicationsList();
    }

    public static ApplicationsList getInstance() {
        return SingletonHolder.sInstance;
    }

    public List<AppInfo> getList() {
        List<AppInfo> res = new ArrayList<>();
        String appFilePath = App.instance.getFilesDir().getAbsolutePath();
        PackageManager pm = App.instance.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        List<AppInfoRich> apps = new ArrayList<>();
        for (PackageInfo pi : packages) {
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                apps.add(new AppInfoRich(App.instance, pi));
            }
        }

        for (AppInfoRich appInfoRich : apps) {
            Bitmap icon = Utils.drawableToBitmap(appInfoRich.getIcon());
            String name = appInfoRich.getName();
            String iconPath = appFilePath + File.separator + name.trim();
            Utils.storeBitmap(App.instance, icon, iconPath);
            AppInfo appInfo = new AppInfo(name, iconPath, appInfoRich.getLastUpdateTime());
            res.add(appInfo);
        }

        return res;
    }
}
