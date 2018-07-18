package com.bber.company.android.view.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.FragmentVideoBinding;
import com.bber.company.android.databinding.VideoView1Binding;
import com.bber.company.android.databinding.VideoView2Binding;
import com.bber.company.android.databinding.VideoView3Binding;
import com.bber.company.android.databinding.VideoView4Binding;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.util.PingUtil;
import com.bber.company.android.view.activity.Buy_vipActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.view.activity.MobileVerifyActivity;
import com.bber.company.android.view.adapter.RecycleAdapter1;
import com.bber.company.android.view.adapter.TabPagerAdapter;
import com.bber.company.android.viewmodel.VideoViewModel;
import com.bber.company.android.widget.CustomGridLayoutManager;
import com.bber.company.android.widget.LoadMoreRecyclerView;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.PopupWindowHelper;
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


/**
 * @author Vencent
 * @date 2017/3/10
 * @description 侧边栏菜单
 */
@SuppressLint("SetJavaScriptEnabled")
public class VideoFragment extends BaseFragment implements View.OnClickListener, IactionListener<Object> {

    public VideoViewModel videoViewModel;
    public CustomGridLayoutManager layoutManager;
    //progress
    public LinearLayout progress_lin;
    public LinearLayout hiding_linear;
    private FragmentVideoBinding binding;
    //viewpager的两个子页面
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private VideoView1Binding videoView1Binding;
    private VideoView2Binding videoView2Binding;
    private VideoView3Binding videoView3Binding;
    private VideoView4Binding videoView4Binding;
    private LoadMoreRecyclerView recycleView1, recycleView2, recycleView3, recycleView4;
    private SwipeRefreshLayout refreshLayout1, refreshLayout2, refreshLayout3, refreshLayout4;
    private LinearLayout video_new, video_type;
    private TextView text_voice, text_vr;
    private ImageView video_new_image, video_type_image;
    private View popView, popView1;
    private PopupWindowHelper popupWindowHelper, popupWindowHelper1;
    private TextView text_new, pop_text_seven, pop_text_month, pop_text_three, pop_text_all, video_new_text, video_type_text;
    //人气的一个集合
    private List<TextView> textViewList;
    private RecyclerView mobile_recycleview;
    private List<String> countryData = new ArrayList<>();
    private ImageView icon_change, icon_search, image_change;
    private LinearLayout search_Lin, type_Lin;
    private EditText keyword_text;
    private Button free_button, buy_vipbutton;
    //套路类型搜索
    private String type = "";
    private String keyword = "";
    private ProgressBar progressWheel;
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
    private String hotType = "new";
    private List<String> datalist;
    private List<Float> fastlist;
    private List<Float> finallylist;
    private List<String> comparelist;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final String url = msg.getData().getString("url");
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setUserVipZoneUrl(url);
                        }
                    }).start();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        //找到databinding的控件
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false);
//        binding.setHeaderBarViewModel(headerBarViewModel);
        videoViewModel = new VideoViewModel(getActivity());
        videoViewModel.setActionListener(this);
        binding.setVideoViewModel(videoViewModel);

        getUserVipZoneUrl(); //进行视频地址测速
        getActivity().getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        //setheader();
        initView();
        setLinsintener();
        initDate();
        return binding.getRoot();
    }

    private void setLinsintener() {
        video_new.setOnClickListener(this);
        video_type.setOnClickListener(this);
        text_voice.setOnClickListener(this);
        text_vr.setOnClickListener(this);
        text_new.setOnClickListener(this);
        pop_text_seven.setOnClickListener(this);
        pop_text_month.setOnClickListener(this);
        pop_text_all.setOnClickListener(this);
        icon_change.setOnClickListener(this);
        icon_search.setOnClickListener(this);
        search_Lin.setOnClickListener(this);
        type_Lin.setOnClickListener(this);
        image_change.setOnClickListener(this);
        progress_lin.setOnClickListener(this);
        free_button.setOnClickListener(this);
        buy_vipbutton.setOnClickListener(this);
        hiding_linear.setOnClickListener(this);
    }

    ///渲染数据
    private void initView() {
        //上头的四个控件
        video_new = binding.videoNew;
        video_type = binding.videoType;
        text_voice = binding.textVoice;
        text_vr = binding.textVr;
        video_new_image = binding.videoNewImage;
        video_type_image = binding.videoTypeImage;
        video_new_text = binding.videoNewText;
        video_type_text = binding.videoTypeText;

        //上头变换的控件
        icon_change = binding.iconChange;
        icon_search = binding.iconSearch;
        search_Lin = binding.searchLin;
        type_Lin = binding.typeLin;
        image_change = binding.imageChange;
        keyword_text = binding.keywordText;

        progressWheel = binding.progress;
        progress_lin = binding.progressLin;
        free_button = binding.freeButton;
        buy_vipbutton = binding.buyVipbutton;
        hiding_linear = binding.hidingLinear;

        //弹出框的控件   最新的弹窗
        popView = LayoutInflater.from(getActivity()).inflate(R.layout.add_popup_video_dialog, null);
        popupWindowHelper = new PopupWindowHelper(popView);
        text_new = popView.findViewById(R.id.text_new);
        pop_text_seven = popView.findViewById(R.id.text_seven);
        pop_text_month = popView.findViewById(R.id.text_month);
//        pop_text_three = (TextView) popView.findViewById(R.id.text_three_month);
        pop_text_all = popView.findViewById(R.id.text_all);
        textViewList = new ArrayList<>();
        textViewList.add(text_new);
        textViewList.add(pop_text_seven);
        textViewList.add(pop_text_month);
//        textViewList.add(pop_text_three);
        textViewList.add(pop_text_all);

        //弹出框的控件  视频的弹窗
        popView1 = LayoutInflater.from(getActivity()).inflate(R.layout.add_video_type, null);
        popupWindowHelper1 = new PopupWindowHelper(popView1);
        mobile_recycleview = popView1.findViewById(R.id.video_recycleview);

        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(getActivity());
        mobile_recycleview.setLayoutManager(layoutManager);
        mobile_recycleview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, 2));
        final RecycleAdapter1 recycleAdapter = new RecycleAdapter1(getActivity(), countryData, R.layout.adapter_simple1);
        mobile_recycleview.setAdapter(recycleAdapter);
        recycleAdapter.setOnItemClickListener(new RecycleAdapter1.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    type = "";
                    videoViewModel.getVipTypeZone(0, "1", type);
                } else {
                    type = countryData.get(position);
                    videoViewModel.getVipTypeZone(0, "1", type);
                }
                recycleAdapter.setTextColcor(position);
                popupWindowHelper1.dismiss();
                video_type_text.setText(countryData.get(position));

            }
        });

        //下方四个页面
        viewPager = binding.chatViewpager;
        //viewpager的两个子页面
        videoView1Binding = DataBindingUtil.inflate(inflater, R.layout.video_view1, null, true);
        videoView2Binding = DataBindingUtil.inflate(inflater, R.layout.video_view2, null, true);
        videoView3Binding = DataBindingUtil.inflate(inflater, R.layout.video_view3, null, true);
        videoView4Binding = DataBindingUtil.inflate(inflater, R.layout.video_view4, null, true);
        videoView1Binding.setVideoViewModel(videoViewModel);
        videoView2Binding.setVideoViewModel(videoViewModel);
        videoView3Binding.setVideoViewModel(videoViewModel);
        videoView4Binding.setVideoViewModel(videoViewModel);
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(addTitle(), addViewList());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(onPageChangeListener);

        //加载loadmoreRecycle
        recycleView1 = videoView1Binding.loadmoreRecycle;
        //找到Refresh的layout设置上拉下拉事件
        refreshLayout1 = videoView1Binding.refreshLayout;
        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView1.setLayoutManager(layoutManager);
        recycleView1.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));


        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView1.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recycleView1.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recycleView1.setLoadMoreEnable(true);

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
//                        chatPicViewModel.datatime = "";
                        if (hotType.equals("new")) {
                            videoViewModel.getVipZone(0, "0", keyword);
                            recycleView1.getAdapter().notifyItemInserted(recycleView1.getAdapter().getItemCount());
                        } else {
                            videoViewModel.getHotVideo(0, hotType);
                        }

