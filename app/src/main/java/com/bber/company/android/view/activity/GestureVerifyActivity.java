package com.bber.company.android.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.GestureContentView;
import com.bber.company.android.widget.GestureDrawline;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 手势绘制/校验界面
 */
public class GestureVerifyActivity extends Activity implements android.view.View.OnClickListener {
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextForget;
    private String mVerifyTpye;
    private int count = 3;//解锁操作三次
    private String gestureCode = "";
    private SimpleDraweeView user_icon;
    private Toolbar toolbar;
    private TextView title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Main);
        AppManager.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_gesture_verify_layout);
        ObtainExtraData();
        setUpViews();
        setUpListeners();
        initDate();
    }

    private void initDate() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");//去掉默认居左的title
        title = toolbar.findViewById(R.id.title);
        title.setText(R.string.gesture_tip);
    }


    private void ObtainExtraData() {
        Intent intent = getIntent();
        HomeWatcher.mCamHomeKeyDown = false;
        mVerifyTpye = intent.getStringExtra(PointState.GESTURETYPE);
        gestureCode = (String) SharedPreferencesUtils.get(GestureVerifyActivity.this, "gesturePsw", "");

        user_icon = findViewById(R.id.user_logo);
        String headURL = SharedPreferencesUtils.get(this, Constants.HEADURL, "") + "";

        RoundingParams roundingParams =
                user_icon.getHierarchy().getRoundingParams();
        roundingParams.setBorder(Color.parseColor("#ff0066"), 4);
        roundingParams.setRoundAsCircle(true);
        user_icon.getHierarchy().setRoundingParams(roundingParams);

        if (!Tools.isEmpty(headURL)) {
            Uri uri = Uri.parse(headURL);
            user_icon.setImageURI(uri);
        }

    }

    private void setUpViews() {
        mTextTip = findViewById(R.id.text_tip);
        mTextTip.setVisibility(View.VISIBLE);
        mTextTip.setText(Html.fromHtml("<font color='#ff0000'>请输入手势密码</font>"));
        mGestureContainer = findViewById(R.id.gesture_container);
        mTextForget = findViewById(R.id.text_forget_gesture);
        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, true, gestureCode,
                new GestureDrawline.GestureCallBack() {
                    @Override
                    public void onGestureCodeInput(String inputCode) {
                    }

                    @Override
                    public void checkedSuccess() {
                        if (count > 0) {
                            HomeWatcher.mCamHomeKeyDown = true;
                            if (PointState.GESTURE_TYPE_VERIFY_MAIN.equals(mVerifyTpye)) {
                                String adPicture = (String) SharedPreferencesUtils.get(GestureVerifyActivity.this, "adPicture", "");
                                if (!Tools.isEmpty(adPicture) && HomeWatcher.toAds == true) {
                                    Intent intent = new Intent(GestureVerifyActivity.this, ADActivity.class);
                                    intent.putExtra("isLogin", false);
                                    intent.putExtra("chen", "4");

                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(GestureVerifyActivity.this, MainActivity.class);
                                    intent.putExtra("isLogin", false);
                                    startActivity(intent);
                                }
                            }
                            finish();
                        } else {
                            showExceedTimes();
                            mGestureContentView.clearDrawlineState(0L);
                        }
                    }

                    @Override
                    public void checkedFail(String inputCode) {
                        if (!isInputPassValidate(inputCode)) {
                            mGestureContentView.clearDrawlineState(0l);
                            mTextTip.setVisibility(View.VISIBLE);
                            mTextTip.setText(Html.fromHtml("<font color='#ff0000'>最少链接4个点, 请重新输入</font>"));
                            // 左右移动动画
                            Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
                            mTextTip.startAnimation(shakeAnimation);
                            return;
                        }
                        count--;
                        if (count == 0) {
                            mGestureContentView.clearDrawlineState(0l);
                            mTextTip.setVisibility(View.VISIBLE);
                            mTextTip.setText(Html
                                    .fromHtml("<font color='#ff0000'>密码错误，还有</font>"
                                            + "<font color='#333333'>" + count + "</font>"
                                            + "<font color='#ff0000'>次机会</font>"));
                            // 左右移动动画
                            Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
                            mTextTip.startAnimation(shakeAnimation);
                            showExceedTimes();
                        } else if (count > -1) {
                            mGestureContentView.clearDrawlineState(1300L);
                            mTextTip.setVisibility(View.VISIBLE);
                            mTextTip.setText(Html
                                    .fromHtml("<font color='#ff0000'>密码错误，还有</font>"
                                            + "<font color='#333333'>" + count + "</font>"
                                            + "<font color='#ff0000'>次机会</font>"));
                            // 左右移动动画
                            Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
                            mTextTip.startAnimation(shakeAnimation);
                        } else {
                            showExceedTimes();
                            mGestureContentView.clearDrawlineState(0L);
                            mTextTip.setText(Html
                                    .fromHtml("<font color='#ff0000'>密码错误，还有</font>"
                                            + "<font color='#333333'>" + 0 + "</font>"
                                            + "<font color='#ff0000'>次机会</font>"));
                        }
                    }
                });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(mGestureContainer);
    }

    private void setUpListeners() {
        mTextForget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_forget_gesture:
                SharedPreferencesUtils.put(GestureVerifyActivity.this, "gesturePsw", "");
                Intent intent = new Intent(GestureVerifyActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void showExceedTimes() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.custom_alertdialog, null);
        final AlertDialog dialog = DialogTool.createSelectDialog(this, layout, R.string.gusture_tip, R.string.cancle_tip, R.string.sure_tip);
        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GestureVerifyActivity.this, LoginActivity.class);
                SharedPreferencesUtils.put(GestureVerifyActivity.this, "gesturePsw", "");
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private boolean isInputPassValidate(String inputPassword) {
        return !(TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
