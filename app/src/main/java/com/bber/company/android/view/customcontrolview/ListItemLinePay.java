package com.bber.company.android.view.customcontrolview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bumptech.glide.Glide;

/**
 * Created by carlo.c on 2018/3/28.
 */

public class ListItemLinePay extends RelativeLayout {

    private Context context;

    public ListItemLinePay(Context context) {
        super(context);
        this.context = context;
    }

    public ListItemLinePay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public ListItemLinePay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(String imageurl, String name, boolean isSelect) {

        TextView nametext = findViewById(R.id.payinputlistviewnameid);
        ImageView apyicon = findViewById(R.id.payinputlistviewinconid);
        TextView apycheck = findViewById(R.id.payinputlistviewxuanid);
        nametext.setText(name);
        Glide.with(context).load(imageurl)/*.error(R.mipmap.icon_pay)*/.into(apyicon);

        if (isSelect) {
            apycheck.setBackgroundResource(R.mipmap.icon_choose);
        } else {
            apycheck.setBackgroundResource(R.mipmap.icon_no_choose);

        }

    }

}
