package com.bber.company.android.view.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.FragmentChatBinding;
import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.ViewpagerChatPic0Binding;
import com.bber.company.android.databinding.ViewpagerChatPic1Binding;
import com.bber.company.android.databinding.ViewpagerChatPic2Binding;
import com.bber.company.android.databinding.ViewpagerChatPicBinding;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.NoDoubleUtils;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.util.LoadingDialog;
import com.bber.company.android.view.activity.Buy_vipActivity;
import com.bber.company.android.view.activity.MobileVerifyActivity;
import com.bber.company.android.view.adapter.TabPagerAdapter;
import com.bber.company.android.viewmodel.ChatPicViewModel;
import com.bber.company.android.viewmodel.HeaderBarViewModel;
import com.bber.company.android.widget.AddPopWindow;
import com.bber.company.android.widget.CustomGridLayoutManager;
import com.bber.company.android.widget.LoadMoreRecyclerView;
import com.bber.company.android.widget.PopupWindowHelper;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;


/**
 * 图片
 * Author: Vencent
 * Date: 2017/3/4
 * Version:
 * Describe:
 */
public class ChatPicFragment extends BaseFragment implements IactionListener<Object>, SensorEventListener {

    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;
    public ChatPicViewModel chatPicViewModel;
    public HeaderBarViewModel headerBarViewModel;
    public CustomGridLayoutManager layoutManager;
    //    private TabLayout tabLayout;
    public Ringtone rt;
    public LoadingDialog.Builder builder1;
    private FragmentChatBinding binding;
    private LoadMoreRecyclerView recyclerView, recyclerView1, recyclerView2;
    private SwipeRefreshLayout refreshLayout, refreshLayout2, refreshLayout1;
    //viewpager的两个子页面
    private ViewPager viewPager;
    //新加的摇图界面
    private ViewpagerChatPic0Binding viewpagerChatPicBinding0;
    private ViewpagerChatPic1Binding viewpagerChatPicBinding1;
    private ViewpagerChatPicBinding viewpagerChatPicBinding;
    private ViewpagerChatPic2Binding viewpagerChatPicBinding2;
    private LayoutInflater inflater;
    private ArrayList<String> options1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    //    private OptionsPickerView pvOptions;
    private TextView chat_image_date;
    //摇一摇
    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Vibrator mVibrator;//手机震动
    private LinearLayout shake_linear;
    private RecyclerView shake_recycle;
    private LinearLayout hiding_linear;
    private Button free_button;
    private Button buyvip_button;
    private TextView text_new, text_mms, text_shake, text_seven;
    private TextView pop_text_seven, pop_text_month, pop_text_three, pop_text_all;
    private LinearLayout linear_hot;
    private ImageView image_tri;
    private AddPopWindow addPipwindow;
    private View view_hot;
    private PopupWindowHelper popupWindowHelper;
    private View popView;
    private String hotType = "hot1";
    //人气的一个集合
    private List<TextView> textViewList;
    private boolean isVisibleToUser;
    //手指左右滑动的时候触发事件
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switchPager(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    //记录摇动状态
    private boolean isShake = false;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //摇一摇
                case START_SHAKE:
                    shake_recycle.setVisibility(View.GONE);
                    shake_linear.setVisibility(View.VISIBLE);
                    rt.play();
                    chatPicViewModel.getShakePicture(0, "shake");
                    mVibrator.vibrate(300);
                    break;
                case AGAIN_SHAKE:
                    mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
//                    MyToast.makeTextAnim(getActivity(), "恭喜,啥都没摇到", 0, R.style.PopToast).show();
                    //整体效果结束, 将震动设置为false
                    TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -1.0f);
                    mHiddenAction.setDuration(500);
                    shake_linear.setAnimation(mHiddenAction);
                    shake_linear.setVisibility(View.GONE);

                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    mShowAction.setDuration(500);
                    shake_recycle.setAnimation(mShowAction);
                    shake_recycle.setVisibility(View.VISIBLE);
                    isShake = false;
                    // 展示上下两种图片回来的效果
                    break;
//                default:
//                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        //找到databinding的控件
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_pic, container, false);

        //双向绑定
        chatPicViewModel = new ChatPicViewModel(getActivity());
        chatPicViewModel.setActionListener(this);
        binding.setRecyclerViewModel(chatPicViewModel);
        headerBarViewModel = new HeaderBarViewModel();
