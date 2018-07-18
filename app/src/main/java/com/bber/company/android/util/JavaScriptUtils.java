package com.bber.company.android.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.webViewListener;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.view.activity.Buy_vipActivity;
import com.bber.company.android.view.activity.MobileVerifyActivity;
import com.bber.customview.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * author Vencent on 2017/3/10.
 */
public class JavaScriptUtils implements Serializable {

    protected IactionListener iactionListener;
    private webViewListener webViewListener;
    private Context context;

    public JavaScriptUtils(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showUserVipPage(Object object) {
        LogUtils.e(object);
        Constants.isStartActivity = false;
        Intent intent = new Intent(context, Buy_vipActivity.class);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void showVerifyPhonePage(Object object) {
        LogUtils.e(object);
        Constants.isStartActivity = false;
        Intent intent = new Intent(context, MobileVerifyActivity.class);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void getFreeVipPage(Object object) {
        LogUtils.e(object);
        Constants.isStartActivity = false;
        String code = (String) SharedPreferencesUtils.get(context, "code", "0");
        String url = (String) SharedPreferencesUtils.get(context, "url", "");
        if (code.equals("1")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            if (!url.trim().equals("")) {
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                context.startActivity(intent);
            }
        } else {
            Intent intent = new Intent(context, Buy_vipActivity.class);
            context.startActivity(intent);
        }
    }

    @JavascriptInterface
    public void openPlayer(String urlJson) {
        try {
            JSONObject jsonObject = new JSONObject(urlJson);
            String url = jsonObject.getString("url");
            this.webViewListener.openPlayer(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setActionListener(IactionListener _listener) {
        iactionListener = _listener;
    }

    @JavascriptInterface
    public void getValidation(Object object) {
        iactionListener.SuccessCallback("");
    }


    public void callOpenPlayerBack(webViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }

}
