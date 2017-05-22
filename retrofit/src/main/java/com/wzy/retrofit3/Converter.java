package com.wzy.retrofit3;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * Retrofit关键的转换接口
 * @param <F>
 * @param <T>
 */
public interface Converter<F, T> {
    T convert(F value) throws IOException;

    abstract class Factory {
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            return null;
        }

        public Converter<?, ResponseBody> requestBodyConverter(Type type,
                                                               Annotation[] parameterAnnotations,
                                                               Annotation[] methodAnnotations,
                                                               Retrofit retrofit) {
            return null;
        }

        public Converter<?, String> stringConverter(Type type, Annotation[] annotations,
                                                    Retrofit retrofit) {
            return null;
        }
    }
}
