package com.bber.company.android.view.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.VideoErrorDialog;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 *
 * */
public class StreamingMediaVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener
        , SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    //surfaceView
    private SurfaceView surfaceView;
    //视频最外层layout
    private RelativeLayout videoLayout;
    //控制台layout
    private LinearLayout controlLayout;
    //播放、全屏button
    private ImageButton playBtn, screenBtn;

    private ImageView image_meng_id, back_imageview_id;

    //进度条
    private SeekBar seekBar;
    //加载视频进度progressBar
    //private ProgressBar progressBar;
    //当前时间，总时间 ,视频名称
    private TextView currTime, countTime, video_name;
    //surface holder
    private SurfaceHolder mHolder;
    //媒体控制 mediaPlayer
    private MediaPlayer mediaPlayer;
    //是否全屏
    private boolean isFullScreen = false;
    //是否正在播放
    private boolean isPlay = false;
    //控制台是否显示
    private boolean isControl = false;
    //是否正在拖动seekBar
    private boolean isSetProgress = false;
    //是否播放完成
    private boolean isPlayCom = false;
    //是否是第一次加载视频
    private boolean isFirstLoadVideo = true;
    //是否销毁activity
    private boolean isOnDestroy = false;
    //是否可见
    private boolean isPause = false;
    //媒体音量管理
    private AudioManager audioManager;
    //点击纵坐标
    private float dY = 0;
    //点击横坐标
    private float dX = 0;
    //抬起纵坐标
    private float uY = 0;
    //抬起横坐标
    private float uX = 0;
    //屏幕当前亮度
    private float f = 0;
    //手机当前亮度模式 0 1
    private int countLight;
    //系统当前亮度 1-255
    private int currLight;

    private static final int HIDE_CONTROL_LAYOUT = -1;
    //这个地址是我抓的某平台的，我发现这个地址是变化的，所以有可能不能使用，如果不能播放，换个正常的就可以运行了，不要用模拟器运行
    // private static String VIDEO_URL = "http://110.53.223.51/staticResource/hls_vipZone/A3661/A3661.m3u8";
    private static String VIDEO_URL, namevideotext, imageurl;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HIDE_CONTROL_LAYOUT) {
                refreshControlLayout();
            } else {
                currTime.setText(formatTime(msg.what));
                seekBar.setProgress(msg.what);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_media_video);

        Vitamio.isInitialized(getApplicationContext());
        VIDEO_URL = getIntent().getStringExtra("url");
        //VIDEO_URL = "http://10.60.2.15:1888/staticResource//talkPicture/3067/1526637587582M1Dark.m3u8";
        namevideotext = getIntent().getStringExtra("name");
        imageurl = getIntent().getStringExtra("source");
        initView();
        initVideoSize();
        initSurface();
        setListener();
        ChenQianLog.i("VIDEO_URL:" + VIDEO_URL);
    }

    private void initView() {
        surfaceView = findViewById(R.id.surface_view);
        controlLayout = findViewById(R.id.control_layout);
        playBtn = findViewById(R.id.playBtn);
        screenBtn = findViewById(R.id.screenBtn);
        seekBar = findViewById(R.id.seekBar);
        video_name = findViewById(R.id.video_name);
        //  progressBar = (ProgressBar) findViewById(R.id.load_bar);
        currTime = findViewById(R.id.curr_time);
        countTime = findViewById(R.id.count_time); //image_meng_id
        image_meng_id = findViewById(R.id.image_meng_id);
        back_imageview_id = findViewById(R.id.back_imageview_id);
        mHolder = surfaceView.getHolder();
        mediaPlayer = new MediaPlayer(StreamingMediaVideoActivity.this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //加载视频名称
        if (!TextUtils.isEmpty(namevideotext)) {
            video_name.setText(namevideotext);
        }
        //如果是声优 则加载海报
        if (!TextUtils.isEmpty(imageurl)) {
            image_meng_id.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(imageurl).into(image_meng_id);
            screenBtn.setEnabled(false);
        }
        DialogView.show(this, true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initScreenLight();
        refreshControlLayout();
    }

    //初始化屏幕亮度
    private void initScreenLight() {
        try {
            //获取亮度模式 0：手动 1：自动
            countLight = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            //设置手动设置
            //  Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            //获取屏幕亮度,获取失败则返回255
            currLight = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    255);
            f = currLight / 255f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新控制台 显示则隐藏 隐藏则显示 并5S之后隐藏
     */
    private void refreshControlLayout() {
        if (isControl) {
            controlLayout.setVisibility(View.INVISIBLE);
            isControl = false;
        } else {
            controlLayout.setVisibility(View.VISIBLE);
            isControl = true;
            handler.removeMessages(HIDE_CONTROL_LAYOUT);
            handler.sendEmptyMessageDelayed(HIDE_CONTROL_LAYOUT, 5000);
        }
    }

    private void setListener() {
        playBtn.setOnClickListener(this);
        screenBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        back_imageview_id.setOnClickListener(this);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = event.getX();
                        dY = event.getY();
                        refreshControlLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isFullScreen) {
                            /*uY = event.getY();
                            if (dX > getWidth() / 2) {//声音控制
                                if (Math.abs(uY - dY) > 25)
                                    setVolume(uY - dY);
                            } else if (dX <= getWidth() / 2) {//亮度控制
                                setLight(dY - uY);
                            }*/
                        }
                        break;
                }
                return true;
            }
        });

    }

    //手势调节音量
    private void setVolume(float vol) {
        if (vol < 0) {//增大音量
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        } else if (vol > 0) {//降低音量
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }

    /**
     * 手势设置屏幕亮度
     * 设置当前的屏幕亮度值，及时生效 0.004-1
     * 该方法仅对当前应用屏幕亮度生效
     */
    private void setLight(float vol) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        f += vol / getWidth();
        if (f > 1) {
            f = 1f;
        } else if (f <= 0) {
            f = 0.004f;
        }
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    //初始化surfaceView
    private void initSurface() {
        //设置回调参数
        mHolder.addCallback(this);
        //设置SurfaceView自己不管理的缓冲区
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mHolder.setFormat(PixelFormat.RGBA_8888);
        //显示的分辨率,不设置为视频默认
//        mHolder.setFixedSize(320, 220);
    }


    private void playUrl(String url) {
        try {
            //使mediaPlayer重新进入ide状态
            mediaPlayer.reset();
            //设置媒体类型
            // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            //将影像输出到surfaceView
            mediaPlayer.setDisplay(mHolder);
            //设置 视频资源 可以是本地视频 也可是网络资源
//            mediaPlayer.setDataSource("/storage/sdcard1/DCIM/Camera/VID_20160629_164144.mp4");
            mediaPlayer.setDataSource(url);

            //同步准备
//            mediaPlayer.prepare();
            //因为是网络视频 这里用异步准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化视频显示的大小
    private void initVideoSize() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = getWidth() / 10 * 6;
        surfaceView.setLayoutParams(params);
    }

    //surfaceView创建完成
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("TAG", "surfaceCreated");
        //等surfaceView创建完成再开始播放视频
        if (!isPause) {
            playUrl(VIDEO_URL);
        } else {
            isPause = false;
            mediaPlayer.setDisplay(holder);
            if (isPlay) mediaPlayer.start();
        }
    }

    //surfaceView改变
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("TAG", "surfaceChanged");
    }

    //surfaceView销毁
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("TAG", "surfaceDestroyed");
    }

    @Override
    public void onClick(View v) {
        isControl = false;
        refreshControlLayout();
        if (isFirstLoadVideo) {
            return;
        }
        switch (v.getId()) {
            case R.id.playBtn:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPlay = false;
                    playBtn.setBackgroundResource(R.mipmap.play);
                } else if (isPlayCom) {
                    mediaPlayer.seekTo(0);
                    isPlay = true;
                    isPlayCom = false;
                    playBtn.setBackgroundResource(R.mipmap.pause);
                } else {
                    mediaPlayer.start();
                    isPlay = true;
                    playBtn.setBackgroundResource(R.mipmap.pause);
                }
                break;
            case R.id.screenBtn:
                if (isFullScreen) {
                    smallScreen();
                    screenBtn.setBackgroundResource(R.mipmap.large_screen);
                } else {
                    fullScreen();
                    screenBtn.setBackgroundResource(R.mipmap.small_screen);
                }
                break;

            case R.id.back_imageview_id:

                finish();
                break;
        }
    }

    //横竖屏切换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("TAG", "当前屏幕为横屏");
            isFullScreen = true;
            fullScreen();
            screenBtn.setBackgroundResource(R.mipmap.small_screen);
        } else {
            Log.e("TAG", "当前屏幕为竖屏");
            isFullScreen = false;
            smallScreen();
            screenBtn.setBackgroundResource(R.mipmap.large_screen);
        }
        super.onConfigurationChanged(newConfig);
    }

    //全屏
    private void fullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        showFullSurface();
    }

    //竖屏
    private void smallScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        showSmallSurface();
    }

    private void showFullSurface() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(params);
    }

    private void showSmallSurface() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = getWidth() / 10 * 6;
        surfaceView.setLayoutParams(params);
    }

    //进度改变
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currTime.setText(formatTime(seekBar.getProgress()));
        if (isSetProgress) {
            Log.e("TAG", "onProgressChanged:refreshControlLayout");
            isControl = false;
            refreshControlLayout();
        }
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        currTime.setText(formatTime(seekBar.getProgress()));
        isSetProgress = true;
        isControl = false;
        refreshControlLayout();
    }

    //停止拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSetProgress = false;
        isControl = false;
        refreshControlLayout();
        if (isFirstLoadVideo) {
            return;
        }
        mediaPlayer.seekTo(seekBar.getProgress());
        currTime.setText(formatTime(seekBar.getProgress()));
    }

    public int getWidth() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    //更新进度
    private void updateSeekBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isOnDestroy) { //结束线程标示

                    if (isPlay && !isPause) {
                        try {
                            Message message = new Message();
                            message.what = (int) mediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Log.e("TAG", "while");
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    //播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
//        Log.e("TAG", "播放完成");
        playBtn.setBackgroundResource(R.mipmap.play);
        isPlay = false;
        isPlayCom = true;
        isControl = false;
        Message message = new Message();
        message.what = (int) mediaPlayer.getDuration();
        handler.sendMessage(message);
        refreshControlLayout();
    }

    //播放出错
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        isPlay = false;
        DialogView.dismiss(StreamingMediaVideoActivity.this);
        try {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.video_error_alertdialog, null, true);

            final VideoErrorDialog videoErrorDialog = new VideoErrorDialog(StreamingMediaVideoActivity.this, view);

            view.findViewById(R.id.button_sure_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoErrorDialog.dismiss();
                    finish();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String formatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(time);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e("TAG", "onBufferingUpdate" + ",percent:" + percent);
    }

    //准备完成
    @Override
    public void onPrepared(MediaPlayer mp) {
        //设置最大进度
        seekBar.setMax((int) mediaPlayer.getDuration());
        //设置按钮背景图片
        playBtn.setBackgroundResource(R.mipmap.pause);
        //设置视频最大时间
        countTime.setText(formatTime(mediaPlayer.getDuration()));
        //隐藏加载进度条
        //  progressBar.setVisibility(View.INVISIBLE);
        DialogView.dismiss(StreamingMediaVideoActivity.this);
        //开始播放
        mediaPlayer.start();
        //更改播放状态
        isPlay = true;
        //更改状态
        if (isFirstLoadVideo)
            isFirstLoadVideo = false;
        //开启线程更新进度
        updateSeekBar();
    }

    @Override
    protected void onDestroy() {
        Log.e("TAG", "onDestroy");
        isOnDestroy = true;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            isPlay = false;
        }
        mediaPlayer.release();
        super.onDestroy();
    }

    //seekTo()是异步的方法 在此监听是否执行完毕
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.e("TAG", "onSeekComplete");
        if (!isPlay) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }

    }

    //监听返回键 如果是全屏状态则返回竖屏 否则直接返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFullScreen) {
                smallScreen();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        Log.e("TAG", "onPause");
        isPause = true;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause && isPlay && mHolder.getSurface().isValid()) {
            isPause = false;
            mediaPlayer.start();
        }
    }
}
