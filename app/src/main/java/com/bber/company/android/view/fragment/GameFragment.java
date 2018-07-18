package com.bber.company.android.view.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bber.company.android.FragmentGameBinding;
import com.bber.company.android.R;
import com.bber.company.android.bean.GameMessageEnterBean;
import com.bber.company.android.bean.MarBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.NettyClientBootstrap;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.util.TimeUtil;
import com.bber.company.android.view.activity.Buy_vipActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.viewmodel.GameViewModel;
import com.bber.company.android.viewmodel.HeaderBarViewModel;
import com.bber.company.android.widget.CustomGridLayoutManager;
import com.bber.company.android.widget.FlakeView;
import com.bber.company.android.widget.MarqueeFactory;
import com.bber.company.android.widget.MarqueeView;
import com.bber.company.android.widget.NoticeMF;
import com.bber.customview.utils.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Bruce
 * Date: 2016/5/9
 * Version:
 * Describe:
 */
public class GameFragment extends BaseFragment implements IactionListener {

    public GameViewModel gameViewModel;
    public LinearLayout hiding_lin, contact_lin, hiding_hot;
    public ImageView load_image, image_no_open, image_button, imageview_music;
    private FragmentGameBinding binding;
    private RecyclerView recyclerView;
    private MarqueeView title_text;
    private Button button;
    private SwipeRefreshLayout refreshLayout;
    //    private TextView text_freemoney;
// 掉落动画的主体动画
    private FlakeView flakeView;
    private String endtime;
    private boolean isMusic;
    private String time = "";
    private MarqueeFactory<TextView, String> marqueeFactory1;
    private List<String> datas = new ArrayList<>();
    /***
     * 开启金币掉落的效果
     */
//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            showPopWindows(title_text, "", false);
//            super.handleMessage(msg);
//        }
//    };

    private boolean isVisibleToUser;

//    /***
//     * 广播。一旦链接上，进度条消失
//     */
//    public void registerBoradcastReceiver() {
//        IntentFilter myIntentFilter = new IntentFilter();
//        myIntentFilter.addAction(Constants.ACTION_GAME_TYPE1);
//        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
//    }
//
//
//    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Handler mainHandler = new Handler(Looper.getMainLooper());
//            mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    contact_lin.setVisibility(View.GONE);
//                }
//            });
//
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //找到databinding的控件

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false);
        gameViewModel = new GameViewModel(getActivity());
        binding.setGameViewModel(gameViewModel);
        gameViewModel.setActionListener(this);
        headerBarViewModel = new HeaderBarViewModel();

        binding.setHeaderBarViewModel(headerBarViewModel);
        setheader();
        //渲染界面
//        registerBoradcastReceiver();
        try {
            initView();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }

//        gameViewModel.setSocket();
        //设置socket链接
