package com.bber.company.android.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.livebroadcast.BroadcasFragmentClassificationBean;
import com.bber.company.android.bean.livebroadcast.BroadcaseClasssificationBean;
import com.bber.company.android.bean.livebroadcast.BroadcaseLowMonet;
import com.bber.company.android.bean.livebroadcast.BroadcaseSearchSingleBean;
import com.bber.company.android.bean.livebroadcast.BroadcastDataBean;
import com.bber.company.android.bean.livebroadcast.NearbyBroadcastBean;
import com.bber.company.android.bean.livebroadcast.RecommendBroadcastBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.SocketHelp;
import com.bber.company.android.view.adapter.BroadcastSearchGridViewAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * 主播详情页面
 */
public class LiveBroadcastDetailsActivity extends BaseAppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, SocketHelp.SocketClientDelegateData {

    private BGABanner details_carousell_figure_nearby_id;
    private FrameLayout details_back_image_id, details_back_tobay_id, details_edit_id;
    private LinearLayout details_broadcast_id, button_layout;
    private LinearLayout details_smment_id;
    private RelativeLayout toolbarid;
    private TextView details_broadcast_name;
    private TextView details_boradcase_poastage;
    private TextView details_broadcast_age;
    private TextView details_broadcast_time;
    private TextView details_broadcast_id_id, title_name_id;
    private String fragmentname;//从什么地方到这个页面

    private RecommendBroadcastBean.DataCollectionBean broadcastdata;
    private NearbyBroadcastBean.DataCollectionBean nearbybroadcastdata;
    private BroadcaseSearchSingleBean.DataCollectionBean dataCollectionBean;
    private BroadcaseClasssificationBean.DataCollectionBean calssdataCollectionBean;

    private BroadcasFragmentClassificationBean.DataCollectionBean.AnchorBean anchorBean;

    private BroadcastDataBean broadcastDataBean;
    private List<String> imagelist, labelist;
    private ImageView details_broadcast_styte;
    private int broadcaststatu;//主播状
    private String broadcast_name, boradcase_poastage, broadcast_age,
            broadcast_id_id, broadcast_money, broadcast_describetext, usetid, usetHeadm;

    private SpannableStringBuilder broadcast_time, broadcast_describe;
    private TextView details_boradcase_poastagetext;
    private GridView exhibition_grviewview;
    private Switch details_broadcast_styte_switch;

    private SocketHelp socketClientHelp;

    private String MD5key = "842abfcf808c32877ac9186f240e3b41";
    private SocketHelp socketHelp;

    /**
     * 接通主播是的最低金额
     */
    private BroadcaseLowMonet broadcaseLowMonet;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:

