package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BuyerUserEntity;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.StringUtils;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;


public class LoginActivity extends BaseAppCompatActivity implements View.OnClickListener {


    public BuyerUserEntity buyerUserEntity;
    private EditText edit_nickname, edit_psd;
    private TextView forget_psd, version, guest_email;
    private Button btn_login;
    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String psdStr = edit_psd.getText() + "";
            if (charSequence.length() == 0 || psdStr.length() == 0) {
                btn_login.setEnabled(false);
            } else {
                btn_login.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    TextWatcher psdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String emailStr = edit_nickname.getText() + "";
            if (charSequence.length() == 0 || emailStr.length() == 0) {
                btn_login.setEnabled(false);
            } else {
                btn_login.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private boolean relogin = false;//登录状态时效，重新登录
    private RelativeLayout bottom_layout;
    private String head;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.login);
        edit_nickname = findViewById(R.id.edit_nickname);
        edit_psd = findViewById(R.id.edit_psd);
        forget_psd = findViewById(R.id.forget_psd);
        btn_login = findViewById(R.id.btn_login);
        version = findViewById(R.id.version);
        guest_email = findViewById(R.id.guest_email);
        bottom_layout = findViewById(R.id.bottom_layout);

    }

    private void setListeners() {
        forget_psd.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        bottom_layout.setOnClickListener(this);
        edit_nickname.addTextChangedListener(emailWatcher);
        edit_psd.addTextChangedListener(psdWatcher);
    }

