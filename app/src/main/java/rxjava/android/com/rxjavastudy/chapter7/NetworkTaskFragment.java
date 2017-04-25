package rxjava.android.com.rxjavastudy.chapter7;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.wzy.progress.ArcProgress;

import rxjava.android.com.rxjavastudy.R;

public class NetworkTaskFragment extends Fragment {
    private ArcProgress mArcProgress;
    private Button mButton;


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
        initData();
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
        
    }

    private void initData() {

    }
}
