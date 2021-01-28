package com.component.http;



import com.component.http.builder.GetBuilder;
import com.component.http.builder.PostFormBuilder;
import com.component.http.request.OkHttpRequest;
import com.component.http.request.RequestCall;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author jason
 * @date create on 2021/1/3.
 * @describe
 */
public class HttpHelper {

    public static final int DEFAULT_TOME_OUT = 10; //超时时间
    private volatile static HttpHelper mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;
    private OkRequestWrapper okRequestWrapper;


    private HttpHelper(OkHttpClient client) {

        if (client == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = client;
        }
        mPlatform = Platform.get();
    }

    public static HttpHelper initClient(OkHttpClient okHttpClient)
    {
        if (mInstance == null)
        {
            synchronized (HttpHelper.class)
            {
                if (mInstance == null)
                {
                    mInstance = new HttpHelper(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static HttpHelper getInstance() {
        return initClient(null);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public OkHttpRequest wrap(OkHttpRequest request){
        if(getInstance().okRequestWrapper == null){
            return request;
        }else{
            return getInstance().okRequestWrapper.wrap(request);
        }

    }

    /**
     * 设置request请求的包装类
     * */
    public void setOkRequestWrapper(OkRequestWrapper okRequestWrapper) {
        this.okRequestWrapper = okRequestWrapper;
    }

    public void execute(RequestCall requestCall, Callback callback) {

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailResultCallback(call, e, finalCallback, id);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                try {
                    if (call.isCanceled()) {
                        sendFailResultCallback(call, new IOException("Canceled!"), finalCallback, id);
                        return;
                    }

                    if (!finalCallback.validateResponse(response, id)) {
                        sendFailResultCallback(call, new IOException("request failed , reponse's code is : " + response.code()), finalCallback, id);
                        return;
                    }

                    Object o = finalCallback.parseNetworkResponse(response, id);
                    sendSuccessResultCallback(o, finalCallback, id);
                } catch (Exception e) {
                    sendFailResultCallback(call, e, finalCallback, id);
                } finally {
                    if (response.body() != null)
                        response.body().close();
                }

            }
        });
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {
        if (callback == null) return;

        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e, id);
                callback.onAfter(id);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback == null) return;
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object, id);
                callback.onAfter(id);
            }
        });
    }

    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

}
