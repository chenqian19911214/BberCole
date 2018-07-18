package com.bber.company.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.bean.GameGirlsBean;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by bn on 2017/7/14.
 */

public class ImageMoveView extends LinearLayout {
    Thread thread;
    private Paint mPaint;
    private boolean isRunning = false;//表示运行状态默认处于开启
    private int frist = 0;
    private ImageView imageView;
    private Context context;
    private List<GameGirlsBean> lightGirlsPicList;
    private String path;
    private Handler MyHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Glide.with(context).load(path + lightGirlsPicList.get(frist).imgPath).dontAnimate().into(imageView);
                    postInvalidate();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (!isRunning) {
                frist++;
                if (frist >= 8) {
                    frist = 0;
                }
                Message message = new Message();
                message.what = 1;
                MyHandler.sendMessage(message);

//                txt_ft=frist+"";

                try {
                    Thread.sleep(150);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public ImageMoveView(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.view_model_move, this);

        setupView();
    }

    public ImageMoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.view_model_move, this);
        setupView();
    }

    public ImageMoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate(context, R.layout.view_model_move, this);
        setupView();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageMoveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        inflate(context, R.layout.view_model_move, this);
        setupView();
    }

    public void setUriData(List<GameGirlsBean> lightGirlsPicList) {
        this.lightGirlsPicList = lightGirlsPicList;
    }

    public void setbgnull() {
        Glide.with(context).load(R.drawable.icon_null).dontAnimate().into(imageView);
    }

    public void startChange() {
        isRunning = false;
        thread = new Thread(runnable);
        thread.start();
    }

    public void stopChange(int position) {
        isRunning = true;
        if (position != -1) {
            Glide.with(context).load(path + lightGirlsPicList.get(position).imgPath).dontAnimate().into(imageView);
        }
    }

    private void setupView() {
        imageView = findViewById(R.id.change_photo);
        path = (String) SharedPreferencesUtils.get(context, "IMAGE_FILE", "");

    }
}
