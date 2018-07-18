package com.bber.company.android.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.tools.FlurryTypes;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.widget.MyToast;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.jivesoftware.smack.AbstractXMPPConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.agora.AgoraAPIOnlySignal;

/**
 * Created by Administrator on 2015-09-g_1.
 */
public class MyApplication extends MultiDexApplication {

    public static int screenWidth, screenHeigth;
    public static AbstractXMPPConnection connection;
    public static boolean isRestartInitData = false;
    private static MyApplication instance;
    public HomeWatcher mHomeWatcher;
    /* 启动程序的Activity集合 */
    public List<Activity> allActivity;
    /* 启动程序的Activity集合 */
    public List<Activity> newActivity;
    private double balance;

    /**信令 */
    private AgoraAPIOnlySignal m_agoraAPI;

    public static MyApplication getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setDeviceScreen();
        instance = this;
        FlurryTypes.FlurryTypesInit(this, (String) SharedPreferencesUtils.get(this, Constants.USERNAME, ""));
        allActivity = new ArrayList<Activity>();
        newActivity = new ArrayList<>();
        initFresco();
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.startWatch();
        setupAgoraEngine();
    }

    /**获取信令实体*/
    public AgoraAPIOnlySignal getmAgoraAPI() {
        return m_agoraAPI;
    }


    private void setupAgoraEngine() {
        String appID = getString(R.string.agora_app_id);

        try {
            m_agoraAPI = AgoraAPIOnlySignal.getInstance(this, appID);


        } catch (Exception e) {
            Log.e("MyApplication", Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void initFresco() {
        ImagePipelineConfig configureCaches = configureCaches();
        Fresco.initialize(this, configureCaches);
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }
    public void cleanFresco() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * 初始化配置
     */
    private ImagePipelineConfig configureCaches() {
        //内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                Constants.MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中图片的最大数量。
                Constants.MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);                    // 内存缓存中单个图片的最大大小。

        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };


        //默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())//缓存图片基路径
                .setBaseDirectoryName(Constants.IMAGE_PIPELINE_CACHE_DIR)//文件夹名
                .setMaxCacheSize(Constants.MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(Constants.MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(Constants.MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .build();

        //缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(this)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)//内存缓存配置（一级缓存，已解码的图片）
                .setMainDiskCacheConfig(diskCacheConfig)//磁盘缓存配置（总，三级缓存）
                ;
        return configBuilder.build();
    }

    public void exitApp() {
        for (int i = 0; i < allActivity.size(); i++) {
            this.allActivity.get(i).finish();
        }
        mHomeWatcher.stopWatch();
        System.exit(0);
    }

    public void closeListActivity() {
        for (int i = 0; i < newActivity.size(); i++) {
            this.newActivity.get(i).finish();
        }
    }

    public void finishAllactiity() {
        for (int i = 0; i < allActivity.size(); i++) {
            this.allActivity.get(i).finish();
        }
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取屏幕分辨率
     * *
     */
    public void setDeviceScreen() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        displaymetrics = getResources().getDisplayMetrics();
//        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
        screenHeigth = displaymetrics.heightPixels;
    }

    public boolean isAppRunning() {
        //判断应用是否在运行
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        String MY_PKG_NAME = "com.bber.company.android";
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 显示未联网信息
     */
    public void displayNotConnectedNetwork() {
        MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
    }

    /**
     * 无限制判断强制跳转
     *
     * @param context
     * @param str
     * @param bundle
     */
    public void startActivityForRoot(Context context, String str, Bundle bundle) {
        Intent intent = new Intent();
        if (str.contains("http://")) {
            if (!isNetworkConnected()) {
                this.displayNotConnectedNetwork();
                return;
            }
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString("url", str);
            intent.putExtras(bundle);
//            intent.setClass(context, WebViewActivity.class);
        } else {
            try {
                intent.setClass(context, Class.forName(str));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                MyToast.makeTextAnim(this, "找不到对应的界面", 0, R.style.PopToast).show();
                return;
            }
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
//        ActivityEffectUtils.setActivityEffect(context, ActivityEffectUtils.ENTERRIGHTTOLEFT);
    }


}
