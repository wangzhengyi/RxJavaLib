package rxjava.android.com.rxjavastudy.interfaces;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GankIoService {
    @GET("api/random/data/Android/{num}")
    Call<ResponseBody> listAndroidInfo(@Path("num") String num);
}