//                        recyclerView.refreshCompleted();0
                    }
                }, 200);
            }
        });

        //上来加载回调接口
        recycleView1.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        if (hotType.equals("new")) {
                            videoViewModel.getVipZone(1, "0", keyword);
                            recycleView1.getAdapter().notifyItemInserted(recycleView1.getAdapter().getItemCount());
                        } else {
                            videoViewModel.getHotVideo(1, hotType);
                        }
                        recycleView1.refreshCompleted();
                    }
                }, 200);
            }
        });

        refreshLayout2 = videoView2Binding.refreshLayout;
        //加载loadmoreRecycle
        recycleView2 = videoView2Binding.loadmoreRecycle;
        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView2.setLayoutManager(layoutManager);
        recycleView2.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        //找到Refresh的layout设置上拉下拉事件
        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView2.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recycleView2.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recycleView2.setLoadMoreEnable(true);

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
//                        chatPicViewModel.datatime = "";
                        videoViewModel.getVipTypeZone(0, "1", type);
                        recycleView2.getAdapter().notifyItemInserted(recycleView2.getAdapter().getItemCount());
//                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });

        //上来加载回调接口
        recycleView2.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        videoViewModel.getVipTypeZone(1, "1", type);
                        recycleView2.refreshCompleted();
                    }
                }, 200);
            }
        });


        //找到Refresh的layout设置上拉下拉事件
        refreshLayout3 = videoView3Binding.refreshLayout;
        //加载loadmoreRecycle
        recycleView3 = videoView3Binding.loadmoreRecycle;
        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView3.setLayoutManager(layoutManager);
        recycleView3.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView3.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recycleView3.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recycleView3.setLoadMoreEnable(true);

        //设置下拉回调
        refreshLayout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout3.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshLayout3.setRefreshing(false);
                        //数据请求
