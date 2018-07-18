package com.bber.company.android.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bber.company.android.bean.adsBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;


/**
 * Author: Bruce
 * Date: 2017/1/13
 * Version:
 * Describe:
 */

public class commonActivity {

    public static void startBrowebuyURL(Context context, adsBean ads) {
        if (!Tools.isEmpty(ads.getAdTarget())) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            if (!ads.getAdTarget().trim().contains("http")) {
                ads.setAdTarget("http://" + ads.getAdTarget());
            }
            Uri content_url = Uri.parse(ads.getAdTarget().trim());
            intent.setData(content_url);
            context.startActivity(intent);
            adsAddTrace(context, ads.getId());
        }
    }

    /**
     * 获取广告的状态
     */
    private static void adsAddTrace(Context context, int adId) {
        RequestParams params = new RequestParams();
        String head = new JsonUtil(context).httpHeadToJson(context);
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(context, Constants.USERID, "-1") + "");
        params.put("head", head);
        params.put("userType", "BUYER");
        params.put("userId", buyerId);
        params.put("adId", adId);
        params.put("userSex", 1);
        HttpUtil.post(Constants.getInstance().adsAddTrace, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }
}
