package com.component.http;

/**
 * @author jason
 * @date create on 2021/1/19.
 * @describe
 */
public interface IGenericsParser {
    <T> T transform(String response, Class<T> classOfT);
}
