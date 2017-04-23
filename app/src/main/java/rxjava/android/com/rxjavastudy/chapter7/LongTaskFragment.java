package rxjava.android.com.rxjavastudy.chapter7;

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

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.adapter.ApplicationAdapter;
import rxjava.android.com.rxjavastudy.bean.AppInfo;
import rxjava.android.com.rxjavastudy.bean.ApplicationsList;

public class LongTaskFragment extends Fragment {
    private static final String TAG = LongTaskFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApplicationAdapter mAdapter;
    private List<AppInfo> mAddApps = new ArrayList<>();

    public LongTaskFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fg_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fg_refresh_container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ApplicationAdapter(new ArrayList<AppInfo>(), R.layout.apps_list_item);
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()
        ));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        initData();
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        List<AppInfo> apps = ApplicationsList.getInstance().getList();
        mAddApps.clear();
        getObservableApps(apps).onBackpressureBuffer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Here is long task list!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        mAddApps.add(appInfo);
                        mAdapter.addApplications(mAddApps);
                    }
                });
    }

    private Observable<AppInfo> getObservableApps(final List<AppInfo> apps) {
        return Observable.create(new Observable.OnSubscribe<AppInfo>() {

            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                for (double i = 0; i < 100000000; i++) {
                    double y = i * i;
                }

                if (!subscriber.isUnsubscribed()) {
                    for (AppInfo app : apps) {
                        subscriber.onNext(app);
                    }
                    subscriber.onCompleted();
                }
            }
        });
    }
}
