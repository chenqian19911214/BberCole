package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.bber.company.android.R;

public class LoadingDialog extends Dialog {
    private View mView;

    public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadingDialog(Context context) {
        super(context, R.style.Theme_Transparent);
        mView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        setContentView(mView);
        // 设置window属性
        LayoutParams a = getWindow().getAttributes();
        a.dimAmount = 0; // 去背景遮盖
        getWindow().setAttributes(a);
    }
}
