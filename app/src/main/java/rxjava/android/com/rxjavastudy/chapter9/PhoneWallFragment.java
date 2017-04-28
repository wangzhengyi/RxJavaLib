package rxjava.android.com.rxjavastudy.chapter9;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.adapter.PhoneAdapter;

public class PhoneWallFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PhoneAdapter adapter;

    public PhoneWallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fg_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fg_refresh_container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhoneAdapter(new ArrayList<>(Arrays.asList(images)));
        recyclerView.setAdapter(adapter);
    }

    public static final String[] images = new String[] {
            "http://img0.imgtn.bdimg.com/it/u=2162443587,1663946403&fm=11&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=1867674969,36032548&fm=11&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=413019943,3942951410&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2499252402,4107722607&fm=23&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3297020899,1547378592&fm=11&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=3664718883,3054833522&fm=23&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1564007966,1202254160&fm=23&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3301397110,3202372269&fm=11&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2058451262,4135721012&fm=23&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4151727954,891206300&fm=11&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1486182270,2322064845&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=878307094,3192793514&fm=11&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=19471491,3145722931&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=437345209,228449740&fm=23&gp=0.jpg"
    };
}
