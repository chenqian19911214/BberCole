package com.bber.company.android.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.Validator;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册第三步 设置密码
 * *
 */
public class RegThiActivity extends BaseAppCompatActivity implements View.OnClickListener, AMapLocationListener {


    private Button btn_next;
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() == 0) {
                btn_next.setEnabled(false);
            } else {
                btn_next.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private EditText edit_psd, edit_psd_agin, edit_nickname;
    private String userName, city, psd, psd_agin, key;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private int mark;
    private TextView agreement_text;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_reg_thi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        btn_next = findViewById(R.id.btn_next);
        edit_psd = findViewById(R.id.edit_psd);
        edit_psd_agin = findViewById(R.id.edit_psd_agin);
        edit_nickname = findViewById(R.id.edit_nickname);
        agreement_text = findViewById(R.id.agreement_text);
    }

    private void setListeners() {
        btn_next.setOnClickListener(this);
        edit_psd_agin.addTextChangedListener(watcher);
        agreement_text.setOnClickListener(this);
    }

    private void initData() {
        HomeWatcher.mCamHomeKeyDown = false;
        city = getIntent().getStringExtra("city");
        key = getIntent().getStringExtra("inviteCode");
        title.setText(R.string.reg);
        agreement_text.setText(Html.fromHtml("<font color=#999999>" + "注册代表你已阅读并同意" + "</font>" + "<br/>" +
                "<font color=#ff0066>" + "<u>《啪啪免责声明》</u>" + "</font>"));
    }

    private void startLocation() {
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
                Log.e("eeeeeeeeeeeeeeee", "city:" + amapLocation.getCountry());
                // 停止定位
                locationClient.stopLocation();
                if (!Tools.isEmpty(city)) {
                    register();
                } else {
                    MyToast.makeTextAnim(this, R.string.lbs_fail, 0, R.style.PopToast).show();
                    DialogTool.dismiss(this);

                }
            } else {
                MyToast.makeTextAnim(this, R.string.lbs_fail, 0, R.style.PopToast).show();
                DialogTool.dismiss(this);

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    private void register() {

        DialogTool.createProgressDialog(this, true);
        //  }
        if (Tools.isEmpty(city)) {
            startLocation();
        } else {
            RequestParams params = new RequestParams();
            params.put("userName", userName);
            params.put("password", psd);
            params.put("city", city);
            params.put("inviteCode", key);
            params.put("apkSource", Constants.APK_CHANNEL_SOURCE_ID);
            HttpUtil.get(Constants.getInstance().registerBuyerByName, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                    Log.e("eeeeeeeeeeee", "registerBuyerByName onSuccess--jsonObject:" + jsonObject);
                    if (Tools.jsonResult(RegThiActivity.this, jsonObject, null)) {
                        return;
                    }
                    login(null);
                }

                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    Log.e("eeeeeeeeeeee", "registerBuyerByName onFailure--throwable:" + throwable);
                    MyToast.makeTextAnim(RegThiActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                }

                @Override
                public void onFinish() { // 完成后调用，失败，成功，都要调用
                    DialogTool.dismiss(RegThiActivity.this);
                }
            });
        }
    }


    private void login(final ProgressDialog progressDialog) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("userName", userName);
        params.put("password", psd);
        HttpUtil.get(Constants.getInstance().buyerLoginByName, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "buyerLogin onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(RegThiActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");

                    String userId = dataCollection.getString(Constants.USERID);
                    String session = dataCollection.getString(Constants.SESSION);
                    String name = dataCollection.getString(Constants.USERNAME);
                    String url = dataCollection.getString(Constants.HEADURL);
                    SharedPreferencesUtils.put(RegThiActivity.this, Constants.USERNAME, name);
                    SharedPreferencesUtils.put(RegThiActivity.this, Constants.HEADURL, url);
                    SharedPreferencesUtils.put(RegThiActivity.this, Constants.USERID, userId);
                    SharedPreferencesUtils.put(RegThiActivity.this, Constants.SESSION, session);

                    Intent intent = new Intent(RegThiActivity.this, RegPreferenceActivity.class);
                    intent.putExtra("isregister", true);
                    startActivity(intent);
                    app.finishAllactiity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "buyerLogin onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(RegThiActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(RegThiActivity.this);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                psd = edit_psd.getText() + "";
                psd_agin = edit_psd_agin.getText() + "";
                userName = edit_nickname.getText() + "";
                if (psd.isEmpty() || psd.length() < 6) {
                    MyToast.makeTextAnim(this, R.string.set_psd_error, 0, R.style.PopToast).show();
                    return;
                }
                if (!psd_agin.equals(psd)) {
                    MyToast.makeTextAnim(this, R.string.repeat_psd_error, 0, R.style.PopToast).show();
                    return;
                }
                if (!netWork.isNetworkConnected()) {
                    MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
                    return;
                }
                if (Tools.isEmpty(userName)) {
                    MyToast.makeTextAnim(this, R.string.nickname_error, 0, R.style.PopToast).show();
                    return;
                }
                if (Validator.isDigit(userName)) {
                    MyToast.makeTextAnim(this, R.string.not_digit, 0, R.style.PopToast).show();
                    return;
                }
                mark = 0;
                register();
                break;
            case R.id.agreement_text:
                Intent intent = new Intent(this, AgreementActivity.class);
                startActivity(intent);
                break;
        }

    }

}
