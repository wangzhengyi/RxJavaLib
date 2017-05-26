package com.wzy.retrofit3;


import android.view.MotionEvent;

import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

final class ServiceMethod<R, T> {
    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

//    final okhttp3.Call.Factory callFactory;
//    final CallAdapter<R, T> callAdapter;
//
//    private final HttpUrl baseUrl;
//    private final Converter<ResponseBody, R> responseBodyRConverter;
//    private final String httpMethod;

    MotionEvent motionEvent;
}
