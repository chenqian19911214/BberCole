package com.bber.company.android.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.adsBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.FlurryTypes;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.GetIpFromWeb;
import com.bber.company.android.util.InterfaceSetIpList;
import com.bber.company.android.widget.AnimatorPath;
import com.bber.company.android.widget.AutoZoomInImageView;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class WelcomeActivity extends Activity implements AMapLocationListener, InterfaceSetIpList {

    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/download/";
    private static final String saveFileName = savePath + "papa.apk";
    /* 进度条与通知ui刷新的handler和msg常量 */
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int MY_PERMISSIONS_WRITE = 2;
    private final int gotoSeccion = 0;
    private final int gotoAdStatus = 1;
    private NetWork netWork;
    private MyApplication app;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private String city = null;
    //    private static boolean isGotosession = false;
    private boolean getIpFlag = false;
    private double Longitude = 0.0;
    private double Latitude = 0.0;
    private GetIpFromWeb mGetIpFromWeb;
    private LinearLayout down_load;
    private Dialog downloadDialog;
    private ImageView fab;
    private AutoZoomInImageView autoZoomInImageView;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case gotoSeccion:
//                    if (isGotosession == false) {
                    // checkSession();
//                        isGotosession = true;
//                    }
                    break;
                case gotoAdStatus:
                    getAdStatus();
                    checkSession();
                    break;
                case 2:

                    break;
                default:
                    break;
            }
        }
    };
    private TextView tv_text;
    private AnimatorPath path;//声明动画集合
    /**
     * 作者：B
     * 检查版本是否更新
     * 版本号：v.1
     */
    private String apkUrl;
    private Thread downLoadThread;
    private int progress = 0;
    private boolean interceptFlag = false;
    private ProgressBar progressBar;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    tv_text.setText(progress + "%");
                    progressBar.setProgress(progress);
                    //   popup_tv.setText(progress + "%");
                    //     popup_progress.setProgress(progress);
                    if (progress == 99) {
                        downloadDialog.dismiss();
//                    popWindow.dismiss();
                    }
                    break;
                case DOWN_OVER:
                    down_load.setVisibility(View.GONE);
                    installApk();
                    break;
                default:
                    break;
            }
        }

    };
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                }
                while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        AppManager.getAppManager().addActivity(this);
        app = MyApplication.getContext();
        netWork = new NetWork(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//API19(android4.4)以上才有沉浸式
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initViews();
        setListeners();
        initData();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initeLocation();
            } else {

                MyToast.makeTextAnim(getApplicationContext(), R.string.permission_on_location, 0, R.style.PopToast).show();

            }
        } else if (requestCode == MY_PERMISSIONS_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                downloadApk();
            } else {

                MyToast.makeTextAnim(getApplicationContext(), "下载更新失败，请打开存储权限", 1, R.style.PopToast).show();

            }
        }
    }

    /**
     * 检查定位权限
     */

    private void applicationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                initeLocation();
            }
        } else {
            initeLocation();

        }

    }


    private void applicationPermissionsxie() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE);
            } else {
                //下载apk
                downloadApk();
            }
        }

    }

    private void initViews() {
        // 下载器
        down_load = findViewById(R.id.lin_download);
        tv_text = findViewById(R.id.tv_text);
        progressBar = findViewById(R.id.progress);
        autoZoomInImageView = findViewById(R.id.antoZoomImageView);
        Display mDisplay = getWindowManager().getDefaultDisplay();
        final int height = mDisplay.getHeight();

        ChenQianLog.i("height :" + height);
        autoZoomInImageView.post(new Runnable() {

            @Override
            public void run() {
                //使用较为具体的方式启动放大动画
                autoZoomInImageView.init()
                        .setScaleDelta(0.3f)//放大的系数是原来的（1 + 0.3）倍
                        .setDurationMillis(3000)//动画的执行时间为3000毫秒
                        .setOnZoomListener(new AutoZoomInImageView.OnZoomListener() {
                            @Override
                            public void onStart(View view) {
                                //放大动画开始时的回调
                                fab = findViewById(R.id.image_logo);
                                /** 设置位移动画 向上位移150 */
                                int[] location = new int[2];
                                fab.getLocationInWindow(location);//获取Imageview在屏幕中的位置
                                Animation heroTranslate = new TranslateAnimation(0f, 0f, 0f, 0f - (height / 2 - 150));//移动动画
                                fab.setAnimation(heroTranslate);
                                heroTranslate.setDuration(3000);
                                heroTranslate.setFillAfter(true);
                                heroTranslate.start();

                                applicationPermissions();
                            }

                            @Override
                            public void onUpdate(View view, float progress) {
                                //放大动画进行过程中的回调 progress取值范围是[0,1]
                            }

                            @Override
                            public void onEnd(View view) {
                                //放大动画结束时的回调
                                //  vanishView(autoZoomInImageView);
                                getIp();
                                //
                            }
                        })
                        .start(0);//延迟1000毫秒启动
            }
        });
    }

    private void vanishView(final View view) {
        if (view == null) return;
        ValueAnimator va = ValueAnimator.ofFloat(1f, 0f);
        va.setDuration(500);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float alpha = (Float) animation.getAnimatedValue();
                if (alpha != null) {
                    view.setAlpha(alpha);
                }
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
    }

    private void setListeners() {
    }

    private void initData() {
        //  getIp();
        SharedPreferencesUtils.put(WelcomeActivity.this, "adPicture", "");
        SharedPreferencesUtils.put(WelcomeActivity.this, "adTarget", "");
    }

    /**
     * 验证登录状态
     */
    private void checkSession() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        String session = SharedPreferencesUtils.get(this, Constants.SESSION, "") + "";
        params.put("userId", userId);
        params.put("session", session);
        params.put("type", "1");
        Log.e("eeeeeeeeeeee", "sessionIsInvalid:" + Constants.getInstance().sessionIsInvalid);
        HttpUtil.post(Constants.getInstance().sessionIsInvalid, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "sessionIsInvalid onSuccess--jsonObject:" + jsonObject);
                if (jsonObject == null) {
                    MyToast.makeTextAnim(WelcomeActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 0) {
                        MyToast.makeTextAnim(WelcomeActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                    } else if (resultCode == 2) {
                        Intent intent;
                        if (Tools.isEmpty(userId)) {
                            boolean isFirstOpen = (boolean) SharedPreferencesUtils.get(WelcomeActivity.this, Constants.FIRST_OPEN, true);
                            // 如果是第一次启动，则先进入功能引导页
                            if (isFirstOpen) {
                                //第一次进入的引导页
                                intent = new Intent(WelcomeActivity.this, WelcomeGuideActivity.class);
                                intent.putExtra("isLogin", true);
                                startActivity(intent);
                            } else {
                                //陆注册
                                intent = new Intent(WelcomeActivity.this, EnterActivity.class);
                            }
                        } else {
                            intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                String gesturePsw = (String) SharedPreferencesUtils.get(WelcomeActivity.this, "gesturePsw", "");
                                boolean gesture_push = (boolean) SharedPreferencesUtils.get(WelcomeActivity.this, "gesture_push", false);
                                String adPicture = (String) SharedPreferencesUtils.get(WelcomeActivity.this, "adPicture", "");
                                if (Tools.isEmpty(gesturePsw) && gesture_push == true) {
                                    Intent intent = new Intent(WelcomeActivity.this, GestureLockActivity.class);
                                    intent.putExtra(PointState.GESTURETYPE, PointState.GESTURE_TYPE_VERIFY_MAIN);
                                    startActivity(intent);
                                } else if (gesture_push == false) {

                                    if (!Tools.isEmpty(adPicture)) {
                                        Intent intent = new Intent(WelcomeActivity.this, ADActivity.class);
                                        intent.putExtra("isLogin", false);
                                        intent.putExtra("chen", "2");

                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                        intent.putExtra("isLogin", false);
                                        startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(WelcomeActivity.this, GestureVerifyActivity.class);
                                    intent.putExtra(PointState.GESTURETYPE, PointState.GESTURE_TYPE_VERIFY_MAIN);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        }, 100);
                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "sessionIsInvalid onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(WelcomeActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                MyToast.makeTextAnim(WelcomeActivity.this, R.string.getData_out_time, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("latestVersion", Tools.getVersion(this));
        params.put("os", "android");
        params.put("cilentType", 1);
        HttpUtil.get(Constants.getInstance().updateVersion, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "updateVersion onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(WelcomeActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }

                    String status = dataCollection.getString("status");
                    if (status.equals("3")) {
                        handler.sendEmptyMessage(gotoAdStatus);
//                        handler.sendEmptyMessageDelayed(gotoSeccion, 4000);
                    } else {
                        String content = dataCollection.getString("content");
                        final String URL = dataCollection.getString("updateUrl");
                        apkUrl = URL;
                        LogUtils.e("apkUrl :" + apkUrl);
                        LayoutInflater inflater = LayoutInflater.from(WelcomeActivity.this);
                        View layout = inflater.inflate(R.layout.custom_alertdialog_tip, null);
                        final AlertDialog dialog = DialogTool.createDialogTip(WelcomeActivity.this, layout, content, R.string.next_time, R.string.update);
                        if (status.equals("1")) {//选择更新
                            dialog.setCancelable(true);
                            layout.findViewWithTag(0).setVisibility(View.VISIBLE);
                        } else if (status.equals("2")) {//强制更新
                            dialog.setCancelable(false);
                            layout.findViewWithTag(0).setVisibility(View.GONE);
                        }//Manifest.permission.WRITE_EXTERNAL_STORAGE

                        //  MyToast.makeTextAnim(getApplicationContext(),"下载更新失败，请打开存储权限",1,R.style.PopToast).show();

                        //downloadApk();

                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                applicationPermissionsxie();
                            }
                        });
                        //设置取消按钮监听
                        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                handler.sendEmptyMessage(gotoAdStatus);

                            }
                        });
                    }


                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "updateVersion onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(WelcomeActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        downloadDialog = builder.create();
        downloadDialog.show();

        down_load.setVisibility(View.VISIBLE);
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);// 显示用户数据
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        this.startActivity(i);
    }


    private void getIp() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
