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
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.adapter.ApplicationAdapter;
import rxjava.android.com.rxjavastudy.bean.AppInfo;
import rxjava.android.com.rxjavastudy.bean.ApplicationsList;

public class ZipExampleFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApplicationAdapter mAdapter;
    private List<AppInfo> mAddApps = new ArrayList<>();

    public ZipExampleFragment() {
        super();
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
        initView();
        initData();
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ApplicationAdapter(new ArrayList<AppInfo>(), R.layout.apps_list_item);
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()
        ));
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(true);
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        List<AppInfo> apps = ApplicationsList.getInstance().getList();
        loadList(apps);
    }

    private void loadList(List<AppInfo> apps) {
        Observable<AppInfo> observableApp = Observable.from(apps);
        final Observable<Long> tictoc = Observable.interval(1, TimeUnit.SECONDS);
        Observable.zip(observableApp, tictoc, new Func2<AppInfo, Long, AppInfo>() {
            @Override
            public AppInfo call(AppInfo appInfo, Long time) {
                appInfo.setName(time + " " + appInfo.getName());
                return appInfo;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Here is zip fragment list!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        mAddApps.add(appInfo);
                        int position = mAddApps.size() - 1;
                        mAdapter.addApplication(position, appInfo);
                        recyclerView.smoothScrollToPosition(position);
                    }
                });
    }
}
