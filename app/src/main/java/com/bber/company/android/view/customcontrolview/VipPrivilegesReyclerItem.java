package com.bber.company.android.view.customcontrolview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;

/**
 * Created by carlo.c on 2018/3/29.
 */

public class VipPrivilegesReyclerItem extends LinearLayout {

    private Context context;

    public VipPrivilegesReyclerItem(Context context) {
        super(context);
        this.context = context;

    }

    public VipPrivilegesReyclerItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public VipPrivilegesReyclerItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    public void init(int imageurl, String name, boolean isSelect) {

        TextView nametext = findViewById(R.id.vip_priviler_reyclerview_item_text_id);
        ImageView apyicon = findViewById(R.id.vip_priviler_reyclerview_item_image_id);
        LinearLayout apycheck = findViewById(R.id.linearlayouttopid);
        nametext.setText(name);
        apyicon.setBackgroundResource(imageurl);
        if (isSelect) {
            apycheck.setBackgroundResource(R.mipmap.viprivileges_select);
        } else {
            //apycheck.setBackgroundResource(R.mipmap.icon_no_choose);

        }

    }
}
