package com.bber.company.android.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.Order;
import com.bber.company.android.bean.Session;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.NettyClientBootstrap;
import com.bber.company.android.service.MsfService;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.StringUtils;
import com.bber.company.android.util.TimeUtil;
import com.bber.company.android.view.fragment.ChatPicFragment;
import com.bber.company.android.view.fragment.GameFragment;
import com.bber.company.android.view.fragment.HomeFragment;
import com.bber.company.android.view.fragment.LeftFragment;
import com.bber.company.android.view.fragment.LiveBroadcastMainFragment;
import com.bber.company.android.view.fragment.MsgFragment;
import com.bber.company.android.view.fragment.VideoFragment;
import com.bber.company.android.widget.ChangeColorIconWithTextView;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.MyViewPager;
import com.bber.company.android.widget.SoundPlayUtils;
import com.bber.customview.utils.LogUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseFragmentActivity implements
        View.OnClickListener, ViewPager.OnPageChangeListener {

    /* 下载包安装路径 */
    private static final String savePath = "/sdcard/download/";
    private static final String saveFileName = savePath + "papa.apk";
    /* 进度条与通知ui刷新的handler和msg常量 */
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private final Timer timer = new Timer();
    public int curFragment = 0;
    private MyViewPager mViewPager;
    private HomeFragment homeFragment;
    private MsgFragment fragmentMsg;
    private LiveBroadcastMainFragment liveBroadcastMainFragment;
    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<>();
    private VideoFragment fragmentme;
    //    private BusinessFragment businessFragment;
    private ChatPicFragment discussPicFragment;
    private GameFragment gameFragment;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private int buyerId;
    private RelativeLayout rl_mainBack;
    private LinearLayout mTittleTab;
    private TextView red_pot;
    private int userTotalKey;
    private SessionDao sessionDao;
    private int mBackgroundRid = R.mipmap.model3;
    private AlphaAnimation alphaAnimation;
    private TransitionDrawable transitionDrawable;
    private int progress = 0;
    private LinearLayout down_load;
    private TextView tv_text;
    private ProgressBar progressBar;
    private Dialog downloadDialog;
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    private Fragment mContent;
    private String startTime = "";
    private String endTime = "";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private TimerTask task;
    private boolean isOnce = false;
    private boolean isOnce1 = false;
    private String apkUrl;
    // 退出时间
    private long mExitTime;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    tv_text.setText(progress + "%");
                    progressBar.setProgress(progress);
//                popup_tv.setText(progress + "%");
//                popup_progress.setProgress(progress);
                    if (progress == 99) {
                        downloadDialog.dismiss();
//                    popWindow.dismiss();
                    }
                    break;
                case DOWN_OVER:
                    down_load.setVisibility(View.GONE);
                    installApk();
                    break;
                default:
                    break;
            }
        }

    };
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                }
                while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverflowShowingAlways();

        initViews();
        AppManager.getAppManager().addActivity(this);
        initSlidingMenu(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            initData();
        } catch (Exception e) {
            LogUtils.e("发送错误信息" + e.getMessage());
            mobilException(e.getMessage());
        }
    }

    /**
     * 设置视频全屏播放的时候，显示头部和导航栏的状态
     * 由下面的fragment进行调用
     */
    public void setTittleVisable(int visibility) {
        mTittleTab.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示状态栏
        }
    }

    private void initViews() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//透明导航栏，就是下面三个虚拟按钮
        mViewPager = (MyViewPager) findViewById(R.id.id_viewpager);
        rl_mainBack = (RelativeLayout) findViewById(R.id.rl_mainBack);
        alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(800);    //深浅动画持续时间
        alphaAnimation.setFillAfter(true);   //动画结束时保持结束的画面

        mTittleTab = (LinearLayout) findViewById(R.id.tittle_tab);
        red_pot = (TextView) findViewById(R.id.red_pot);

