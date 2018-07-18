package com.bber.company.android.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.listener.NoDoubleClickListener;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.Validator;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.view.adapter.RecycleAdapter;
import com.bber.company.android.widget.CustomGridLayoutManager;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.PopupWindowHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Create by bruce
 * Time:2016.5.1
 * Version:v1.6.1
 * Describle:手机认证的页面
 */
public class MobileVerifyActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private static long LastTime;
    private final Long countTime = Long.valueOf(120000);
    TextWatcher MobileWather = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private Button sure_btn, getverifycode_btn;
    TextWatcher verufyCodeWather = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (edit_VerifyCode.length() == 0) {
                sure_btn.setEnabled(false);
            } else {
                sure_btn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private EditText edit_mobile, edit_VerifyCode;
    //private TextView edit_countryName;
    private String countryCode;
    private VerifyCode verifyCode;
    //  private ProgressDialog progressDialog;
    private Long StartTime;
    private boolean forgetPsd = false;
    private PopupWindowHelper popupWindowHelper;
    private View popView;
    private RecyclerView mobile_recycleview;
    private List<String> data = new ArrayList<>();
    private List<String> countryData = new ArrayList<>();
    private TextView mobile_text, mobile_number;
    private String IMEI;
    private String spiltNum;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_mobile_verify;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListener();
        initdata();
    }

    /**
     * create by brucs
     * date:2016/5/4
     * describle:获取手机号码，并进行校验
     */

    private boolean getMoliePhoneNum(String spiltNum, String moblie_num) {
        boolean isRight = false;
        if (spiltNum.equals("0086")) {
            if (Validator.isMobile(moblie_num)) {
                return true;
            } else {
                MyToast.makeTextAnim(MobileVerifyActivity.this,
                        R.string.mobile_mobile_num_wrong, 0, R.style.PopToast).show();
                return false;
            }
        }

        if (moblie_num.length() >= 7) {
            isRight = true;
        } else {
            MyToast.makeTextAnim(MobileVerifyActivity.this,
                    R.string.mobile_mobile_num_wrong, 0, R.style.PopToast).show();
            isRight = false;
        }
        return isRight;
    }

    private void initViews() {
        sure_btn = findViewById(R.id.btn_sure);
        getverifycode_btn = findViewById(R.id.getverifycode_btn);

        edit_mobile = findViewById(R.id.edit_mobile);
        edit_VerifyCode = findViewById(R.id.edit_VerifyCode);
        // edit_countryName = (TextView) findViewById(R.id.country);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");//去掉默认居左的title
        title = toolbar.findViewById(R.id.title);
        mobile_text = findViewById(R.id.mobile_text);
        mobile_number = findViewById(R.id.mobile_number);
        //弹出框的控件
        popView = LayoutInflater.from(this).inflate(R.layout.add_mobile_dialog, null);
        popupWindowHelper = new PopupWindowHelper(popView);
        mobile_recycleview = popView.findViewById(R.id.mobile_recycleview);

        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(this);
        mobile_recycleview.setLayoutManager(layoutManager);
        mobile_recycleview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, -1));
        data.add("0086");
        data.add("0063");
        data.add("0852");
        data.add("0853");
        data.add("0081");
        data.add("0082");
        data.add("0060");
        data.add("0065");
        data.add("0886");

        countryData.add("中国 0086");
        countryData.add("菲律宾 0063");
        countryData.add("香港 0852");
        countryData.add("澳门 0853");
        countryData.add("日本 0081");
        countryData.add("韩国 0082");
        countryData.add("马来西亚 0060");
        countryData.add("新加坡 0065");
        countryData.add("台湾 0886");
//        String[] aaaa= data.get(1).split(" ");
//        String bb = aaaa[0];
//        String cc = aaaa[1];
        spiltNum = data.get(0);
        RecycleAdapter recycleAdapter = new RecycleAdapter(this, countryData, R.layout.adapter_simple);
        mobile_recycleview.setAdapter(recycleAdapter);
        recycleAdapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                popupWindowHelper.dismiss();
                mobile_number.setText(data.get(position));
                mobile_text.setText(countryData.get(position));
                spiltNum = data.get(position);
            }
        });
    }

    private void setListener() {
        //防止重复点击的按钮
        sure_btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String edite_code = edit_VerifyCode.getText().toString();
                String edite_mobile = spiltNum + edit_mobile.getText().toString();
                checkSmsCode(edite_mobile, edite_code);
            }
        });

        getverifycode_btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String moblie_num = spiltNum + edit_mobile.getText().toString();
                if (getMoliePhoneNum(spiltNum, edit_mobile.getText().toString()) == true) {

                    getSmsCode(moblie_num);
                  /*  verifyCode.start();
                    LastTime = new Date().getTime();*/
                }
            }
        });

        edit_VerifyCode.addTextChangedListener(verufyCodeWather);
        edit_mobile.addTextChangedListener(MobileWather);
        mobile_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowHelper.showAsDropDown(mobile_text);

            }
        });
        //edit_countryName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.country:
