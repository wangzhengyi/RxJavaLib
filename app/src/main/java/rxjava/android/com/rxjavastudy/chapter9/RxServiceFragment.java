package rxjava.android.com.rxjavastudy.chapter9;


import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wzy.rxdownload.RxDownload;
import com.wzy.rxdownload.entity.DownloadEvent;
import com.wzy.rxdownload.entity.DownloadFlag;
import com.wzy.rxdownload.entity.DownloadStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.utils.DownloadController;

/**
 * A simple {@link Fragment} subclass.
 */
public class RxServiceFragment extends Fragment {
    private static final String TAG = RxServiceFragment.class.getSimpleName();
    final String saveName = "xiyou.apk";
    final String defaultPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getPath();
    final String url = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";

    @BindView(R.id.img)
    ImageView mImg;

    @BindView(R.id.percent)
    TextView mPercent;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.size)
    TextView mSize;

    @BindView(R.id.status)
    TextView mStatusText;

    @BindView(R.id.action)
    Button mAction;

    private RxDownload mRxDownload;
    private DownloadController mDownloadController;

    public RxServiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rx_service, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String icon = "http://image.coolapk.com/apk_logo/2015/0330/12202_1427696232_8648.png";
        Picasso.with(getActivity()).load(icon).into(mImg);

        mRxDownload = RxDownload.getInstance().context(getActivity());
        mDownloadController = new DownloadController(mStatusText, mAction);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRxDownload.receiveDownloadStatus(url)
        .subscribe(new Action1<DownloadEvent>() {
            @Override
            public void call(DownloadEvent downloadEvent) {
                if (downloadEvent.getFlag() == DownloadFlag.FAILED) {
                    Throwable throwable = downloadEvent.getError();
                    Log.w(TAG, "call: " + throwable);
                }
                mDownloadController.setEvent(downloadEvent);
                updateProgress(downloadEvent);
            }
        });
    }

    private void updateProgress(DownloadEvent event) {
        DownloadStatus status = event.getDownloadStatus();
        mProgress.setIndeterminate(status.isChunked);
        mProgress.setMax((int) status.getTotalSize());
        mProgress.setProgress((int) status.getDownloadSize());
        mPercent.setText(status.getPercent());
        mSize.setText(status.getFormatStatusString());
    }

    @OnClick(R.id.action)
    public void onClick() {
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
                cancel();
            }

            @Override
            public void install() {

            }
        });
    }

    private void start() {
        new RxPermissions(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (!granted) {
                            throw new RuntimeException("no permission");
                        }
                    }
                })
                .compose(mRxDownload.transformService(url, saveName, defaultPath))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Toast.makeText(getActivity(), "下载开始", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pause() {
        mRxDownload.pauseServiceDownload(url).subscribe();
    }

    private void cancel() {
        mRxDownload.cancelServiceDownload(url).subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
    }
}
