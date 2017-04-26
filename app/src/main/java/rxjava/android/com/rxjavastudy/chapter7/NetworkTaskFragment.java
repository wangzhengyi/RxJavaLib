package rxjava.android.com.rxjavastudy.chapter7;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.wzy.progress.ArcProgress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rxjava.android.com.rxjavastudy.App;
import rxjava.android.com.rxjavastudy.R;

public class NetworkTaskFragment extends Fragment {
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final String TAG = NetworkTaskFragment.class.getSimpleName();
    private static final String FILE_NAME = "sdcardsoftboy.avi";
    private ArcProgress mArcProgress;
    private Button mButton;

    private PublishSubject<Integer> mDownloadProgress = PublishSubject.create();

    public NetworkTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        mArcProgress = (ArcProgress) view.findViewById(R.id.arc_progress);
        mButton = (Button) view.findViewById(R.id.button_download);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
    }

    private void download() {
        mButton.setText(getString(R.string.downloading));
        mButton.setClickable(false);
        mDownloadProgress.distinct()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mArcProgress.setProgress(integer);
                    }
                });
        String destination;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            destination = App.getInstance().getExternalCacheDir().getAbsolutePath()
                    + File.separator + FILE_NAME;
        } else {
            destination = App.getInstance().getCacheDir().getAbsolutePath() + File.separator
                    + FILE_NAME;
        }
        String source = "http://archive.blender.org/fileadmin/movies/softboy.avi";
        File downloadFile = new File(destination);
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
        Log.i(TAG, "download: destination=" + destination);
        final String finalDestination = destination;
        observableDownload(source, destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        resetDownloadButton();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG)
                                .show();
                        resetDownloadButton();
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(Boolean success) {
                        if (success) {
                            Log.w(TAG, "onNext: success=" + success);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            File file = new File(finalDestination);
                            intent.setDataAndType(Uri.fromFile(file), "video/avi");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    private Observable<Boolean> observableDownload(final String source, final String destination) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                boolean result = downloadFile(source, destination);
                if (result) {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable("Download failed,"));
                }
            }
        });
    }

    private boolean downloadFile(String source, String destination) {
        boolean result = false;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(source);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            int fileLength = connection.getContentLength();
            bout = new BufferedOutputStream(new FileOutputStream(destination));
            bin = new BufferedInputStream(connection.getInputStream());
            int count;
            long total = 0;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = bin.read(data)) != -1) {
                total += count;
                int percentage = (int) ((total * 100) / fileLength);
                mDownloadProgress.onNext(percentage);
                bout.write(data, 0, count);
            }
            mDownloadProgress.onCompleted();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadProgress.onError(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (bin != null) {
                try {
                    bin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private void resetDownloadButton() {
        mButton.setText(getString(R.string.download));
        mButton.setClickable(true);
        mArcProgress.setProgress(0);
    }
}
