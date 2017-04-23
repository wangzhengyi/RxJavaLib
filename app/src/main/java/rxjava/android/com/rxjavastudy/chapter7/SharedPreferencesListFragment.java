package rxjava.android.com.rxjavastudy.chapter7;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
import rxjava.android.com.rxjavastudy.utils.Utils;

public class SharedPreferencesListFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<AppInfo> mAddApps = new ArrayList<>();
    private ApplicationAdapter mAdapter;
    private static final String TAG = SharedPreferencesListFragment.class.getSimpleName();

    public SharedPreferencesListFragment() {

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
        swipeRefreshLayout.setProgressViewOffset(true, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()
        ));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList();
            }
        });

        initData();
    }

    private void initData() {
        loadList();
    }

    private void loadList() {
        mAddApps.clear();
        getApps().onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "Here is sp list!", Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG)
                                .show();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        mAddApps.add(appInfo);
                        mAdapter.addApplications(mAddApps);
                    }
                });
    }

    private Observable<AppInfo> getApps() {
        return Observable.create(new Observable.OnSubscribe<AppInfo>() {
            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                List<AppInfo> apps;

                SharedPreferences sharedPref = getActivity().getSharedPreferences(
                        Utils.SP_FILE_NAME, Context.MODE_PRIVATE);
                String serializedApps = sharedPref.getString(Utils.SP_APPS_KEY, "");
                Log.w(TAG, "call: apps=" + serializedApps);
                Type type = new TypeToken<List<AppInfo>>() {
                }.getType();
                if (!"".equals(serializedApps)) {
                    apps = new Gson().fromJson(serializedApps, type);
                    for (AppInfo app : apps) {
                        subscriber.onNext(app);
                    }
                    subscriber.onCompleted();
                }
            }
        });
    }
}
