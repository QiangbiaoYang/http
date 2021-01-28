package com.component.http;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

/**
 * @author jason
 * @date create on 2021/1/19.
 * @describe
 */
public abstract class GenericsCallback<T> extends Callback<T> {
    IGenericsParser mGenericsParser;

    public GenericsCallback(IGenericsParser parser) {
        mGenericsParser = parser;
    }

    public GenericsCallback(){
        mGenericsParser = new JsonGenericsParser();
    }


    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string();
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T) string;
        }
        T bean = mGenericsParser.transform(string, entityClass);
        return bean;
    }

}
