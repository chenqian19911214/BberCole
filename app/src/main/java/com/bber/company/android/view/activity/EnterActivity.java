package com.bber.company.android.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.FlurryTypes;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登陆注册
 */
public class EnterActivity extends Activity implements View.OnClickListener, AMapLocationListener {


    private TextView btn_enter;
    private Button btn_login;
    private MyApplication app;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private int inviteCodeSwitch = 0;
    private int forciblyInput = 0;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏应用程序的标题栏，即当前activity的标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_enter);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        AppManager.getAppManager().addActivity(this);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        btn_enter = findViewById(R.id.btn_enter);
        btn_login = findViewById(R.id.btn_login);
    }


    private void setListeners() {
        btn_enter.setOnClickListener(this);
        btn_login.setOnClickListener(this);
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

                initLocation();
            }
        } else {
            initLocation();

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initLocation();
            } else {

                MyToast.makeTextAnim(getApplicationContext(), R.string.permission_on_location, 0, R.style.PopToast).show();

            }
        }

    }

    private void initData() {

        applicationPermissions();
        getInviteCodeSwitch();
        HomeWatcher.mCamHomeKeyDown = false;
    }

    /**
     * 初始化 定位
     */
    private void initLocation() {
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

        startLocation();
    }


    private void startLocation() {
        if (!new NetWork(this).isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();

    }

    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (null != amapLocation) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                //获取位置信息

                city = amapLocation.getCity();
                if (city != null) {
                    SharedPreferencesUtils.put(EnterActivity.this, "currentCity", city);
                } else {
                    city = "null";
                }
                Log.e("eeeeeeeeeeeeeeee", "city:" + amapLocation.getCity());
                // 停止定位
                locationClient.stopLocation();
            } else {
                MyToast.makeTextAnim(this, R.string.lbs_fail, 0, R.style.PopToast).show();
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_enter:
                if (inviteCodeSwitch == 1) {
                    intent = new Intent(EnterActivity.this, RegFirActivity.class);
                    intent.putExtra("city", city);
                    intent.putExtra("forciblyInput", forciblyInput);
                    startActivity(intent);
                } else {
                    intent = new Intent(EnterActivity.this, RegistereActivity.class);
                    intent.putExtra("city", city);
                    startActivity(intent);
                }
                break;
            case R.id.btn_login:
                intent = new Intent(EnterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void getInviteCodeSwitch() {
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("type", 1);
        HttpUtil.post(Constants.getInstance().getInviteCodeSwitch, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(EnterActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    inviteCodeSwitch = dataCollection.getInt("inviteCodeSwitch");
                    forciblyInput = dataCollection.getInt("forciblyInput");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(EnterActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            app.exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
