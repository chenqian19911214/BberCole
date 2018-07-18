package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.GameBeenSoldBean;
import com.bber.company.android.bean.GameGirlsBean;
import com.bber.company.android.bean.GameHistroyBean;
import com.bber.company.android.bean.GameHistroyUserBean;
import com.bber.company.android.bean.GameMessageEnterBean;
import com.bber.company.android.bean.GameSendBean;
import com.bber.company.android.bean.GameWinBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.ActivityGameBinding;
import com.bber.company.android.databinding.ViewpagerGame01Binding;
import com.bber.company.android.databinding.ViewpagerGame02Binding;
import com.bber.company.android.listener.GameItemListener;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.NettyClientBootstrap;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.util.LoadingDialog;
import com.bber.company.android.util.StringUtils;
import com.bber.company.android.util.TimeUtil;
import com.bber.company.android.view.adapter.AutoTabPagerAdapter;
import com.bber.company.android.view.adapter.GameHistroyAdapter;
import com.bber.company.android.view.adapter.UnlimmitedViewPagerAdapter;
import com.bber.company.android.widget.BannerView;
import com.bber.company.android.widget.CustomGridLayoutManager;
import com.bber.company.android.widget.GameBetDialog;
import com.bber.company.android.widget.GameNoMoneyDialog;
import com.bber.company.android.widget.GameRepeatDialog;
import com.bber.company.android.widget.ImageMoveView;
import com.bber.company.android.widget.Loading_Dialog;
import com.bber.company.android.widget.Loading_orgin_Dialog;
import com.bber.company.android.widget.LuckyMonkeyPanelView;
import com.bber.company.android.widget.MarqueeFactory;
import com.bber.company.android.widget.MarqueeView;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.NoticeMF_;
import com.bber.company.android.widget.SoundPlayUtils;
import com.bber.company.android.widget.UnlimitViewPager;
import com.bber.customview.utils.LogUtils;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bn on 2017/5/26.
 */

public class GameActivity extends Activity implements GameItemListener<Object> {

    //所有图片集合
    public List<GameGirlsBean> girlspicList;
    //亮着的模特头像
    public List<GameGirlsBean> lightGirlsPicList;
    //黑着的模特头像
    public List<GameGirlsBean> blackGirlsPicList;
    //背景图
    public List<GameGirlsBean> backPicList;
    public List<GameBeenSoldBean> beanlist;
    public List<GameGirlsBean> dataPicList;
    NettyClientBootstrap nettyStart = new NettyClientBootstrap();
    private ActivityGameBinding binding;
    private Button buttonA, buttonB, buttonStrart;
    private AnimationDrawable animationDrawable;
    private ImageView animationIV;
    //    private GameViewModel viewModel;
    private LuckyMonkeyPanelView lucky_panel;
    private LayoutInflater inflater;
    private BannerView game_viewpager;
    private ViewpagerGame01Binding viewpagerGamebinding01;
    private ViewpagerGame02Binding viewpagerGamebinding02;
    private String MD5key = "aa455bf23756e5f8168b54e0dde38bf3";
    private TextView watching;
    //图片地址
    private String path;
    private int watchingPeople;
    private List<GameMessageEnterBean> GameMessagelist;
    private GameMessageEnterBean GameMessageBean;
    private String currentToken;
    private TextView game_wallert;
    private TextView free_money;
    private TextView game_state;
    private ImageMoveView imageMoveView;
    private RelativeLayout hidingLin, relativeLayout;
    private LinearLayout visLin, money_winner, girl_winner, meaulin, no_winner, spectators;
    private TextView winner_money_text, winner_girl_text, limit_table, winner_girl_text1, spectators_text;
    private ImageView return_button, game_bg, game_guide1, game_guide2, game_guide3;
    private TextView prizeool_data, text_rule;
    private ImageView menu, game_rule_detail, game_winhistory, game_voice;
    private LinearLayout game_rule;
    private LinearLayout game_history;
    private RecyclerView history_list;
    private Loading_Dialog dialog;
    private Loading_orgin_Dialog _dialog;
    private UnlimitViewPager viewPager;
    private ImageView chest_image, add_money;
    private Button cancal_history;
    private String userMoney;
    private boolean isHaveVoice = true;
    private String game_money;
    private List<TextView> hottextViewList;
    private List<TextView> coldtextViewList;
    private List<TextView> pasttextViewList;
    private JsonUtil jsonUtil;
    private String winName = "";
    //与GAME游戏服务建立链接
//    private void setSocket() {
//        viewModel.loginSocket();
//    }
    private String winNumber = "";
    private Double winAmount;
    private String jackpotNumber = "";
    private Double jackpotAmount;
    private Double winMoney;
    private double ubMoney = 0.0;
    private int driveDay = 0;
    private String gameFreeMoney = "";
    private List<String> hotlist;
    private List<String> coldlist;
    //以往开奖号码
    private List<String> pastList;
    private String MarrTextData;
    private List<GameHistroyUserBean> gameHistroyList;
    private List<String> ubNameList;
    private int wait;
    private String errordata;
    private List<String> datas;
    private MarqueeView marqueeView;
    private MarqueeFactory<TextView, String> marqueeFactory1;
    private int currentPosition;
    private String time = "";
    private String endtime = "";
    //当前的游戏局号
    private String gameNumber;
    private Double jackpotBalance;
    private boolean isselect = false;
    private int index = 0;
    //    private Double jackpotAmount;
    private List<GameMessageEnterBean> ganelist;
    private String beenSold = "";
    private String nextbeenSold = "";
    private boolean isDestroy = true;
    private int stopNum;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Handler MyHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isDestroy) {
                        String username = (String) SharedPreferencesUtils.get(GameActivity.this, Constants.USERNAME, "");
                        if (winName.equals(username)) {
                            game_wallert.setText(StringUtils.doubleChangeIne(winMoney));
                            SharedPreferencesUtils.put(GameActivity.this, Constants.USER_MONEY, winMoney);
                        }

                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 9;
                                MyHandler.sendMessage(message);
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 1000);


                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(2500);
                                    Message message = new Message();
                                    message.what = 3;
                                    MyHandler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;
                    }
                case 2:
                    watching.setText(watchingPeople + "");
                    break;
                case 3:
                    if (isDestroy) {
                        if (isHaveVoice) {
                            SoundPlayUtils.play(3);
                        }
                        chest_image.setVisibility(View.GONE);
                        viewPager.setVisibility(View.VISIBLE);
                        viewPager.setCurrentItem(0);
                        viewPager.startGmae(Integer.parseInt(jackpotNumber));
//                        viewPager.startGmae(1);
//                        imageMoveView.startChange();
                        game_state.setText("宝箱美模开奖中");
//                        imageMoveView.setBackground(null);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(5000);
                                    Message message = new Message();
                                    message.what = 4;
                                    MyHandler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                    break;
                case 4:
                    if (isDestroy) {
//                        MyToast.makeTextAnim(GameActivity.this, "幸运美模数字为" + jackpotNumber, 0, R.style.PopToast).show();
//                        LogUtils.e("幸运美模数字为" + jackpotNumber);
//                        imageMoveView.stopChange(Integer.parseInt(jackpotNumber) - 1);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(2000);
                                    Message message = new Message();
                                    message.what = 5;
                                    MyHandler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                    break;
                case 5:
                    if (isDestroy) {

                        LogUtils.e("jackpotNumber" + jackpotNumber);
                        String username = (String) SharedPreferencesUtils.get(GameActivity.this, Constants.USERNAME, "");
                        if (!winName.equals(username)) {
                            boolean isSameName = false;
                            for (int i = 0; i < ubNameList.size(); i++) {

                                if (ubNameList.get(i).equals(username)) {
                                    isSameName = true;
                                    break;
                                } else {
                                    isSameName = false;
                                }
                            }
                            if (isSameName) {
                                girl_winner.setVisibility(View.GONE);
                                money_winner.setVisibility(View.GONE);
                                spectators.setVisibility(View.GONE);
                                no_winner.setVisibility(View.VISIBLE);
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            Thread.sleep(3500);
                                            Message message = new Message();
                                            message.what = 11;
                                            MyHandler.sendMessage(message);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;
                            } else {
                                LoadingDialog.Builder builder1 = new LoadingDialog.Builder(GameActivity.this)
                                        .setMessage("进入下一局...")
                                        .setCancelable(false);
                                final LoadingDialog dialog1 = builder1.create();
                                dialog1.show();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog1.dismiss();
                                    }
                                }, 6300);
                                Message message = new Message();
                                message.what = 7;
                                MyHandler.sendMessage(message);
                                break;
                            }


                        } else {
                            hidingLin.setVisibility(View.VISIBLE);
                            if (Integer.parseInt(jackpotNumber) == stopNum) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(5);
                                }
                                String data = getResources().getString(R.string.winner_girl_win);
                                String st = StringUtils.doubleChangeIne(jackpotAmount);
                                String str = String.format(data, stopNum, st);
                                winner_girl_text.setText(str);
                                money_winner.setVisibility(View.GONE);
                                no_winner.setVisibility(View.GONE);
                                girl_winner.setVisibility(View.VISIBLE);
                                spectators.setVisibility(View.GONE);
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            Thread.sleep(4500);
                                            Message message = new Message();
                                            message.what = 10;
                                            MyHandler.sendMessage(message);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            } else {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(6);
                                }
                                String data = getResources().getString(R.string.winner_money_win);
                                String st = StringUtils.doubleChangeIne(winAmount);
                                String str = String.format(data, stopNum, st);
                                winner_money_text.setText(str);
                                girl_winner.setVisibility(View.GONE);
                                no_winner.setVisibility(View.GONE);
                                spectators.setVisibility(View.GONE);
                                money_winner.setVisibility(View.VISIBLE);

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            Thread.sleep(3500);
                                            Message message = new Message();
                                            message.what = 11;
                                            MyHandler.sendMessage(message);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            }
                        }

                        ubNameList.clear();

