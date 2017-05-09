package rxjava.android.com.rxjavastudy.mvp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.mvp.presenter.ILoginPresenter;
import rxjava.android.com.rxjavastudy.mvp.presenter.LoginPresenterImpl;
import rxjava.android.com.rxjavastudy.mvp.view.ILoginView;


public class LoginFragment extends Fragment implements View.OnClickListener, ILoginView{
    private EditText mNameEditText;
    private EditText mPwdEditText;
    private Button mLoginBtn;
    private Button mClearBtn;
    private ProgressBar mProgressBar;

    private ILoginPresenter mILoginPresenter;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mNameEditText = (EditText) view.findViewById(R.id.username);
        mPwdEditText = (EditText) view.findViewById(R.id.password);
        mLoginBtn = (Button) view.findViewById(R.id.login_btn);
        mClearBtn = (Button) view.findViewById(R.id.clear_btn);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mLoginBtn.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mILoginPresenter = new LoginPresenterImpl(this);
        mILoginPresenter.setProgressBarVisibility(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_btn:
                mILoginPresenter.clear();
                break;
            case R.id.login_btn:
                mILoginPresenter.setProgressBarVisibility(true);
                mLoginBtn.setEnabled(false);
                mClearBtn.setEnabled(false);
                mILoginPresenter.doLogin(mNameEditText.getText().toString(),
                        mPwdEditText.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onClearUserName() {
        if (mNameEditText != null) {
            mNameEditText.setText("");
        }
    }

    @Override
    public void onClearPwd() {
        if (mPwdEditText != null) {
            mPwdEditText.setText("");
        }
    }

    @Override
    public void onLoginResult(boolean result) {
        mLoginBtn.setEnabled(true);
        mClearBtn.setEnabled(true);
        mILoginPresenter.setProgressBarVisibility(false);
        if (result) {
            Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetProgressBarVisibility(boolean visibility) {
        if (visibility) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
