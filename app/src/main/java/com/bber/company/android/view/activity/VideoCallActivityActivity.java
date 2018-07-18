package com.bber.company.android.view.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.SocketHelp;
import com.bber.company.android.view.customcontrolview.InvertedTimeView;
import com.bber.company.android.view.customcontrolview.TimeView;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;


/**
 * 视频聊天 窗口
 * */
public class VideoCallActivityActivity extends BaseAppCompatActivity implements View.OnClickListener, SocketHelp.SocketClientDelegateData {

    private RtcEngine mRtcEngine;
    private ImageView vido_suspend;
    private ImageView vido_outside;
    private ImageView vido_check_camera;
    private ImageView video_stop;
    private String channelName, appid, sellBuyerId;
    /**
     * 对方id
     */
    private String usetid;
    private TextView watermark;
    private TimeView Timertext;
    private SocketHelp socketHelp;
    private String token;
    private int broadecast_level;
    private InvertedTimeView inverted_time;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        //远端视频接收解码回调
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {

            ChenQianLog.i("VideoCall视频接收解码回调:" + uid);
            // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
            Timertext.setVisibility(View.VISIBLE);
            Timertext.reStart();
            String startDate = getStrtSocketLogData();
            if (broadecast_level != 3) {//非主播请求扣费

                ChenQianLog.i("请求扣费次数:");
             //   requstSocketService(startDate);//开始请求计费
            } else { //主播改变自己的状态
                uploadAnchor(1);
            }

            inverted_time.setVisibility(View.VISIBLE);
            inverted_time.reStart(100);
        }