    private void initData() {
        version.setText("当前最新版本号" + StringUtils.getVersion(this));
        edit_nickname.setText(SharedPreferencesUtils.get(this, Constants.USERNAME, "") + "");
        edit_nickname.setSelection(edit_nickname.length());
        relogin = getIntent().getBooleanExtra("relogin", false);
        HomeWatcher.mCamHomeKeyDown = false;
        getQQinfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                final String nowName = SharedPreferencesUtils.get(this, Constants.USERNAME, "") + "";
                final String userName = edit_nickname.getText() + "";
                if (Tools.isEmpty(userName)) {
                    MyToast.makeTextAnim(this, R.string.nickname_error, 0, R.style.PopToast).show();
                    return;
                }
                String psd = edit_psd.getText() + "";
                if (psd.isEmpty() || psd.length() < 6) {
                    MyToast.makeTextAnim(this, R.string.set_psd_error, 0, R.style.PopToast).show();
                    return;
                }

                if (!netWork.isNetworkConnected()) {
                    MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
                    return;
                }
                //  final ProgressDialog progressDialog = DialogTool.createProgressDialog(this, "验证中...");
                DialogView.show(this, true);
                RequestParams params = new RequestParams();
                params.put("userName", userName);
                params.put("password", psd);

                String logurl = Constants.getInstance().buyerLoginByName;

                ChenQianLog.i("logurl:" + logurl);
                HttpUtil.get(logurl, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                        Log.e("eeeeeeeeeeee", "login onSuccess--jsonObject:" + jsonObject);
                        if (Tools.jsonResult(LoginActivity.this, jsonObject, null)) {
                            return;
                        }
                        try {
                            LogUtils.e(jsonObject.toString());
                            JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                            String userId = dataCollection.getString(Constants.USERID);
                            String session = dataCollection.getString(Constants.SESSION);
                            String name = dataCollection.getString(Constants.USERNAME);
                            String url = dataCollection.getString(Constants.HEADURL);
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.USERNAME, name);
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.HEADURL, url);
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.USERID, userId);
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.SESSION, session);
                            LogUtils.e("玩家登陆的session" + session);
                            LogUtils.e("玩家登陆的userId" + userId);
                            //因为本地记录手机验证是存在本地，很有可能上一个用户有验证，这个没有
                            SharedPreferencesUtils.remove(LoginActivity.this, Constants.CHECK_PHONE);
                            SharedPreferencesUtils.remove(LoginActivity.this, Constants.PHONE);
                            getUserInfo();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("eeeeeeeeeeee", "login JSONException:");
                        }

                    }

                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Log.e("eeeeeeeeeeee", "login onFailure--throwable:" + throwable);
                        MyToast.makeTextAnim(LoginActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (throwable instanceof SocketTimeoutException) {
                            Log.e("eeeeeeeeeeee", "login onFailure  SocketTimeoutException:");

                        }

                    }

                    @Override
                    public void onFinish() { // 完成后调用，失败，成功，都要调用
//                        progressDialog.dismiss();
                        DialogView.dismiss(LoginActivity.this);

                    }
                });

                break;
            case R.id.forget_psd:
                Intent intent = new Intent(LoginActivity.this, MobileVerifyActivity.class);
                intent.putExtra("forgetPsd", true);
                startActivity(intent);
                break;
            case R.id.bottom_layout:
                intent = new Intent(LoginActivity.this, WebViewGuestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isHaveValue", false);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }

    }

    /**
     * 获取个人信息
     */
    public void getUserInfo() {
        RequestParams params = new RequestParams();
        JsonUtil jsonUtil = new JsonUtil(this);
        head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);

        DialogView.show(this, true);
        String getUserInfourl = Constants.getInstance().getUserInfo;
        params.put("id", SharedPreferencesUtils.get(LoginActivity.this, Constants.USERID, ""));

        ChenQianLog.i("getUserInfourl：" + getUserInfourl);

        HttpUtil.get(getUserInfourl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(LoginActivity.this, jsonObject, null)) {
                    return;
                }

                ChenQianLog.i("getUserInfo：" + jsonObject);
                try {

                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");

                        String data = dataCollection.getString("BuyerUserEntity");
                        JsonUtil jsonUtil = new JsonUtil(LoginActivity.this);
                        buyerUserEntity = jsonUtil.jsonToBuyerUserEntity(data); //获取个人数据
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.USERNAME, buyerUserEntity.getUbName());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.UBSEX, buyerUserEntity.getUbSex());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.BIRTHDAY, buyerUserEntity.getuBirthday());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.VIP_ID, buyerUserEntity.getVipId());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.USERICON, buyerUserEntity.getUbHeadm());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.VIP_NAME, buyerUserEntity.getVipName());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.VIP_START_TIME, buyerUserEntity.getVipStartTime());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.VIP_END_TIEM, buyerUserEntity.getVipEndTime());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.USER_MONEY, buyerUserEntity.getUbMoney());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.INVITECODE, buyerUserEntity.getInviteCode());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.GAMEFREEMONEY, buyerUserEntity.getGameFreeMoney());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.VIP_LEVEL, buyerUserEntity.getVipLevel());
                        SharedPreferencesUtils.put(LoginActivity.this, Constants.BROADCAST_LEVEL, buyerUserEntity.getAnchorStatus());

                        String phone = buyerUserEntity.getUbPhone();
                        String isVerify = buyerUserEntity.getiSVerify();

                        if (isVerify.equals("1") || phone.length() > 5) {
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.PHONE, phone);
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.CHECK_PHONE, true);

                        } else {
                            SharedPreferencesUtils.put(LoginActivity.this, Constants.CHECK_PHONE, false);
                        }
//                    money = buyerUserEntity.getUbMoney();
//                    tv_moneybag.setText(money+"");
                        MyApplication.getContext().setBalance(buyerUserEntity.getUbMoney());
//                    setVipInfor();
                        Intent intent = new Intent(LoginActivity.this, ADActivity.class);
                        intent.putExtra("isLogin", true);
                        intent.putExtra("chen", "9");

                        startActivity(intent);

                        finish();
                    }
//                            iactionListener.SuccessCallback(buyerUserEntity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUserInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(LoginActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用

                DialogView.dismiss(LoginActivity.this);
            }
        });
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            if(relogin){
//                app.exitApp();
//            }else{
//                Intent intent = new Intent(LoginActivity.this, EnterActivity.class);
//                startActivity(intent);
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 获取QQ号码
     */
    public void getQQinfo() {
        RequestParams params = new RequestParams();
        params.put("type", 7);
        HttpUtil.get(Constants.getInstance().getQQNumber, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        String[] data = (resultMessage).split(",");
                        guest_email.setText(Html.fromHtml("<font color=#999999>" + "客服协助邮箱:" +
                                "<font color=#ff0066>" + data[1] + "</font>"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (relogin) {
                app.exitApp();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
