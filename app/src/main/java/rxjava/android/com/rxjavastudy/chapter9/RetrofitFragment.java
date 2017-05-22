package rxjava.android.com.rxjavastudy.chapter9;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.bean.AndroidInfo;
import rxjava.android.com.rxjavastudy.interfaces.GankIoService;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetrofitFragment extends Fragment {
    private static final String TAG = RetrofitFragment.class.getSimpleName();

    @BindView(R.id.id_start_btn)
    Button mStartBtn;

    @BindView(R.id.id_content)
    TextView mContent;

    public RetrofitFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_retrofit, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.id_start_btn)
    public void onStartRetrofit(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GankIoService service = retrofit.create(GankIoService.class);
        Call<ResponseBody> call = service.listAndroidInfo("10");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
                Toast.makeText(getActivity(), "Request Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
