package rxjava.android.com.rxjavastudy.mvp.presenter;


public interface ILoginPresenter {
    void clear();
    void doLogin(String name, String pwd);
    void setProgressBarVisibility(boolean visibility);
}
