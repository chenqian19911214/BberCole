package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;

public class PayModeActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_unionpay, rl_alipay, rl_wechatpay;
    private String isUnionpay, isPayAlipay, isPayWechat;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_paymode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setvisiblety();
        setListeners();
        initData();

    }

    private void initViews() {
        Intent intent = getIntent();

        isUnionpay = intent.getStringExtra("isUnionpay");
        isPayAlipay = intent.getStringExtra("isPayAlipay");
        isPayWechat = intent.getStringExtra("isPayWechat");

        title = toolbar.findViewById(R.id.title);
        rl_unionpay = findViewById(R.id.rl_unionpay);
        rl_alipay = findViewById(R.id.rl_alipay);
        rl_wechatpay = findViewById(R.id.rl_wechatpay);
    }

    /***
     * 是否显示隐藏itemview
     */
    private void setvisiblety() {
        if (isUnionpay.equals("0")) {
            rl_unionpay.setVisibility(View.GONE);
        } else {
            rl_unionpay.setVisibility(View.VISIBLE);
        }

        if (isPayAlipay.equals("0")) {
            rl_alipay.setVisibility(View.GONE);
        } else {
            rl_alipay.setVisibility(View.VISIBLE);
        }

        if (isPayWechat.equals("0")) {
            rl_wechatpay.setVisibility(View.GONE);
        } else {
            rl_wechatpay.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners() {
        rl_unionpay.setOnClickListener(this);
        rl_alipay.setOnClickListener(this);
        rl_wechatpay.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.payMode);
    }


    @Override
    public void onClick(View v) {
        int payCode = 0;
        int iconResId = R.mipmap.icon_pay;
        String payName = "";
        switch (v.getId()) {
            case R.id.rl_unionpay:
                payCode = Constants.UNIONPAY_CODE;
                payName = "银联卡";
                iconResId = R.mipmap.unionpay;
                break;
            case R.id.rl_alipay:
                payCode = Constants.ALIPAY_CODE;
                payName = "支付宝";
                iconResId = R.mipmap.alipay;
                break;
            case R.id.rl_wechatpay:
                payCode = Constants.WECHATPAY_CODE;
                payName = "微信";
                iconResId = R.mipmap.wechatpay;
                break;
        }
        Intent mIntent = new Intent();
        mIntent.putExtra("payCode", payCode);
        mIntent.putExtra("payName", payName);
        mIntent.putExtra("iconResId", iconResId);
        setResult(RESULT_OK, mIntent);
        finish();
    }
}