                    MyToast.makeTextAnim(LiveBroadcastDetailsActivity.this, (String) msg.obj, 2, R.style.PopToast).show();

                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_live_broadcast_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if (!fragmentname.equals("Main")) {
        //    requstSocketService(); // 非主播用户去请求socket 服务

            getAnchorQuota(); // 获取主播接通的最低金额
            initView();
            initLiener();
            setBroadcastStatus();

        }
    }

    /**
     * 登录socket
     */
    private void requstSocketService() {
        socketHelp = SocketHelp.initService(getApplication());
        if (!socketHelp.getSocketConnectState()) {
            //10.60.170.83 30000
            //10.60.2.6 8822
            socketHelp.connenctSocket("10.60.170.165", 8822);
        }
        if (socketHelp != null) {
            socketHelp.sendMessage(getSocketLogData(), this);
        }
    }

    /**
     * 获取 请求socket 需要的参数
     */
    private String getSocketLogData() {
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        String datawei = "head=" + head + "#type=1842abfcf808c32877ac9186f240e3b41";
        String sign = Tools.md5(datawei);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("head", head);
            jsonObject.put("type", 1);
            jsonObject.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private void initLiener() {
        details_back_image_id.setOnClickListener(this);
        details_edit_id.setOnClickListener(this);
        details_back_tobay_id.setOnClickListener(this);
        details_broadcast_id.setOnClickListener(this);
        details_smment_id.setOnClickListener(this);

        if (fragmentname.equals("Main")) {
            details_broadcast_styte_switch.setOnCheckedChangeListener(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * 获取主播数据
     */
    private void initData() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("broadcest_setvice");
        registerReceiver(broadcastReceiver, intentFilter);
        Intent intent = getIntent();
        fragmentname = intent.getStringExtra("fragmentname");

        switch (fragmentname) {

            case "Recommend":// 推荐
                broadcastdata = (RecommendBroadcastBean.DataCollectionBean) intent.getSerializableExtra("data");
                imagelist = broadcastdata.getImages();

                String labe = broadcastdata.getType(); //标签
                String[] labels = (labe.split(","));
                labelist = Arrays.asList(labels);

                broadcaststatu = broadcastdata.getStatus();
                long age = broadcastdata.getAge();//年龄
                long agelog = (System.currentTimeMillis() - age) / 1000 / 60 / 60 / 24 / 365;
                broadcast_age = "年龄：" + agelog + " 岁";
                int money = broadcastdata.getMoney();//资费
                int time = broadcastdata.getTime();//主播分钟
                usetid = broadcastdata.getBuyer_id() + "";//主播id
                usetHeadm = broadcastdata.getHeadm();//头像
                broadcast_id_id = "ID: " + usetid;//用户id

                broadcast_time = getSpannableString("接通时间：", time + "分钟");
                broadcast_describe = getSpannableString("资费：", money + "元/分钟");
                broadcast_name = broadcastdata.getName();//主播名称
                broadcast_describetext = broadcastdata.getDescribe();//简介
                break;

            case "Nearby"://附近
                nearbybroadcastdata = (NearbyBroadcastBean.DataCollectionBean) intent.getSerializableExtra("data");
                imagelist = nearbybroadcastdata.getImages();
                broadcaststatu = nearbybroadcastdata.getStatus();
                long Nearbyage = nearbybroadcastdata.getAge();//年龄
                long agee = (System.currentTimeMillis() - Nearbyage) / 1000 / 60 / 60 / 24 / 365;
                broadcast_age = "年龄：" + agee + " 岁";
                ChenQianLog.i("age:" + Nearbyage + "  agee：" + agee);
                int Nearbymoney = nearbybroadcastdata.getMoney();//资费
                int Nearbytime = nearbybroadcastdata.getTime();//主播分钟

                usetid = nearbybroadcastdata.getBuyer_id() + "";//主播id
                usetHeadm = nearbybroadcastdata.getHeadm();//头像
                broadcast_id_id = "ID: " + usetid;//用户id


                String labenearby = nearbybroadcastdata.getType(); //标签
                String[] labelsnearby = (labenearby.split(","));
                labelist = Arrays.asList(labelsnearby);
                broadcast_time = getSpannableString("接通时间：", Nearbytime + "分钟");
                broadcast_describe = getSpannableString("资费：", Nearbymoney + "元/分钟");
                broadcast_name = nearbybroadcastdata.getName();//主播名称
                broadcast_describetext = nearbybroadcastdata.getDescribe();//简介
                break;

            case "search":// 搜索
                dataCollectionBean = (BroadcaseSearchSingleBean.DataCollectionBean) intent.getSerializableExtra("data");
                imagelist = dataCollectionBean.getImages();
                broadcaststatu = dataCollectionBean.getStatus();
                long agesearch = dataCollectionBean.getAge();//年龄
                long agelogs = (System.currentTimeMillis() - agesearch) / 1000 / 60 / 60 / 24 / 365;
                broadcast_age = "年龄：" + agelogs + " 岁";
                int moneysearch = dataCollectionBean.getMoney();//资费
                int timesearch = dataCollectionBean.getTime();//主播分钟


                usetid = dataCollectionBean.getBuyer_id() + "";//主播id
                usetHeadm = dataCollectionBean.getHeadm();//头像
                broadcast_id_id = "ID: " + usetid;//用户id


                String labensearch = dataCollectionBean.getType(); //标签
                String[] labelsnsearch = (labensearch.split(","));
                labelist = Arrays.asList(labelsnsearch);

                broadcast_time = getSpannableString("接通时间：", timesearch + "分钟");
                broadcast_describe = getSpannableString("资费：", moneysearch + "元/分钟");
                broadcast_name = dataCollectionBean.getName();//主播名称
                broadcast_describetext = dataCollectionBean.getDescribe();//简介
                break;
            case "broadcastcalssfiftion":// 分类搜索 fragementclassification  anchorBean
                calssdataCollectionBean = (BroadcaseClasssificationBean.DataCollectionBean) intent.getSerializableExtra("data");
                imagelist = calssdataCollectionBean.getImages();
                broadcaststatu = calssdataCollectionBean.getStatus();
                long agescalssfiftion = calssdataCollectionBean.getAge();//年龄
                long agelogscalssfiftion = (System.currentTimeMillis() - agescalssfiftion) / 1000 / 60 / 60 / 24 / 365;
                broadcast_age = "年龄：" + agelogscalssfiftion + " 岁";
                int moneycalssfiftion = calssdataCollectionBean.getMoney();//资费
                int timecalssfiftion = calssdataCollectionBean.getTime();//主播分钟


                usetid = calssdataCollectionBean.getBuyer_id() + "";//主播id
                usetHeadm = calssdataCollectionBean.getHeadm();//头像
                broadcast_id_id = "ID: " + usetid;//用户id

                String labebroadcastcalssfiftion = calssdataCollectionBean.getType(); //标签
                String[] labbroadcastcalssfiftion = (labebroadcastcalssfiftion.split(","));
                labelist = Arrays.asList(labbroadcastcalssfiftion);

                broadcast_time = getSpannableString("接通时间：", timecalssfiftion + "分钟");
                broadcast_describe = getSpannableString("资费：", moneycalssfiftion + "元/分钟");
                broadcast_name = calssdataCollectionBean.getName();//主播名称
                broadcast_describetext = calssdataCollectionBean.getDescribe();//简介
                break;

            case "fragementclassification":// fragement分类  anchorBean
                anchorBean = (BroadcasFragmentClassificationBean.DataCollectionBean.AnchorBean) intent.getSerializableExtra("data");
                imagelist = anchorBean.getImages();
                broadcaststatu = anchorBean.getStatus();
                long ageanchorBean = anchorBean.getAge();//年龄
                long agelogsanchorBean = (System.currentTimeMillis() - ageanchorBean) / 1000 / 60 / 60 / 24 / 365;
                broadcast_age = "年龄：" + agelogsanchorBean + " 岁";
                int moneyanchorBean = anchorBean.getMoney();//资费
                int timeanchorBean = anchorBean.getTime();//主播分钟


                usetid = anchorBean.getBuyer_id() + "";//主播id
                usetHeadm = anchorBean.getHeadm();//头像
                broadcast_id_id = "ID: " + usetid;//用户id

                String labebfragementclassification = anchorBean.getType(); //标签
                String[] lafragementclassification = (labebfragementclassification.split(","));
                labelist = Arrays.asList(lafragementclassification);

                broadcast_time = getSpannableString("接通时间：", timeanchorBean + "分钟");
                broadcast_describe = getSpannableString("资费：", moneyanchorBean + "元/分钟");
                broadcast_name = anchorBean.getName();//主播名称
                broadcast_describetext = anchorBean.getDescribe();//简介
                break;

            case "Main": //主播页面

                getLiveBroadcaseData();
                break;
        }
    }

    /**
     * 从网络获取到数据
     */
    private void getLiveBroadcaseData() {

        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getAnchorUser, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);

                        ChenQianLog.i("主播数据json:" + requst);

                        if (jsonObject.getInt("resultCode") == 1) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            broadcastDataBean = new Gson().fromJson(requst, BroadcastDataBean.class);
                            // toolbar.setVisibility(View.VISIBLE);
                            broadcaststatu = broadcastDataBean.getDataCollection().getStatus();

                            long ureage = broadcastDataBean.getDataCollection().getAge(); //年龄的计算
                            long agee;
                            if (ureage != 0) {
                                agee = (System.currentTimeMillis() - ureage) / 1000 / 60 / 60 / 24 / 365;
                            } else {
                                agee = 0;
                            }
                            broadcast_age = "年龄：" + agee + " 岁";
                            ChenQianLog.i("age:" + "  agee：" + agee);
                            int Nearbymoney = broadcastDataBean.getDataCollection().getMoney();//资费
                            int Nearbytime = broadcastDataBean.getDataCollection().getTime();//主播分钟
                            int Nearbyid = broadcastDataBean.getDataCollection().getBuyer_id();//主播id


                            String labebfragementclassification = broadcastDataBean.getDataCollection().getType(); //标签
                            String[] lafragementclassification = (labebfragementclassification.split(","));
                            labelist = Arrays.asList(lafragementclassification);

                            broadcast_id_id = "ID:" + Nearbyid;
                            broadcast_time = getSpannableString("接通时间：", Nearbytime + "分钟");
                            broadcast_describe = getSpannableString("资费：", Nearbymoney + "元/分钟");
                            broadcast_name = broadcastDataBean.getDataCollection().getName();//主播名称
                            broadcast_describetext = broadcastDataBean.getDataCollection().getDescribe();//简介

                            imagelist = (List<String>) broadcastDataBean.getDataCollection().getImages();

                            initView();
                            initLiener();
                            setBroadcastStatus();
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
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(LiveBroadcastDetailsActivity.this);
            }
        });

    }


    /**
     * 更新主播状态
     */
    public void uploadAnchor(int status) {

        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        params.put("flag", 2);
        params.put("status", status);
        String uploadAnchoryui = com.bber.company.android.constants.Constants.getInstance().uploadAnchor;

        HttpUtil.post(uploadAnchoryui, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);

                        String resultMessage = jsonObject.getString("resultMessage");
                        ChenQianLog.i("修改主播状态json:" + requst);

                        if (jsonObject.getInt("resultCode") == 1) {

                            getLiveBroadcaseData();
                            MyToast.makeTextAnim(getApplicationContext(), resultMessage, 0, R.style.PopToast).show();

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
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(LiveBroadcastDetailsActivity.this);
            }
        });
    }

    /**
     * 从网络获取用户接通主播账户最低金额
     */
    private void getAnchorQuota() {

        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getAnchorQuota, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);

                        ChenQianLog.i("用户接通主播账户最低金额json:" + requst);

                        if (jsonObject.getInt("resultCode") == 1) {

                            broadcaseLowMonet = new Gson().fromJson(requst, BroadcaseLowMonet.class);
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
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(LiveBroadcastDetailsActivity.this);
            }
        });

    }

    private void initView() {
        details_carousell_figure_nearby_id = findViewById(R.id.details_carousell_figure_nearby_id);
        details_back_image_id = findViewById(R.id.details_back_image_id);
        title_name_id = findViewById(R.id.title_name_id);
        details_edit_id = findViewById(R.id.details_edit_id);
        details_back_tobay_id = findViewById(R.id.details_back_tobay_id);
        details_broadcast_id = findViewById(R.id.details_broadcast_id);
        details_smment_id = findViewById(R.id.details_smment_id);
        button_layout = findViewById(R.id.button_layout);
        toolbarid = findViewById(R.id.toolbarid);
        details_broadcast_styte_switch = findViewById(R.id.details_broadcast_styte_switch);
        if (fragmentname.equals("Main")) {
            details_back_image_id.setVisibility(View.GONE);
            toolbarid.setVisibility(View.VISIBLE);
            title_name_id.setText(broadcast_name);
            button_layout.setVisibility(View.GONE);
            details_broadcast_styte_switch.setVisibility(View.VISIBLE);
        } else {
            details_back_image_id.setVisibility(View.VISIBLE);
            toolbarid.setVisibility(View.GONE);
            details_broadcast_styte_switch.setVisibility(View.GONE);
        }
        details_carousell_figure_nearby_id.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, final int position) {
                /**
                 * 图片地址
                 * */
                StringBuffer imageurlhead = new StringBuffer();
                String iamgeurihead = (String) SharedPreferencesUtils.get(LiveBroadcastDetailsActivity.this, "IMAGE_FILE", "");
                imageurlhead.append(iamgeurihead);
                imageurlhead.append(model);
                Glide.with(LiveBroadcastDetailsActivity.this).load(imageurlhead.toString()).into(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] images = imagelist.toArray(new String[imagelist.size()]);
                        Intent intents = new Intent(getApplicationContext(), BrowseImageActivity.class);
                        intents.putExtra("fromAlbum", true);
                        intents.putExtra("images", images);
                        intents.putExtra("currentIndex", position);
                        startActivity(intents);
                    }
                });
            }
        });
        details_carousell_figure_nearby_id.setData(imagelist, null);
        details_carousell_figure_nearby_id.setAutoPlayInterval(3000);
        details_broadcast_styte = findViewById(R.id.details_broadcast_styte);
        details_broadcast_styte.setOnClickListener(this);

        details_broadcast_name = findViewById(R.id.details_broadcast_name);
        details_broadcast_name.setOnClickListener(this);
        details_boradcase_poastage = findViewById(R.id.details_boradcase_poastage);
        details_boradcase_poastage.setOnClickListener(this);
        details_broadcast_age = findViewById(R.id.details_broadcast_age);
        details_broadcast_age.setOnClickListener(this);
        details_broadcast_time = findViewById(R.id.details_broadcast_time);
        details_broadcast_time.setOnClickListener(this);
        details_broadcast_id_id = findViewById(R.id.details_broadcast_id_id);
        details_broadcast_id_id.setOnClickListener(this);
        details_boradcase_poastagetext = findViewById(R.id.details_boradcase_poastagetext);
        details_boradcase_poastagetext.setOnClickListener(this);
        exhibition_grviewview = findViewById(R.id.exhibition_grviewview);
        exhibition_grviewview.setAdapter(new BroadcastSearchGridViewAdapter(getApplicationContext(), labelist));


    }

    /**
     * 设置主播状态
     */
    private void setBroadcastStatus() {
        switch (broadcaststatu) {

            case 1:

                details_broadcast_styte.setImageResource(R.mipmap.livebroadcast_status_offline);
                details_broadcast_styte_switch.setChecked(false);
                break;
            case 2:
                details_broadcast_styte.setImageResource(R.mipmap.livebroadcast_status_bebusy);

                break;
            case 3:
                details_broadcast_styte.setImageResource(R.mipmap.livebroadcast_status_online);
                details_broadcast_styte_switch.setChecked(true);

                break;
        }
        details_broadcast_name.setText(broadcast_name);
        details_boradcase_poastage.setText(broadcast_describe);
        details_broadcast_time.setText(broadcast_time);
        details_broadcast_id_id.setText(broadcast_id_id);
        details_broadcast_age.setText(broadcast_age);
        details_boradcase_poastagetext.setText(broadcast_describetext);
    }

    boolean isConnect;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.details_back_image_id: //返回按钮
            case R.id.details_back_tobay_id:

                finish();
                break;
            case R.id.details_smment_id: //发消息
                Intent intent2 = new Intent(this, BroadcastChatActivity.class);
                intent2.putExtra("username", broadcast_name);
                intent2.putExtra("account", usetid);
                intent2.putExtra("ubHeadm", usetHeadm);
                startActivity(intent2);
                break;
            case R.id.details_broadcast_id: //视频聊天

                String userMoney = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.USER_MONEY, "");
                int live = (int) SharedPreferencesUtils.get(getApplicationContext(), Constants.BROADCAST_LEVEL, 1); // 是否是主播
                String lowMoney = broadcaseLowMonet.getDataCollection().getLowMoney();

                if (live == 3) {
                    MyToast.makeTextAnim(getApplicationContext(), "主播用户不能与主播聊天", 0, R.style.PopToast).show();
                    break;
                }

                if (broadcaststatu != 3) {
                    MyToast.makeTextAnim(getApplicationContext(), "主播忙碌", 0, R.style.PopToast).show();
                    break;
                }

               /* if (Double.parseDouble(userMoney) < Integer.parseInt(lowMoney)) {
                    MyToast.makeTextAnim(getApplicationContext(), "余额不足", 0, R.style.PopToast).show();
                    break;
                }*/
                String token = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.BROADCAST_SETVICE_TOKEN, "");
                String sign = (String) SharedPreferencesUtils.get(getApplicationContext(), Constants.BROADCAST_SETVICE_SIGN, "");
               /* if (sign == null || token == null) {
                 //   requstSocketService();
                    // MyToast.makeTextAnim(getApplicationContext(), "token 为空", 0, R.style.PopToast).show();

                    break;
                } else {
                    sendBroadcastRequest();
                }*/

                sendBroadcastRequest();
                break;

            case R.id.details_edit_id: // 到编辑主播资料
                Intent intent = new Intent(getApplicationContext(), EditBroadcastDataActivity.class);
                intent.putExtra("broadcastDataBean", broadcastDataBean);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 发起直播请求
     */
    private void sendBroadcastRequest() {
        String userids = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        String usetname = (String) SharedPreferencesUtils.get(this, Constants.USERNAME, "");
        DialogView.show(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", userids); //自己的id
            jsonObject.put("messageType", Constants.MKAgoraMessageTypeSendRequestLive);
            jsonObject.put("ubHeadm", usetHeadm); //头像
            jsonObject.put("ubName", usetname); // 用户名
            jsonObject.put("createDate", getDateToString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        isConnect = false;
        MyApplication.getContext().getmAgoraAPI().messageInstantSend(usetid, 0, jsonObject.toString(), Constants.MKAgoraMessageTypeSendRequestLive + "");

        Thread time = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 20);

                    if (!isConnect) {
                        DialogView.dismiss(LiveBroadcastDetailsActivity.this);
                        //  MyToast.makeTextAnim(LiveBroadcastDetailsActivity.this, "主播未接听", 2, R.style.PopToast).show();

                        Message message = new Message();
                        message.what = 1;
                        message.obj = "主播未接听";
                        handler.sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        time.start();

    }

    public static String getDateToString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 拼接不同颜色的字符串
     *
     * @param str1 黑色字体
     * @param str2 粉红色字体
     */

    private SpannableStringBuilder getSpannableString(String str1, String str2) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        SpannableString spannableString1 = Tools.getSpannableString(str1, 0xff666666, 1.0f);
        SpannableString spannableString2 = Tools.getSpannableString(str2, 0xFFFF0066, 1.0f);
        spannableString.append(spannableString1).append(spannableString2);
        return spannableString;
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
                    case "refuse_video": //拒绝视频请求

                        String text = getResources().getString(R.string.upload_video_refuse);
                        isConnect = true;
                        MyToast.makeTextAnim(LiveBroadcastDetailsActivity.this, broadcast_name + text, 0, R.style.PopToast).show();
                        DialogView.dismiss(LiveBroadcastDetailsActivity.this);
                        break;

                    case "allow_video"://接受 视频
                        //关闭Dialog

                        isConnect = true;
                        DialogView.dismiss(LiveBroadcastDetailsActivity.this);
                        //从广播里拿到房间号
                        String channelName = intent.getStringExtra("channelName");
                        String usetidss = intent.getStringExtra("usetid");
                        Intent intents = new Intent(LiveBroadcastDetailsActivity.this, VideoCallActivityActivity.class);
                        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intents.putExtra("channelName", channelName);
                        intents.putExtra("userid", channelName);
                        intents.putExtra("sellBuyerId", usetid);
                        startActivity(intents);
                        break;
                }
            }
        }
    };


    int socketRequsetnumber = 1;

    /**
     * 请求 socket 回传数据
     */
    @Override
    public void sendSuccess(String message) {

        /**
         * {
         "resultCode": 1,
         "sign": "e75e17a358a0495842d27c96dbefa5ef",
         "massage": "",
         "resultMessage": "",
         "type": 1,
         "token": "d96e2f56d60b4d6dbfdc5697019613d9"
         }*/

        ChenQianLog.i("Socket Json:" + message);
        try {
            JSONObject object = new JSONObject(message);
            String resultMessage = object.getString("resultMessage");
            int type = object.getInt("type");

            int resultCode = object.getInt("resultCode");

            if (resultCode != 0 && type == 1) {
                String sign = object.getString("sign");
                String token = object.getString("token");
                if (sign != null && token != null) {//请求token 成功
                    SharedPreferencesUtils.put(getApplicationContext(), Constants.BROADCAST_SETVICE_SIGN, sign);
                    SharedPreferencesUtils.put(getApplicationContext(), Constants.BROADCAST_SETVICE_TOKEN, token);
                } else { //否则再请求一次
                    socketRequsetnumber++;
                    if (socketRequsetnumber < 5) {
                     //   requstSocketService(); //如果请求失败 再请求一次
                    } else {

                        ChenQianLog.i("socket 请求次数过多");
                    }
                }
            } else {
                if (type != 30) {
                    MyToast.makeTextAnim(getApplicationContext(), resultMessage, 0, R.style.PopToast).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***/
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            uploadAnchor(3);
        } else {
            uploadAnchor(1);
        }
    }
}
