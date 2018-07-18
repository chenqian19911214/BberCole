package com.bber.company.android.http;


import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

/**
 * Created by Administrator on 2015-06-19.
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

    static {

        client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s
        client.setResponseTimeout(15000);// 设置服务器返回延迟
    }

    // 用一个完整url获取一个string对象
    public static void get(String urlString, AsyncHttpResponseHandler res) {

        client.get(urlString, res);
    }

    // url里面带参数
    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler res) {

        client.get(urlString, params, res);

    }

    // 不带参数，获取json对象或者数组
    public static void get(String urlString, JsonHttpResponseHandler res) {
        client.get(urlString, res);

    }

    // 带参数，获取json对象或者数组
    public static void getLogin(String urlString, RequestParams params,
                                JsonHttpResponseHandler res) {
//        client.setTimeout(8000);
        client.get(urlString, params, res);

    }

    // 带参数，获取json对象或者数组
    public static void get(String urlString, RequestParams params,
                           JsonHttpResponseHandler res) {
        client.get(urlString, params, res);

    }

    // 带参数，获取json对象或者数组
    public static RequestHandle getWithReturn(String urlString, RequestParams params,
                                              JsonHttpResponseHandler res) {
        RequestHandle rh = client.get(urlString, params, res);
        return rh;

    }

    // 下载数据使用，会返回byte数据
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {

        client.get(uString, bHandler);

    }

    //上传数据
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (MyApplication.getContext().isNetworkConnected()) {
            client.post(url, params, responseHandler);
        } else {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
        }
    }

    public static AsyncHttpClient getClient() {

        return client;
    }

}
