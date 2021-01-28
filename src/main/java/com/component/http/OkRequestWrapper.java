package com.component.http;

import com.component.http.request.OkHttpRequest;

/**
 * @author jason
 * @date create on 2021/1/21.
 * @describe 请求装饰类（可以用于给所有请求装饰url、公共请求参数等）
 */
public interface OkRequestWrapper {
    OkHttpRequest wrap(OkHttpRequest request);
}
