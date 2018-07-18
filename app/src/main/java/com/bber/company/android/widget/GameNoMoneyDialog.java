package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bber.company.android.R;
import com.bber.company.android.bean.GameBeenSoldBean;
import com.bber.company.android.view.activity.PayInputMoneyActivity;


public class GameNoMoneyDialog extends Dialog {

    private Context context;
    private String title;
    private String confirmButtonText;
    private String cacelButtonText;
    private int position;
    private GameBeenSoldBean gameBeenSoldBean;

    public GameNoMoneyDialog(Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
    }

    public GameNoMoneyDialog(Context context, int position) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.position = position;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alert_game_nomoney_image, null);
        setContentView(view);
        Button button_cancal = view.findViewById(R.id.button_cancal);
        Button button_sure = view.findViewById(R.id.button_sure);
        button_cancal.setOnClickListener(new clickListener());
        button_sure.setOnClickListener(new clickListener());


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
                case R.id.button_sure:
                    Intent intent = new Intent(context, PayInputMoneyActivity.class);
                    context.startActivity(intent);
                    dismiss();
                    break;
                case R.id.button_cancal:
                    dismiss();
                    break;
            }
        }

    }

}