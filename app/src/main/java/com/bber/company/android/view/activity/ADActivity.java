package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.adsBean;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.commonActivity;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 广告页面
 */
public class ADActivity extends Activity implements View.OnClickListener {

    private TextView btn_enter_papa;
    private LinearLayout ll_papa;
    private SimpleDraweeView draweeView;
    private SkipTime m_skipTime;
    private boolean isLogin = false;
    private adsBean m_adsBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//API19(android4.4)以上才有沉浸式
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ChenQianLog.i(getIntent().getStringExtra("chen"));
        initViews();
        setListeners();
        initData();
    }

    private void setListeners() {
        btn_enter_papa.setOnClickListener(this);
        draweeView.setOnClickListener(this);
        ll_papa.setOnClickListener(this);
    }

    private void initViews() {
        draweeView = findViewById(R.id.btn_enter_addetail);
        btn_enter_papa = findViewById(R.id.btn_enter_papa);
        ll_papa = findViewById(R.id.ll_papa);
    }

    private void initData() {
        String adPicture;
        String adTarget;
        int id;
        adPicture = (String) SharedPreferencesUtils.get(ADActivity.this, "adPicture", "");
        adTarget = (String) SharedPreferencesUtils.get(ADActivity.this, "adTarget", "");
        id = Tools.StringToInt(SharedPreferencesUtils.get(ADActivity.this, "adid", 0) + "");
        isLogin = getIntent().getBooleanExtra("isLogin", false);
        m_skipTime = new SkipTime(4000, 1000);
        m_skipTime.start();
        m_adsBean = new adsBean();
        m_adsBean.setId(id);
        m_adsBean.setAdTarget(adTarget);
        m_adsBean.setAdPicture(adPicture);
        HomeWatcher.toAds = false;

        if (!Tools.isEmpty(adPicture)) {
            Uri uri = Uri.parse(adPicture);
            draweeView.setImageURI(uri);
            draweeView.setVisibility(View.VISIBLE);
        } else {
            enterToPaPa();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter_addetail:
                m_skipTime.cancel();
                enterToPaPa();
                commonActivity.startBrowebuyURL(this, m_adsBean);
                break;
            case R.id.ll_papa:
                enterToPaPa();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            enterToPaPa();
        }
    }

    /**
     * 进入啪啪
     */
    private void enterToPaPa() {
        Intent intent = new Intent(ADActivity.this, MainActivity.class);
        intent.putExtra("isLogin", isLogin);
        startActivity(intent);
        finish();
    }

    /**
     * 时间倒计时器
     */
    public class SkipTime extends CountDownTimer {

        public SkipTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btn_enter_papa.setEnabled(true);
            enterToPaPa();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_enter_papa.setText(millisUntilFinished / 1000 + "");
        }
    }
}
