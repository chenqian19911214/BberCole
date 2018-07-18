package com.bber.company.android.bean;

import android.content.Context;

import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;

/**
 * Created by Administrator on 2015/11/3 0003.
 */
public class HttpHead {
    private String userId;
    private String session;

    public HttpHead(Context context) {
        userId = SharedPreferencesUtils.get(context, Constants.USERID, "") + "";
        session = SharedPreferencesUtils.get(context, Constants.SESSION, "") + "";
    }

    public HttpHead() {
    }

}
