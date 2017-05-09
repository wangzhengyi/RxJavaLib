package rxjava.android.com.rxjavastudy.mvp.view;


public interface ILoginView {
    void onClearUserName();

    void onClearPwd();

    void onLoginResult(boolean result);

    void onSetProgressBarVisibility(boolean visibility);
}
