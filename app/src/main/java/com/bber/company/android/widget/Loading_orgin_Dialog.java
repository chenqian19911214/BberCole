package com.bber.company.android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.listener.IactionListener;

/**
 * 加载中Dialog
 *
 * @author lexyhp
 */
public class Loading_orgin_Dialog extends AlertDialog {

    public boolean isRunning = true; // 一直是运行的状态
    public IactionListener iactionListener;
    private TextView tips_loading_msg;
    private ImageView image_gif;
    private int layoutResId;
    private String message = null;
    private Context mContext;
    private Handler MyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    iactionListener.SuccessCallback("");
                    dismiss();
                    break;
            }
        }
    };

    /**
     * 构造方法
     *
     * @param context    上下文
     * @param imageResId 要传入的dialog布局文件的id
     */
    public Loading_orgin_Dialog(Context context, String text) {
        super(context);
        mContext = context;
//        message = context.getResources().getString(R.string.loading);
        message = text;
    }

    public void startToMiss(final int time) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Message message_ = new Message();
                message_.what = 1;
                MyHandler.sendMessage(message_);


            }
        }).start();
    }

    public void stopThread(boolean stop) {
        isRunning = stop;
    }

    public void setLinstener(IactionListener iactionListener) {
        this.iactionListener = iactionListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_tips_loadings);
        tips_loading_msg = findViewById(R.id.tips_loading_msg);
        image_gif = findViewById(R.id.image_gif);
        tips_loading_msg.setText(this.message);
    }

}