//       下载器
        down_load = (LinearLayout) findViewById(R.id.lin_download);
        tv_text = (TextView) findViewById(R.id.tv_text);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        homeFragment = new HomeFragment();
        fragmentMsg = new MsgFragment();
        fragmentme = new VideoFragment();
        discussPicFragment = new ChatPicFragment();
        gameFragment = new GameFragment();
        liveBroadcastMainFragment = new LiveBroadcastMainFragment();
        fragmentme.isInLayout();
        //     businessFragment = new BusinessFragment();

        mTabs.add(homeFragment);
        mTabs.add(fragmentMsg);
        //    mTabs.add(businessFragment);
        mTabs.add(discussPicFragment);
        mTabs.add(fragmentme);
        mTabs.add(gameFragment);
        mTabs.add(liveBroadcastMainFragment);
        AppManager.getAppManager().addActivity(this);
        //更新的界面
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setPagingEnabled(false);
        initTabIndicator();
        sessionDao = new SessionDao(this);
        SoundPlayUtils.init(this);
    }

    private void initTabIndicator() {
        ChangeColorIconWithTextView home = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_home);
        ChangeColorIconWithTextView msg = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_msg);
        //  ChangeColorIconWithTextView business = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_business);
        ChangeColorIconWithTextView me = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_me);
        ChangeColorIconWithTextView discuss = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_discusspic);
        ChangeColorIconWithTextView game = (ChangeColorIconWithTextView) findViewById(R.id.id_game);

        mTabIndicator.add(home);
        mTabIndicator.add(msg);
        //   mTabIndicator.add(business);
        mTabIndicator.add(discuss);
        mTabIndicator.add(me);
        mTabIndicator.add(game);

        home.setOnClickListener(this);
        msg.setOnClickListener(this);
        //   business.setOnClickListener(this);
        me.setOnClickListener(this);
        game.setOnClickListener(this);
        discuss.setOnClickListener(this);
        home.setIconAlpha(1.0f);
    }

    private void initData() {
        buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        //为了保证数据错误，所以每次进来，需要初始化一下变量
        //  SharedPreferencesUtils.put(this, Constants.VIP_ID, 0);
        boolean isLogin = getIntent().getBooleanExtra("isLogin", true);
        //启动Service
        Intent s = new Intent(this, MsfService.class);
        s.putExtra("isLogin", isLogin);
        startService(s);

        //获取版本信息
//        checkVersion();
        //获取手机认证相关信息
        getCheckMobileInfo();
        //获取用户是否可聊天的装填
        getBuyerCanChat();

        getOrderInfo();
        //是否解锁
        initLockGestrue();
        //获取是否开通会员
        getBuyerUserStatus();
    }

    /**
     * 初始化侧边栏
     */
    private void initSlidingMenu(Bundle savedInstanceState) {
        // 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }

        if (mContent == null) {
            mContent = new LeftFragment();
        }

        // 设置左侧滑动菜单
        setBehindContentView(R.layout.menu_frame_left);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new LeftFragment()).commit();

        // 实例化滑动菜单对象
        SlidingMenu sm = getSlidingMenu();
        // 设置可以左右滑动的菜单
        sm.setMode(SlidingMenu.LEFT);
        // 设置滑动阴影的宽度
        sm.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动菜单阴影的图像资源
        sm.setShadowDrawable(null);
        // 设置滑动菜单视图的宽度
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        sm.setFadeDegree(0.35f);
        // 设置触摸屏幕的模式,这里设置为全屏
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        // 设置下方视图的在滚动时的缩放比例
        sm.setBehindScrollScale(0.0f);

    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        switch (v.getId()) {
            case R.id.id_indicator_home:
                TimeUtil timeUtil = new TimeUtil();
                startTime = endTime;
                endTime = timeUtil.getNowTime();
                if (!startTime.equals("")) {
                    if (Double.parseDouble(timeUtil.getTimeDifferenceHour(startTime, endTime)) > 0.015) {
                        homeFragment.getMarquee();
                    }
                }
                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                mTittleTab.setBackgroundColor(getResources().getColor(R.color.black60));
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                curFragment = 0;
                changeBackground(mBackgroundRid);
                SoundPlayUtils.setMusicSt(false);
                break;
            case R.id.id_indicator_msg:
                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                curFragment = 1;
                SoundPlayUtils.setMusicSt(false);
                break;

            case R.id.id_indicator_discusspic:

                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);

                curFragment = 2;

