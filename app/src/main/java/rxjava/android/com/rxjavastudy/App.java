package rxjava.android.com.rxjavastudy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rxjava.android.com.rxjavastudy.bean.AppInfo;
import rxjava.android.com.rxjavastudy.bean.ApplicationsList;
import rxjava.android.com.rxjavastudy.utils.Utils;

public class App extends Application {
    public static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        saveAppsToSp();
    }

    private void saveAppsToSp() {
        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                doSaveProcess();
            }

            private void doSaveProcess() {
                SharedPreferences sp = getSharedPreferences(
                        Utils.SP_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                List<AppInfo> apps = ApplicationsList.getInstance().getList();
                String serializedJson = new Gson().toJson(apps);
                editor.putString(Utils.SP_APPS_KEY, serializedJson);
                editor.apply();
            }
        });
    }
}
