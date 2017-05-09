package rxjava.android.com.rxjavastudy.chapter9;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wzy.rxdownload.RxDownload;
import com.wzy.rxdownload.entity.DownloadStatus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rxjava.android.com.rxjavastudy.App;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.utils.DownloadController;

public class RxDownloadFragment extends Fragment {
    private static final String DOWNLOAD_DIR = "rxdownload";
    @BindView(R.id.img)
    ImageView mImg;

    @BindView(R.id.status)
    TextView mStatus;

    @BindView(R.id.percent)
    TextView mPercent;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.size)
    TextView mSize;

    @BindView(R.id.action)
    Button mAction;

    private String saveName = "weixin.apk";
    private String url = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";

//    private String saveName = "keep.apk";
//    private String url = "https://dl-android.keepcdn.com/keep-latest_71785d03a16692e5a5c7bc3d6e7c85db.apk?download/keep-latest.apk";

    private String defaultPath = getDownloadDirectory(DOWNLOAD_DIR);

    private Unbinder unbinder;
    private RxDownload mRxDownload;
    private Subscription subscription;
    private DownloadController mDownloadController;

    public RxDownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rx_download, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Picasso.with(getActivity()).load("http://static.yingyonghui.com/icon/128/4200197.png").into(mImg);
        mAction.setText("开始");

        mRxDownload = RxDownload.getInstance()
                .maxThread(3)
                .context(App.getInstance())
                .autoInstall(true);

        mDownloadController = new DownloadController(mStatus, mAction);
        mDownloadController.setState(new DownloadController.Normal());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private String getDownloadDirectory(String dirName) {
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = App.getInstance().getExternalCacheDir().getPath();
        } else {
            path = App.getInstance().getCacheDir().getPath();
        }

        return path + File.separator + dirName;
    }

    @OnClick(R.id.action)
    public void onAction(View view) {
        mDownloadController.handleClick(new DownloadController.Callback() {
            @Override
            public void startDownload() {
                start();
            }

            @Override
            public void pauseDownload() {
                pause();
            }

            @Override
            public void cancelDownload() {

            }

            @Override
            public void install() {
                installApk();
            }
        });
    }

    private void start() {
        subscription = new RxPermissions(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (!granted) {
                            throw new RuntimeException("no permission");
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .compose(mRxDownload.transform(url, saveName, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DownloadStatus>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mDownloadController.setState(new DownloadController.Started());
                    }

                    @Override
                    public void onCompleted() {
                        mDownloadController.setState(new DownloadController.Completed());
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDownloadController.setState(new DownloadController.Failed());
                    }

                    @Override
                    public void onNext(DownloadStatus downloadStatus) {
                        mProgress.setIndeterminate(downloadStatus.isChunked);
                        mProgress.setMax((int) downloadStatus.getTotalSize());
                        mProgress.setProgress((int) downloadStatus.getDownloadSize());
                        mPercent.setText(downloadStatus.getPercent());
                        mSize.setText(downloadStatus.getFormatStatusString());
                    }
                });
    }

    private void pause() {
        mDownloadController.setState(new DownloadController.Paused());
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void installApk() {
        Uri uri = Uri.fromFile(new File(defaultPath + File.separator + saveName));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
