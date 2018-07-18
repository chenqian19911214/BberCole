package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.listener.IactionListener;


public class GameBetDialog extends Dialog {

    public IactionListener iactionListener;
    private Context context;
    private String title;
    private String confirmButtonText;
    private String cacelButtonText;
    private int position;
    private CheckBox checkBox;
    private boolean isselect = false;

    public GameBetDialog(Context context, String title, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.title = title;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
    }


    public GameBetDialog(Context context, int position) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.position = position;
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
        View view = inflater.inflate(R.layout.alert_game_bet_image, null);
        setContentView(view);

        checkBox = view.findViewById(R.id.no_chenck_box);
        Button button_cancal = view.findViewById(R.id.button_cancal);
        Button button_sure = view.findViewById(R.id.button_sure);
        TextView number_model = view.findViewById(R.id.number_model);

        String result = String.format(context.getResources().getString(R.string.game_bet), position);
        number_model.setText(result);

//        tvTitle.setText(title);
//        tvConfirm.setText(confirmButtonText);
//        tvCancel.setText(cacelButtonText);

//        checkBox.setOnClickListener(new clickListener());
        button_cancal.setOnClickListener(new clickListener());
        button_sure.setOnClickListener(new clickListener());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isselect = isChecked;
            }
        });


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
                case R.id.no_chenck_box:
                    break;
                case R.id.button_sure:
                    iactionListener.SuccessCallback("2");
                    if (isselect) {
                        iactionListener.FailCallback("");
                    }
                    dismiss();
                    break;
                case R.id.button_cancal:
                    dismiss();
                    break;
            }
        }

    }

}