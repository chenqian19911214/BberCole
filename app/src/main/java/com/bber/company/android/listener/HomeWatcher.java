package com.bber.company.android.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.bber.company.android.tools.SharedPreferencesUtils;

/**
 * Home键监听封装
 *
 * @author way
 */
public class HomeWatcher {

    public static boolean mIsHomeDown = false;
    public static boolean toAds = false;
    public static boolean mCamHomeKeyDown = true;
    private Context mContext;
    private IntentFilter mFilter;
    private IntentFilter mPowerOffFilter;
    private InnerRecevier mRecevier;
    private InnerPowerOffRecevier mPowerOffRecevier;


    public HomeWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mPowerOffFilter = new IntentFilter();
        mPowerOffFilter.addAction(Intent.ACTION_SCREEN_ON);
        mPowerOffFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mRecevier = new InnerRecevier();
        mPowerOffRecevier = new InnerPowerOffRecevier();

    }


    /**
     * 开始监听，注册广播
     */
    public void startWatch() {
        if (mRecevier != null) {
            mContext.registerReceiver(mRecevier, mFilter);
        }
        if (mPowerOffRecevier != null) {
            mContext.registerReceiver(mPowerOffRecevier, mPowerOffFilter);
        }
    }

    /**
     * 开始监听，注册广播
     */
    public void stopWatch() {
        mContext.unregisterReceiver(mRecevier);
        mContext.unregisterReceiver(mPowerOffRecevier);
    }


    /**
     * 广播接收者
     */
    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                String gesturePsw = (String) SharedPreferencesUtils.get(mContext, "gesturePsw", "");
                boolean gesture_push = (boolean) SharedPreferencesUtils.get(mContext, "gesture_push", false);

                if (reason != null && mCamHomeKeyDown == true && gesturePsw != null && gesture_push == true) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        mIsHomeDown = true;
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        mIsHomeDown = true;
                    }
                }
            }
        }
    }

    /**
     * 广播接收者
     */
    class InnerPowerOffRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String gesturePsw = (String) SharedPreferencesUtils.get(mContext, "gesturePsw", "");
            boolean gesture_push = (boolean) SharedPreferencesUtils.get(mContext, "gesture_push", false);

            if (Intent.ACTION_SCREEN_OFF.equals(action) && mCamHomeKeyDown == true
                    && gesturePsw != null && gesture_push == true) {
                mIsHomeDown = true;
            }
        }
    }

}