package com.bber.company.android.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.service.MsfService;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.imageload.AudioGetFromHttp;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends BaseAppCompatActivity implements View.OnClickListener {


    CompoundButton.OnCheckedChangeListener msgCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
            SharedPreferencesUtils.put(SettingActivity.this, "msg_push", check);
        }
    };
    CompoundButton.OnCheckedChangeListener ringCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
            SharedPreferencesUtils.put(SettingActivity.this, "ring", check);
        }
    };
    CompoundButton.OnCheckedChangeListener vibrateCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
            SharedPreferencesUtils.put(SettingActivity.this, "vibrate", check);
        }
    };
    private RelativeLayout view_new_version, view_change_psd, btn_logout, view_clean_cache;
    private SwitchCompat msg_switch, ring_switch, vibrate_switch, gesture_switch;
    private boolean ignoreChange;
    CompoundButton.OnCheckedChangeListener gestureCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
            SharedPreferencesUtils.put(SettingActivity.this, "gesture_push", check);
            if (!ignoreChange) {
                if (check == true) {
                    Intent intent = new Intent(SettingActivity.this, GestureLockActivity.class);
                    intent.putExtra(PointState.GESTURETYPE, PointState.GESTURE_TYPE_VERIFY_OTHER);
                    startActivity(intent);
                } else {
                    SharedPreferencesUtils.put(SettingActivity.this, "gesturePsw", "");
                }
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.setting);
        view_new_version = findViewById(R.id.view_new_version);
        view_change_psd = findViewById(R.id.view_change_psd);
        view_clean_cache = findViewById(R.id.view_clean_cache);
        btn_logout = findViewById(R.id.btn_logout);
        msg_switch = findViewById(R.id.msg_switch);
        gesture_switch = findViewById(R.id.gesture_switch);
        ring_switch = findViewById(R.id.ring_switch);
        vibrate_switch = findViewById(R.id.vibrate_switch);

    }

    private void setListeners() {
        view_new_version.setOnClickListener(this);
        view_change_psd.setOnClickListener(this);
        view_clean_cache.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        msg_switch.setOnCheckedChangeListener(msgCheckedChangeListener);
        gesture_switch.setOnCheckedChangeListener(gestureCheckedChangeListener);
        ring_switch.setOnCheckedChangeListener(ringCheckedChangeListener);
        vibrate_switch.setOnCheckedChangeListener(vibrateCheckedChangeListener);
    }

    private void initData() {
        ignoreChange = true;
        msg_switch.setChecked((Boolean) SharedPreferencesUtils.get(this, "msg_push", true));
        ring_switch.setChecked((Boolean) SharedPreferencesUtils.get(this, "ring", true));
        vibrate_switch.setChecked((Boolean) SharedPreferencesUtils.get(this, "vibrate", true));
        gesture_switch.setChecked((Boolean) SharedPreferencesUtils.get(this, "gesture_push", false));
        ignoreChange = false;
    }

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
                if (Tools.jsonResult(SettingActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }

                    String status = dataCollection.getString("status");
                    if (status.equals("3")) {
                        MyToast.makeTextAnim(SettingActivity.this, R.string.no_newversion, 0, R.style.PopToast).show();
                        return;
                    }
                    String content = dataCollection.getString("content");
                    final String URL = dataCollection.getString("updateUrl");
                    LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
                    View layout = inflater.inflate(R.layout.custom_alertdialog_tip, null);
                    final AlertDialog dialog = DialogTool.createDialogTip(SettingActivity.this, layout, content, R.string.next_time, R.string.update);
                    if (status.equals("1")) {//选择更新
                        dialog.setCancelable(true);
                        layout.findViewWithTag(0).setVisibility(View.VISIBLE);
                    } else if (status.equals("2")) {//强制更新
                        dialog.setCancelable(false);
                        layout.findViewWithTag(0).setVisibility(View.GONE);
                    }
                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Uri uri = Uri.parse(URL);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });

                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "updateVersion onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(SettingActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_new_version:
                checkVersion();
                break;
            case R.id.view_change_psd:
                final Intent intent = new Intent(this, ChangePsdActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_logout:
                LayoutInflater inflater = LayoutInflater.from(this);
                View layout = inflater.inflate(R.layout.custom_alertdialog, null);
                final AlertDialog dialog = DialogTool.createDialog(this, layout, R.string.logout_text, R.string.cancle_tip, R.string.sure_tip);
                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        AppManager.getAppManager().finishAllActivity();
                        Intent s = new Intent(SettingActivity.this, MsfService.class);
                        stopService(s);/*退出登录 */
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.USERID);
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.SESSION);
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.CHECK_PHONE);
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.PHONE);
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.USER_PHONE);
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.VIP_END_TIEM);
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.ISRMINDING); //与主播聊天是否需要提示
                        SharedPreferencesUtils.remove(SettingActivity.this, "touurl");//上传主播头像
                        SharedPreferencesUtils.remove(SettingActivity.this, "uploadVideouri");//上传主播视频
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.BROADCAST_LEVEL);//主播状态 是否是主播
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.AGORAKOKEN);// 信令需要的token
                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.BROADCAST_SETVICE_SIGN);// 直播后台需要的sign
						SharedPreferencesUtils.remove(SettingActivity.this, Constants.VIP_ID);

                        SharedPreferencesUtils.remove(SettingActivity.this, Constants.BROADCAST_SETVICE_TOKEN);// 直播后台需要的token
                        Intent intent1 = new Intent(SettingActivity.this, EnterActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        finish();
                    }
                });
                break;
            case R.id.view_clean_cache:
                DialogTool.createProgressDialog(this, true);
                ImageFileCache imageFileCache = new ImageFileCache();
                AudioGetFromHttp audio = new AudioGetFromHttp(this);
                imageFileCache.cleanCache();
                app.cleanFresco();
                audio.removeCaches();
                DialogTool.dismiss(this);
                MyToast.makeTextAnim(SettingActivity.this, R.string.clean_cache_success, 0, R.style.PopToast).show();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ignoreChange = true;
        gesture_switch.setChecked((Boolean) SharedPreferencesUtils.get(this, "gesture_push", false));
        ignoreChange = false;
    }
}
