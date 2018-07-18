package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.GestureContentView;
import com.bber.company.android.widget.GestureDrawline;
import com.bber.company.android.widget.LockIndicator;

/**
 * 手势密码设置界面
 */
public class GestureLockActivity extends Activity implements OnClickListener {
    private String mInputCode;
    private LockIndicator mLockIndicator;
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextReset;
    private String mVerifyTpye;
    private boolean mIsFirstInput = true;
    private Toolbar toolbar;
    private TextView title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Main);
        AppManager.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_psw_canvas_layout);

        setUpViews();
        setUpListeners();
        initDate();
    }

    private void initDate() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");//去掉默认居左的title
        title = toolbar.findViewById(R.id.title);
        title.setText(R.string.set_gesture);
    }

    private void setUpViews() {
        mVerifyTpye = getIntent().getStringExtra(PointState.GESTURETYPE);
        mTextReset = findViewById(R.id.text_show);
        mTextReset.setClickable(false);
        mLockIndicator = findViewById(R.id.lock_indicator);
        mTextTip = findViewById(R.id.text_tip);
        mGestureContainer = findViewById(R.id.gesture_container);
        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, false, "", new GestureDrawline.GestureCallBack() {
            @Override
            public void onGestureCodeInput(String inputCode) {
                if (!isInputPassValidate(inputCode)) {
                    mTextTip.setText(Html.fromHtml("<font color='#ff0000'>最少链接4个点, 请重新输入</font>"));
                    mGestureContentView.clearDrawlineState(0L);
                    return;
                }
                if (mIsFirstInput) {
                    mInputCode = inputCode;
                    updateCodeList(inputCode);
                    mGestureContentView.clearDrawlineState(300L);
                    mTextReset.setClickable(true);
                    mTextReset.setText("清除手势密码");
                    mTextTip.setText("请再次绘制密码");
                } else {
                    if (inputCode.equals(mInputCode)) {
                        mGestureContentView.clearDrawlineState(0L);
                        SharedPreferencesUtils.put(GestureLockActivity.this, "gesturePsw", mInputCode);
                        if (PointState.GESTURE_TYPE_VERIFY_MAIN.equals(mVerifyTpye)) {
                            String adPicture = (String) SharedPreferencesUtils.get(GestureLockActivity.this, "adPicture", "");
                            if (!Tools.isEmpty(adPicture) && HomeWatcher.toAds == true) {
                                Intent intent = new Intent(GestureLockActivity.this, ADActivity.class);
                                intent.putExtra("isLogin", false);
                                intent.putExtra("chen", "3");

                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(GestureLockActivity.this, MainActivity.class);
                                intent.putExtra("isLogin", false);
                                startActivity(intent);
                            }
                        }
                        finish();
                    } else {
                        mTextTip.setText(Html.fromHtml("<font color='#ff0000'>与上一次绘制不一致，请重新绘制</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(GestureLockActivity.this, R.anim.shake);
                        mTextTip.startAnimation(shakeAnimation);
                        // 保持绘制的线，1.5秒后清除
                        mGestureContentView.clearDrawlineState(1500L);
                    }
                }
                mIsFirstInput = false;
            }

            @Override
            public void checkedSuccess() {
            }

            @Override
            public void checkedFail(String inputCode) {
            }
        });
        mGestureContentView.setParentView(mGestureContainer);
        updateCodeList("");
    }

    private void setUpListeners() {
        mTextReset.setOnClickListener(this);
    }

    private void updateCodeList(String inputCode) {
        // 更新选择的图案
        mLockIndicator.setPath(inputCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_show:
                mIsFirstInput = true;
                updateCodeList("");
                mTextTip.setText("请绘制手势密码");
                break;
            default:
                break;
        }
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

