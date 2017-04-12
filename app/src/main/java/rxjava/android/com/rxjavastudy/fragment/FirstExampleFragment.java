package rxjava.android.com.rxjavastudy.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rxjava.android.com.rxjavastudy.App;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.adapter.ApplicationAdapter;
import rxjava.android.com.rxjavastudy.bean.AppInfo;
import rxjava.android.com.rxjavastudy.bean.AppInfoRich;
import rxjava.android.com.rxjavastudy.utils.Utils;


public class FirstExampleFragment extends Fragment {
    private static final String TAG = FirstExampleFragment.class.getSimpleName();
    private File mFilesDir;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApplicationAdapter mAdapter;

    public FirstExampleFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity()
                .findViewById(R.id.fragment_first_example_swipe_container);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.fragment_first_example_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ApplicationAdapter(new ArrayList<AppInfo>(), R.layout.applications_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: is called");
                refreshTheList();
            }
        });
    }

    private void initData() {
        // Progress
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setVisibility(View.GONE);

        getFileDir()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File file) {
                        mFilesDir = file;
                        refreshTheList();
                    }
                });
    }

    private Observable<File> getFileDir() {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                subscriber.onNext(App.instance.getFilesDir());
                subscriber.onCompleted();
            }
        });
    }

    private void refreshTheList() {
        getApps().toSortedList()
                .subscribe(new Observer<List<AppInfo>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e == null) {
                            Log.e(TAG, "onError: throwable is null");
                        }
                        Log.e(TAG, "onError: " + e.getMessage());
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<AppInfo> appInfos) {
                        Log.d(TAG, "onNext: observer receive msg, appInfos size=" + appInfos.size());
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mAdapter.addApplications(appInfos);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private Observable<AppInfo> getApps() {
        return Observable.create(new Observable.OnSubscribe<AppInfo>() {
            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                List<AppInfoRich> apps = new ArrayList<AppInfoRich>();
                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> infos = getActivity().getPackageManager()
                        .queryIntentActivities(mainIntent, 0);
                for (ResolveInfo ri : infos) {
                    apps.add(new AppInfoRich(getActivity(), ri));
                }

                for (AppInfoRich appInfoRich : apps) {
                    Bitmap icon = Utils.drawableToBitmap(appInfoRich.getIcon());
                    String name = appInfoRich.getName();
                    String iconPath = mFilesDir + "/" + name;
                    Utils.storeBitmap(getActivity(), icon, name);

                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    Log.d(TAG, "call: name=" + name + ", icon=" + iconPath);
                    subscriber.onNext(new AppInfo(name, iconPath, appInfoRich.getLastUpdateTime()));
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }
}
