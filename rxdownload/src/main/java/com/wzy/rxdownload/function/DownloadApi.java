package com.wzy.rxdownload.function;


import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface DownloadApi {
    @HEAD
    Observable<Response<Void>> getHttpHeader(@Header("Range") String range, @Url String url);

    @HEAD
    Observable<Response<Void>> getHttpHeaderWithIfRange(@Header("Range") String range, @Header("If-Range") String lastModify, @Url String url);

    @GET
    @Streaming
    Observable<Response<ResponseBody>> download(@Header("Range") String range, @Url String url);

    @GET
    Observable<Response<Void>> requestWithIfRange(@Header("Range") String range, @Header("If-Range") String lastModify, @Url String url);
}
