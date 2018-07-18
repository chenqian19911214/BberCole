package com.bber.company.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bumptech.glide.Glide;

/**
 * Created by jeanboy on 2017/4/20.
 */

public class PanelItemView extends FrameLayout implements ItemView {

    public boolean isChoose = false;
    private View overlay;
    private ImageView choose_text, model_image;
    private TextView username;
    private Drawable modelDraw;
    private Context mContext;

    public PanelItemView(Context context) {
        this(context, null);
    }

    public PanelItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inflate(context, R.layout.view_panel_item, this);
        overlay = findViewById(R.id.overlay);
        choose_text = findViewById(R.id.choose_text);
        model_image = findViewById(R.id.model_image);
        username = findViewById(R.id.choose_name);
    }

    @Override
    public void setFocus(boolean isFocused) {
        if (overlay != null) {
            overlay.setVisibility(isFocused ? INVISIBLE : VISIBLE);
        }
    }

    public void setYellow() {
        username.setTextColor(getResources().getColor(R.color.text_name));
    }

    public void setPurper() {
        username.setTextColor(getResources().getColor(R.color.text_purper));
    }

    public void setModelBack(String uri) {
//        model_image.setBackground(drawable);
        isChoose = true;
        Glide.with(mContext).load(uri).dontAnimate().into(model_image);
    }

    public void setModelNumber(Drawable uri) {
//        model_image.setBackground(drawable);
        username.setBackground(uri);
    }

    public void setOrgialModel(int uri) {
//        model_image.setBackground(drawable);
        isChoose = false;
        Glide.with(mContext).load(uri).dontAnimate().into(model_image);
    }

    public void setAlpha_(float alpha) {
        overlay.setAlpha(alpha);
    }

    public void setUseName(String name) {
//        model_image.setBackground(drawable);
        username.setText(name);
    }

    public void setChooseTrue() {
        isChoose = true;
//        choose_text.setVisibility(View.VISIBLE);
    }

    public void setChooseFlase() {
        isChoose = false;
//        choose_text.setVisibility(View.GONE);
    }

}