//        setSocket();
        return binding.getRoot();
    }

    //true是有音乐，false是关音乐
    @Override
    public void onStart() {
        super.onStart();

        LogUtils.e("是不是经过的onStart");
        TimeUtil timeUtil = new TimeUtil();
        if (!time.equals("")) {

            endtime = timeUtil.getNowTime();
            LogUtils.e("onStartTime" + time);
            LogUtils.e("onendtime" + endtime);
            LogUtils.e("时间差" + timeUtil.getTimeDifferenceHour(time, endtime));
            if (isVisibleToUser) {
                if (Double.parseDouble(timeUtil.getTimeDifferenceHour(time, endtime)) > 0.05) {
                    if (gameViewModel == null) {
                        Intent i = getActivity().getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            }
        }

        if (gameViewModel.gameTableNumber != null) {
            MarBean marBean = new MarBean();
            marBean.type = 99;
            marBean.from = gameViewModel.gameCode + gameViewModel.gameTableNumber;
            marBean.to = "LOBBY";
            jsonUtil = new JsonUtil(getActivity());
            String sendData = jsonUtil.GameMarString(marBean);
            NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
        }
        if (Constants.ISCONTACT) {
            LogUtils.e("重新连接大厅，获取大厅的最新信息");
            reContact();
            setSocket();
            Constants.ISLink = false;
            Constants.ISCONTACT = false;
//            contact_lin.setVisibility(View.VISIBLE);
        }
    }

    private void setheader() {
        headerBarViewModel.setBarTitle("游戏大厅");
        //左边返回键设置隐藏
        headerBarViewModel.setLeftDisable();
        headerBarViewModel.setOnClickListener(new HeaderBarViewModel.headerclickListener() {
            @Override
            public void leftClickListener(View v) {
            }

            @Override
            public void rightClickListener(View v) {
            }
        });
    }

    /***
     * 渲染界面
     */
    private void initView() {
        title_text = binding.titleText;
//        title_text.run();
        recyclerView = binding.gameLoadMoreRecycleView;
        hiding_lin = binding.hidingLin;
        load_image = binding.loadImage;
        contact_lin = binding.contactLin;
        image_no_open = binding.imageNoOpen;
        image_button = binding.imageButton;
        imageview_music = binding.imageviewMusic;
        refreshLayout = binding.refreshLayout;
//        text_freemoney = binding.textFreemoney;
        hiding_hot = binding.hidingHot;
        contact_lin.setVisibility(View.VISIBLE);
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, 50));
        button = binding.button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Buy_vipActivity.class);
                startActivity(intent);
            }
        });

        String code = (String) SharedPreferencesUtils.get(getActivity(), "code", "0");
        if (code.equals("1")) {
            image_button.setBackground(getResources().getDrawable(R.drawable.icon_glod_user));
        } else {
            image_button.setBackground(getResources().getDrawable(R.drawable.icon_charge));
        }
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = (String) SharedPreferencesUtils.get(getActivity(), "code", "0");
                String url = (String) SharedPreferencesUtils.get(getActivity(), "url", "");
                if (code.equals("1")) {

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    if (!url.trim().equals("")) {
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                } else {

                    Intent intent = new Intent(getActivity(), Buy_vipActivity.class);
                    startActivity(intent);
                }
            }
        });

        isMusic = (boolean) SharedPreferencesUtils.get(getActivity(), "isMusic", true);
        if (isMusic) {
            imageview_music.setImageDrawable(getResources().getDrawable(R.drawable.icon_music));
        } else {
            imageview_music.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_music));
        }
        imageview_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).isMusicSt()) {
                    imageview_music.setImageDrawable(getResources().getDrawable(R.drawable.icon_close_music));
                    ((MainActivity) getActivity()).setMusicSt(false);
                    isMusic = false;
                    SharedPreferencesUtils.put(getActivity(), "isMusic", false);
                } else {
                    imageview_music.setImageDrawable(getResources().getDrawable(R.drawable.icon_music));
                    ((MainActivity) getActivity()).startMusic();
                    isMusic = true;
                    SharedPreferencesUtils.put(getActivity(), "isMusic", true);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        if (gameViewModel == null) {

                        }
                        setSocket();
                    }
                }, 200);

            }
        });


        Glide.with(getActivity()).load(R.drawable.icon_game_car)
                .asGif() //判断加载的url资源是否为gif格式的资源
                .error(R.drawable.icon_model_1)
                .into(load_image);

        marqueeFactory1 = new NoticeMF(getActivity());
        title_text.setMarqueeFactory(marqueeFactory1);

        marqueeFactory1.setOnItemClickListener(new MarqueeFactory.OnItemClickListener<TextView, String>() {
            @Override
            public void onItemClickListener(MarqueeFactory.ViewHolder<TextView, String> holder) {
                Toast.makeText(getActivity(), holder.data, Toast.LENGTH_SHORT).show();
            }
        });
        datas = new ArrayList<>();
        subMessageString("欢迎来到papa娱乐大厅");

        marqueeFactory1.setData(datas);
        title_text.startFlipping();
    }

    /***
     * 截取跑马灯字符串
     */
    private void subMessageString(String MarrTextData) {

        if (MarrTextData.length() >= 22) {
            String dataStr = "";
            dataStr = MarrTextData.substring(0, 22);
            datas.add(dataStr);
            subMessageString(MarrTextData.substring(22, MarrTextData.length()));
        } else {
            datas.add(MarrTextData);
        }
    }

    /***
     * 设置socket链接
     */
    public void setSocket() {
        gameViewModel.loginSocket();
    }

    @Override
    public void onStop() {
        LogUtils.e("是不是经过的onStop");
        super.onStop();
        ((MainActivity) getActivity()).setMusicSt(false);
        TimeUtil timeUtil = new TimeUtil();
        time = timeUtil.getNowTime();
        LogUtils.e("Time" + time);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        gameViewModel.nettyStart.mNettyClientHandler=null;
    }

    /***
     *
     */
    public void reContact() {
        try {
            NettyClientBootstrap.getInstance().startNetty();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        MyToast.makeTextAnim(getActivity(), "网络不稳定，为您重连...", 0, R.style.PopToast).show();
    }

    @Override
    public void SuccessCallback(Object o) {
        if (o instanceof String) {
//            title_text.setText((String) o);
            //当返回的时候无数组，给一张占位图片
            if (o.equals("1")) {
                hiding_hot.setVisibility(View.VISIBLE);
                contact_lin.setVisibility(View.GONE);
                Glide.with(this).load(gameViewModel.notOpenImg).placeholder(R.mipmap.icon_game_place).error(R.mipmap.icon_game_place).dontAnimate().into(image_no_open);
                return;
            }
            try {
                marqueeFactory1 = new NoticeMF(getActivity());
                title_text.setMarqueeFactory(marqueeFactory1);

                marqueeFactory1.setOnItemClickListener(new MarqueeFactory.OnItemClickListener<TextView, String>() {
                    @Override
                    public void onItemClickListener(MarqueeFactory.ViewHolder<TextView, String> holder) {
                        Toast.makeText(getActivity(), holder.data, Toast.LENGTH_SHORT).show();
                    }
                });
                datas = new ArrayList<>();
                subMessageString((String) o);

                marqueeFactory1.setData(datas);
                title_text.startFlipping();
            } catch (Exception e) {

            }

        }
        if (o instanceof List) {
            if (((List) o).get(0) instanceof GameMessageEnterBean) {

            }
        }
        if (o instanceof Integer) {
            //0 不能进入游戏大厅
            //1 能进入大厅
            if ((int) o == 0) {
                hiding_hot.setVisibility(View.VISIBLE);
                contact_lin.setVisibility(View.GONE);
                Glide.with(this).load(gameViewModel.picList).placeholder(R.mipmap.icon_game_place).error(R.mipmap.icon_game_place).dontAnimate().into(image_no_open);
            } else if ((int) o == 1) {
                hiding_lin.setVisibility(View.GONE);
                hiding_hot.setVisibility(View.GONE);
                if (NettyClientBootstrap.getInstance().isLink() == false) {
                    gameViewModel.setSocket();
                    setSocket();
                } else {
                    setSocket();
                }

            } else if ((int) o == 2) {
                contact_lin.setVisibility(View.GONE);
                hiding_hot.setVisibility(View.GONE);
            } else if ((int) o == 3) {
                //当103返回的时候，需要一天掉落金币以及数字增加的效果

            }
        }
    }

    @Override
    public void FailCallback(String result) {
        if (result.equals("1")) {
            contact_lin.setVisibility(View.GONE);
            hiding_hot.setVisibility(View.VISIBLE);
            Glide.with(this).load(gameViewModel.notOpenImg).dontAnimate().into(image_no_open);
        }
    }

    /***
     * 当切换到游戏界面时候，重开的逻辑
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (isMusic) {
                if (!((MainActivity) getActivity()).isMusicSt()) {
                    ((MainActivity) getActivity()).startMusic();
                }
            }
        }
    }


//    private PopupWindow pop;
//    private MediaPlayer player;
//    private PopupWindow showPopWindows(View v, String moneyStr, boolean show) {
//        View view = LayoutInflater.from(getActivity()).inflate(
//                R.layout.view_login_reward, null, false);
//        // this.getLayoutInflater().inflate(R.layout.view_login_reward, null);
////        TextView tvTips = (TextView) view.findViewById(R.id.tv_tip);
////        TextView money = (TextView) view.findViewById(R.id.tv_money);
////        tvTips.setText("打造国内领先的掌上投资理财");
////        money.setText(moneyStr);
//        final LinearLayout container = (LinearLayout) view
//                .findViewById(R.id.container);
//        // 将flakeView 添加到布局中
//        flakeView = new FlakeView(getActivity());
//        container.addView(flakeView);
//        // 设置背景
//        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//        // 设置同时出现在屏幕上的数量 建议64以内 过多会引起卡顿
//        flakeView.addFlakes(50);
//        /**
//         * 绘制的类型
//         *
//         * @see View.LAYER_TYPE_HARDWARE
//         * @see View.LAYER_TYPE_SOFTWARE
//         * @see View.LAYER_TYPE_NONE
//         */
//        flakeView.setLayerType(View.LAYER_TYPE_NONE, null);
//        pop = new PopupWindow(view, FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT);
//        ColorDrawable dw = new ColorDrawable(getResources().getColor(
//                R.color.half_color));
//        pop.setBackgroundDrawable(dw);
//        pop.setOutsideTouchable(true);
//        pop.setFocusable(true);
//        pop.showAtLocation(v, Gravity.CENTER, 0, 0);
//
//        player = MediaPlayer.create(getActivity(), R.raw.shake);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 * 移除动画
//                 */
//                player.start();
//                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        mp.stop();
//                        mp.release();
//                        container.removeAllViews();
//                        pop.dismiss();
//                    }
//
//                });
//            }
//        },1500);
//
//
//        return pop;
//    }
}
