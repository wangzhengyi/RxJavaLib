package rxjava.android.com.rxjavastudy.chapter6;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.adapter.ApplicationAdapter;
import rxjava.android.com.rxjavastudy.bean.AppInfo;
import rxjava.android.com.rxjavastudy.bean.ApplicationsList;

public class MergeExampleFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApplicationAdapter mAdapter;
    private List<AppInfo> mAddApps = new ArrayList<>();

    public MergeExampleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                view.findViewById(R.id.fg_refresh_container);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fg_list);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ApplicationAdapter(new ArrayList<AppInfo>(), R.layout.apps_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        // Progress
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        mAddApps.clear();
        List<AppInfo> apps = ApplicationsList.getInstance().getList();
        List<AppInfo> reversedApps = new ArrayList<>(apps);
        Collections.reverse(reversedApps);
        Observable<AppInfo> observableApps = Observable.from(apps);
        Observable<AppInfo> observableReversedApps = Observable.from(reversedApps);
        Observable<AppInfo> mergedObservable = Observable
                .mergeDelayError(observableApps, observableReversedApps);
        mergedObservable.subscribe(new Observer<AppInfo>() {
            @Override
            public void onCompleted() {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "merge list!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(AppInfo appInfo) {
                mAddApps.add(appInfo);
                mAdapter.addApplications(mAddApps);
            }
        });
    }
}
