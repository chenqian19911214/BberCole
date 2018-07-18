package com.bber.company.android.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.VipInforBean;
import com.bber.company.android.bean.VipServiceBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.adapter.BuyVipAdapter;
import com.bber.company.android.view.adapter.VipItemListAdapter;
import com.bber.company.android.widget.MyToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BuyvipActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView, recyclerView_item;
    private BuyVipAdapter adapter;
    private SimpleDraweeView user_icon;
    private VipItemListAdapter vipAdapter;
    private List<VipInforBean> viplist;
    private List<VipServiceBean> vipServiiceList;
    private int mType = 0;
    private HashMap listMoney = new HashMap();
    private Button btn_sure;
    private Double payMoney;
    private String headurl, actiStartTime, actiEndTime;
    private TextView tv_vip_time, tv_now_level, tv_vip_activity_time;
    private int nowVipId, nowVipLevel;
    private String nowVipName, vipStartTime, vipEndTime;
    private int payVipId = 0;
    private int mPosition = 0;
    private int resId[] = {R.mipmap.vip_big_golden, R.mipmap.vip_big_golden, R.mipmap.vip_big_golden,
            R.mipmap.vip_big_golden, R.mipmap.vip_big_golden, R.mipmap.vip_big_golden,
            R.mipmap.vip_big_golden, R.mipmap.vip_big_golden, R.mipmap.vip_big_golden};

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_buyvip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getContext();
        app.newActivity.add(this);

        initViews();
        setListeners();
        initData();

        //获取订单消息
        getVipListInfor();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView_item = findViewById(R.id.recyclerView_item);
        tv_vip_time = findViewById(R.id.tv_vip_time);
        tv_now_level = findViewById(R.id.tv_now_level);
        tv_vip_activity_time = findViewById(R.id.tv_vip_activity_time);
        user_icon = findViewById(R.id.user_icon);
        btn_sure = findViewById(R.id.btn_sure);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_item.setLayoutManager(linearLayoutManager);
    }


    /**
     * 设置监听器
     */
    private void setListeners() {
        btn_sure.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        headurl = (String) SharedPreferencesUtils.get(BuyvipActivity.this, Constants.USERICON, "");
        nowVipId = (int) SharedPreferencesUtils.get(BuyvipActivity.this, Constants.VIP_ID, 0);
        vipStartTime = (String) SharedPreferencesUtils.get(BuyvipActivity.this, Constants.VIP_START_TIME, "");
        vipEndTime = (String) SharedPreferencesUtils.get(BuyvipActivity.this, Constants.VIP_END_TIEM, "");

        Uri uri = Uri.parse(headurl);
        user_icon.setImageURI(uri);
        title.setText(R.string.vip_list);
        vipServiiceList = new ArrayList<>();
        viplist = new ArrayList<>();
        adapter = new BuyVipAdapter(vipServiiceList);
        vipAdapter = new VipItemListAdapter(this, viplist);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_item.setAdapter(vipAdapter);
        recyclerView_item.setVisibility(View.VISIBLE);
        vipAdapter.setOnItemClickListener(new VipItemListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPosition = position;
                payVipId = viplist.get(position).getVipId();
                actiStartTime = viplist.get(position).getVipDisStartTime();
                actiEndTime = viplist.get(position).getVipDisEndTime();
                actiStartTime = Tools.dateToDate(actiStartTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
                actiEndTime = Tools.dateToDate(actiEndTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
                int level = viplist.get(position).getVipLevel();
                setVipInfor(position, level);
            }
        });

        if (nowVipId > 0) {
            getVipUpgradeMoney();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                Intent intent = new Intent(BuyvipActivity.this, PayVipActivity.class);
                intent.putExtra(Constants.PAY_MONEY, payMoney);
                intent.putExtra(Constants.VIP_ID, payVipId);
                intent.putExtra("vipname", viplist.get(mPosition).getVipName());
                intent.putExtra("type", mType);
                intent.putExtra("icon", viplist.get(mPosition).getVipResId());
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取VIP的信息
     */
    public void getVipListInfor() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getVipList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(BuyvipActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray data = jsonObject.getJSONArray("dataCollection");
                    getVipListData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BuyvipActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 会员升级
     */
    public void getVipUpgradeMoney() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerUserId", buyerId);
        final String str = buyerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);

        HttpUtil.post(Constants.getInstance().getVipUpgradeMoney, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(BuyvipActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    JSONArray UpgradeMoney = dataCollection.getJSONArray("UpgradeMoney");
                    for (int j = 0; j < UpgradeMoney.length(); j++) {
                        JSONObject obj = (JSONObject) UpgradeMoney.get(j);
                        int vipId = obj.getInt("vipId");
                        double money = obj.getDouble("money");
                        listMoney.put(vipId, money);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BuyvipActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 解析数据
     *
     * @param data
     */
    public void getVipListData(JSONArray data) {
        JsonUtil jsonUtil = new JsonUtil(BuyvipActivity.this);
        for (int i = 0; i < data.length(); i++) {
            try {
                VipInforBean vipInforBean = new VipInforBean();
                List<VipServiceBean> vipInforBeanList = new ArrayList<>();
                JSONObject obj = (JSONObject) data.get(i);
                vipInforBean.setVipId(obj.getInt("vipId"));
                vipInforBean.setVipName(obj.getString("vipName"));
                vipInforBean.setVipValidity(obj.getString("vipValidity"));
                vipInforBean.setVipMoney(obj.getDouble("vipMoney"));
                vipInforBean.setVipLevel(obj.getInt("vipLevel"));
                vipInforBean.setVipDisMoney(obj.getDouble("vipDisMoney"));
                vipInforBean.setVipDisStartTime(obj.getString("vipDisStartTime"));
                vipInforBean.setVipDisEndTime(obj.getString("vipDisEndTime"));
                String objarry = obj.getString("vipDetail");

                //设置vip为游客的信息
                setNoVipInfor(obj.getInt("vipId"), vipInforBean);
                //数组大于0才开始进行下一步的解析
                if (objarry.length() > 0) {
                    vipInforBeanList = jsonUtil.jsonToVipServiceBean(objarry);
                }
                vipInforBean.setVipDetail(vipInforBeanList);
                vipInforBean.setVipResId(R.mipmap.vip_big_golden);
                //过滤掉会员等级为0的情况，表示基础会员
                if (vipInforBean.getVipLevel() > 0) {
                    viplist.add(vipInforBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setVipInfor();
    }

    /**
     * 设置VIP相关的信息
     */

    private void setNoVipInfor(int vipId, VipInforBean vipInforBean) {
        //设置当前会员与某一个会员等级相同的值
        if (vipId == nowVipId) {
            nowVipName = vipInforBean.getVipName();
            tv_now_level.setText(getSpannableString("当前等级：", nowVipName));
            nowVipLevel = vipInforBean.getVipLevel();
            actiStartTime = vipInforBean.getVipDisStartTime();
            actiEndTime = vipInforBean.getVipDisEndTime();
            vipStartTime = Tools.dateToDate(vipStartTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
            vipEndTime = Tools.dateToDate(vipEndTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
            actiStartTime = Tools.dateToDate(actiStartTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
            actiEndTime = Tools.dateToDate(actiEndTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
            tv_vip_time.setText(getSpannableString("有效期为：", vipStartTime + "-" + vipEndTime));
        }
    }


    /**
     * 设置VIP相关的信息
     */

    private void setVipInfor(int index, int level) {
        payMoney = getPayMoney(index, level);
        vipAdapter.updataList(viplist);
        adapter.updateListView(viplist.get(index).getVipDetail());
        if (nowVipLevel == 0) {
            mType = 1;
            btn_sure.setText(payMoney + "元/" +
                    viplist.get(index).getVipValidity() + "天" + "   立即开通");
        } else if (level == nowVipLevel) {
            mType = 3;
            btn_sure.setText(payMoney + "元/" +
                    viplist.get(index).getVipValidity() + "天" + "   立即续费");
        } else {
            mType = 2;
            btn_sure.setText(payMoney + "元   立即升级");
        }
        if ("".equals(actiStartTime) && "".equals(actiEndTime)) {
            tv_vip_activity_time.setVisibility(View.GONE);
        } else {
            tv_vip_activity_time.setVisibility(View.VISIBLE);
        }
        tv_vip_activity_time.setText("活动时间：" + actiStartTime + " - " + actiEndTime);
        if (level < nowVipLevel) {
            btn_sure.setEnabled(false);
            btn_sure.setText("请选择更高级会员升级或者续费");
        } else {
            btn_sure.setEnabled(true);
        }
    }


    /**
     * 获取实际上要支付的金额
     */

    private Double getPayMoney(int index, int level) {
        Double payMoney = 0.0;
        Double discountMoney = viplist.get(index).getVipDisMoney();
        Double money = viplist.get(index).getVipMoney();
        Double otherMoney = (Double) listMoney.get(viplist.get(index).getVipId());
        //购买会员,活动期内
        if (nowVipLevel == 0 || level == nowVipLevel) {
            if (isInActivity(viplist.get(index).getVipDisEndTime()) == true) {
                payMoney = discountMoney;
            } else {
                payMoney = money;
            }
        } else {
            payMoney = otherMoney;
        }
        return payMoney;
    }

    /**
     * 设置VIP相关的信息
     */

    private boolean isInActivity(String time) {
        String nowTime = (new Date()).getTime() + "";
        String endTime = Tools.date2TimeStamp(time, "yyyy-MM-dd HH:mm:ss");
        int result = nowTime.compareTo(endTime);
        return result <= 0;
    }

    /**
     * 设置VIP相关的信息
     */

    private void setVipInfor() {
        vipAdapter.updataList(viplist);
        btn_sure.setText("请选择会员");

        btn_sure.setEnabled(false);
        if ("".equals(actiStartTime) && "".equals(actiEndTime)) {
            tv_vip_activity_time.setVisibility(View.GONE);
        } else {
            tv_vip_activity_time.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 拼接字符串
     */

    private SpannableStringBuilder getSpannableString(String str1, String str2) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        SpannableString spannableString1 = Tools.getSpannableString(str1, 0xff333333, 1.0f);
        SpannableString spannableString2 = Tools.getSpannableString(str2, 0xFFFF0066, 1.0f);
        spannableString.append(spannableString1).append(spannableString2);
        return spannableString;
    }

}
