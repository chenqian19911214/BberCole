package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.listener.HomeWatcher;

/**
 * 注册第一步 输入邀请码
 * *
 */
public class RegFirActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private EditText edit_key;
    private Button btn_next;
    private String city;
    private int forciblyInput = 0;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() == 0) {
                if (forciblyInput == 1) {
                    btn_next.setText(R.string.next);
                    btn_next.setEnabled(false);
                } else {
                    btn_next.setEnabled(true);
                    btn_next.setText(R.string.skip);
                }
            } else {
                btn_next.setText(R.string.next);
                btn_next.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_reg_fir;
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
        title.setText(R.string.key_tip);
        btn_next = findViewById(R.id.btn_next);
        edit_key = findViewById(R.id.edit_key);
    }

    private void setListeners() {
        btn_next.setOnClickListener(this);
        edit_key.addTextChangedListener(textWatcher);
    }

    private void initData() {
        HomeWatcher.mCamHomeKeyDown = false;
        city = getIntent().getStringExtra("city");
        forciblyInput = getIntent().getIntExtra("forciblyInput", 0);
        if (forciblyInput == 1) {
            btn_next.setText(R.string.next);
            btn_next.setEnabled(false);
            btn_next.setHint(R.string.key_hint);
        } else {
            btn_next.setEnabled(true);
            btn_next.setText(R.string.skip);
            btn_next.setHint(R.string.key_hint_skip);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                String key = edit_key.getText() + "";
                Intent intent = new Intent(RegFirActivity.this, RegistereActivity.class);
                intent.putExtra("city", city);
                intent.putExtra("inviteCode", key);
                startActivity(intent);
                break;

        }

    }

}
