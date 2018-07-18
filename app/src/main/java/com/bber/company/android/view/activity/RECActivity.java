package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.view.customcontrolview.MovieRecorderView;

import java.util.Random;

public class RECActivity extends Activity implements View.OnClickListener {
    private String videoPath = Environment.getExternalStorageDirectory().getPath() + "/bber.mp4";
    /**
     * 是否正在录制true录制中 false未录制
     */
    private MovieRecorderView movieRV;
    private ImageView start;
    private TextView codeNum;
    private int uSellerID;
    private String usVideonumber, videoURL;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initViews();
        setListeners();
        initData();
    }


    private void initViews() {
        // TODO Auto-generated method stub
        movieRV = findViewById(R.id.moive_rv);
        start = findViewById(R.id.start);
        codeNum = findViewById(R.id.codeNum);

    }

    private void setListeners() {
        start.setOnClickListener(this);
    }

    private void initData() {
        //  videoURL = getIntent().getStringExtra("videoURL");
        // uSellerID = getIntent().getIntExtra(Constants.SELLERID, -1);
        usVideonumber = new Random().nextInt(9999 - 1000 + 1) + 1000 + "";//生成4位随机数字
        String strNum = usVideonumber.substring(0, 1) + " " + usVideonumber.substring(1, 2)
                + " " + usVideonumber.substring(2, 3) + " " + usVideonumber.substring(3, usVideonumber.length());
        codeNum.setText(strNum);
        Log.e("eeeeeeeeeeeeeeeeeeee", "------usVideonumber:" + usVideonumber);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start:
                //start.setVisibility(View.GONE);
                movieRV.record(new MovieRecorderView.OnRecordFinishListener() {
                    @Override
                    public void onRecordFinish() {

                        Intent intent = new Intent(RECActivity.this, LiveBroadcastAuthenticationPhotoActivity.class);
                        //  intent.putExtra(Constants.SELLERID, uSellerID);
                        //  intent.putExtra("videoURL", videoURL);
                        intent.putExtra("usVideonumber", usVideonumber);
                        startActivity(intent);
                        finish();
                    }
                });
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieRV.stop();

    }


}
