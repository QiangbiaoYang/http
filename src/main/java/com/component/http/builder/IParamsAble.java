package com.component.http.builder;

import java.util.Map;

/**
 * Created by zhy on 16/3/1.
 */
public interface IParamsAble
{
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParams(String key, String val);
}
