package com.component.http;

import com.google.gson.Gson;

/**
 * @author jason
 * @date create on 2021/1/19.
 * @describe
 */
public class JsonGenericsParser implements IGenericsParser{
    Gson mGson = new Gson();
    @Override
    public <T> T transform(String response, Class<T> classOfT) {
        return mGson.fromJson(response, classOfT);
    }
}
