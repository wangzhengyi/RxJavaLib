package com.wzy.retrofit3;


import android.os.Build;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static com.wzy.retrofit3.Utils.checkNotNull;
import static java.util.Collections.unmodifiableList;

public class Retrofit {
    /**
     * 缓存Map，用于缓存解析出来的方法
     */
    private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();

    /**
     * 构造网络请求的工厂类，默认是OkHttp
     */
    final okhttp3.Call.Factory callFactory;

    /**
     * 请求的url
     */
    final HttpUrl baseUrl;

    /**
     * 请求网络得到的response的转换器的集合，默认会加入BuiltInConverters中
     */
    final List<Converter.Factory> converterFactories;

    /**
     * 把Call对象转换为其他类型对象
     */
    final List<CallAdapter.Factory> adapterFactories;

    /**
     * 用于执行回调的Executor.默认Android是MainThreadExecutor.
     */
    final Executor callbackExecutor;

    /**
     * 是否需要立即解析接口中的方法.
     */
    final boolean validateEagerly;

    Retrofit(okhttp3.Call.Factory callFactory, HttpUrl baseUrl,
             List<Converter.Factory> converterFactories, List<CallAdapter.Factory> adapterFactories,
             Executor callbackExecutor, boolean validateEagerly) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
        this.converterFactories = unmodifiableList(converterFactories); // Defensive copy at call site.
        this.adapterFactories = unmodifiableList(adapterFactories); // Defensive copy at call site.
        this.callbackExecutor = callbackExecutor;
        this.validateEagerly = validateEagerly;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        Utils.validateServiceInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        ServiceMethod<Object, Object> serviceMethod =
                                (ServiceMethod<Object, Object>) loadServiceMethod(method);
                        
                         return null;
                    }
                });
    }

    ServiceMethod<?, ?> loadServiceMethod(Method method) {
        ServiceMethod<?, ?> result = serviceMethodCache.get(method);
        if (result != null) return result;
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
//                result = new ServiceMethod.Builder<>(this, method).build();
//                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }


    public static final class Builder {
        private final Platform platform;
        private okhttp3.Call.Factory callFactory;
        private HttpUrl baseUrl;
        private final List<Converter.Factory> converterFactories = new ArrayList<>();
        private final List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        private Executor callbackExecutor;
        private boolean validateEagerly;

        Builder(Platform platform) {
            this.platform = platform;
            converterFactories.add(new BuiltInConverters());
        }

        public Builder() {
            this(Platform.get());
        }

        /**
         * 根据传入字符串设置Retrofit的基准URL
         */
        public Builder baseUrl(String baseUrl) {
            checkNotNull(baseUrl, "baseUrl == null");
            HttpUrl httpUrl = HttpUrl.parse(baseUrl);
            if (httpUrl == null) {
                throw new IllegalArgumentException("Illegal URL: " + baseUrl);
            }
            return baseUrl(httpUrl);
        }

        /**
         * 根据HttpUrl设置Retrofit的基准URL
         */
        public Builder baseUrl(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "baseUrl == null");
            List<String> pathSegments = baseUrl.pathSegments();
            if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
                throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
            }
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * 添加对请求和返回参数转换的类.
         */
        public Builder addConverterFactory(Converter.Factory factory) {
            converterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            adapterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Retrofit build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            okhttp3.Call.Factory callFactory = this.callFactory;
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }

            Executor callbackExecutor = this.callbackExecutor;
            if (callbackExecutor == null) {
                callbackExecutor = platform.defaultCallbackExecutor();
            }

            List<CallAdapter.Factory> adapterFactories = new ArrayList<>(this.adapterFactories);
            adapterFactories.add(platform.defaultCallAdapterFactory(callbackExecutor));

            List<Converter.Factory> converterFactories = new ArrayList<>(this.converterFactories);

            return new Retrofit(callFactory, baseUrl, converterFactories, adapterFactories,
                    callbackExecutor, validateEagerly);
        }
    }
}