//        binding.setHeaderBarViewModel(headerBarViewModel);
        //设置头部的标题
        setheader();
        //页面层的回执
        initViews();
        //请求数据
        initData();
        return binding.getRoot();
    }

    private void setheader() {
        headerBarViewModel.setBarTitle("聊图");
        //左边返回键设置隐藏
        headerBarViewModel.setLeftDisable();
        headerBarViewModel.setRightImgResourceId(R.mipmap.card_add);
        headerBarViewModel.setOnClickListener(new HeaderBarViewModel.headerclickListener() {
            @Override
            public void leftClickListener(View v) {
            }

            @Override
            public void rightClickListener(View v) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取 SensorManager 负责管理传感器
        mSensorManager = ((SensorManager) getActivity().getSystemService(SENSOR_SERVICE));
        if (mSensorManager != null && isVisibleToUser) {
            //获取加速度传感器
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            //选中的0下标的时候才注册这个摇惶机制，避免返回的时候其他两个页面可以有
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }

        //播放声音
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        rt = RingtoneManager.getRingtone(getContext(), uri);
//        rt.play();
        //聊图试看的接口
        int vipid = (int) SharedPreferencesUtils.get(getActivity(), Constants.VIP_ID, 1);
        if (vipid < 2) {
            if (chatPicViewModel.istpfStatus.get() == false) {
                chatPicViewModel.applyForTalkPictureFree();
            }
        }


    }

    private void initViews() {
        viewPager = binding.chatViewpager;
        //viewpager的两个子页面
        viewpagerChatPicBinding0 = DataBindingUtil.inflate(inflater, R.layout.viewpager_chat_pic0, null, true);
        viewpagerChatPicBinding1 = DataBindingUtil.inflate(inflater, R.layout.viewpager_chat_pic1, null, true);
        viewpagerChatPicBinding = DataBindingUtil.inflate(inflater, R.layout.viewpager_chat_pic, null, true);
        viewpagerChatPicBinding2 = DataBindingUtil.inflate(inflater, R.layout.viewpager_chat_pic2, null, true);
        viewpagerChatPicBinding0.setViewModel(chatPicViewModel);
        viewpagerChatPicBinding1.setRecyclerViewModel(chatPicViewModel);
        viewpagerChatPicBinding.setRecyclerViewModel(chatPicViewModel);
        viewpagerChatPicBinding2.setRecyclerViewModel(chatPicViewModel);

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(addTitle(), addViewList());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(onPageChangeListener);
//        tabLayout.setupWithViewPager(viewPager);
        //按时间选择月份的聊图
//        chat_image_date = binding.chatImageDate;

        shake_linear = viewpagerChatPicBinding0.shakeLinear;
        shake_recycle = viewpagerChatPicBinding0.shakeRecycle;

        //控件
        text_new = binding.textNew;
        text_mms = binding.textMms;
        text_shake = binding.textShake;
        linear_hot = binding.linearHot;
        image_tri = binding.imageTri;
        view_hot = binding.viewHot;
        text_seven = binding.textSeven;

        //弹出框的控件
        popView = LayoutInflater.from(getActivity()).inflate(R.layout.add_popup_dialog, null);
        popupWindowHelper = new PopupWindowHelper(popView);
        pop_text_seven = popView.findViewById(R.id.text_seven);
        pop_text_month = popView.findViewById(R.id.text_month);
//        pop_text_three = (TextView) popView.findViewById(R.id.text_three_month);
        pop_text_all = popView.findViewById(R.id.text_all);
        textViewList = new ArrayList<>();
        textViewList.add(pop_text_seven);
        textViewList.add(pop_text_month);
//        textViewList.add(pop_text_three);
        textViewList.add(pop_text_all);


        layoutManager = new CustomGridLayoutManager(getActivity());
        shake_recycle.setLayoutManager(layoutManager);
        shake_recycle.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        //找到Refresh的layout设置上拉下拉事件
        refreshLayout = viewpagerChatPicBinding.refreshLayout;
        //加载loadmoreRecycle
        recyclerView = viewpagerChatPicBinding.loadmoreRecycle;
        layoutManager = new CustomGridLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recyclerView.setLoadMoreEnable(true);

        //设置下拉回调
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        //数据请求
//                        chat_image_date.setText("");
//                        chat_image_date.setBackground(getResources().getDrawable(R.mipmap.icon_date));
                        chatPicViewModel.datatime = "";
                        chatPicViewModel.getTalkPicture(0, "new");
                        recyclerView.getAdapter().notifyItemInserted(recyclerView.getAdapter().getItemCount());
//                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });

        //上来加载回调接口
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        chatPicViewModel.getTalkPicture(1, "new");
                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });


        //找到Refresh的layout设置上拉下拉事件
        refreshLayout1 = viewpagerChatPicBinding1.refreshLayout;
        //加载loadmoreRecycle

        recyclerView1 = viewpagerChatPicBinding1.loadmoreRecycle;
//        recyclerView2.setVerticalScrollBarEnabled(false);
        //CustomGridLayoutManager 是否滑动的manager
        layoutManager = new CustomGridLayoutManager(getActivity());

        recyclerView1.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recyclerView1.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recyclerView1.setLoadMoreEnable(true);

        //设置下拉回调
        refreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout1.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshLayout1.setRefreshing(false);
                        //数据请求
//                        chat_image_date.setText("");
//                        chat_image_date.setBackground(getResources().getDrawable(R.mipmap.icon_date));
                        chatPicViewModel.datatime = "";
                        chatPicViewModel.getMmsPicture(0, "mms");
                        recyclerView1.getAdapter().notifyItemInserted(recyclerView1.getAdapter().getItemCount());
//                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });
        //上来加载回调接口
        recyclerView1.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout1.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        chatPicViewModel.getMmsPicture(1, "mms");
                        recyclerView1.refreshCompleted();
                    }
                }, 200);
            }
        });


        //找到Refresh的layout设置上拉下拉事件
        refreshLayout2 = viewpagerChatPicBinding2.refreshLayout;
        //加载loadmoreRecycle

        recyclerView2 = viewpagerChatPicBinding2.loadmoreRecycle;
