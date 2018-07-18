package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.widget.MyToast;

public class LiveBroadcastAuthenticationActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private Button authentication_button_id;
    private int broadcaselenel;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_live_broadcast_authentication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView();
        initView();
    }


    private void initView() {

        broadcaselenel = (int) SharedPreferencesUtils.get(getApplicationContext(), Constants.BROADCAST_LEVEL, 0);
        authentication_button_id = findViewById(R.id.authentication_button_id);
        authentication_button_id.setOnClickListener(this);
        if (broadcaselenel == 2) {//主播审核中

            authentication_button_id.setBackground(getResources().getDrawable(R.mipmap.renzhengzhong));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authentication_button_id://成为主播
                Intent intent = new Intent();
                switch (broadcaselenel) {
                    case 0:
                        intent.setClass(getApplicationContext(), LiveBroadcastAgreementActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        intent.setClass(getApplicationContext(), LiveBroadcastAuthenticationPhotoActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        MyToast.makeTextAnim(getApplicationContext(), "主播审核中", 1, R.style.PopToast).show();
                        break;
                }
                break;
        }
    }
}