//                        chatPicViewModel.datatime = "";
                        videoViewModel.getVipVoiceZone(0, "3");
                        recycleView3.getAdapter().notifyItemInserted(recycleView3.getAdapter().getItemCount());
//                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });

        //上来加载回调接口
        recycleView3.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        videoViewModel.getVipVoiceZone(1, "3");
                        recycleView3.refreshCompleted();
                    }
                }, 200);
            }
        });


        //找到Refresh的layout设置上拉下拉事件
        refreshLayout4 = videoView4Binding.refreshLayout;
        //加载loadmoreRecycle
        recycleView4 = videoView4Binding.loadmoreRecycle;
        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView4.setLayoutManager(layoutManager);
        recycleView4.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        layoutManager = new CustomGridLayoutManager(getActivity());
        recycleView4.setLayoutManager(layoutManager);
        //添加每个item的分割线

        recycleView4.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        //开启加载更多
        recycleView4.setLoadMoreEnable(true);

        //设置下拉回调
        refreshLayout4.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout4.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshLayout4.setRefreshing(false);
                        //数据请求
//                        chatPicViewModel.datatime = "";
                        videoViewModel.getVipVRZone(0, "4");
                        recycleView4.getAdapter().notifyItemInserted(recycleView4.getAdapter().getItemCount());
//                        recyclerView.refreshCompleted();
                    }
                }, 200);
            }
        });

        //上来加载回调接口
        recycleView4.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshLayout4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mRecyclerView.setLoadingMore(true);
                        //数据请求
                        videoViewModel.getVipVRZone(1, "4");
                        recycleView3.refreshCompleted();
                    }
                }, 200);
            }
        });
    }

    //渲染各种数据
    public void initDate() {
//       videoViewModel.getVipZoneKeyWords();
    }

    //tab的头部标题
    private List<String> addTitle() {
        List<String> titleList = new ArrayList<>();
        titleList.add("摇图");
        titleList.add("最新");
        titleList.add("最热");
        titleList.add("最热");
        return titleList;
    }

    //tab的内容
    private List<View> addViewList() {
        List<View> viewList = new ArrayList<>();
        //最新
        viewList.add(videoView1Binding.getRoot());
        //萌萌说
        viewList.add(videoView2Binding.getRoot());
        //最热
        viewList.add(videoView3Binding.getRoot());
        //摇图
        viewList.add(videoView4Binding.getRoot());
        return viewList;
    }

    /***
     * 滑动到相应的pager
     * video_new = binding.videoNew;
     * video_type = binding.videoType;
     * text_voice = binding.textVoice;
     * text_vr
     */
    private void switchPager(int position) {
        video_new.setBackground(null);
        video_type.setBackground(null);
        text_voice.setBackground(null);
        text_vr.setBackground(null);
        video_new_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_ecpand));
        video_type_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_ecpand));
        if (position == 0) {
            video_new.setBackground(getResources().getDrawable(R.drawable.icon_choose_left));
            video_new_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_menu_choose));
        } else if (position == 1) {
            video_type.setBackground(getResources().getDrawable(R.drawable.icon_choose_middle));
            video_type_image.setImageDrawable(getResources().getDrawable(R.drawable.icon_menu_choose));
        } else if (position == 2) {
            text_voice.setBackground(getResources().getDrawable(R.drawable.icon_choose_middle));

        } else if (position == 3) {
            text_vr.setBackground(getResources().getDrawable(R.drawable.icon_choose_right));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_new:
                switchPager(0);
                viewPager.setCurrentItem(0);
                popupWindowHelper.showAsDropDown(video_new);
                break;
            case R.id.video_type:
                switchPager(1);
                viewPager.setCurrentItem(1);
                popupWindowHelper1.showAsDropDown(video_type);
                break;
            case R.id.text_voice:
                switchPager(2);
                viewPager.setCurrentItem(2);
                break;
            case R.id.text_vr:
                switchPager(3);
                viewPager.setCurrentItem(3);
                break;
            case R.id.text_seven:
                popupWindowHelper.dismiss();
                setPopText(pop_text_seven);
                hotType = "7";
                videoViewModel.getHotVideo(0, hotType);
                break;
            case R.id.text_new:
                popupWindowHelper.dismiss();
                setPopText(text_new);
                hotType = "new";
                videoViewModel.getVipZone(0, "0", keyword);
                break;
            case R.id.text_month:
                popupWindowHelper.dismiss();
                setPopText(pop_text_month);
                hotType = "30";
                videoViewModel.getHotVideo(0, hotType);
                break;
            case R.id.text_all:
                popupWindowHelper.dismiss();
                setPopText(pop_text_all);
                hotType = "";
                videoViewModel.getHotVideo(0, hotType);
                break;
            case R.id.icon_change:
                icon_change.setVisibility(View.GONE);
                type_Lin.setVisibility(View.GONE);
                search_Lin.setVisibility(View.VISIBLE);

                break;
            case R.id.icon_search:
                //搜索
                switchPager(0);
                viewPager.setCurrentItem(0);
                keyword = keyword_text.getText().toString();
                videoViewModel.getVipZone(0, "0", keyword);
                break;
            case R.id.image_change:
                //
                keyword = "";
                keyword_text.setText("");
                search_Lin.setVisibility(View.GONE);
                type_Lin.setVisibility(View.VISIBLE);
                icon_change.setVisibility(View.VISIBLE);
                break;
            case R.id.progress_lin:
                //

                break;
            case R.id.hiding_linear:
                //

                break;
            case R.id.free_button:
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
                    boolean isvirified = (boolean) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.CHECK_PHONE, false);
                    if (isvirified) {
                        videoViewModel.setVipZoneFree();
                    } else {
                        Intent intent = new Intent(getActivity(), MobileVerifyActivity.class);
                        startActivity(intent);
                    }

                }

                break;
            case R.id.buy_vipbutton:
                //
