package com.bber.company.android.util.country;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import static android.content.ContentValues.TAG;

/**
 * Created by carlo.c on 2018/3/31.
 * px dp 转换工具栏
 */

public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    //获得手机的宽度和高度像素单位为px
    // 通过WindowManager获取
    public void getScreenDensity_ByWindowManager(Activity context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();//屏幕分辨率容器
        context.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;
        float density = mDisplayMetrics.density;
        int densityDpi = mDisplayMetrics.densityDpi;
        Log.d(TAG, "Screen Ratio: [" + width + "x" + height + "],density=" + density + ",densityDpi=" + densityDpi);
        Log.d(TAG, "Screen mDisplayMetrics: " + mDisplayMetrics);
    }

    // 通过Resources获取
    public void getScreenDensity_ByResources(Context context) {
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;
        float density = mDisplayMetrics.density;
        int densityDpi = mDisplayMetrics.densityDpi;
        Log.d(TAG, "Screen Ratio: [" + width + "x" + height + "],density=" + density + ",densityDpi=" + densityDpi);
        Log.d(TAG, "Screen mDisplayMetrics: " + mDisplayMetrics);

    }

    // 获取屏幕的默认分辨率
    public void getDefaultScreenDensity(Activity context) {
        Display mDisplay = context.getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();
        int height = mDisplay.getHeight();
        Log.d(TAG, "Screen Default Ratio: [" + width + "x" + height + "]");
        Log.d(TAG, "Screen mDisplay: " + mDisplay);
    }
}