//        recyclerView2.setVerticalScrollBarEnabled(false);
        //CustomGridLayoutManager 是否滑动的manager
        layoutManager = new CustomGridLayoutManager(getActivity());

        recyclerView2.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recyclerView2.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recyclerView2.setLoadMoreEnable(true);

        //设置下拉回调
        refreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout2.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshLayout2.setRefreshing(false);
                        //数据请求
//                        chat_image_date.setText("");
//                        chat_image_date.setBackground(getResources().getDrawable(R.mipmap.icon_date));
                        chatPicViewModel.datatime = "";
                        chatPicViewModel.getChatPicture(0, hotType);
                        recyclerView2.getAdapter().notifyItemInserted(recyclerView2.getAdapter().getItemCount());
//                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });
        //上来加载回调接口
        recyclerView2.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout2.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        chatPicViewModel.getChatPicture(1, hotType);
                        recyclerView2.refreshCompleted();
                    }
                }, 200);
            }
        });

        //覆盖层消失
        hiding_linear = binding.hidingLinear;
        hiding_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPicViewModel.isShow.set(false);
            }
        });
        //验证手机号
        free_button = binding.freeButton;
        buyvip_button = binding.buyVipbutton;
        free_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPicViewModel.isShow.set(false);
                chatPicViewModel.istpfStatus.set(false);
                String code = (String) SharedPreferencesUtils.get(getActivity(), "code", "0");
                String url = (String) SharedPreferencesUtils.get(getActivity(), "url", "");
                if (code.equals("1") && chatPicViewModel.tpfStatus.get() == 4) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    if (!url.trim().equals("")) {
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getActivity(), MobileVerifyActivity.class);
                    startActivity(intent);
                }

//                Intent intent = new Intent(getActivity(), MobileVerifyActivity.class);
//                startActivity(intent);
            }
        });
        buyvip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPicViewModel.isShow.set(false);
                chatPicViewModel.istpfStatus.set(false);

                Intent vipintent = new Intent(getActivity(), Buy_vipActivity.class);
                startActivity(vipintent);
            }
        });

        builder1 = new LoadingDialog.Builder(getActivity())
                .setMessage("加载中...")
                .setCancelable(false);

        text_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPager(0);
                viewPager.setCurrentItem(0);
            }
        });
        text_mms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPager(1);
                viewPager.setCurrentItem(1);
            }
        });
        linear_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPager(2);
                popupWindowHelper.showAsDropDown(v);
                viewPager.setCurrentItem(2);
            }
        });
        text_shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPager(3);
                viewPager.setCurrentItem(3);
            }
        });
        pop_text_seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowHelper.dismiss();
                setPopText(pop_text_seven);
                hotType = "hot1";
                chatPicViewModel.getChatPicture(0, hotType);
            }
        });
        pop_text_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowHelper.dismiss();
                setPopText(pop_text_month);
                hotType = "hot2";
                chatPicViewModel.getChatPicture(0, hotType);
            }
        });
