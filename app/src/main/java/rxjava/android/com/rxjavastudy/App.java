package rxjava.android.com.rxjavastudy;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhengyi.wzy on 2017/4/12.
 */

public class App extends Application {
    public static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
