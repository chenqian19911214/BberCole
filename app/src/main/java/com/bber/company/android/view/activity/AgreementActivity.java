package com.bber.company.android.view.activity;

import android.os.Bundle;

import com.bber.company.android.R;

public class AgreementActivity extends BaseAppCompatActivity {

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_agree;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.agree_title);
    }

    private void setListeners() {
    }

    private void initData() {
    }
}
