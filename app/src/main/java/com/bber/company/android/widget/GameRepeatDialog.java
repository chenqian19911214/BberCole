package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.GameBeenSoldBean;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.facebook.drawee.view.SimpleDraweeView;


public class GameRepeatDialog extends Dialog {

    private Context context;
    private String title;
    private String confirmButtonText;
    private String cacelButtonText;
    private int position;
    private GameBeenSoldBean gameBeenSoldBean;

    public GameRepeatDialog(Context context, GameBeenSoldBean gameBeenSoldBean) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.gameBeenSoldBean = gameBeenSoldBean;
    }

    public GameRepeatDialog(Context context, int position) {
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
        View view = inflater.inflate(R.layout.alert_game_repeat_image, null);
        setContentView(view);


        LinearLayout dismissButton = view.findViewById(R.id.dismiss_lin);
        SimpleDraweeView userHead = view.findViewById(R.id.user_head);
        TextView number_buyer = view.findViewById(R.id.number_buyer);
        TextView username = view.findViewById(R.id.username);
        TextView ubmoney = view.findViewById(R.id.ubmoney);
        TextView location_text = view.findViewById(R.id.location_text);

        String result = String.format(context.getResources().getString(R.string.game_buyuser), gameBeenSoldBean.betNumber);
        number_buyer.setText(result);

        String path = (String) SharedPreferencesUtils.get(context, "IMAGE_FILE", "");
        Uri uri = Uri.parse(path + gameBeenSoldBean.bHeadm);
        userHead.setImageURI(uri);
        username.setText(gameBeenSoldBean.bName);
        ubmoney.setText("车龄" + gameBeenSoldBean.driveDay + "天");
        location_text.setText(gameBeenSoldBean.bLocation);
        dismissButton.setOnClickListener(new clickListener());
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
                case R.id.dismiss_lin:
                    dismiss();
                    break;
            }
        }

    }

}