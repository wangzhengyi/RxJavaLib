package rxjava.android.com.rxjavastudy.mvp.model;

import android.text.TextUtils;

public class UserModel {
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public boolean canLogin() {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
            return false;
        }

        if (userName.equals("wangzhengyi") && passWord.equals("1234")) {
            return true;
        }

        return false;
    }
}