//        params.put("head", head);
        params.put("username", "admin");
        params.put("password", "bber!@#");
        String url = Constants.getIP;

        ChenQianLog.i("MainURl:" + url);
        HttpUtil.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "getIP onSuccess--jsonObject:" + jsonObject);
                if (jsonObject == null) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("data");
                    String responsecode = jsonObject.getString("responsecode");
                    if (responsecode == null || !responsecode.equals("SUCCESS")) {
                        return;
                    }
                    String addr = dataCollection.getString("addr");
                    if (addr == null || addr.length() == 0) {
                        return;
                    }

                    JSONObject jsonObject1 = new JSONObject(addr);

                    String im = jsonObject1.getString("im-server");
                    String im_file = jsonObject1.getString("im-file-server");
                    String bberUser = jsonObject1.getString("bberUser");
                    String order = jsonObject1.getString("order");
                    String push_server = jsonObject1.getString("push-server");
                    String h5_server = jsonObject1.getString("h5-server");
                    String file_image_path = jsonObject1.getString("file-visit-path");
                    String bbergame = jsonObject1.getString("bbergame");
                    SharedPreferencesUtils.remove(getApplicationContext(), "URL");
                    setgetIpList(im, im_file, bberUser, order, push_server, h5_server, file_image_path, bbergame);
                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getIP JSONException:" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (Constants.FLURRY_ENABLE) {
                    mGetIpFromWeb = new GetIpFromWeb();
                    mGetIpFromWeb.getWebFromUrl(WelcomeActivity.this);
                    String throwabledata = throwable.getMessage();
                } else {
                    MyToast.makeTextAnim(WelcomeActivity.this, R.string.getData_fail, 1, R.style.PopToast).show();
                }
            }


            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 设置IP地址
     *
     * @param im
     * @param im_file
     * @param bberUser
     * @param order
     * @param push_server
     */
    public void setgetIpList(String im, String im_file, String bberUser, String order, String push_server, String h5_server, String file_image_path, String bbergame) {

        SharedPreferencesUtils.remove(this, "URL");

        ChenQianLog.i("bberUser+" + bberUser);
        SharedPreferencesUtils.put(WelcomeActivity.this, "XMPP_HOST", im);
        SharedPreferencesUtils.put(WelcomeActivity.this, "IM_FILE_SERVER", "http://" + im_file);
        SharedPreferencesUtils.put(WelcomeActivity.this, "URL", "http://" + bberUser + "/bber/bberUser/");
        SharedPreferencesUtils.put(WelcomeActivity.this, "randomURL", "http://" + bberUser);
        SharedPreferencesUtils.put(WelcomeActivity.this, "ORDER_URL", "http://" + order + "/");
        SharedPreferencesUtils.put(WelcomeActivity.this, "PUSH_SERVER", "http://" + push_server);
        SharedPreferencesUtils.put(WelcomeActivity.this, "H5_SERVER", "http://" + h5_server);
        SharedPreferencesUtils.put(WelcomeActivity.this, "IMAGE_FILE", "http://" + file_image_path);
        SharedPreferencesUtils.put(WelcomeActivity.this, "bbergame", bbergame);

        getIpFlag = true;


        //检查更新
        checkVersion();

    }

    @Override
    public void setIpList(String im, String im_file, String bberUser, String order, String push_server, String h5_server, String file_image_path, String bbergame) {
        setgetIpList(im, im_file, bberUser, order, push_server, h5_server, file_image_path, bbergame);
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            app.exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            //获取位置信息
            city = aMapLocation.getCity();
            Latitude = aMapLocation.getLatitude();
            Longitude = aMapLocation.getLongitude();

            ChenQianLog.i("Latitude:" + Latitude + "  Longitude:" + Longitude);
            // 停止定位
            locationClient.stopLocation();

        }
    }

    /**
     * * 定位
     */
    public void initeLocation() {
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
//        locationOption.setGpsFirst(false);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);

        locationClient.startLocation();
    }

    /**
     * 获取广告的状态
     */
    private void getAdStatus() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("cityLongitude", Longitude);
        params.put("cityLatitude", Latitude);
        params.put("adPlace", "INDEX");
        HttpUtil.get(Constants.getInstance().adsList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode != 0) {
                        String dataCollection = jsonObject.getString("dataCollection");
                        if (dataCollection != null) {
                            List<adsBean> adsBeenList = jsonUtil.jsonToadsBean(dataCollection);
                            if (adsBeenList.size() > 0) {
                                HomeWatcher.toAds = true;
                                SharedPreferencesUtils.put(WelcomeActivity.this, "adPicture", adsBeenList.get(0).getAdPicture());
                                SharedPreferencesUtils.put(WelcomeActivity.this, "adTarget", adsBeenList.get(0).getAdTarget());
                                SharedPreferencesUtils.put(WelcomeActivity.this, "adid", adsBeenList.get(0).getId());
                            }
                        }
                    }
                    handler.sendEmptyMessage(gotoSeccion);
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
//                handler.sendEmptyMessage(gotoSeccion);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

}