//                        new Thread() {
//                            @Override
//                            public void run() {
//                                super.run();
//                                try {
//                                    Thread.sleep(4000);
//                                    Message message = new Message();
//                                    message.what = 7;
//                                    MyHandler.sendMessage(message);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }.start();
                    }
                    break;

                case 6:
                    MyToast.makeTextAnim(GameActivity.this, errordata, 0, R.style.PopToast).show();
                    break;
                case 7:
                    if (isDestroy) {

                        setOrg();
                        if (hotlist != null || coldlist != null) {
                            setHotAndCold();
                        }
                        if (pastList != null) {
                            setPastWinner();
                        }
                    }
                    break;
                case 8:
                    game_history.setVisibility(View.VISIBLE);
                    GameHistroyAdapter gameHistroyAdapter = new GameHistroyAdapter(GameActivity.this, gameHistroyList);
                    history_list.setAdapter(gameHistroyAdapter);
//                    gameHistroyAdapter.setOnItemClickLitener(new GameHistroyAdapter.OnItemClickLitener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            game_history.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onItemLongClick(View view, int position) {
//
//                        }
//                    });
                    break;
                case 9:
                    LogUtils.e("blackGirlsPicList.size() = " + blackGirlsPicList.size());
                    for (int i = 0; i < blackGirlsPicList.size(); i++) {
                        if (i != stopNum - 1) {
                            lucky_panel.setbackgirl(i, path + blackGirlsPicList.get(i).imgPath);
                        } else {
//                            lucky_panel.setVisFlase(i);
                        }
                    }
                    break;
                case 10:
                    girl_winner.setVisibility(View.GONE);
                    hidingLin.setVisibility(View.GONE);
                    //中奖池大奖
                    LoadingDialog.Builder builder1 = new LoadingDialog.Builder(GameActivity.this)
                            .setMessage("进入下一局...")
                            .setCancelable(false);
                    final LoadingDialog dialog1 = builder1.create();
                    dialog1.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog1.dismiss();
                            Message message = new Message();
                            message.what = 7;
                            MyHandler.sendMessage(message);
                        }
                    }, 1800);
                    break;
                case 11:
                    no_winner.setVisibility(View.GONE);
                    money_winner.setVisibility(View.GONE);
                    hidingLin.setVisibility(View.GONE);
                    //中普通奖
                    LoadingDialog.Builder builder2 = new LoadingDialog.Builder(GameActivity.this)
                            .setMessage("进入下一局...")
                            .setCancelable(false);
                    final LoadingDialog dialog2 = builder2.create();
                    dialog2.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog2.dismiss();
                            Message message = new Message();
                            message.what = 7;
                            MyHandler.sendMessage(message);
                        }
                    }, 2800);
                    break;
                case 12:
                    break;
                default:
                    break;
            }
        }
    };
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {


                int type = intent.getIntExtra("type", -1);
                //等于0的时候服务器异常
                if (type == 0) {
                    String message = intent.getStringExtra("message");
                    JSONObject dataJson = null;
                    String datatype = "";
                    try {
                        dataJson = new JSONObject(message);
                        datatype = dataJson.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (datatype.equals("200")) {
                        Message message_ = new Message();
                        message_.what = 6;
                        MyHandler.sendMessage(message_);
                    } else {
//                   MyToast.makeTextAnim(GameActivity.this, "服务器异常", 0, R.style.PopToast).show();
//                    finish();
                    }

                } else if (type == 3) {
                    dialog.dismiss();
                    String beenSold = intent.getStringExtra("beenSold");
                    String gameLuckymmImg = intent.getStringExtra("gameLuckymmImg");
                    String hotAndCold = intent.getStringExtra("hotAndCold");
                    String winNumberArray = intent.getStringExtra("winNumberArray");
                    wait = intent.getIntExtra("wait", 0);
//                jackpotAmount = intent.getDoubleExtra("jackpotAmount",0.0);
                    if (gameLuckymmImg.equals("[]") && Tools.isEmpty(gameLuckymmImg)) {
                        MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
                        finish();
                        return;
                    }
                    JSONObject hotAndColdJson = null;
                    try {
                        hotAndColdJson = new JSONObject(hotAndCold);
                        String hot = hotAndColdJson.getString("hot");
                        String cold = hotAndColdJson.getString("cold");
                        hotlist = jsonUtil.jsonToDStringBean(hot);
                        coldlist = jsonUtil.jsonToDStringBean(cold);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pastList = jsonUtil.jsonToDStringBean(winNumberArray);
                    gameNumber = intent.getStringExtra("gameNumber");
                    jackpotBalance = intent.getDoubleExtra("jackpotBalance", 0.0);
                    beanlist = new ArrayList<>();
                    jsonUtil = new JsonUtil(GameActivity.this);
                    if (!beenSold.equals("[]")) {
                        beanlist = jsonUtil.GameSoldToString(beenSold);
                        //将已购买
                    }
                    if (!Tools.isEmpty(gameLuckymmImg)) {
                        //所有图片集合
                        lightGirlsPicList = new ArrayList<>();
                        blackGirlsPicList = new ArrayList<>();
                        backPicList = new ArrayList<>();
                        girlspicList = jsonUtil.GameGilrsToString(gameLuckymmImg);

                        for (int i = 0; i < girlspicList.size(); i++) {
                            if (girlspicList.get(i).imgType.equals("BG")) {
                                backPicList.add(girlspicList.get(i));
                                Glide.with(GameActivity.this).load(path + backPicList.get(0).imgPath).dontAnimate().into(game_bg);
                            }
                            //将亮着的模特图片加到一个list里面
                            if (girlspicList.get(i).imgType.equals("MM")) {

                                lightGirlsPicList.add(girlspicList.get(i));
                            }
                            if (girlspicList.get(i).imgType.equals("MH")) {
                                blackGirlsPicList.add(girlspicList.get(i));
                            }
                        }
                    }


                    //dialog消失时间
                    if (hotlist != null || coldlist != null) {
                        setHotAndCold();
                    }
                    if (pastList != null) {
                        setPastWinner();
                    }

//              设置图片数据和选中项
                    setPic();
                    setAdapter();

                    ubNameList = new ArrayList<>();
                    setDialog(wait * 1000, R.drawable.icon_game_wait_, "");
                } else if (type == 4) {
                    String messagedata = intent.getStringExtra("message");
                    ubMoney = intent.getDoubleExtra("ubMoney", 0.0);
                    driveDay = intent.getIntExtra("ubMoney", 0);
                    gameFreeMoney = intent.getStringExtra("gameFreeMoney");
                    beanlist = new ArrayList<>();
                    if (!beenSold.equals("[]")) {
                        beanlist = jsonUtil.GameSoldToString(messagedata);
                        if (beanlist.size() == lightGirlsPicList.size()) {
                            for (int i = 0; i < beanlist.size(); i++) {
                                ubNameList.add(beanlist.get(i).bName);
                            }
                        }
                    }

                    SharedPreferencesUtils.put(GameActivity.this, Constants.USER_MONEY, StringUtils.doubleChangeIne(ubMoney));
                    SharedPreferencesUtils.put(GameActivity.this, Constants.GAMEFREEMONEY, gameFreeMoney);
                    game_wallert.setText(StringUtils.doubleChangeIne(ubMoney));

                    free_money.setText(gameFreeMoney);

                    _dialog.dismiss();
                    setPic();
                } else if (type == 5) {
                    String messagedata = intent.getStringExtra("message");
                    String massage = "";
                    try {
                        JSONObject dataJson = new JSONObject(messagedata);
                        massage = dataJson.getString("massage");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gameHistroyList = jsonUtil.GameHistroyToString(massage);

                    Message message = new Message();
                    message.what = 8;
                    MyHandler.sendMessage(message);

                } else if (type == 100) {  //当等于100就是开奖
                    String messagedata = intent.getStringExtra("message");
                    String currentData = "";
                    String currentMassage = "";


                    String nextGame = "";
                    GameWinBean beanlist = null;
                    try {
                        JSONObject dataJson = new JSONObject(messagedata);
                        currentData = dataJson.getString("massage");

                        JSONObject currentDataJson = new JSONObject(currentData);
                        winName = currentDataJson.getString("winName");
                        winNumber = currentDataJson.getString("winNumber");
                        winAmount = currentDataJson.getDouble("winAmount");
                        winMoney = currentDataJson.getDouble("ubMoney");
                        jackpotNumber = currentDataJson.getString("jackpotNumber");
                        LogUtils.e("type开奖出来的幸运美模数字为" + jackpotNumber);
                        jackpotAmount = currentDataJson.getDouble("jackpotAmount");

                        nextGame = currentDataJson.getString("nextGame");
                        JSONObject jsonObject = new JSONObject(nextGame);

                        nextbeenSold = jsonObject.getString("beenSold");
                        gameNumber = jsonObject.getString("gameNumber");
                        jackpotBalance = jsonObject.getDouble("jackpotBalance");
                        String hotAndCold = jsonObject.getString("hotAndCold");
                        String winNumberArray = jsonObject.getString("winNumberArray");
                        wait = jsonObject.getInt("wait");
                        JSONObject hotAndColdJson = new JSONObject(hotAndCold);
                        String hot = hotAndColdJson.getString("hot");
                        String cold = hotAndColdJson.getString("cold");

                        hotlist = jsonUtil.jsonToDStringBean(hot);
                        coldlist = jsonUtil.jsonToDStringBean(cold);
                        pastList = jsonUtil.jsonToDStringBean(winNumberArray);
                        setDialog(wait, R.drawable.icon_game_wait_, "");
//                    setHotAndCold();
//                    setPastWinner();
                        //dialog消失时间
//                    dialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    final String finalWinNumber = winNumber;
                    stopNum = Integer.parseInt(finalWinNumber);

                    //如果全部被选中的话
                    if (lucky_panel.checkALLChoose()) {
//                    MyToast.makeTextAnim(GameActivity.this, "幸运数字为" + (stopNum-1), 0, R.style.PopToast).show();
                        lucky_panel.setFabVisable();
                        lucky_panel.startAm(stopNum - 1);

                    }
                    game_state.setText("幸运号码开奖中");
                    if (isHaveVoice) {
                        SoundPlayUtils.play(4);
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(3000);
                                Message message = new Message();
                                message.what = 1;
                                MyHandler.sendMessage(message);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
//                /**
//                 * 监听停止
//                 */
//                lucky_panel.setIactionListener(new IactionListener() {
//                    @Override
//                    public void SuccessCallback(Object o) {
//                        Message message = new Message();
//                        message.what = 1;
//                        MyHandler.sendMessage(message);
//                    }
//
//                    @Override
//                    public void FailCallback(String result) {
//
//                    }
//                });

                } else if (type == 101) {
                    String messagedata = intent.getStringExtra("message");
                    beanlist = new ArrayList<>();
                    if (!beenSold.equals("[]")) {
                        beanlist = jsonUtil.GameSoldToString(messagedata);
                        if (beanlist.size() == lightGirlsPicList.size()) {
                            for (int i = 0; i < beanlist.size(); i++) {
                                ubNameList.add(beanlist.get(i).bName);
                            }
                        }
                    }
                    //如果全部被选中的话
                    if (beanlist.size() == 8) {
                        //lucky_panel.startGame();
                    }
                    setPic();

                } else if (type == 102) {
                    String messagedata = intent.getStringExtra("message");

                    try {
                        JSONObject dataJson = new JSONObject(messagedata);
                        String watchingdata = dataJson.getString("massage");
                        JSONObject dataJson1 = new JSONObject(watchingdata);
                        watchingPeople = dataJson1.getInt("watching");
                        Message message = new Message();
                        message.what = 2;
                        MyHandler.sendMessage(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type == 103) {
                    String messagedata = intent.getStringExtra("message");

                    try {
                        JSONObject dataJson = new JSONObject(messagedata);
                        String watchingdata = dataJson.getString("massage");
                        JSONObject dataJson1 = new JSONObject(watchingdata);
                        watchingPeople = dataJson1.getInt("watching");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type == 199) {
                    String messagedata = intent.getStringExtra("message");
                    try {
                        JSONObject dataJson = new JSONObject(messagedata);
                        MarrTextData = dataJson.getString("massage");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                LogUtils.e("MarrTextData传过来的长度是："+MarrTextData.length());
//                marqueeView.setText(MarrTextData);
                    datas = new ArrayList<>();
                    subMessageString(MarrTextData);
                    LogUtils.e("跑马灯的长度：" + MarrTextData.length());
                    marqueeFactory1.setData(datas);
                    marqueeView.startFlipping();

                } else if (type == 200) {
                    String messagedata = intent.getStringExtra("message");
                    try {
                        JSONObject dataJson = new JSONObject(messagedata);
                        errordata = dataJson.getString("resultMessage");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = 6;
                    MyHandler.sendMessage(message);
                    _dialog.dismiss();
                } else if (type == 201) {
                    _dialog.dismiss();
                    GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(GameActivity.this);
                    gameNoMoneyDialog.show();
                    lucky_panel.setChooseFlase(index);
                }

            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                NettyClientBootstrap.getInstance().onStop();
                MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//API19(android4.4)以上才有沉浸式
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        MyApplication.getContext().allActivity.add(this);
        AppManager.getAppManager().addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);
        registerBoradcastReceiver();
        //逻辑层，调用所有的socket接口
//        viewModel = new GameViewModel(this);
//        binding.setViewModel(viewModel);
//        viewModel.setActionListener(this);
        SoundPlayUtils.init(this);
        initView();
        initViewPager();
        initData();

//        setSocket();
//        setNetty();
    }

    private void initView() {
        buttonStrart = binding.btnAction;
        lucky_panel = binding.luckyPanel;
        game_viewpager = binding.gameViewpager;
        game_wallert = binding.gameWallert;
        free_money = binding.freeMoney;
        watching = binding.watching;
//        imageMoveView = binding.imageMove;
        hidingLin = binding.hidingLinear;
        visLin = binding.visLin;
        money_winner = binding.moneyWinner;
        girl_winner = binding.girlWinner;
        no_winner = binding.noWinner;
        spectators = binding.spectators;
        meaulin = binding.meaulin;
        winner_money_text = binding.winnerMoneyText;
        winner_girl_text = binding.winnerGirlText;
        return_button = binding.returnButton;
        prizeool_data = binding.prizeoolData;
        marqueeView = binding.marqueeView;
//        LogUtils.e("MarrTextData传过来的长度是："+marqueeView.getText().length());
        menu = binding.menu;
        game_rule = binding.gameRule;
        game_history = binding.gameHistory;
        game_rule_detail = binding.gameRuleDetail;
        game_winhistory = binding.gameWinhistory;
        game_state = binding.gameState;
        game_voice = binding.gameVoice;
        history_list = binding.historyList;
        game_bg = binding.gameBg;
        limit_table = binding.limitTable;
//        winner_girl_text1 = binding.winnerGirlText1;
//        spectators_text = binding.spectatorsText;
        viewPager = binding.viewpagerChange;
        chest_image = binding.chestImage;
        add_money = binding.addMoney;
        cancal_history = binding.cancalHistory;
        text_rule = binding.textRule;

        relativeLayout = binding.gameGuidebg;
        game_guide1 = binding.gameGuide1;
        game_guide2 = binding.gameGuide2;
        game_guide3 = binding.gameGuide3;

        boolean fiveth = (boolean) SharedPreferencesUtils.get(this, "fiveth", true);
        if (fiveth) {
            relativeLayout.setVisibility(View.VISIBLE);
            game_guide1.setVisibility(View.VISIBLE);
//            guide_call2.setVisibility(View.VISIBLE);
//            guide_call3.setVisibility(View.VISIBLE);
            SharedPreferencesUtils.put(this, "fiveth", false);
        }

        history_list.setLayoutManager(new CustomGridLayoutManager(this));
        //添加每个item的分割线
        history_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        lucky_panel.setGameItemListener(this);
        jsonUtil = new JsonUtil(this);

        game_rule_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_rule.setVisibility(View.VISIBLE);
            }
        });
        game_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_rule.setVisibility(View.GONE);

            }
        });
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyApplication.getContext().isNetworkConnected()) {
                    MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
                    finish();
                    return;
                }
                if (beanlist == null && Constants.ISLink) {
                    Constants.ISCONTACT = true;
                    Constants.ISLink = false;
                    MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
                    finish();
                    LogUtils.e("进来了关闭界面");
                    return;
                } else {
                    finish();
                }
            }
        });
        hidingLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setOrg();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyApplication.getContext().isNetworkConnected()) {
                    MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
                    finish();
                    return;
                }
                if (meaulin.getVisibility() == View.VISIBLE) {
                    meaulin.setVisibility(View.GONE);
                } else {
                    meaulin.setVisibility(View.VISIBLE);
                }
            }
        });
        game_winhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GameMessageBean.gameTableNumber != null) {
                    GameHistroyBean mbean = new GameHistroyBean();
                    mbean.type = 5;
                    mbean.gameCode = GameMessageBean.gameCode;
                    mbean.gameTableNumber = GameMessageBean.gameTableNumber;
                    mbean.token = currentToken;
                    jsonUtil = new JsonUtil(GameActivity.this);
                    String sendData = jsonUtil.GameHistroyString(mbean);
                    NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                }
            }
        });
        game_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHaveVoice) {
                    isHaveVoice = false;
                    game_voice.setBackground(getResources().getDrawable(R.drawable.game_close_voice));
                } else {
                    isHaveVoice = true;
                    game_voice.setBackground(getResources().getDrawable(R.drawable.game_voice_));
                }
            }
        });
        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, PayInputMoneyActivity.class);
                GameActivity.this.startActivity(intent);
            }
        });
        cancal_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_history.setVisibility(View.GONE);
            }
        });
        marqueeFactory1 = new NoticeMF_(GameActivity.this);
        marqueeView.setMarqueeFactory(marqueeFactory1);

        marqueeFactory1.setOnItemClickListener(new MarqueeFactory.OnItemClickListener<TextView, String>() {
            @Override
            public void onItemClickListener(MarqueeFactory.ViewHolder<TextView, String> holder) {
                Toast.makeText(GameActivity.this, holder.data, Toast.LENGTH_SHORT).show();
            }
        });
        game_guide1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_guide1.setVisibility(View.GONE);
                game_guide2.setVisibility(View.VISIBLE);
            }
        });
        game_guide2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_guide2.setVisibility(View.GONE);
                game_guide3.setVisibility(View.VISIBLE);
            }
        });
        game_guide3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game_guide3.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
            }
        });

        datas = new ArrayList<>();
        subMessageString("投注人满后开启幸运大奖");

        marqueeFactory1.setData(datas);
        marqueeView.startFlipping();
    }

    //设置成还没开奖的状态
    private void setOrg() {
        if (isHaveVoice) {
            SoundPlayUtils.play(1);
        }
        no_winner.setVisibility(View.GONE);
        LogUtils.e("到这里就应该消失了啊");
        lucky_panel.setALLWord();
        lucky_panel.setFabGone();
        hidingLin.setVisibility(View.GONE);
        prizeool_data.setText(StringUtils.doubleChangeIne(jackpotBalance));
        lucky_panel.setAllChooseFlase();
        lucky_panel.setUserNameText("等待下注");
        chest_image.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
//        imageMoveView.setbgnull();
//        imageMoveView.setBackground(getResources().getDrawable(R.drawable.icon_chest));
    }

    private void initData() {
        Intent intent = getIntent();
        String currentData = intent.getStringExtra("gameTpye2data");
        int position = intent.getIntExtra("position", -1);
        currentToken = intent.getStringExtra("currentToken");
        userMoney = (String) SharedPreferencesUtils.get(this, Constants.USER_MONEY, "");
        Double money = Double.parseDouble(userMoney);

        game_wallert.setText(StringUtils.doubleChangeIne(money));
        jsonUtil = new JsonUtil(GameActivity.this);
        GameMessagelist = jsonUtil.GameMessageEnterBeanToString(currentData);
        GameMessageBean = GameMessagelist.get(position);
        limit_table.setText(GameMessageBean.gameBetLimit + "元桌");
        path = (String) SharedPreferencesUtils.get(GameActivity.this, "IMAGE_FILE", "");
        game_money = (String) SharedPreferencesUtils.get(this, Constants.GAMEFREEMONEY, "");
        free_money.setText(game_money);
//        viewModel.enterGameDesk(beanlist.get(position));

        //设置加载的dialog
        setDialog(20000, R.drawable.icon_game_entering_, "");
        DecimalFormat df = new DecimalFormat("00%");
        DecimalFormat ddf = new DecimalFormat("0%");
        String userRatio = "";
        String jackpotWinRatio = "";
        String jackpotRatio = "";
        if (GameMessageBean.userRatio != null || GameMessageBean.jackpotWinRatio != null || GameMessageBean.jackpotRatio != null) {
            if (Double.parseDouble(GameMessageBean.userRatio) >= 0.1) {
                userRatio = df.format(Double.parseDouble(GameMessageBean.userRatio));
            } else {
                userRatio = ddf.format(Double.parseDouble(GameMessageBean.userRatio));
            }
            if (Double.parseDouble(GameMessageBean.jackpotWinRatio) >= 0.1) {
                jackpotWinRatio = df.format(Double.parseDouble(GameMessageBean.jackpotWinRatio));
            } else {
                jackpotWinRatio = ddf.format(Double.parseDouble(GameMessageBean.jackpotWinRatio));
            }
            if (Double.parseDouble(GameMessageBean.jackpotRatio) >= 0.1) {
                jackpotRatio = df.format(Double.parseDouble(GameMessageBean.jackpotRatio));
            } else {
                jackpotRatio = ddf.format(Double.parseDouble(GameMessageBean.jackpotRatio));
            }
        }


        String data = getResources().getString(R.string.game_rule);
        String str = String.format(data, userRatio, jackpotWinRatio, jackpotRatio);
        text_rule.setText(str);
    }

    private void setReContact() {
        if (Constants.ISLink && Constants.ISCONTACT) {
            onBackPressed();
        }
    }

    /***
     * 设置dialog的加载页面
     */
    public void setDialog(int time, int imageID, String data) {
//        dialog = new Loading_Dialog(this, R.drawable.icon_game_entering,"游戏资源加载中");
        dialog = new Loading_Dialog(this, imageID, data);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.startToMiss(time);
    }

    /***
     * 设置dialog的加载页面
     */
    public void set_Dialog(int time, String data) {
//        dialog = new Loading_Dialog(this, R.drawable.icon_game_entering,"游戏资源加载中");
        _dialog = new Loading_orgin_Dialog(this, data);
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();
        _dialog.startToMiss(time);
        _dialog.setLinstener(new IactionListener() {
            @Override
            public void SuccessCallback(Object o) {
                lucky_panel.setChooseFlase(index);
            }

            @Override
            public void FailCallback(String result) {

            }
        });
    }

    //轮播图
    private void initViewPager() {
        inflater = getLayoutInflater();

        viewpagerGamebinding01 = DataBindingUtil.inflate(inflater, R.layout.viewpager_game_01, null, true);
        viewpagerGamebinding02 = DataBindingUtil.inflate(inflater, R.layout.viewpager_game_02, null, true);
        TextView pastNum1 = viewpagerGamebinding01.pastNum1;
        TextView pastNum2 = viewpagerGamebinding01.pastNum2;
        TextView pastNum3 = viewpagerGamebinding01.pastNum3;
        TextView pastNum4 = viewpagerGamebinding01.pastNum4;
        TextView pastNum5 = viewpagerGamebinding01.pastNum5;

        TextView hot1 = viewpagerGamebinding02.hot1;
        TextView hot2 = viewpagerGamebinding02.hot2;
        TextView hot3 = viewpagerGamebinding02.hot3;
        TextView cold1 = viewpagerGamebinding02.cold1;
        TextView cold2 = viewpagerGamebinding02.cold2;
        TextView cold3 = viewpagerGamebinding02.cold3;

        pasttextViewList = new ArrayList<>();
        pasttextViewList.add(pastNum1);
        pasttextViewList.add(pastNum2);
        pasttextViewList.add(pastNum3);
        pasttextViewList.add(pastNum4);
        pasttextViewList.add(pastNum5);

        hottextViewList = new ArrayList<>();
        hottextViewList.add(hot1);
        hottextViewList.add(hot2);
        hottextViewList.add(hot3);
        coldtextViewList = new ArrayList<>();
        coldtextViewList.add(cold1);
        coldtextViewList.add(cold2);
        coldtextViewList.add(cold3);

        AutoTabPagerAdapter pagerAdapter = new AutoTabPagerAdapter(addViewList());
        game_viewpager.setAdapter(pagerAdapter);
        game_viewpager.startAutoScroll(6000, 6000);
    }

    //tab的内容
    private List<View> addViewList() {
        List<View> viewList = new ArrayList<>();
        viewList.add(viewpagerGamebinding01.getRoot());
        viewList.add(viewpagerGamebinding02.getRoot());
        return viewList;
    }

    private void setNetty() {
        try {
            nettyStart.startNetty();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setReContact();
        isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", false);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_GAME_TYPE3);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /***
     * 截取跑马灯字符串
     */
    private void subMessageString(String MarrTextData) {

        if (MarrTextData.length() >= 22) {
            String dataStr = "";
            dataStr = MarrTextData.substring(0, 22);
            datas.add(dataStr);
            subMessageString(MarrTextData.substring(20, MarrTextData.length()));
        } else {
            datas.add(MarrTextData);
        }

//
//        marqueeFactory1.setData(datas);
//        title_text.startFlipping();
    }

    public void setAdapter() {
        dataPicList = new ArrayList<>();
        dataPicList.add(lightGirlsPicList.get(lightGirlsPicList.size() - 1));
        dataPicList.addAll(lightGirlsPicList);
        dataPicList.add(lightGirlsPicList.get(0));
        UnlimmitedViewPagerAdapter adapter = new UnlimmitedViewPagerAdapter(this, dataPicList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (lightGirlsPicList.size() > 1) { //多于1，才会循环跳转
                    if (position > 8) { //末位之后，跳转到首位（1）
                        viewPager.setCurrentItem(1, false); //false:不显示跳转过程的动画
                        position = 1;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void setPic() {
        prizeool_data.setText(StringUtils.doubleChangeIne(jackpotBalance));
//        imageMoveView.setUriData(lightGirlsPicList);


        if (beanlist.size() != 0) {
            for (int i = 0; i < beanlist.size(); i++) {
                int position = Integer.parseInt((beanlist.get(i)).betNumber);
                if (!lucky_panel.isSeclect(position - 1)) {
                    lucky_panel.setUserName(position - 1, (beanlist.get(i)).bName);
                    lucky_panel.setYellowWord(position - 1);
                    lucky_panel.setChooseTrue(position - 1);
                    lucky_panel.setbackgirl(position - 1, path + lightGirlsPicList.get(position - 1).imgPath);
                }
            }
        }
    }

    //设置热号码和冷号码
    public void setHotAndCold() {
        for (int i = 0; i < hotlist.size(); i++) {
            hottextViewList.get(i).setText(hotlist.get(i));
        }
        for (int i = 0; i < coldlist.size(); i++) {
            coldtextViewList.get(i).setText(coldlist.get(i));
        }
    }

    //往期中奖的号码
    public void setPastWinner() {
        for (int i = 0; i < pastList.size(); i++) {
            pasttextViewList.get(i).setText(pastList.get(i));
        }

    }

    //拿到选中的那一个bean
    public GameBeenSoldBean getSelectBean(int position) {
        GameBeenSoldBean gameBeenSoldBean = new GameBeenSoldBean();
        for (int i = 0; i < beanlist.size(); i++) {
            if (Integer.parseInt(beanlist.get(i).betNumber) == position) {
                gameBeenSoldBean = beanlist.get(i);
                return gameBeenSoldBean;
            }
        }
        return null;
    }

    /***
     * 锁屏的情况下
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TimeUtil timeUtil = new TimeUtil();

        if (!time.equals("")) {

            endtime = timeUtil.getNowTime();
            LogUtils.e("onStartTime" + time);
            LogUtils.e("onendtime" + endtime);
            LogUtils.e("时间差" + timeUtil.getTimeDifferenceHour(time, endtime));
            if (Double.parseDouble(timeUtil.getTimeDifferenceHour(time, endtime)) > 0.1) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return;
            }
        }
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }


        Constants.ISLink = true;
        Constants.ISCONTACT = true;
        beanlist = null;
        LogUtils.e("应该要断线才对");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        imageMoveView.stopChange(-1);
        unregisterReceiver(mBroadcastReceiver);
        isDestroy = false;
        isHaveVoice = false;
        dialog.dismiss();
        TimeUtil timeUtil = new TimeUtil();
        time = timeUtil.getNowTime();
        LogUtils.e("Time" + time);
//        viewModel.nettyStart.onStop();
//        if (viewModel.socketClient.isConnected()) {
//            viewModel.socketClient.disconnect();
//        }
//
//        viewModel.setNoDisConnect();
    }

    @Override
    public void onItemView1(Object o) {
        LogUtils.e("Snock是否连接" + NettyClientBootstrap.getInstance().isLink());

        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }

        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(0)) {
            if (getSelectBean(1) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(1));
                gameRepeatDialog.show();
            }

        } else {
            //检测是否还在链接状态
            if (!Constants.ISCONTACT) {
                if (isselect) {
                    if (isHaveVoice) {
                        SoundPlayUtils.play(2);
                    }
                    GameSendBean gamebean = new GameSendBean();
                    gamebean.token = currentToken;
                    gamebean.gameCode = GameMessageBean.gameCode;
                    gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                    gamebean.gameNumber = gameNumber;
                    int betNumber = 1;
                    gamebean.betNumber = betNumber;
                    Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                    gamebean.betAmount = betAmount;
                    gamebean.type = 4;
                    gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                    String sendData = jsonUtil.GameSendString(gamebean);
                    NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
//                   //index = 0;
//                    //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(0, path + lightGirlsPicList.get(0).imgPath);
                    //如果全部被选中的话
                    set_Dialog(20000, "");
                } else {
                    GameBetDialog gameBetDialog = new GameBetDialog(this, 1);
                    gameBetDialog.show();
                    gameBetDialog.setActionListener(new IactionListener() {
                        @Override
                        public void SuccessCallback(Object o) {
                            if (o instanceof String) {
                                if (o.equals("1")) {

                                }
                                if (o.equals("2")) {
                                    if (isHaveVoice) {
                                        SoundPlayUtils.play(2);
                                    }
                                    GameSendBean gamebean = new GameSendBean();
                                    gamebean.token = currentToken;
                                    gamebean.gameCode = GameMessageBean.gameCode;
                                    gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                    gamebean.gameNumber = gameNumber;
                                    int betNumber = 1;
                                    gamebean.betNumber = betNumber;
                                    Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                    gamebean.betAmount = betAmount;
                                    gamebean.type = 4;
                                    gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                    String sendData = jsonUtil.GameSendString(gamebean);
                                    NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                    //index = 0;
                                    //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(0, path + lightGirlsPicList.get(0).imgPath);
                                    //如果全部被选中的话
                                    set_Dialog(20000, "");
                                }
                            }
                        }

                        @Override
                        public void FailCallback(String result) {
                            SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                            // isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                            isselect = true;
                        }
                    });
                }
            } else {
                MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            }

        }
//        }
    }

    @Override
    public void onItemView2(Object o) {
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(1)) {
            if (getSelectBean(2) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(2));
                gameRepeatDialog.show();
            }
        } else {
            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }

                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 2;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 1;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(1, path + lightGirlsPicList.get(1).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 2);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }

                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 2;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 1;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(1, path + lightGirlsPicList.get(1).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");
                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        // isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }
//            }
        }
    }

    @Override
    public void onItemView3(Object o) {
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(2)) {
            if (getSelectBean(3) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(3));
                gameRepeatDialog.show();
            }
        } else {
            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }
                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 3;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 2;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(2, path + lightGirlsPicList.get(2).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 3);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }
                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 3;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 2;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(2, path + lightGirlsPicList.get(2).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");
                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        //isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }

//            }
        }
    }

    @Override
    public void onItemView4(Object o) {
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(3)) {
            if (getSelectBean(4) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(4));
                gameRepeatDialog.show();
            }
        } else {
            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }
                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 4;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 3;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(3, path + lightGirlsPicList.get(3).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 4);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }
                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 4;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 3;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(3, path + lightGirlsPicList.get(3).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");
                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        //isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }

//            }
        }

    }

    @Override
    public void onItemView5(Object o) {
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(4)) {
            if (getSelectBean(5) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(5));
                gameRepeatDialog.show();
            }
        } else {
            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }
                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 5;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 4;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(4, path + lightGirlsPicList.get(4).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 5);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }
                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 5;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 4;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(4, path + lightGirlsPicList.get(4).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");
                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        //isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }

        }
//        }

    }

    @Override
    public void onItemView6(Object o) {
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(5)) {
            if (getSelectBean(6) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(6));
                gameRepeatDialog.show();
            }
        } else {
            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }
                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 6;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 5;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(5, path + lightGirlsPicList.get(5).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 6);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }
                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 6;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 5;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(5, path + lightGirlsPicList.get(5).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");
                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        // isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }

//            }
        }

    }

    @Override
    public void onItemView7(Object o) {

        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }

        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
//        if (Double.parseDouble(userMoney) + Double.parseDouble(game_money) < Double.parseDouble(GameMessageBean.gameBetLimit)) {
//            GameNoMoneyDialog gameNoMoneyDialog = new GameNoMoneyDialog(this);
//            gameNoMoneyDialog.show();
//
//        } else {
        if (lucky_panel.isSeclect(6)) {
            if (getSelectBean(7) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(7));
                gameRepeatDialog.show();
            }
        } else {
            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }
                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 7;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 6;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(6, path + lightGirlsPicList.get(6).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 7);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }
                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 7;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 6;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(6, path + lightGirlsPicList.get(6).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");

                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        // isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }

        }
//        }

    }

    @Override
    public void onItemView8(Object o) {
        if (!MyApplication.getContext().isNetworkConnected()) {
            MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }

        if (beanlist == null && Constants.ISLink) {
            Constants.ISCONTACT = true;
            Constants.ISLink = true;
            MyToast.makeTextAnim(GameActivity.this, R.string.no_contact, 0, R.style.PopToast).show();
            finish();
            LogUtils.e("进来了关闭界面");
            return;
        }
        game_state.setText("等待玩家进入");
        if (lucky_panel.isSeclect(7)) {
            if (getSelectBean(8) != null) {
                GameRepeatDialog gameRepeatDialog = new GameRepeatDialog(this, getSelectBean(8));
                gameRepeatDialog.show();
            }
        } else {

            if (isselect) {
                if (isHaveVoice) {
                    SoundPlayUtils.play(2);
                }
                GameSendBean gamebean = new GameSendBean();
                gamebean.token = currentToken;
                gamebean.gameCode = GameMessageBean.gameCode;
                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                gamebean.gameNumber = gameNumber;
                int betNumber = 8;
                gamebean.betNumber = betNumber;
                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                gamebean.betAmount = betAmount;
                gamebean.type = 4;
                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                String sendData = jsonUtil.GameSendString(gamebean);
                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                index = 7;
                //lucky_panel.setChooseTrue(index);
//                    lucky_panel.setbackgirl(7, path + lightGirlsPicList.get(7).imgPath);
                //如果全部被选中的话
                set_Dialog(20000, "");
            } else {
                GameBetDialog gameBetDialog = new GameBetDialog(this, 8);
                gameBetDialog.show();
                gameBetDialog.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        if (o instanceof String) {
                            if (o.equals("1")) {

                            }
                            if (o.equals("2")) {
                                if (isHaveVoice) {
                                    SoundPlayUtils.play(2);
                                }
                                GameSendBean gamebean = new GameSendBean();
                                gamebean.token = currentToken;
                                gamebean.gameCode = GameMessageBean.gameCode;
                                gamebean.gameTableNumber = GameMessageBean.gameTableNumber;
                                gamebean.gameNumber = gameNumber;
                                int betNumber = 8;
                                gamebean.betNumber = betNumber;
                                Double betAmount = Double.parseDouble(GameMessageBean.gameBetLimit);
                                gamebean.betAmount = betAmount;
                                gamebean.type = 4;
                                gamebean.sign = spellSign(gamebean.token, gamebean.gameCode, gamebean.gameTableNumber, gamebean.gameNumber, gamebean.betNumber, gamebean.betAmount, gamebean.type);
                                String sendData = jsonUtil.GameSendString(gamebean);
                                NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
                                index = 7;
                                //lucky_panel.setChooseTrue(index);
//                                    lucky_panel.setbackgirl(7, path + lightGirlsPicList.get(7).imgPath);
                                //如果全部被选中的话
                                set_Dialog(20000, "");
                            }
                        }
                    }

                    @Override
                    public void FailCallback(String result) {
                        SharedPreferencesUtils.put(GameActivity.this, "isselect", true);
                        // isselect = (boolean) SharedPreferencesUtils.get(GameActivity.this, "isselect", true);
                        isselect = true;
                    }
                });
            }

        }
//        }
    }

    /***
     * 拼接sign的加密字符
     */
    public String spellSign(String token, String gameCode, String gameTableNumber, String gameNumber, int betNumber, Double betAmount, int type) {
        return Tools.md5("token=" + token + "#gameCode=" + gameCode + "#gameTableNumber=" + gameTableNumber + "#gameNumber=" + gameNumber + "#betNumber=" + betNumber + "#betAmount=" + betAmount + "#type=" + type + MD5key);
    }
}
