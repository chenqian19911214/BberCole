package com.bber.company.android.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.FragmentVipBinding;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.webViewListener;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.JavaScriptUtils;
import com.bber.company.android.util.PingUtil;
import com.bber.company.android.view.activity.LandscapeActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.widget.FullScreenVideoView;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Vencent
 * @date 2017/3/10
 * @description 侧边栏菜单
 */
@SuppressLint("SetJavaScriptEnabled")
public class VIPFragment extends BaseFragment implements View.OnClickListener {

    private final Handler handler = new Handler();
    String head;
    private FragmentVipBinding binding;
    private WebView webview;
    private LinearLayout progressbar;
    private String intentUrl;
    private boolean flag = true;
    private RelativeLayout video_rela;
    private FullScreenVideoView videoView;
    private ProgressBar mp4_progressBar;
    //video准备工作监听
    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            videoView.setVideoWidth(mediaPlayer.getVideoWidth());
            videoView.setVideoHeight(mediaPlayer.getVideoHeight());
            mp4_progressBar.setVisibility(View.GONE);
        }
    };
    private TextView btn_close, change_screen;
    private JavaScriptUtils jsUtils;
    private RelativeLayout videolist;
    private MainActivity mActivty;
    //video播放完毕
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            video_rela.setVisibility(View.GONE);
            videolist.setVisibility(View.VISIBLE);
            setVisable(View.VISIBLE);
            if (!isScreenOriatationPortrait(getActivity())) {// 当屏幕是横屏时
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
            }

        }
    };
    private String url;
    private MediaController mediaController;
    private long old_duration;
    private boolean isPlayFlag = false;
    MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (isPlayFlag == false) return true;
            new AlertDialog.Builder(mActivty)
                    .setMessage("抱歉,播放错误")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    stopVideoPlaying();
                                }
                            })
                    .setCancelable(false)
                    .show();
            return true;
        }
    };
    private Runnable runnable;
    private ImageView sur_image;
    private LinearLayout image_linearlayout;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    Intent intent = new Intent(getActivity(), LandscapeActivity.class);
                    intent.putExtra("url", msg.getData().getString("url"));
                    getActivity().startActivity(intent);
                    Constants.isStartActivity = true;

//                    Uri uri = Uri.parse(msg.getData().getString("url"));
//                    videoView.setVideoURI(uri);
//                    video_rela.setVisibility(View.VISIBLE);
//                    videoView.start();
//                    videoView.requestFocus();
//                    videoView.seekTo(0);
//                    videolist.setVisibility(View.GONE);
//                    setVisable(View.GONE);
//                    isPlayFlag = true;
                    break;
                case 2:
                    String uri_ = msg.getData().getString("url");
                    setUserVipZoneUrl(uri_);
                    break;
            }
        }
    };
    private int i = 8;
    private Timer timer = null;//计时器
    private TimerTask timerTask = null;
    //关于计时器的handler
    private Handler gHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //覆盖层显示
            image_linearlayout.setVisibility(View.VISIBLE);
            stopTime();
        }
    };
    private List<String> datalist;
    private List<Float> fastlist;
    private List<Float> finallylist;
    private List<String> comparelist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //找到databinding的控件
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vip, container, false);
//        binding.setHeaderBarViewModel(headerBarViewModel);
        //获取视频列表
        getUserVipZoneUrl();
        getActivity().getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //setheader();
        initView();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //携带参数
        head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());

        //WebView加载web资源
        int level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
        boolean isvirified = (boolean) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.CHECK_PHONE, false);
        String code = (String) SharedPreferencesUtils.get(getActivity(), "code", "0");
        int checkPhone = 0;
        if (isvirified) {
            checkPhone = 1;
        } else {
            checkPhone = 0;
        }
        webview.addJavascriptInterface(jsUtils, "WebViewJavascriptBridge");
        progressbar.setVisibility(View.VISIBLE);
        url = Constants.getInstance().getH5Ip + "head=" + head + "&isvirified=" + checkPhone + "&viplevel=" + level + "&code=" + code;
        LogUtils.e("url = " + url);
