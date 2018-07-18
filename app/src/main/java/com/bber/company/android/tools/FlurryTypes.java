package com.bber.company.android.tools;

import android.content.Context;
import android.os.SystemClock;

import com.bber.company.android.constants.Constants;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Bruce
 * Date: 2016/6/21
 * Version:
 * Describe:
 */
public class FlurryTypes {

    public static final void FlurryTypesInit(Context context, String userId) {
        if (Constants.FLURRY_ENABLE) {
            FlurryAgent.setUserId(Tools.md5(userId));
            // configure Flurry
            FlurryAgent.setLogEnabled(true);
            FlurryAgent.setReportLocation(true);
            // init Flurry
            FlurryAgent.init(context, Constants.API_KEY);
        }
    }

    public static final void onStartSession(Context context) {
        if (Constants.FLURRY_ENABLE) {
            try {
                FlurryAgent.onStartSession(context, Constants.API_KEY);
            } catch (Throwable t) {
            }
        }
    }

    public static final void onEndSession(Context context) {
        if (Constants.FLURRY_ENABLE) {
            try {
                FlurryAgent.onEndSession(context);
            } catch (Throwable t) {
            }
        }
    }

    public static final void onEvent(String eventId) {
        if (Constants.FLURRY_ENABLE) {
            long currentTime = SystemClock.elapsedRealtime();
            try {
                Map<String, String> params = new HashMap<String, String>();
                FlurryAgent.onEvent(eventId, params);
            } catch (Throwable t) {
            }
        }
    }

    public static final void onEventUseMap(String eventId, Map<String, String> map) {
        if (Constants.FLURRY_ENABLE) {
            try {
                FlurryAgent.onEvent(eventId, map);
            } catch (Throwable t) {
            }
        }
    }


    public static final void onEvent(String eventId, String paramValue) {
        if (Constants.FLURRY_ENABLE) {
            try {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(eventId, paramValue);
                FlurryAgent.onEvent(eventId, params);
            } catch (Throwable t) {
            }
        }
    }

    public static final void onEvent(String eventId, String paramKey, String paramValue) {
        if (Constants.FLURRY_ENABLE) {
            long currentTime = SystemClock.elapsedRealtime();
            try {
                Map<String, String> params = new HashMap<String, String>();
                params.put(paramKey, paramValue);
                FlurryAgent.onEvent(eventId, params);
            } catch (Throwable t) {
            }
        }
    }


}
