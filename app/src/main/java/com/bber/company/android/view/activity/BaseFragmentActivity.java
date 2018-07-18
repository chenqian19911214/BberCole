package com.bber.company.android.view.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.FlurryTypes;
import com.bber.company.android.util.WightHeighUtil;
import com.bber.customview.utils.LogUtils;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Date;
import java.util.List;


/**
 * Created by Aspsine on 2015/9/1.
 */
public abstract class BaseFragmentActivity extends SlidingFragmentActivity {

    public static BaseFragmentActivity instance;
    public boolean isForeground = false;
    public long time;
    protected NetWork netWork;
    protected MyApplication app;
    protected View top_statusbar;//状态栏View

    protected abstract int getContentViewLayoutId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        initWindow();
        HomeWatcher.mCamHomeKeyDown = true;
        app.mHomeWatcher.startWatch();
        Navigationheight();
    }

    //判断底部是否有虚拟按键，然后计算出高度。隔离开
    private void Navigationheight() {
        WightHeighUtil util = new WightHeighUtil();
        top_statusbar = findViewById(R.id.top_statusbar);
        if (WightHeighUtil.isNavigationBarShow(this)) {
            if (top_statusbar != null) {
                ViewGroup.LayoutParams param = top_statusbar.getLayoutParams();
                param.height = util.getNavigationBarHeight(this);
                LogUtils.e("底部状态栏的高度" + param.height);
                top_statusbar.setLayoutParams(param);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
        if (isForeground == true) {
            isForeground = false;
            Date now = new Date();
            long nowtime = now.getTime();
            if (nowtime > time + 5 * 60 * 1000) {
                HomeWatcher.toAds = true;
            }
        }
        if (HomeWatcher.mIsHomeDown) {
            Intent intent = new Intent(this, GestureVerifyActivity.class);
            intent.putExtra(PointState.GESTURETYPE, PointState.GESTURE_TYPE_VERIFY_OLD);
            startActivity(intent);
            HomeWatcher.mIsHomeDown = false;
        } else if (HomeWatcher.toAds == true) {
            Intent intent = new Intent(this, ADActivity.class);
            intent.putExtra("isLogin", true);
            intent.putExtra("chen", "6");

            //startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryTypes.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryTypes.onEndSession(this);
        if (!isAppOnForeground()) {
            isForeground = true;
            Date now = new Date();
            time = now.getTime();
        }
    }

    private void setTheme() {
        if (getLocalClassName().contains("Reg")
                || getLocalClassName().contains("Login")
                || getLocalClassName().contains("Enter")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme_Main);
        }

    }

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getStatusBarColor());
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    public int getStatusBarColor() {
        return getColorPrimary();
    }

    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