        //加入频道回调
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);

            ChenQianLog.i("VideoCall入频道回调:" + uid);

        }


        //其他用户离开当前频道回调
        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7

            ChenQianLog.i("VideoCall用户离开当前频道回调:" + uid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        //其他用户已停发/已重发视频流回调
        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10

            ChenQianLog.i("VideoCall其他用户已停发/已重发视频流回调:" + uid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }

        //错误回调
        @Override
        public void onError(int err) {
            super.onError(err);

            ChenQianLog.i("错误回调:" + err);
        }

        //警告回调
        @Override
        public void onWarning(int warn) {
            super.onWarning(warn);
            ChenQianLog.i("警告回调:" + warn);

        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_video_call_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("broadcest_setvice");
        registerReceiver(broadcastReceiver, intentFilter);

        appid = getResources().getString(R.string.agora_app_id);
        channelName = getIntent().getStringExtra("channelName");
        usetid = getIntent().getStringExtra("userid");
        sellBuyerId = getIntent().getStringExtra("sellBuyerId");
        token = (String) SharedPreferencesUtils.get(getApplicationContext(), com.bber.company.android.constants.Constants.BROADCAST_SETVICE_TOKEN, "");
        // sign = (String) SharedPreferencesUtils.get(getApplicationContext(), com.bber.company.android.constants.Constants.BROADCAST_SETVICE_SIGN, "");
        initView();

        broadecast_level = (int) SharedPreferencesUtils.get(getApplicationContext(), com.bber.company.android.constants.Constants.BROADCAST_LEVEL, 0);

        inintLinener();
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel();
        }

      /*  全屏
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//API19(android4.4)以上才有沉浸式
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
    }

    private void requstSocketService(String message) {
        socketHelp = SocketHelp.initService(getApplication());
        if (!socketHelp.getSocketConnectState()) {
            //10.60.170.165 30000
            //10.60.2.6 8822
            socketHelp.connenctSocket("10.60.170.165", 8822);
        }
        if (socketHelp != null) {
            socketHelp.sendMessage(message, this);
        }
    }

    /**
     * 请求扣费的参数
     */
    public String getStrtSocketLogData() {
        JSONObject jsonObject = new JSONObject();
        if (token != null) {
            String datawei = "token=" + token + "#type=2842abfcf808c32877ac9186f240e3b41";
            String sign = Tools.md5(datawei);
            try {
                jsonObject.put("token", token);
                jsonObject.put("type", "2");
                jsonObject.put("sign", sign);
                jsonObject.put("sellBuyerId", sellBuyerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 停止扣费的参数
     */
    public String getStopSocketLogData(int flag) {
        JSONObject jsonObject = new JSONObject();


        if (token != null) {
            String datawei = "token=" + token + "#type=3842abfcf808c32877ac9186f240e3b41";
            String sign = Tools.md5(datawei);
            try {
                jsonObject.put("token", token);
                jsonObject.put("type", "3");
                jsonObject.put("sign", sign);
                jsonObject.put("sellBuyerId", sellBuyerId);
                jsonObject.put("flag", flag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.makeTextAnim(getApplicationContext(), msg, 0, R.style.PopToast).show();
            }
        });
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // 初始化 视频通话
        setupVideoProfile();         // 打开视频模式
        setupLocalVideo();           // 创建本地视频预览
        joinChannel();               // 建立连接
    }

    /**
     * 初始化 视频通话
     */
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appid, mRtcEventHandler);
            mRtcEngine.setChannelProfile(0);
            mRtcEngine.enableAudio();
            mRtcEngine.enableVideo();

        } catch (Exception e) {
            ChenQianLog.i("初始化 " + Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    /**
     * 设置本地视频 属性
     */
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);//设置本地视频属性
        //  mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);//设置本地视频属性

        mRtcEngine.setEncryptionSecret("123456");
        mRtcEngine.setEncryptionMode("aes-128-xts");
        mRtcEngine.createDataStream(true, true);

    }

    /**
     * 创建渲染图层
     */
    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(this); //创建渲染视图
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));//设置本地视频显示属性
        mRtcEngine.startPreview();
    }

    /**
     * 加入频道
     */
    private void joinChannel() {

        /**
         * s  token
         * chenqian
         * s1 channelName 标识通话的频道名称
         *
         * s2 optionalInfo 开发者需加入的任何附加信息。一般可设置为空字符串，或频道相关信息
         *
         * i optionalUid  用户ID，32位无符号整数。建议设置范围：1到(2^32-1)，并保证唯一性。如果不指定（即设为0），SDK 会自动分配一个，并在 onJoinChannelSuccess 回调方法中返回，App 层必须记住该返回值并维护，SDK不对该返回值进行维护
         uid 在 SDK 内部用 32 位无符号整数表示，由于 Java 不支持无符号整数，uid 被当成 32 位有符号整数处理，对于过大的整数，Java 会表示为负数，如有需要可以用(uid&0xffffffffL)转换成 64 位整数
         * */
        String token = (String) SharedPreferencesUtils.get(this, "agorakoken", "");

        if (channelName != null && token != null) {
            mRtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you

            String SdkVersion = mRtcEngine.getSdkVersion();
            ChenQianLog.i("  加入频道成功 SdkVersion:" + SdkVersion);
        }
        ChenQianLog.i("房间Name:" + channelName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.stopPreview();
        mRtcEngine.leaveChannel(); //结束通话
        RtcEngine.destroy();
        mRtcEngine = null;

    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    /**
     * 其他用户已停发/已重发视频流回调
     *
     * @param uid   用户uid
     * @param muted true 回复  false  停发
     */
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);

        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 用户离开 销毁显示对方视图
     */
    private void onRemoteUserLeft() {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
        // View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        // tipMsg.setVisibility(View.VISIBLE);
    }

    /**
     * 显示 对方信息
     */
    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

        surfaceView.setTag(uid); // for mark purpose
        // View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        // tipMsg.setVisibility(View.GONE);

    }

    private void inintLinener() {

        vido_suspend.setOnClickListener(this);
        vido_outside.setOnClickListener(this);
        vido_check_camera.setOnClickListener(this);
        video_stop.setOnClickListener(this);
    }


    private void initView() {
        vido_suspend = (ImageView) findViewById(R.id.vido_suspend);
        vido_outside = (ImageView) findViewById(R.id.vido_outside);
        vido_check_camera = (ImageView) findViewById(R.id.vido_check_camera);
        video_stop = (ImageView) findViewById(R.id.video_stop);

        watermark = findViewById(R.id.wanqianmei_text_id);
        Timertext = findViewById(R.id.quick_tips_when_use_agora_sdk);
        inverted_time = findViewById(R.id.inverted_time_id);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.vido_suspend: //暂停

                break;
            case R.id.vido_outside: //声音切换

                if (vido_outside.isSelected()) {
                    vido_outside.setSelected(false);
                    // mRtcEngine.setEnableSpeakerphone(true);
                    vido_outside.setImageResource(R.drawable.vido_outside_on);

                } else {
                    vido_outside.setSelected(true);

                    vido_outside.setImageResource(R.drawable.vido_outside_off);
                }
                mRtcEngine.setEnableSpeakerphone(vido_outside.isSelected());
                break;
            case R.id.vido_check_camera: //切换摄像头
                mRtcEngine.switchCamera();
                break;
            case R.id.video_stop: //停止
                if (broadecast_level == 3) { //主播 挂断
                    anchorCloseVideoRequest();
                } else { // 自己挂断
                   // requstSocketService(getStopSocketLogData(3));
                    guestCloseVideoRequest();
                }
                break;
        }
    }

    /**
     * 客户关闭视频请求
     */
    private void guestCloseVideoRequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", "");
            jsonObject.put("messageType", com.bber.company.android.constants.Constants.MKAgoraMessageTypeGuestCloseVideo);
            jsonObject.put("ubHeadm", "");
            jsonObject.put("unName", "");
            // jsonObject.put("createDate", getDateToString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyApplication.getContext().getmAgoraAPI().messageInstantSend(usetid, 0, jsonObject.toString(), com.bber.company.android.constants.Constants.MKAgoraMessageTypeGuestCloseVideo + "");
        finish();
    }

    /**
     * 主播关闭视频请求
     */
    private void anchorCloseVideoRequest() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", "");
            jsonObject.put("messageType", com.bber.company.android.constants.Constants.MKAgoraMessageTypeAnchorCloseVideo);
            jsonObject.put("ubHeadm", "");
            jsonObject.put("unName", "");
            // jsonObject.put("createDate", getDateToString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyApplication.getContext().getmAgoraAPI().messageInstantSend(usetid, 0, jsonObject.toString(), com.bber.company.android.constants.Constants.MKAgoraMessageTypeAnchorCloseVideo + "");
        uploadAnchor(3); //修改状态
        // finish();
    }

    /**
     * 更新主播状态
     */
    private void uploadAnchor(final int status) {

        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("flag", 2);
        params.put("status", status);
        String uploadAnchoryui = com.bber.company.android.constants.Constants.getInstance().uploadAnchor;
        HttpUtil.post(uploadAnchoryui, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                ChenQianLog.i("修改主播状态json:");
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);

                        String resultMessage = jsonObject.getString("resultMessage");
                        ChenQianLog.i("修改主播状态json:" + requst);

                        if (jsonObject.getInt("resultCode") == 1) {

                            if (status == 3) {
                                finish();
                            }

                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                try {
                    ChenQianLog.i("更新主播状态失败：" + new String(bytes, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                // DialogView.dismiss(LiveBroadcastDetailsActivity.this);
            }
        });
    }

    /**
     * 广播接收器
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("broadcest_setvice")) {

                String type = intent.getStringExtra("state");
                switch (type) {
                    case "anchor_close_video": //主播结束通话
                        MyToast.makeTextAnim(getApplicationContext(), "主播已经结束本次通话", 0, R.style.PopToast).show();
                      //  requstSocketService(getStopSocketLogData(3));
                        finish();
                        break;
                    case "guest_close_video"://客户结束通话
                        MyToast.makeTextAnim(getApplicationContext(), "客户已经结束本次通话", 0, R.style.PopToast).show();
                        uploadAnchor(3); //修改状态
                        //  finish();
                        break;
                }
            }
        }
    };

    @Override
    public void sendSuccess(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            int resultCode = jsonObject.getInt("resultCode");
            if (resultCode != 0) {
                int type = jsonObject.getInt("type");
                switch (type) {
                    case 2:  //请求扣费
                        ChenQianLog.i("视频聊天 开始扣费回调：" + message);
                        break;
                    case 3:// 结束扣费
                        ChenQianLog.i("视频聊天 结束扣费回调：" + message);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