//        url = "http://10.14.2.15/mobile/#/moments/?" + "head=" + head + "&isvirified=" + checkPhone + "&viplevel=" + level;
//        url = "https://chat56.live800.com/live800/chatClient/chatbox.jsp?companyID=896572&configID=141991&jid=1734856383&s=1&info=userId%3D17299%26name%3Dabcde%26memo%3D2";
        if (Constants.isStartActivity == false) {
            webview.post(new Runnable() {
                @Override

                public void run() {
                    webview.loadUrl(url);
                    webview.reload(); //url为初始的值
                }
            });
        }
    }

    private void initView() {
        webview = binding.webview;
        videoView = binding.videoView;
        video_rela = binding.videoRela;
        mp4_progressBar = binding.mp4ProgressBar;
        btn_close = binding.btnClose;
        change_screen = binding.changeScreen;
        videolist = binding.videolist;
        progressbar = binding.loading;
        sur_image = binding.surImage;
        image_linearlayout = binding.imageLinearlayout;
        btn_close.setOnClickListener(this);
        change_screen.setOnClickListener(this);
        mActivty = (MainActivity) getActivity();
        video_rela.setVisibility(View.GONE);
        videoView.setOnCompletionListener(completionListener);
        videoView.setOnPreparedListener(preparedListener);
        videoView.setOnErrorListener(errorListener);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        //随后将本地的类（被js调用的）映射出去：
        jsUtils = new JavaScriptUtils(getActivity());
        mediaController = new MediaController(getActivity());
        videoView.setMediaController(mediaController);
        jsUtils.callOpenPlayerBack(new webViewListener() {
            @Override
            public void openPlayer(String jsonString) {
                if (!Tools.isEmpty(jsonString)) {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", jsonString);
                    msg.setData(bundle);
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        });
        jsUtils.setActionListener(new IactionListener() {
            @Override
            public void SuccessCallback(Object o) {
                //如果调用了，就是对的IP，覆盖层消失，计时器消失

                image_linearlayout.setVisibility(View.GONE);
                stopTime();
            }

            @Override
            public void FailCallback(String result) {

            }
        });


        //最重要的方法，一定要设置，这就是出不来的主要原因
        settings.setDomStorageEnabled(true);

        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + "papa");
        //优先使用缓存
//        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //不使用缓存：
//        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

//        webview.loadUrl(url);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                webview.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
                startTime();
            }
        });

        runnable = new Runnable() {
            public void run() {
                int duration = videoView.getCurrentPosition();
                if (old_duration == duration && isPlayFlag == true) {
                    mp4_progressBar.setVisibility(View.VISIBLE);
                } else {
                    mp4_progressBar.setVisibility(View.GONE);
                }
                old_duration = duration;

                handler.postDelayed(runnable, 1000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    /**
     * 开始自动减时
     */
    private void startTime() {
        if (timer == null) {
            timer = new Timer();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                i--;//自动减1
                Message message = new Message();
//                message.arg1=i;
                if (i == 0) {
                    gHandler.sendMessage(message);//发送消息
                }
            }
        };
        if (timer == null) {
            timer.schedule(timerTask, 0, 1000);//1000ms执行一次
        }

    }

    /**
     * 停止自动减时
     */
    private void stopTime() {
        if (timer != null)
            timer.cancel();
    }

    /**
     * 获取屏幕的状态
     *
     * @param context
     * @return
     */
    public boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                stopVideoPlaying();
                break;
            case R.id.change_screen:
                if (isScreenOriatationPortrait(getActivity())) {// 当屏幕是竖屏时
                    // 点击后变横屏
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置当前activity为横屏
                    // 当横屏时 把除了视频以外的都隐藏
                    //隐藏其他组件的代码
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
                    //显示其他组件
                }
                break;
        }
    }

    private void setVisable(int visable) {
        mActivty.setTittleVisable(visable);
    }

    public boolean getVideoPlaying() {
        return isPlayFlag;
    }

    /**
     * 停止播放视频
     */
    public void stopVideoPlaying() {
        video_rela.setVisibility(View.GONE);
        videolist.setVisibility(View.VISIBLE);
        setVisable(View.VISIBLE);
        videoView.pause();
        isPlayFlag = false;
        videoView.stopPlayback();
        if (!isScreenOriatationPortrait(getActivity())) {// 当屏幕是横屏时
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
        }
    }

    /**
     * 获取vip视频地址
     *
     * @param
     */
    public void getUserVipZoneUrl() {
        LogUtils.e("getUserVipZoneUrl开始获取测速地址.....");
        RequestParams params = new RequestParams();
        String head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());
        final JsonUtil jsonUtil = new JsonUtil(getActivity());

        params.put("head", head);
        HttpUtil.post(Constants.getUserVipZoneUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }

                try {
                    final JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    final String resultCode = jsonObject.getString("resultCode");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultCode.equals("1")) {
                                datalist = jsonUtil.jsonToDStringBean(dataCollection.toString());
                                fastlist = new ArrayList<Float>();
                                finallylist = new ArrayList<Float>();
                                comparelist = new ArrayList<String>();
                                fastlist.clear();
                                comparelist.clear();
                                finallylist.clear();
                                HashMap hm = new HashMap();
                                HashMap gm = new HashMap();
                                int index = 0;
                                //测试丢包率的几个url
                                for (int j = 0; j < datalist.size(); j++) {
                                    float a = PingUtil.getPacketLossFloat(datalist.get(j));
                                    if (a != -1) {
                                        index++;
                                        hm.put(index + a, datalist.get(j));
                                        fastlist.add(a);
                                    }
                                }
                                index = 0;
                                float min = Collections.min(fastlist);
                                //收集最低的丢包率的几个url
                                for (int j = 0; j < fastlist.size(); j++) {
                                    if (min == fastlist.get(j)) {
                                        if (fastlist.get(j) != -1) {
                                            index++;
                                            comparelist.add((String) hm.get(index + fastlist.get(j)));
                                        }
                                    }
                                }
                                //测试延迟最低的那个
                                for (int k = 0; k < comparelist.size(); k++) {
                                    float a = PingUtil.getAvgRTT(comparelist.get(k));
                                    if (a != -1) {
                                        gm.put(a, comparelist.get(k));
                                        finallylist.add(a);
                                    }
                                }
                                if (finallylist.size() > 0) {
                                    LogUtils.e("getUserVipZoneUrl测速完成.....测速地址数量为：" + finallylist.size());
                                    float low = Collections.min(finallylist);
                                    LogUtils.e("getUserVipZoneUrl测速延迟最低为：" + low);
                                    String url = (String) gm.get(low);
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("url", url);
                                    msg.setData(bundle);
                                    msg.what = 2;
                                    mHandler.sendMessage(msg);
                                }


//                                setUserVipZoneUrl(url);
                            }
                        }
                    }).start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("getUserVipZoneUrl发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(getActivity(), R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

//    final Handler m_Handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.arg1 == 1){
//                setUserVipZoneUrl(url);
//            }
//        }
//    };

    /**
     * 设置vip视频地址
     *
     * @param
     */
    public void setUserVipZoneUrl(String url) {
        LogUtils.e("setUserVipZoneUrl开始设置最快地址.....");
        RequestParams params = new RequestParams();
//        String head = new JsonUtil(AppManager.getAppManager().currentActivity()).httpHeadToJson(getActivity());
        final JsonUtil jsonUtil = new JsonUtil(getActivity());

        params.put("head", head);
        params.put("url", url);
        HttpUtil.post(Constants.setUserVipZoneUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    if (Tools.jsonResult(AppManager.getAppManager().currentActivity(), jsonObject, null)) {
                        LogUtils.e("jsonObject为空哦");
                        return;
                    }
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("1")) {

                    }
                    LogUtils.e("setUserVipZoneUrl设置最快地址成功.....");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("jsonObject为空哦");
                MyToast.makeTextAnim(getActivity(), R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }
}