//                chatPicViewModel.isShow.set(false);
//                chatPicViewModel.istpfStatus.set(false);

                Intent vipintent = new Intent(getActivity(), Buy_vipActivity.class);
                startActivity(vipintent);
                break;
            default:
                break;
        }
    }

    /***
     * 设置热度人气的字体
     */
    private void setPopText(TextView textview) {
        for (int i = 0; i < textViewList.size(); i++) {
            textViewList.get(i).setTextColor(getResources().getColor(R.color.black));
        }
        textview.setTextColor(getResources().getColor(R.color.more_color));
        video_new_text.setText(textview.getText());
    }

    @Override
    public void SuccessCallback(Object o) {
        if (o instanceof String) {
            if (o.equals("0")) {
                recycleView1.setLoadMoreEnable(false);
                recycleView1.setFooter(getString(R.string.drop_down_list_footer_no_more_text));
            }
            if (o.equals("1")) {
                recycleView2.setLoadMoreEnable(false);
                recycleView2.setFooter(getString(R.string.drop_down_list_footer_no_more_text));
            }
            if (o.equals("3")) {
                recycleView3.setLoadMoreEnable(false);
                recycleView3.setFooter(getString(R.string.drop_down_list_footer_no_more_text));
            }
            if (o.equals("4")) {
                recycleView4.setLoadMoreEnable(false);
                recycleView4.setFooter(getString(R.string.drop_down_list_footer_no_more_text));
            }
        } else if (o instanceof List) {
            //关键字的一个回调
            countryData.clear();
            countryData.add("视频");
            if (((List) o).get(0) instanceof String) {
                countryData.addAll((List) o);
            }
        }

    }

    @Override
    public void FailCallback(String result) {

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

                ChenQianLog.i("getUserVipZoneUrl:" + jsonObject);
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
                                    final String url = (String) gm.get(low);
                                    /*Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("url", url);
                                    msg.setData(bundle);
                                    msg.what = 2;
                                    mHandler.handleMessage(msg);*/
                                    ((MainActivity) getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setUserVipZoneUrl(url);
                                        }
                                    });
                                    //setUserVipZoneUrl(url);
                                }
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

    /**
     * 设置vip视频地址
     *
     * @param
     */
    public void setUserVipZoneUrl(String url) {
        LogUtils.e("setUserVipZoneUrl开始设置测速最快地址.....");
        RequestParams params = new RequestParams();
        //String head = new JsonUtil(AppManager.getAppManager().currentActivity()).httpHeadToJson(getActivity());
        String head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());
        //final JsonUtil jsonUtil = new JsonUtil(getActivity());

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
                    String resultCode = null;
                    resultCode = jsonObject.getString("resultCode");

                    if (resultCode.equals("1")) {

                    }
                    LogUtils.e("setUserVipZoneUrl设置测速最快地址成功.....");
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