//        pop_text_three.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindowHelper.dismiss();
//                setPopText(pop_text_three);
//                hotType = "hot3";
//                chatPicViewModel.getChatPicture(0,hotType);
//            }
//        });
        pop_text_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowHelper.dismiss();
                setPopText(pop_text_all);
                hotType = "hot4";
                chatPicViewModel.getChatPicture(0, hotType);
            }
        });
    }

    /***
     * 设置热度人气的字体
     */
    private void setPopText(TextView textview) {
        for (int i = 0; i < textViewList.size(); i++) {
            textViewList.get(i).setTextColor(getResources().getColor(R.color.black));
        }
        textview.setTextColor(getResources().getColor(R.color.more_color));
        text_seven.setText(textview.getText());
    }

    public void initData() {
        //获取Vibrator震动服务
        mVibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        //获取聊图
//        chatPicViewModel.getTalkPicture(0, "new");
//        chatPicViewModel.getChatPicture(0, "hot");
//        chatPicViewModel.getShakePicture(0, "shake");
    }

    //tab的头部标题
    private List<String> addTitle() {
        List<String> titleList = new ArrayList<>();
        titleList.add("摇图");
        titleList.add("最新");
        titleList.add("最热");
        return titleList;
    }

    //tab的内容
    private List<View> addViewList() {
        List<View> viewList = new ArrayList<>();
        //最新
        viewList.add(viewpagerChatPicBinding.getRoot());
        //萌萌说
        viewList.add(viewpagerChatPicBinding1.getRoot());
        //最热
        viewList.add(viewpagerChatPicBinding2.getRoot());
        //摇图
        viewList.add(viewpagerChatPicBinding0.getRoot());
        return viewList;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            if (mSensorManager != null) {
                //获取加速度传感器
                mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (mAccelerometerSensor != null) {
                    mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
                }
            }
        } else {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
            }
            //相当于Fragment的onPause
        }
    }

    /***
     * 滑动到相应的pager
     */
    private void switchPager(int position) {
        text_new.setBackground(null);
        text_mms.setBackground(null);
        text_shake.setBackground(null);
        linear_hot.setBackground(null);
        image_tri.setImageDrawable(getResources().getDrawable(R.drawable.icon_ecpand));
        if (position == 0) {
            text_new.setBackground(getResources().getDrawable(R.drawable.icon_choose_left));
        } else if (position == 1) {
            text_mms.setBackground(getResources().getDrawable(R.drawable.icon_choose_middle));
        } else if (position == 2) {
            linear_hot.setBackground(getResources().getDrawable(R.drawable.icon_choose_middle));
            image_tri.setImageDrawable(getResources().getDrawable(R.drawable.icon_menu_choose));
        } else if (position == 3) {
            text_shake.setBackground(getResources().getDrawable(R.drawable.icon_choose_right));
        }
    }

    @Override
    public void onPause() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (mSensorManager != null && isVisibleToUser == false) {
            mSensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    public void SuccessCallback(Object o) {
        if (o instanceof String) {
            if (o.equals("new")) {
                recyclerView.setLoadMoreEnable(false);
                recyclerView.setFooter(getString(R.string.drop_down_list_footer_no_more_text));

            } else if (o.equals("hot")) {
                recyclerView2.setLoadMoreEnable(false);
                recyclerView2.setFooter(getString(R.string.drop_down_list_footer_no_more_text));
            } else if (o.equals("mms")) {
                recyclerView1.setLoadMoreEnable(false);
                recyclerView1.setFooter(getString(R.string.drop_down_list_footer_no_more_text));
            }
        }
    }

    @Override
    public void FailCallback(String result) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (isVisibleToUser) {
            if (type == Sensor.TYPE_ACCELEROMETER) {
                //获取三个方向值
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];


                if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math
                        .abs(z) > 17) && !isShake) {
                    isShake = true;
                    // TODO: 2016/10/19 实现摇动逻辑, 摇动后进行震动
                    Thread thread = new Thread() {
                        @Override
                        public void run() {


                            super.run();
                            try {
//                            Log.d(TAG, "onSensorChanged: 摇动");

                                //开始震动 发出提示音 展示动画效果
                                handler.obtainMessage(START_SHAKE).sendToTarget();
                                Thread.sleep(500);
                                //再来一次震动提示
                                handler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                                Thread.sleep(500);
                                handler.obtainMessage(END_SHAKE).sendToTarget();


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    if (NoDoubleUtils.isFastDoubleClick()) {
                        thread.start();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
