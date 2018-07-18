package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.customview.utils.LogUtils;

/**
 * 手续费提示
 * Created by zaicent on 2016/6/15.
 */
public class PromptDialog extends Dialog implements View.OnClickListener {
    private EditText mEditText_code;
    private Button mButton_sure;
    private Button mImageView_back;
    private Context mContext;
    private String change;
    private TextView chooseText;
    private String data;

    public PromptDialog(Context context, String change) {
        super(context, R.style.dialog_simple);
        mContext = context;
        this.change = change;
        LogUtils.e("实例化手续费");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_prompt);
        initDialog();
    }

    private void initDialog() {
        chooseText = findViewById(R.id.choose_copy);
        data = String.format(chooseText.getText().toString(), change);
        chooseText.setText(data);
        mImageView_back = findViewById(R.id.back);
        mImageView_back.setOnClickListener(this);
    }

    public void back() {

        AppManager.getAppManager().finishActivityForLength(2);
        AppManager.getAppManager().finishActivity();
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                back();
                dismiss();
                break;
        }
    }

}
