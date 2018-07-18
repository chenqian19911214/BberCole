package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.listener.IactionListener;


public class ConfirmDialog extends Dialog {

    public IactionListener iactionListener;
    private Context context;
    private String title;
    private String confirmButtonText;
    private String cacelButtonText;

    public ConfirmDialog(Context context, String title, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
    }

    public ConfirmDialog(Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
    }

    public void setActionListener(IactionListener _listener) {
        iactionListener = _listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alert_save_image, null);
        setContentView(view);

        TextView dialog_cancle = view.findViewById(R.id.button_cancal);
        TextView saveImage_one = view.findViewById(R.id.saveImage_one);
        TextView saveImage_all = view.findViewById(R.id.saveImage_all);

//        tvTitle.setText(title);
//        tvConfirm.setText(confirmButtonText);
//        tvCancel.setText(cacelButtonText);

        dialog_cancle.setOnClickListener(new clickListener());
        saveImage_one.setOnClickListener(new clickListener());
        saveImage_all.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.saveImage_one:
                    iactionListener.SuccessCallback("1");
                    dismiss();
                    break;
                case R.id.saveImage_all:
                    iactionListener.SuccessCallback("2");
                    dismiss();
                    break;
                case R.id.button_cancal:
                    dismiss();
                    break;
            }
        }

    }

}