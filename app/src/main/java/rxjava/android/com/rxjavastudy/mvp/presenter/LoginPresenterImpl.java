package rxjava.android.com.rxjavastudy.mvp.presenter;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import rxjava.android.com.rxjavastudy.mvp.model.UserModel;
import rxjava.android.com.rxjavastudy.mvp.view.ILoginView;

public class LoginPresenterImpl implements ILoginPresenter {
    private static final int MSG_LOGIN = 1;
    private ILoginView iLoginView;
    private UserModel userModel;
    private Handler mainHandler;

    public LoginPresenterImpl(final ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        initUser();
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_LOGIN) {
                    if (iLoginView != null) {
                        iLoginView.onLoginResult(msg.arg1 == 1);
                    }
                }
            }
        };
    }

    private void initUser() {
        userModel = new UserModel();
    }

    @Override
    public void clear() {
        if (iLoginView != null) {
            iLoginView.onClearUserName();
            iLoginView.onClearPwd();
        }
    }

    @Override
    public void doLogin(final String name, final String pwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userModel.setUserName(name);
                userModel.setPassWord(pwd);
                boolean res = userModel.canLogin();
                Message msg = mainHandler.obtainMessage(MSG_LOGIN);
                msg.arg1 = res ? 1 : 0;
                msg.sendToTarget();
            }
        }).start();
    }

    @Override
    public void setProgressBarVisibility(boolean visibility) {
        iLoginView.onSetProgressBarVisibility(visibility);
    }
}