//                Intent intent = new Intent();
//                intent.setClass(MobileVerifyActivity.this, CountryActivity.class);
//                startActivityForResult(intent, Constants.REQUEST_CODE_SEARCH_COUNTRY);
//                break;
//            default:
//                break;
//        }
    }

    private void initdata() {
        title.setText(R.string.verify_title);
        StartTime = countTime;
        Long time = new Date().getTime();
        if (time < LastTime + countTime) {
            StartTime = countTime - (time - LastTime);
        }
        verifyCode = new VerifyCode(StartTime, 1000);
        if (time < LastTime + countTime) {
            verifyCode.start();
        }
        forgetPsd = getIntent().getBooleanExtra("forgetPsd", false);
        if (forgetPsd == true) {
//            sure_btn.setText(R.string.next);
            HomeWatcher.mCamHomeKeyDown = false;
        }
    }

    /**
     * create by brucs
     * date:2016/5/4
     * describle:是否已经注册了手机号码，登陆状态
     */
    private void checkloginPhoneIsRegister(final String phone) {
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("phone", phone);
        DialogTool.createProgressDialog(this, true);
        HttpUtil.post(Constants.getInstance().phoneIsRegister, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response == null) {
                    MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                } else {
                    try {
                        int resultCode = response.getInt("resultCode");
                        String resultMessage = response.getString("resultMessage");
                        String register = "";
                        if (resultCode == 0 && resultMessage.equals("该手机号码尚未注册")) {
                            registerPhoneNumber(phone);
                        }
                        if (resultCode == 0 && !resultMessage.equals("该手机号码尚未注册")) {
                            MyToast.makeTextAnim(MobileVerifyActivity.this, resultMessage, 0, R.style.PopToast).show();
                            return;
                        }
                        JSONObject dataCollection = response.getJSONObject("dataCollection");
                        if (!dataCollection.equals("")) {
                            register = dataCollection.getString("register");
                        } else {
                            MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                            return;
                        }
                        if (resultCode == 1 && register.equals("1")
                                || resultCode == 1 && register.equals("3")) {
                            MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.mobile_is_exist, 0, R.style.PopToast).show();
                        } else if (resultCode == 1 && register.equals("2")) {
                            registerPhoneNumber(phone);
                        } else {
                            MyToast.makeTextAnim(MobileVerifyActivity.this, resultMessage, 0, R.style.PopToast).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogTool.dismiss(MobileVerifyActivity.this);
            }
        });
    }

    /**
     * create by brucs
     * date:2016/5/4
     * describle:注册手机号码
     */
    private void checkNologinPhoneIsRegister(final String phone) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        DialogTool.createProgressDialog(this, true);
        HttpUtil.post(Constants.getInstance().phoneIsRegisterNoLogin, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response == null) {
                    MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                } else {
                    try {
                        int resultCode = response.getInt("resultCode");
                        String resultMessage = response.getString("resultMessage");
                        if (resultCode == 1) {
                            Intent intent = new Intent(MobileVerifyActivity.this, ChangePsdActivity.class);
                            intent.putExtra("forgetPsd", forgetPsd);
                            intent.putExtra("mobliephone", phone);
                            startActivity(intent);
                            finish();
                            DialogTool.dismiss(MobileVerifyActivity.this);
                        } else {
                            MyToast.makeTextAnim(MobileVerifyActivity.this, resultMessage, 0, R.style.PopToast).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogTool.dismiss(MobileVerifyActivity.this);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public String getIMEI() {

        return ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    private void applicationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {

                IMEI = getIMEI();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                IMEI = getIMEI();
            } else {

                MyToast.makeTextAnim(getApplicationContext(), "绑定手机失败，请打开此权限", 0, R.style.PopToast).show();

            }
        }
    }

    /**
     * create by brucs
     * date:2016/5/4
     * describle:注册手机号码
     */
    private void registerPhoneNumber(final String phone) {
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        applicationPermissions();
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        DialogTool.createProgressDialog(this, true);
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("phone", phone);
        params.put("organiId", "");
        params.put("phoneMACAddress", IMEI);
        HttpUtil.post(Constants.getInstance().buyerUpdataPhone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response == null) {
                    ToastUtils.showToast(R.string.getData_fail, 0);
                } else {
                    try {
                        int resultCode = response.getInt("resultCode");
                        String resultMessage = response.getString("resultMessage");
                        if (resultCode == 1) {
                            SharedPreferencesUtils.put(MobileVerifyActivity.this, Constants.CHECK_PHONE, true);
                            if (phone.substring(0, 4).equals("0086")) {
                                SharedPreferencesUtils.put(MobileVerifyActivity.this, Constants.PHONE, phone.substring(4, phone.length()));
                            } else {
                                SharedPreferencesUtils.put(MobileVerifyActivity.this, Constants.PHONE, phone);
                            }

                            Intent intent = new Intent(Constants.ACTION_SETTING);
                            intent.putExtra("type", "verify");
                            sendBroadcast(intent);

                            finish();
                        }
                        if (!Tools.isEmpty(resultMessage)) {
                            getverifycode_btn.setEnabled(true);
                            MyToast.makeTextAnim(MobileVerifyActivity.this, resultMessage, 0, R.style.PopToast).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogTool.dismiss(MobileVerifyActivity.this);
            }
        });
    }

    /**
     * create by brucs
     * date:2016/5/4
     * describle:注册手机号码
     */
    private void getSmsCode(String phone) {
        RequestParams params = new RequestParams();
        DialogTool.createProgressDialog(this, true);
        params.put("userMobile", phone);
        params.put("userType", "1");
        String sign = Tools.md5(phone + ",s3Ms@S!en1D$k0");
        params.put("sign", sign);
        HttpUtil.post(Constants.getInstance().getSmsCode, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response == null) {
                    ToastUtils.showToast(R.string.getData_fail, 0);
                } else {
                    try {
                        String resultMessage = response.getString("resultMessage");
                        int resultCode = response.getInt("resultCode");

                        if (resultCode == 1) {
                            verifyCode.start();
                            LastTime = new Date().getTime();
                        }
                        if (!Tools.isEmpty(resultMessage)) {

                            MyToast.makeTextAnim(MobileVerifyActivity.this, resultMessage, 0, R.style.PopToast).show();
                        }
                        DialogTool.dismiss(MobileVerifyActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogTool.dismiss(MobileVerifyActivity.this);
            }
        });
    }

    /**
     * create by brucs
     * date:2016/5/4
     * describle: 验证号码
     */
    private void checkSmsCode(final String phone, String smsCode) {
        RequestParams params = new RequestParams();
        DialogTool.createProgressDialog(this, true);
        params.put("userMobile", phone);
        params.put("smsCode", smsCode);

        HttpUtil.post(Constants.getInstance().checkSmsCode, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response == null) {
                    ToastUtils.showToast(R.string.getData_fail, 0);
                } else {
                    try {
                        int resultCode = response.getInt("resultCode");
                        String resultMessage = response.getString("resultMessage");
                        if (resultCode == 1) {
                            setCheckPhone(phone);
                            verifyCode.onFinish();
                        }
                        if (!Tools.isEmpty(resultMessage)) {
                            MyToast.makeTextAnim(MobileVerifyActivity.this, resultMessage, 0, R.style.PopToast).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                MyToast.makeTextAnim(MobileVerifyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogTool.dismiss(MobileVerifyActivity.this);
            }
        });
    }

    private void setCheckPhone(String edite_mobile) {
        if (forgetPsd == true) {
            checkNologinPhoneIsRegister(edite_mobile);
        } else {
            checkloginPhoneIsRegister(edite_mobile);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_SEARCH_COUNTRY:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    countryCode = bundle.getString("countryNumber");
                    //edit_countryName.setText(countryName);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class VerifyCode extends CountDownTimer {

        public VerifyCode(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            getverifycode_btn.setText("获取验证码");
            getverifycode_btn.setEnabled(true);
            if (StartTime < countTime) {
                verifyCode = new VerifyCode(countTime, 1000);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getverifycode_btn.setText(millisUntilFinished / 1000 + "秒重新获取");
            getverifycode_btn.setEnabled(false);
        }
    }


}
