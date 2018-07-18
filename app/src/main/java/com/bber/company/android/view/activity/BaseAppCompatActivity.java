package com.bber.company.android.view.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.service.MsfService;
import com.bber.company.android.tools.FlurryTypes;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Date;
import java.util.List;


/**
 * Created by Aspsine on 2015/9/1.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    public static BaseAppCompatActivity instance;
    public boolean isForeground = false;
    public long time;
    protected Toolbar toolbar;
    protected TextView title, status_point;
    protected NetWork netWork;
    protected MyApplication app;

    protected abstract int getContentViewLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        AppManager.getAppManager().addActivity(this);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        initWindow();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");//去掉默认居左的title
        title = toolbar.findViewById(R.id.title);
        status_point = toolbar.findViewById(R.id.status_point);
        setupActionBar();
        HomeWatcher.mCamHomeKeyDown = true;
        app.mHomeWatcher.startWatch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
        Log.e("eeeeeeeeeeeeeeeee", "onResume activity:" + this.getLocalClassName());
        HomeWatcher.toAds = false;
        if (isForeground == true) {
            isForeground = false;
            Date now = new Date();
            long nowtime = now.getTime();
            if (nowtime > time + 5 * 60 * 1000) {
                HomeWatcher.toAds = true;
            }
        }
        if (HomeWatcher.mIsHomeDown) {
            Intent intent = new Intent(BaseAppCompatActivity.this, GestureVerifyActivity.class);
            intent.putExtra(PointState.GESTURETYPE, PointState.GESTURE_TYPE_VERIFY_OLD);
            startActivity(intent);
            HomeWatcher.mIsHomeDown = false;
        } else if (HomeWatcher.toAds == true) {
            Intent intent = new Intent(this, ADActivity.class);
            intent.putExtra("isLogin", true);
            intent.putExtra("chen", "8");

            startActivity(intent);
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

    /***
     * 断开重新连接服务
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (isAppOnForeground()) {
            boolean isLogin = getIntent().getBooleanExtra("isLogin", true);
            //启动Service
            Intent s = new Intent(this, MsfService.class);
            s.putExtra("isLogin", isLogin);
            stopService(s);
            startService(s);
        }

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
        if (getLocalClassName().contains("Reg") || getLocalClassName().contains("Login") || getLocalClassName().contains("Enter")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme_Main);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getLocalClassName().contains("MainActivity")) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black30));
        }
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
//            actionBar.setDisplayHomeAsUpEnabled(false);
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


    //下面的方法作用：只要点击的区域不是EditText已经弹出的软键盘就会消失

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