//                final LoadingDialog dialog1=discussPicFragment.builder1.create();
//                dialog1.show();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog1.dismiss();
//                    }
//                },1000);
                try {
                    int vipid = (int) SharedPreferencesUtils.get(this, Constants.VIP_ID, 0);
                    if (vipid < 2) {
                        discussPicFragment.chatPicViewModel.applyForTalkPictureFree();
                    } else {
                        discussPicFragment.chatPicViewModel.tpfStatus.set(1);
                    }
                    discussPicFragment.chatPicViewModel.getTalkPicture(0, "new");
                    discussPicFragment.chatPicViewModel.getMmsPicture(0, "mms");
                    discussPicFragment.chatPicViewModel.getChatPicture(0, "hot1");
                    discussPicFragment.chatPicViewModel.getShakePicture(0, "shake");
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
                SoundPlayUtils.setMusicSt(false);
                break;

            case R.id.id_indicator_me:
                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                curFragment = 3;
                SoundPlayUtils.setMusicSt(false);
                int vip_level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
//                if (vip_level< 2){
//                    //显示非会员模块fragmentme.videoViewModel
//                    fragmentme.videoViewModel.isVip.set(true);
//                }else {
//                    fragmentme.videoViewModel.isVip.set(false);
//                }
                fragmentme.videoViewModel.isVip.set(false);
                String code = (String) SharedPreferencesUtils.get(this, "code", "0");
                if (code.equals("1")) {
                    fragmentme.videoViewModel.videoStatus.set(4);
                }
                if (!isOnce1) {
                    isOnce1 = true;
                    fragmentme.videoViewModel.getVipZoneKeyWords();
                    fragmentme.videoViewModel.getVipZone(0, "0", "");
                    fragmentme.videoViewModel.getVipTypeZone(0, "1", "");
                    fragmentme.videoViewModel.getVipVoiceZone(0, "3");
                    fragmentme.videoViewModel.getVipVRZone(0, "4");
                }

                break;
            case R.id.id_game:
                getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                mTabIndicator.get(4).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(4, false);
                curFragment = 4;
//                //访问接口能不能链接
                try {
                    int level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
                    if (MyApplication.getContext().isNetworkConnected()) {
                        if (level <= 1) {
                            gameFragment.image_button.setVisibility(View.VISIBLE);
                            gameFragment.gameViewModel.setGameLinmit();
                        } else {
                            if (!isOnce || NettyClientBootstrap.getInstance().isLink() == false) {
                                gameFragment.gameViewModel.setSocket();
                                gameFragment.setSocket();
                                isOnce = true;
//                        gameFragment.setDialog(20000,R.drawable.icon_game_entering,"游戏资源加载中");
                                gameFragment.contact_lin.setVisibility(View.VISIBLE);
                            } else {
                                gameFragment.setSocket();
                            }
                            gameFragment.image_button.setVisibility(View.GONE);
                        }
                    } else {
                        MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
                    }

                } catch (Exception e) {
                    LogUtils.e("点击游戏的时候出现问题了" + e.getMessage());
                }


                break;

            default:
                break;
        }
    }

    /**
     * 替换背景图片
     */
    private void changeBackground(int resId) {
        Drawable[] drawableArray = {
                getResources().getDrawable(mBackgroundRid),
                getResources().getDrawable(resId)
        };
        transitionDrawable = new TransitionDrawable(drawableArray);
        rl_mainBack.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(800);

    }

    public void startMusic() {
        SoundPlayUtils.initMusic();
        SoundPlayUtils.setMusicSt(true);
    }

    public boolean isMusicSt() {
        return SoundPlayUtils.isMusicSt();
    }

    public void setMusicSt(boolean musicSt) {
        SoundPlayUtils.setMusicSt(musicSt);
    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        mTittleTab.setBackgroundColor(getResources().getColor(R.color.main_theme));
        changeBackground(R.color.page_bg);
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        curFragment = position;
        if (position == 0) {
            changeBackground(mBackgroundRid);
            mTittleTab.setBackgroundColor(getResources().getColor(R.color.black60));
        } else {
            changeBackground(R.color.page_bg);
            mTittleTab.setBackgroundColor(getResources().getColor(R.color.main_theme));
        }
        if (positionOffset > 0) {
            ChangeColorIconWithTextView left = mTabIndicator.get(position);
            ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作者：是否要验证手势密码
     * 检查版本是否更新
     * 版本号：v.1
     */

    private void initLockGestrue() {
        String gesturePsw = (String) SharedPreferencesUtils.get(MainActivity.this, "gesturePsw", "");
        boolean gesture_push = (boolean) SharedPreferencesUtils.get(MainActivity.this, "gesture_push", false);
        if (Tools.isEmpty(gesturePsw) && gesture_push == true) {
            Intent intent = new Intent(MainActivity.this, GestureLockActivity.class);
            intent.putExtra(PointState.GESTURETYPE, PointState.GESTURE_TYPE_VERIFY_OTHER);
            startActivity(intent);
        }
    }

    /**
     * 作者：B
     * 检查版本是否更新
     * 版本号：v.1
     */

    private void checkVersion() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("latestVersion", Tools.getVersion(this));
        params.put("os", "android");
        params.put("cilentType", 1);
        HttpUtil.get(Constants.getInstance().updateVersion, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "updateVersion onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(MainActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }

                    String status = dataCollection.getString("status");
                    if (status.equals("3")) {
                        return;
                    }
                    String content = dataCollection.getString("content");
                    final String URL = dataCollection.getString("updateUrl");
                    apkUrl = URL;
                    LogUtils.e("apkUrl :" + apkUrl);
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View layout = inflater.inflate(R.layout.custom_alertdialog_tip, null);
                    final AlertDialog dialog = DialogTool.createDialogTip(MainActivity.this, layout, content, R.string.next_time, R.string.update);
                    if (status.equals("1")) {//选择更新
                        dialog.setCancelable(true);
                        layout.findViewWithTag(0).setVisibility(View.VISIBLE);
                    } else if (status.equals("2")) {//强制更新
                        dialog.setCancelable(false);
                        layout.findViewWithTag(0).setVisibility(View.GONE);
                    }
                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

//                            Uri uri = Uri.parse(URL);
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            downloadApk();
//                            startActivity(intent);
//                            app.exitApp();
                        }
                    });

                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("checkVersion发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(MainActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * create by brucs
     * version V1.6.0
     * date:2016/5/2
     * describle:验证是否绑定手机号码
     */
    private void getCheckMobileInfo() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(MainActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("organiId", "");
        HttpUtil.get(Constants.getInstance().buyerCheckPhone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("手机绑定 信息:" + jsonObject);
                if (jsonObject == null) {
                    ToastUtils.showToast(R.string.getData_fail, 0);
                    return;
                } else {
                    try {
                        int resultCode = jsonObject.getInt("resultCode");
                        String isVerify = jsonObject.getJSONObject("dataCollection").getString("isVerify");
                        String phone = jsonObject.getJSONObject("dataCollection").getString("phone");


                        if (resultCode == 1) {
                            if (phone.length() > 5 || isVerify.equals("1")) {
                                SharedPreferencesUtils.put(MainActivity.this, Constants.CHECK_PHONE, true);
                                SharedPreferencesUtils.put(MainActivity.this, Constants.PHONE, phone);
                                // updataPhone(buyerId,phone);
                                Intent mIntent = new Intent(Constants.ACTION_SETTING);
                                mIntent.putExtra("type", "addkey");
                                sendBroadcast(mIntent);
                            } else {
                                SharedPreferencesUtils.put(MainActivity.this, Constants.CHECK_PHONE, false);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("getCheckMobileInfo发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(MainActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * create by vencent
     * version V1.6.0
     * date:2016/5/2
     * describle:存储异常信息
     */
    private void mobilException(String exception) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(MainActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        params.put("head", head);
        params.put("buyerUser", userId);
        params.put("systemVersion", StringUtils.getVersion(this));
        params.put("exception", exception);
        HttpUtil.post(Constants.getInstance().mobilException, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    ToastUtils.showToast(R.string.getData_fail, 0);
                    return;
                } else {
                    try {
                        int resultCode = jsonObject.getInt("resultCode");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(MainActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * create by brucs
     * version V1.6.0
     * date:2016/6/29
     * describle:是否可以无认证聊天
     * 为啥不直接在checkphone里面返回聊天数据，很操蛋的后台接口。
     */

    private void getBuyerCanChat() {
        // SharedPreferencesUtils.put(MainActivity.this, Constants.PHONEIMSWITCH, true);
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(MainActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }

        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        HttpUtil.get(Constants.getInstance().getBuyerPhoneIMSwitch, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("手机绑定和 会员等级:" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    //后台手机开关
                    String isVerify = jsonObject.getJSONObject("dataCollection").getString("buyerPhoneIMSwitch");
                    //后台vip开关
                    String buyerVipIMSwitch = jsonObject.getJSONObject("dataCollection").getString("buyerVipIMSwitch");
                    if (resultCode == 1) {
                        if (isVerify.equals("1")) {
                            SharedPreferencesUtils.put(MainActivity.this, Constants.PHONEIMSWITCH, true);
                        } else {
                            SharedPreferencesUtils.put(MainActivity.this, Constants.PHONEIMSWITCH, false);
                        }

                        if (buyerVipIMSwitch.equals("1")) {
                            SharedPreferencesUtils.put(MainActivity.this, Constants.VIPIMSWITCH, true);
                        } else {
                            SharedPreferencesUtils.put(MainActivity.this, Constants.VIPIMSWITCH, false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * create by brucs
     * version V1.6.0
     * date:2016/6/29
     * describle:获取用户界面的状态
     */

    private void getBuyerUserStatus() {
        // SharedPreferencesUtils.put(MainActivity.this, Constants.PHONEIMSWITCH, true);
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(MainActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }

        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerUserId", buyerId);
        final String str = buyerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().getBuyerUserStatus, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("getBuyerUserStatus:" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    boolean status = jsonObject.getJSONObject("dataCollection").getBoolean("status");
                    if (resultCode == 1) {
                        if (status == true) {
                            SharedPreferencesUtils.put(MainActivity.this, Constants.FREEVIP, true);
                        } else {
                            SharedPreferencesUtils.put(MainActivity.this, Constants.FREEVIP, false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 获取订单的信息接口
     */
    private void getOrderInfo() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(MainActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        HttpUtil.post(Constants.getInstance().getUnCommentOrder, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "getUnCommentOrder onSuccess:" + jsonObject);
                if (jsonObject == null) {
                    LogUtils.e("getOrderInfo发送错误信息返回了了服务器错误");
                    ToastUtils.showToast(R.string.getData_fail, 0);
                    return;
                } else {
                    try {
                        int resultCode = jsonObject.getInt("resultCode");
                        String resultMessage = jsonObject.getString("resultMessage");
                        if (resultCode != 1) {
                            if (resultCode == 2) {
                            } else {
                                MyToast.makeTextAnim(MainActivity.this, resultMessage, 0, R.style.PopToast).show();
                            }
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }
                    Order order = jsonUtil.jsonToOrder(dataCollection);
                    if (order != null && order.getBusinessCode() != null) {//已有订单
                        int status = order.getStatus();
                        if (status == 4 || status == 6) {
                            Intent intent = new Intent(MainActivity.this, RatingActivity.class);
                            intent.putExtra("status", status);
                            intent.putExtra("ID", order.getId());
                            intent.putExtra("sellerName", order.getSellerNickName());
                            intent.putExtra("sellerHead", order.getSellerHead());
                            intent.putExtra("sellerId", order.getSellerId());
                            intent.putExtra("buyerId", order.getBuyerId());
                            startActivity(intent);
                        } else if (status == 1 || status == 3) {
                            Session session = new Session();
                            session.setFrom(order.getOrganId() + "seller");
                            session.setTo(buyerId + "buyer");
                            session.setContent("订单尚未完成");
                            session.setHeadURL(order.getSellerHead());
                            session.setName(order.getSellerNickName());
                            session.setSellerId(order.getSellerId());
                            if (!sessionDao.isContent(order.getSellerId() + "", buyerId + "buyer")) {//判断最近联系人列表是否已存在记录
                                sessionDao.insertSession(session);
                            }
                        }
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUnCommentOrder onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(MainActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (homeFragment.stopMatching() == false) {
                if ((System.currentTimeMillis() - this.mExitTime) > 2000) {
                    this.mExitTime = System.currentTimeMillis();
                    MyToast.makeTextAnim(MainActivity.this, R.string.onclick_exit, 0, R.style.PopToast).show();
                } else {
                    app.exitApp();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTabs.get(curFragment).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置table的不可见
     * 然后设置背景图片
     * 此时是在匹配的页面
     */

    public void setTittleTabGone() {
        mTittleTab.setVisibility(View.GONE);
        changeBackground(R.mipmap.matching_bg);
    }

    /**
     * 设置table的可见
     * 并且设置fragment的背景图
     */

    public void setTittleTabVisable(int backId) {
        mTittleTab.setVisibility(View.VISIBLE);
        if (curFragment == 0) {
            changeBackground(backId);
        } else {
            changeBackground(R.color.page_bg);
        }
        mBackgroundRid = backId;
    }

    /**
     * 设置消息右上角的那个小数量点
     * 超过99条显示 …
     *
     * @param noreadNum
     */
    public void setPot(int noreadNum) {
        if (noreadNum == 0) {
            red_pot.setVisibility(View.GONE);
        } else {
            red_pot.setVisibility(View.VISIBLE);
            if (noreadNum >= 100) {
                red_pot.setText("...");
            } else {
                red_pot.setText(noreadNum + "");
            }
        }
    }

    public int getUserTotalKey() {
        return userTotalKey;
    }

    /**
     * 设置用户的钥匙的数量，并且给设置页面发送广播
     * 让设置页面进行数量更新
     *
     * @param totalKey
     */
    public void setUserTotalKey(int totalKey) {
        userTotalKey = totalKey;
        Intent mIntent = new Intent(Constants.ACTION_SETTING);
        mIntent.putExtra("type", "addkey");
        sendBroadcast(mIntent);
    }

    /**
     * 设置用户增加的钥匙的数量
     *
     * @param addKey
     */
    public void setUserAddTotalKey(int addKey) {
        userTotalKey = userTotalKey + addKey;
        Intent mIntent = new Intent(Constants.ACTION_SETTING);
        mIntent.putExtra("type", "addkey");
        sendBroadcast(mIntent);
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        downloadDialog = builder.create();
        downloadDialog.show();

        down_load.setVisibility(View.VISIBLE);
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);// 显示用户数据
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        this.startActivity(i);
    }

    /**
     * 跳转到直播的fragment
     */
    public void setLiveBroadcastMainFragment(View vk) {
        mViewPager.setCurrentItem(5, false);
        curFragment = 5;
        SoundPlayUtils.setMusicSt(false);
    }
}
