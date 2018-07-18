package com.bber.company.android.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;
import com.bber.company.android.bean.GlodBean;
import com.bber.company.android.bean.Level;
import com.bber.company.android.bean.RandomGrilBean;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.bean.Tag;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.preferenceConstants;
import com.bber.company.android.db.DBcolumns;
import com.bber.company.android.db.SellerUserDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.InterfaceMap;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.util.TimeUtil;
import com.bber.company.android.view.activity.GirlProfileActivity;
import com.bber.company.android.view.activity.HistroyActivity;
import com.bber.company.android.view.activity.KeyActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.view.activity.MyVIPActivity;
import com.bber.company.android.widget.MarqueeFactory;
import com.bber.company.android.widget.MarqueeView;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.NoticeMFE;
import com.bber.customview.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Author: Bruce
 * Date: 2016/5/9
 * Version:
 * Describe:
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    protected NetWork netWork;
    protected int SeekBarprogressw;
    private View view;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private SellerUserVo sellerUserVo;
    private ImageView call, guide_call1, guide_call2, guide_call3;
    private int currLevel, currStepKey, userTotalKey;
    private ImageView close, tv_matching, image_preferce;
    private TextView price, history_list, need_key, key_count, user_meau, text_1v1_id, doolservice;
    //    private TextUtil text_send;
    private int buyerId;
    private boolean closeMatching = false;
    private SeekBar seekBar;
    private List<Level> levels;
    private SellerUserDao sellerUserDao;
    private String virtureTip;
    private RelativeLayout view_matching, main_content;
    private LinearLayout simleDrawLinearlayout;
    private RelativeLayout view_gray_text;
    private RelativeLayout view_gray_point, guide_bg;
    private AnimationDrawable animationMatching;
    private ProgressBar progressBar;
    private MainActivity mainActivity;
    private MapSupport mapSupport;
    private TranslateAnimation translateAnixmationthedoor, translateAnixmation1v1;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    //1:上门服务, 2:1V1视频
    private int searchFlag;
    //选中的图片
    private int index = 0;
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean s) {
            if (levels == null || levels.size() == 0) {
                return;
            }
            SeekBarprogressw = progress;

            setTextColornew(progress);

        }


        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            setSeekBar(seekBar, SeekBarprogressw);

            // ChenQianLog.i("onProgressChanged:" + SeekBarprogressw);

        }
    };
    //当前定位的城市
    private String currentCity;
    private MarqueeView marqueeView;
    private List<String> datas;
    private MarqueeFactory<TextView, String> marqueeFactory1;
    private TextView icon_point;
    private AnimationDrawable animationDrawable;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String type = intent.getStringExtra("type");
            if (action.equals(Constants.ACTION_HOME)) {
                if ("addkey".equals(type)) {
                    int addkey = Tools.StringToInt(intent.getStringExtra("addKey"));
                    ((MainActivity) getActivity()).setUserAddTotalKey(addkey);
                    userTotalKey = ((MainActivity) getActivity()).getUserTotalKey();
//                    key_count.setText(userTotalKey + "");
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (mapSupport != null) {
                    mapSupport.startLocation();

                }
            } else {
                MyToast.makeTextAnim(getContext(), R.string.permission_on_location, 0, R.style.PopToast).show();
                closeAnimRun();

            }
        }
    }

    private void setSeekBar(SeekBar seekBar, int progress) {

        Level level = new Level();
        level.setExpendKey(4);
        level.setLevel(4);
        level.setName("海选周边美模");
        switch (4) {
            case 2:
                if (progress < 15) {
                    level = levels.get(0);
                    seekBar.setProgress(0);
                    setTextColor(0);
                } else {
                    level = levels.get(1);
                    seekBar.setProgress(20);
                    setTextColor(1);
                }
                break;
            case 3:
                if (progress < 15) {
                    level = levels.get(0);
                    seekBar.setProgress(0);
                    setTextColor(0);

                } else if (progress >= 15 && progress <= 30) {
                    level = levels.get(1);
                    seekBar.setProgress(20);
                    setTextColor(1);
                } else {
                    level = levels.get(2);
                    seekBar.setProgress(40);
                    setTextColor(2);
                }
                break;
            //点击进入的是4
            case 4:
                if (progress <= 50) {
                    //                        level = levels.get(0);
                    seekBar.setProgress(0);
                    setTextColor(0);
                    price.setText("上门服务");
                    searchFlag = 1;
                    index = 0;
                    mainActivity.setTittleTabVisable(preferenceConstants.MODLE_BACK_RID[index]);
                    //                    } else if (progress >= 15 && progress <= 30) {
                    //                        level = levels.get(1);
                    //                        seekBar.setProgress(20);
                    //                        setTextColor(1);
                    //                    } else if (progress > 30 && progress <= 50) {
                    //                        level = levels.get(2);
                    //                        seekBar.setProgress(40);
                    //                        setTextColor(2);
                } else if (progress > 50 && progress <= 100) {
                    //                        level = levels.get(3);
                    seekBar.setProgress(100);
                    setTextColor(1);
                    price.setText("1v1直播");
                    searchFlag = 2;
                    index = 1;
                    mainActivity.setTittleTabVisable(preferenceConstants.MODLE_BACK_RID[index]);
                }
                break;
            case 5://max 80
                if (progress < 15) {
                    level = levels.get(0);
                    seekBar.setProgress(0);
                    setTextColor(0);

                } else if (progress >= 15 && progress <= 30) {
                    level = levels.get(1);
                    seekBar.setProgress(20);
                    setTextColor(1);
                } else if (progress > 30 && progress <= 50) {
                    level = levels.get(2);
                    seekBar.setProgress(40);
                    setTextColor(2);
                } else if (progress > 50 && progress <= 70) {
                    level = levels.get(3);
                    seekBar.setProgress(60);
                    setTextColor(3);
                } else {
                    level = levels.get(4);
                    seekBar.setProgress(80);
                    setTextColor(4);
                }
                break;
            case 6://max 100
                if (progress < 15) {
                    level = levels.get(0);
                    seekBar.setProgress(0);
                    setTextColor(0);

                } else if (progress >= 15 && progress <= 30) {
                    level = levels.get(1);
                    seekBar.setProgress(20);
                    setTextColor(1);
                } else if (progress > 30 && progress <= 50) {
                    level = levels.get(2);
                    seekBar.setProgress(40);
                    setTextColor(2);
                } else if (progress > 50 && progress <= 70) {
                    level = levels.get(3);
                    seekBar.setProgress(60);
                    setTextColor(3);
                } else if (progress > 70 && progress <= 90) {
                    level = levels.get(4);
                    seekBar.setProgress(80);
                    setTextColor(4);
                } else {
                    level = levels.get(5);
                    seekBar.setProgress(100);
                    setTextColor(5);
                }
                break;
        }
        if (level == null) {
            return;
        }
//            if (currLevel != level.getLevel()) {
//                mainActivity.setTittleTabVisable(preferenceConstants.MODLE_BACK_RID[level.getLevel()]);
//            }
        currLevel = level.getLevel();
        currStepKey = level.getExpendKey();
//            need_key.setText(level.getExpendKey() + "");
//            if (levels.size() == 4 && level.getLevel() == 4) {
//                price.setText("￥" + level.getMinMoney() + "+");
//            } else {
//                price.setText("￥" + level.getMinMoney() + "-" + level.getMaxMoney());
//            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);
        mainActivity = (MainActivity) getActivity();
//        initSlidingMenu(savedInstanceState);
        initViews();
        try {
            initData();
        } catch (Exception e) {
            LogUtils.e("发送错误信息" + e.getMessage());
            if (netWork == null) {

            }
        }
        //比较当前时间天是否过了0点
        compreTime();
        setListeners();
        LogUtils.e("HomeFragment");
        return view;
    }

    private void compreTime() {
        String yesterday = (String) SharedPreferencesUtils.get(mainActivity, "yesterday", "");
        TimeUtil timeUtil = new TimeUtil();
        //如果是今天
        if (!Tools.isEmpty(yesterday)) {
            if (timeUtil.getJudgetoDay(yesterday)) {
                icon_point.setVisibility(View.GONE);
            } else {
                icon_point.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initViews() {
        toolbar = view.findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        user_meau = toolbar.findViewById(R.id.user_meau);
        key_count = view.findViewById(R.id.my_key);
        guide_bg = view.findViewById(R.id.guide_bg);
        guide_call1 = view.findViewById(R.id.guide_1);
        guide_call2 = view.findViewById(R.id.guide_2);
        guide_call3 = view.findViewById(R.id.guide_3);
        progressBar = view.findViewById(R.id.progressBar_home);
        main_content = view.findViewById(R.id.main_content);
//        need_key = (TextView) view.findViewById(R.id.need_key);
        call = view.findViewById(R.id.call);
        //   view_gray_point = view.findViewById(R.id.view_gray_point);
        view_gray_text = view.findViewById(R.id.view_gray_text);
        seekBar = view.findViewById(R.id.seekBar);

        price = view.findViewById(R.id.price);
        history_list = toolbar.findViewById(R.id.history_list);
        view_matching = view.findViewById(R.id.view_matching);
        close = view.findViewById(R.id.close);
        tv_matching = view.findViewById(R.id.radar);
        image_preferce = view.findViewById(R.id.image_pre);
//        text_send = (TextUtil) view.findViewById(R.id.text_send);

        doolservice = view.findViewById(R.id.text_dool_service);
        text_1v1_id = view.findViewById(R.id.text_1v1_id);
        doolservice.setPadding(0, 10, 0, 0);
        text_1v1_id.setPadding(0, 0, 0, 10);
        simleDrawLinearlayout = view.findViewById(R.id.simleDrawLinearlayout);
        marqueeView = view.findViewById(R.id.marqueeView);
        icon_point = view.findViewById(R.id.icon_point);
    }


    public void initData() {
        netWork = new NetWork(mainActivity);
        title.setText(R.string.app_chinse_name);
        buyerId = Tools.StringToInt(SharedPreferencesUtils.get(mainActivity, Constants.USERID, "-1") + "");
        boolean firstLoad = (boolean) SharedPreferencesUtils.get(mainActivity, "firstLoad", true);
        if (firstLoad) {
            guide_bg.setVisibility(View.VISIBLE);
            guide_call1.setVisibility(View.VISIBLE);
//            guide_call2.setVisibility(View.VISIBLE);
//            guide_call3.setVisibility(View.VISIBLE);
            SharedPreferencesUtils.put(mainActivity, "firstLoad", false);
        }
        //设置1:上门服务, 2:1V1视频
        searchFlag = 1;
        //设置默认的模特,消耗钥匙数量和等级都是4
        currLevel = 4;
        currStepKey = 4;
//        need_key.setText(4 + "");
        price.setText("上门服务");
        //获取level相关信息
        getLevelInfo();
        getMarquee();
        currentCity = (String) SharedPreferencesUtils.get(getActivity(), "currentCity", "");
        //首页闪烁
//        Glide.with(getActivity()).load(R.drawable.icon_share)
//                .asGif() //判断加载的url资源是否为gif格式的资源
//                .error(R.drawable.icon_model_1)
//                .into(image_preferce);

//        animationDrawable = (AnimationDrawable) image_preferce.getDrawable();
//        animationDrawable.start();

        registerBoradcastReceiver();
        sellerUserDao = new SellerUserDao(mainActivity);
        mapSupport = new MapSupport(mainActivity, new InterfaceMap() {
            @Override
            public void getPosSerchDate(List<PoiItem> poiItems, int type) {

            }

            @Override
            public void getClickLatLonPoint(double latitude, double longitude) {

            }

            @Override
            public void getLocation(String privince, String city, String district, double lat, double lng) {
                latitude = lat;
                longitude = lng;
                currentCity = city;
                matching();

            }

            @Override
            public void noLocation() {
                closeAnimRun();
                MyToast.makeTextAnim(getActivity(), R.string.homenotice, 0, R.style.PopToast).show();
            }
        });
    }

    private void setListeners() {
        call.setOnClickListener(this);
        close.setOnClickListener(this);
        guide_bg.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        history_list.setOnClickListener(this);
        key_count.setOnClickListener(this);
        user_meau.setOnClickListener(this);
        image_preferce.setOnClickListener(this);
        guide_call1.setOnClickListener(this);
        guide_call2.setOnClickListener(this);
        guide_call3.setOnClickListener(this);
    }


    /**
     * 匹配
     */
    private void matching() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(mainActivity);
        String head = jsonUtil.httpHeadToJson(mainActivity);
        params.put("head", head);
        params.put("buyerId", buyerId);
//        latitude = 39.912836;   //114.067714,22.555533 深圳地址
        params.put("latitude", latitude);
//        longitude = 116.41053;
        params.put("longitude", longitude);
        params.put("distance", 500);
        params.put("level", currLevel);
        params.put("keyStep", currStepKey);
        params.put("searchFlag", searchFlag);
        HttpUtil.get(Constants.getInstance().matching, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, JSONObject jsonObject) {
                if (Tools.jsonResult(mainActivity, jsonObject, null) || closeMatching) {
                    try {
                        LogUtils.e("匹配接口" + jsonObject.toString());
                        int resultCode = jsonObject.getInt("resultCode");
                        String resultMessage = jsonObject.getString("resultMessage");
                        //如果距离不匹配
                        if (resultCode == 4) {
                            // getQQinfo();
                            //gotoVirtualGoods();
                            closeAnimRunEnter();
                            MyToast.makeTextAnim(mainActivity, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            closeAnimRunEnter();
                            // 停止定位
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("dataCollection");
                    sellerUserVo = jsonUtil.jsonToSellerUserVo(jsonObject1.toString());
                    String lables = jsonObject1.getString("lables");
                    List<Tag> tags = jsonUtil.jsonToTags(lables);
                    if (sellerUserVo != null) {

                        userTotalKey = userTotalKey - currStepKey;
                        mainActivity.setUserTotalKey(userTotalKey);
//                        key_count.setText(" " + userTotalKey);
                        closeAnimRunEnter();
                        // 停止定位
                        Intent intent = new Intent(mainActivity, GirlProfileActivity.class);
                        intent.putExtra("sellerUserVo", sellerUserVo);
                        insertSellerUseTorHistory();//插入到浏览记录，ByBruce

                        if (tags != null && tags.size() > 0) {
                            intent.putExtra("tags", (Serializable) tags);
                        }
                        startActivityForResult(intent, 1);

                    } else {
                        getQQinfo();
                    }
                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "matching JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("match发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(mainActivity, R.string.getData_fail, 0, R.style.PopToast).show();
                closeAnimRun();
                // 停止定位
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /***
     * 通过妹子ID获取妹子信息
     * @param sellerID
     */
    private void getSellerInfo(int sellerID) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(mainActivity);
        String head = jsonUtil.httpHeadToJson(mainActivity);
        params.put("head", head);
        params.put("uSeller", sellerID);
        HttpUtil.get(Constants.getInstance().getSellerInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(mainActivity, jsonObject, progressBar)) {
                    return;
                }
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("dataCollection");
                    sellerUserVo = jsonUtil.jsonToSellerUserVo(jsonObject1.toString());
                    String lables = jsonObject1.getString("lables");
                    List<Tag> tags = jsonUtil.jsonToTags(lables);
                    if (sellerUserVo != null) {
                        Intent intent = new Intent(mainActivity, GirlProfileActivity.class);

                        if (tags != null && tags.size() > 0) {
                            intent.putExtra("tags", (Serializable) tags);
                        }
                        intent.putExtra("sellerUserVo", sellerUserVo);
                        insertSellerUseTorHistory();//插入到浏览记录，ByBruce
                        startActivityForResult(intent, 1);
                    }

                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getSellerInfo JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("getSellerInfo发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(mainActivity, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
//                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void startAnimRun() {
        if (view_matching != null && view_matching.getVisibility() == View.GONE) {
            closeMatching = false;
            view_matching.setVisibility(View.VISIBLE);
            tv_matching.setImageResource(R.drawable.match_amin);
            animationMatching = (AnimationDrawable) tv_matching.getDrawable();
            animationMatching.start();
            mainActivity.setTittleTabGone();
            Tools.Vibrate(mainActivity);
        }

        applicationPermissions();
    }


    /**
     * 检查定位权限
     */

    private void applicationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                if (mapSupport != null) {
                    mapSupport.startLocation();

                }
            }
        }else {
            if (mapSupport != null) {
                mapSupport.startLocation();

            }
        }

    }


    public void closeAnimRun() {
        if (view_matching != null && view_matching.getVisibility() == View.VISIBLE) {
            closeMatching = true;
            view_matching.setVisibility(View.GONE);
            animationMatching.stop();
            Tools.CancleVibrate();
            mainActivity.setTittleTabVisable(preferenceConstants.MODLE_BACK_RID[index]);
        }
        mapSupport.stopLocation();
    }

    public void closeAnimRunEnter() {
        if (view_matching != null && view_matching.getVisibility() == View.VISIBLE) {
            closeMatching = true;
            view_matching.setVisibility(View.GONE);
            tv_matching.clearAnimation();
            Tools.CancleVibrate();
            mainActivity.setTittleTabVisable(preferenceConstants.MODLE_BACK_RID[index]);
        }
        mapSupport.stopLocation();
    }

    /**
     * 获取QQ号码
     */
    private void getQQinfo() {
        RequestParams params = new RequestParams();
        params.put("type", 1);
        HttpUtil.get(Constants.getInstance().getQQNumber, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        virtureTip = getResources().getString(R.string.dialog_matching_nothing) + resultMessage;
                        gotoVirtualGoods();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });

    }

    /**
     * 插入到历史浏览记录中
     */
    private void insertSellerUseTorHistory() {
        if (!sellerUserDao.isExist(DBcolumns.TABLE_MATCH_HISTORY, sellerUserVo.getuSeller().toString())) {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String time = dateFormat.format(now);
            sellerUserVo.setNowTime(time);
            sellerUserDao.insert(sellerUserVo, DBcolumns.TABLE_MATCH_HISTORY);
            //通知首页刷新聊天会话列表
            Intent mIntent1 = new Intent(Constants.ACTION_HISTORY);
            mainActivity.sendBroadcast(mIntent1);
        }
    }

    /**
     * 如果没有匹配到妹纸，那么需要跳转至商业推广界面
     *
     * @return null
     * @auther brucs
     * @version v1.5.0
     */

    private void gotoVirtualGoods() {
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(mainActivity);
        String head = jsonUtil.httpHeadToJson(mainActivity);
        params.put("head", head);
        params.put("level", currLevel);
        Log.e("eeeeeeeeeeee", "Constants.checkSellerInviteCode:" + Constants.getInstance().getVirtualSellerId);
        HttpUtil.get(Constants.getInstance().getVirtualSellerId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                int resultCode = 1;
                try {
                    resultCode = jsonObject.getInt("dataCollection");
                    closeAnimRunEnter();
                    Intent intent = new Intent(mainActivity, GirlProfileActivity.class);
                    intent.putExtra("from", Constants.COME_FROM_VIRTUAL_GOODS);
                    intent.putExtra("sellerId", resultCode);
                    startActivityForResult(intent, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("gotoVirtualGoods发送错误信息返回了了服务器错误");
                MyToast.makeTextAnim(mainActivity, R.string.getData_fail, 0, R.style.PopToast).show();
                closeAnimRun();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    private void setTextColor(int progress) {
        for (int i = 0; i < levels.size() && i < view_gray_text.getChildCount(); i++) {
            TextView textView = (TextView) view_gray_text.getChildAt(i);
            if (i == progress) {
                // textView.setTextColor(getResources().getColor(R.color.pink));
                // textView.setPadding(0,20,0,0);
            } else {
                // textView.setTextColor(getResources().getColor(R.color.item_bg));
                //  textView.setPadding(0,0,20,0);
            }
        }
    }

    private void setTextColornew(int progress) {

        if (progress < 45 && searchFlag == 2) {
            searchFlag = 1;
            doolservice.setTextColor(getResources().getColor(R.color.pink));
            text_1v1_id.setTextColor(getResources().getColor(R.color.item_bg));
            translateAnixmation1v1 = new TranslateAnimation(0.0f, 0.0f, 0.0f, -10.0f);
            translateAnixmation1v1.setDuration(200);
            translateAnixmation1v1.setFillAfter(true);
            translateAnixmationthedoor = new TranslateAnimation(0.0f, 0.0f, 0.0f, 10.0f);
            translateAnixmationthedoor.setDuration(200);
            translateAnixmationthedoor.setFillAfter(true);
            text_1v1_id.startAnimation(translateAnixmation1v1);
            doolservice.startAnimation(translateAnixmationthedoor);

        } else if (progress > 55 && searchFlag == 1) {
            searchFlag = 2;
            text_1v1_id.setTextColor(getResources().getColor(R.color.pink));
            doolservice.setTextColor(getResources().getColor(R.color.item_bg));
            translateAnixmation1v1 = new TranslateAnimation(0.0f, 0.0f, 0.0f, 20.0f);
            translateAnixmation1v1.setDuration(200);
            translateAnixmation1v1.setFillAfter(true);
            translateAnixmationthedoor = new TranslateAnimation(0.0f, 0.0f, 0.0f, -20.0f);
            translateAnixmationthedoor.setDuration(200);
            translateAnixmationthedoor.setFillAfter(true);

            text_1v1_id.startAnimation(translateAnixmation1v1);
            doolservice.startAnimation(translateAnixmationthedoor);

        }
        // text_1v1_id.startAnimation(translateAnixmation1v1);
        // doolservice.startAnimation(translateAnixmationthedoor);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:

                if (searchFlag == 1) {
                    Calendar ca = Calendar.getInstance();
                    int hour = ca.get(Calendar.HOUR_OF_DAY);//小时
//                int hour = 2;//小时
                    if ((boolean) SharedPreferencesUtils.get(mainActivity, "firstCall", true)) {
                        LayoutInflater inflater = LayoutInflater.from(mainActivity);
                        View layout = inflater.inflate(R.layout.custom_alertdialog, null);
                        final AlertDialog dialog = DialogTool.createDialog(mainActivity, layout, R.string.dialog_matching_tip, R.string.cancle_tip, R.string.know_tip);
                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
//                            if (userTotalKey < currStepKey) {
//                                MyToast.makeTextAnim(mainActivity, R.string.key_out_of, 0, R.style.PopToast).show();
//                                return;
//                            }
                                if (!netWork.isNetworkConnected()) {
                                    MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
                                    return;
                                }
                                startAnimRun();
                            }
                        });
                        layout.findViewWithTag(0).setVisibility(View.GONE);
                        SharedPreferencesUtils.put(mainActivity, "firstCall", false);
                    } else if (hour > 0 && hour < 14) {
                        LayoutInflater inflater = LayoutInflater.from(mainActivity);
                        View layout = inflater.inflate(R.layout.custom_alertdialog, null);//
                        final AlertDialog dialog = DialogTool.createDialog(mainActivity, layout, R.string.dialog_matching_time_miss, R.string.cancle_tip, R.string.know_tip);
                        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
//                            if (userTotalKey < currStepKey) {
//                                MyToast.makeTextAnim(mainActivity, R.string.key_out_of, 0, R.style.PopToast).show();
//                                return;
//                            }
                                if (!netWork.isNetworkConnected()) {
                                    MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
                                    return;
                                }
                                startAnimRun();
                            }
                        });
                    } else {
//                    if (userTotalKey < currStepKey) {
//                        MyToast.makeTextAnim(mainActivity, R.string.key_out_of, 0, R.style.PopToast).show();
//                        return;
//                    }
                        if (!netWork.isNetworkConnected()) {
                            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
                            return;
                        }
                        startAnimRun();
                    }
                } else {
                    //跳转到直播的fragment
                    mainActivity.setLiveBroadcastMainFragment(v);
                }
                break;
            case R.id.close:
                closeAnimRun();
                // 停止定位
                break;
            case R.id.guide_bg:
//                guide_bg.setVisibility(View.GONE);
//                guide_call1.setVisibility(View.GONE);
//                guide_call2.setVisibility(View.GONE);
//                guide_call3.setVisibility(View.GONE);
                break;
            case R.id.history_list:
                Intent intent = new Intent(mainActivity, HistroyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_key:
                intent = new Intent(mainActivity, MyVIPActivity.class);
                startActivity(intent);
                break;
            case R.id.user_meau:
                mainActivity.toggle();
                break;
            case R.id.image_pre:
                icon_point.setVisibility(View.GONE);

                TimeUtil time = new TimeUtil();
                SharedPreferencesUtils.put(mainActivity, "yesterday", time.getTime());

                Bundle bundle = new Bundle();
                String invitecode = (String) SharedPreferencesUtils.get(getActivity(), Constants.INVITECODE, "");
                bundle.putString("inviteCode", invitecode);
                intent = new Intent(getActivity(), KeyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.guide_1:
                guide_call1.setVisibility(View.GONE);
                guide_call2.setVisibility(View.VISIBLE);
                break;
            case R.id.guide_2:
                guide_call2.setVisibility(View.GONE);
                guide_call3.setVisibility(View.VISIBLE);
                break;
            case R.id.guide_3:
                guide_call3.setVisibility(View.GONE);
                guide_bg.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    public void getMarquee() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        params.put("content-type", "application/x-www-form-urlencoded");
        final JsonUtil jsonUtil = new JsonUtil(mainActivity);
        String head = jsonUtil.httpHeadToJson(mainActivity);
        params.put("head", head);
        LogUtils.e("head" + head);
        params.put("city", currentCity);
        LogUtils.e("city" + currentCity);
        HttpUtil.post(Constants.getInstance().getMarquee, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    String resultCode = jsonObject.getString("resultCode");
                    //1表示成功 0表示失败
                    if (resultCode.equals("1")) {
                        JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                        String jsonObject1 = jsonObject.getString("resultMessage");
                        JSONObject jsonObject2 = new JSONObject(jsonObject1);

                        String vipGetWay = jsonObject2.getString("vipGetWay");
                        JsonUtil JsonUtil = new JsonUtil(getActivity());
                        GlodBean glodBean = JsonUtil.jsonToGlodBeanBean(vipGetWay);

                        String code = glodBean.getCode();
                        String url = glodBean.getUrl();
                        LogUtils.e("code" + code);
                        LogUtils.e("url" + url);
                        SharedPreferencesUtils.put(getActivity(), "code", code);
                        SharedPreferencesUtils.put(getActivity(), "url", url);
                        //为1的时候，有一个url地址
                        if (code.equals("1")) {

                        }
                        String data = dataCollection.toString();

                        final List<RandomGrilBean> randomGrilBeanList = jsonUtil.jsonToHomeGrilBean(data);
                        //跑马灯的文字内容
//                        String marqueeContent = dataCollection.getString("marqueeContent");
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        String showData = "";
                        String path = (String) SharedPreferencesUtils.get(getActivity(), "IMAGE_FILE", "");
                        boolean isfirst = false;
                        datas = new ArrayList<String>();
                        datas.clear();
                        for (int j = 0; j < randomGrilBeanList.size(); j++) {
                            datas.add(randomGrilBeanList.get(j).marqueeContent);
                            if (randomGrilBeanList.get(j).marqueeType == 1) {
                                if (isfirst == false) {
                                    for (int k = 0; k < randomGrilBeanList.get(j).sellers.size(); k++) {
                                        isfirst = true;
                                        View view = inflater.inflate(R.layout.fragment_item_image, null);
                                        SimpleDraweeView simpleView = view.findViewById(R.id.user_icon1);
                                        //设置监听事件
                                        final int finalK = k;
                                        final int finalJ = j;
                                        simpleView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                getSellerInfo(randomGrilBeanList.get(finalJ).sellers.get(finalK).uSeller);
                                            }
                                        });
                                        Uri urldata = Uri.parse(path + randomGrilBeanList.get(j).sellers.get(k).usHeadm);
                                        simpleView.setImageURI(urldata);
                                        simleDrawLinearlayout.addView(view);
                                    }
                                }
                            }
                        }
//                        text_send.setText(showData);
//                        datas = Arrays.asList(showData);
                        marqueeFactory1 = new NoticeMFE(getActivity());
                        marqueeView.setMarqueeFactory(marqueeFactory1);

                        marqueeFactory1.setOnItemClickListener(new MarqueeFactory.OnItemClickListener<TextView, String>() {
                            @Override
                            public void onItemClickListener(MarqueeFactory.ViewHolder<TextView, String> holder) {
                                Toast.makeText(getActivity(), holder.data, Toast.LENGTH_SHORT).show();
                            }
                        });
                        marqueeFactory1.setData(datas);
                        marqueeView.startFlipping();

                    } else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("getMarquee发送错误信息返回了了服务器错误" + throwable.getMessage());
                MyToast.makeTextAnim(mainActivity, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取level相关信息
     */
    private void getLevelInfo() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(mainActivity);
        String head = jsonUtil.httpHeadToJson(mainActivity);
        params.put("head", head);
        HttpUtil.get(Constants.getInstance().getSellerLevel, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(mainActivity, jsonObject, progressBar)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    userTotalKey = dataCollection.getInt("userTotalKey");
                    mainActivity.setUserTotalKey(userTotalKey);
//                    key_count.setText(" " + userTotalKey);
                    String list = dataCollection.getString("list");
                    levels = jsonUtil.jsonToLevels(list);

                    if (levels != null && levels.size() > 0) {
//                        setLevel();
                    }
                    progressBar.setVisibility(View.GONE);
                    main_content.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("getLevelInfo发送错误信息返回了了服务器错误" + throwable.getMessage());
                MyToast.makeTextAnim(mainActivity, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
//                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            LogUtils.e("onActivityResult发送错误信息返回了了服务器错误");
            if (data != null) {
                boolean updateIcon = data.getBooleanExtra("updateIcon", false);
                if (updateIcon) {
                    mainActivity.invalidateOptionsMenu();
                }
            } else {
                startAnimRun();
            }
        }
    }

    public boolean stopMatching() {
        if (view_matching != null && view_matching.getVisibility() == View.VISIBLE) {
            closeAnimRun();
            // 停止定位
            return true;
        } else {
            return false;
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_HOME);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        mapSupport.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapSupport.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapSupport.onPause();
    }


}
