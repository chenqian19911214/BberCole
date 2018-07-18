package com.bber.company.android.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.VipInforBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.ActivityBuyVipBinding;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.adapter.Myadapter;
import com.bber.company.android.view.customcontrolview.DataClass;
import com.bber.company.android.viewmodel.HeaderBarViewModel;
import com.bber.company.android.viewmodel.WalletViewModel;
import com.bber.customview.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.ledlau.widgets.HorizontalPageGridView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 我的会员
 * 2018年3月14日11:05:41
 */

public class Buy_vipActivity extends BaseActivity implements View.OnClickListener,
        IactionListener<Object>, AdapterView.OnItemClickListener {

    public boolean isQQ;
    private ActivityBuyVipBinding binding;
    private WalletViewModel viewModel;
    private TextView vip_textview1, vip_textview6, vip_textview12,
            glod_vip_textview, charge_wallert, pay_money_text_id;
    private LinearLayout vip_lin1, vip_lin6, vip_lin12, glod_vip_lin, wallert_recharge, guide_bg;
    private TextView tv_vip_time, tv_now_level, level_onclick, seemoreid;
    private SimpleDraweeView user_icon;
    private ImageView user_icon_xiao;
    private Button button_sure;
    private ImageView guide1, guide3;
    private boolean isFlag = false;
    private boolean isUnion = true;
    private String nowVipName, vipStartTime, vipEndTime;
    private int nowVipId, nowVipLevel;
    private String headurl, actiStartTime, actiEndTime;
    private HorizontalPageGridView vip_privilege_gridView_id;
    private List<DataClass> dataListss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_buy_vip);
        viewModel = new WalletViewModel(this);
        binding.setViewModel(viewModel);
        binding.setHeaderBarViewModel(headerBarViewModel);
        viewModel.setActionListener(this);
        initViews();
        setVipData();
        try {
            initdata();
        } catch (Exception e) {
            LogUtils.e("发送错误信息" + e.getMessage());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setVipData();

    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("我的会员");
        headerBarViewModel.setOnClickListener(new HeaderBarViewModel.headerclickListener() {
            @Override
            public void leftClickListener(View v) {
                finish();
            }

            @Override
            public void rightClickListener(View v) {

            }
        });
    }


    private void initData() {
        //图标
        int icno[] = {R.mipmap.vipadvertising, R.mipmap.vipseethefilm, R.mipmap.vipmapdownload,
                R.mipmap.viphigh, R.mipmap.vipdownload, R.mipmap.vipyoushen, R.mipmap.vipbroadcast,
                R.mipmap.vipaudition, R.mipmap.vipshare, R.mipmap.vipcustomer};
        //图标下的文字
        String name[] = {"免广告", "优先看片", "套图下载", "高清VR", "专属缓存", "撩人声优", "直播1V1", "海选上门", "分享有礼", "专属客服"};
        dataListss = new ArrayList<>();
        dataListss.add(new DataClass(R.mipmap.vipadvertising, "免广告"));
        dataListss.add(new DataClass(R.mipmap.vipseethefilm, "优先看片"));
        dataListss.add(new DataClass(R.mipmap.vipmapdownload, "套图下载"));
        dataListss.add(new DataClass(R.mipmap.viphigh, "高清VR"));
        dataListss.add(new DataClass(R.mipmap.vipdownload, "专属缓存"));
        dataListss.add(new DataClass(R.mipmap.vipyoushen, "撩人声优"));
        dataListss.add(new DataClass(R.mipmap.vipbroadcast, "直播1V1"));
        dataListss.add(new DataClass(R.mipmap.vipaudition, "海选上门"));
        dataListss.add(new DataClass(R.mipmap.vipcustomer, "专属客服"));
        dataListss.add(new DataClass(R.mipmap.vipshare, "分享有礼"));

      /*  for (int i = 0; i < icno.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text", name[i]);
            dataListss.add(map);
        }*/

    }


    private void initViews() {
        //会员充值的两个text
        vip_textview1 = binding.glodVip1;
        vip_textview6 = binding.glodVip6;
        vip_textview12 = binding.glodVip12;
        glod_vip_textview = binding.glodVipAll;
        seemoreid = binding.seemoreid;
        user_icon_xiao = binding.userIconXiao;
        pay_money_text_id = findViewById(R.id.pay_money_text_id);
        pay_money_text_id.setText("请选择充值会员");

        vip_privilege_gridView_id = findViewById(R.id.vip_privilege_gridView_id);

        initData();

        vip_privilege_gridView_id.setNumColumns(4)
                .setPageSize(8);

        vip_privilege_gridView_id.setAdapter(new Myadapter(getApplicationContext(), dataListss));
        //两个点击事件
        vip_lin1 = binding.vipLinearlayout1;
        vip_lin6 = binding.vipLinearlayout6;
        vip_lin12 = binding.vipLinearlayout12;
        glod_vip_lin = binding.glodVipLinearlayout;
        wallert_recharge = binding.wallertRecharge;

        vip_lin1.setOnClickListener(this);
        vip_lin6.setOnClickListener(this);
        vip_lin12.setOnClickListener(this);
        glod_vip_lin.setOnClickListener(this);
        wallert_recharge.setOnClickListener(this);
        seemoreid.setOnClickListener(this);
        vip_privilege_gridView_id.setListener(new com.ledlau.widgets.HorizontalPageListener() {
            @Override
            public void onItemClickListener(Object o, int position) {
                Intent intent = new Intent(Buy_vipActivity.this, VipPrivilegesActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);

                ChenQianLog.i("position:" + position);
            }
        });

        //vip_privilege_gridView_id.setOnItemClickListener(this);

        guide_bg = binding.guideBg;
        guide1 = binding.iconGuide1;
        //  guide2 = binding.iconGuide2;
        guide3 = binding.iconGuide3;
        charge_wallert = binding.chargeWallert;
        level_onclick = binding.levelOnclick;
        button_sure = binding.btnSure;

        level_onclick.setOnClickListener(this);
        button_sure.setOnClickListener(this);
        guide1.setOnClickListener(this);
        // guide2.setOnClickListener(this);
        guide3.setOnClickListener(this);


        button_sure.setEnabled(false);
        // button_sure.setText("请选择充值会员");
        tv_now_level = findViewById(R.id.tv_now_level);
        user_icon = binding.userIcon;
        tv_vip_time = findViewById(R.id.tv_vip_time);
        // qq_pay = findViewById(R.id.qq_pay);

        boolean thirld = (boolean) SharedPreferencesUtils.get(this, "thirld", true);
        if (thirld) {
            guide_bg.setVisibility(View.VISIBLE);
            guide1.setVisibility(View.VISIBLE);
            //   guide3.setVisibility(View.VISIBLE);
//            guide_call2.setVisibility(View.VISIBLE);
//            guide_call3.setVisibility(View.VISIBLE);
            SharedPreferencesUtils.put(this, "thirld", false);
        }
    }

    private void setVipData() {
        nowVipName = (String) SharedPreferencesUtils.get(this, Constants.VIP_NAME, "");
        tv_now_level.setText(getSpannableString("当前等级：", nowVipName));
        nowVipId = (int) SharedPreferencesUtils.get(this, Constants.VIP_ID, 0);
        actiStartTime = (String) SharedPreferencesUtils.get(this, Constants.VIP_START_TIME, "");
        actiEndTime = (String) SharedPreferencesUtils.get(this, Constants.VIP_END_TIEM, "");
        vipStartTime = Tools.dateToDate(actiStartTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
        vipEndTime = Tools.dateToDate(actiEndTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");
//        actiStartTime = Tools.dateToDate(actiStartTime, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd");
//        actiEndTime = Tools.dateToDate(actiEndTime, "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd");
        if (nowVipId == 5) {
            SpannableStringBuilder spannableSt = getSpannableString("有效期为：", "终身黄金会员");
            tv_vip_time.setText(spannableSt);
            user_icon_xiao.setVisibility(View.VISIBLE);
            user_icon_xiao.setBackgroundResource(R.mipmap.forever_glod);
        } else {
            if (vipEndTime.length() > 3) {
                SpannableStringBuilder setTexts = getSpannableString("有效期为：", vipEndTime).append(Tools.getSpannableString(" 到期", 0xff333333, 1.0f));
                tv_vip_time.setText(setTexts);
            } else {

                SpannableStringBuilder setTexts = getSpannableString("有效期为：", "--");

                tv_vip_time.setText(setTexts);
            }

            if (nowVipId == 0) {
                user_icon_xiao.setVisibility(View.GONE);

            } else if (nowVipId > 0 && nowVipId < 5) {
                user_icon_xiao.setVisibility(View.VISIBLE);
                user_icon_xiao.setBackgroundResource(R.mipmap.glod_vip);
            }
        }
    }

    private void initdata() {
        //拿到存储在本地的数据
        headurl = (String) SharedPreferencesUtils.get(Buy_vipActivity.this, Constants.USERICON, "");
        nowVipId = (int) SharedPreferencesUtils.get(Buy_vipActivity.this, Constants.VIP_ID, 0);
        vipStartTime = (String) SharedPreferencesUtils.get(Buy_vipActivity.this, Constants.VIP_START_TIME, "");//会员开始时间
        vipEndTime = (String) SharedPreferencesUtils.get(Buy_vipActivity.this, Constants.VIP_END_TIEM, ""); //会员到期时间
        //拿取这边的VIP信息

        viewModel.getVipListInfor();
        //s设置头像
        Uri uri = Uri.parse(headurl);
        user_icon.setImageURI(uri);

        if (nowVipId > 0) {
            viewModel.getVipUpgradeMoney();
        }
        String userMoney = (String) SharedPreferencesUtils.get(this, Constants.USER_MONEY, "");
        viewModel.balance = Double.parseDouble(userMoney);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //会员1
            case R.id.vip_linearlayout1:
//                isFlag = false;
                setSelectone();
                break;
            case R.id.vip_linearlayout6:
//                isFlag = false;
                viewModel.payVipId = viewModel.viplist.get(1).getVipId();
                viewModel.level = viewModel.viplist.get(1).getVipLevel();
                viewModel.money = viewModel.viplist.get(1).getVipDisMoney();
                setMoney(1);
                vip_textview6.setBackground(getResources().getDrawable(R.drawable.btn_pink_price));
                vip_textview6.setTextColor(getResources().getColor(R.color.white));

                vip_textview1.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview1.setTextColor(getResources().getColor(R.color.pink));

                vip_textview12.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview12.setTextColor(getResources().getColor(R.color.pink));

                glod_vip_textview.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                glod_vip_textview.setTextColor(getResources().getColor(R.color.pink));

                charge_wallert.setBackground(null);
                break;
            case R.id.vip_linearlayout12:
//                isFlag = false;
                viewModel.payVipId = viewModel.viplist.get(2).getVipId();
                viewModel.level = viewModel.viplist.get(2).getVipLevel();
                viewModel.money = viewModel.viplist.get(2).getVipDisMoney();
                setMoney(2);
                vip_textview12.setBackground(getResources().getDrawable(R.drawable.btn_pink_price));
                vip_textview12.setTextColor(getResources().getColor(R.color.white));

                vip_textview6.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview6.setTextColor(getResources().getColor(R.color.pink));

                vip_textview1.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview1.setTextColor(getResources().getColor(R.color.pink));

                glod_vip_textview.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                glod_vip_textview.setTextColor(getResources().getColor(R.color.pink));
                charge_wallert.setBackground(null);
                break;
            //终身黄金会员
            //终身黄金会员
            case R.id.glod_vip_linearlayout:
                viewModel.payVipId = viewModel.viplist.get(3).getVipId();
                viewModel.level = viewModel.viplist.get(3).getVipLevel();
                viewModel.money = viewModel.viplist.get(3).getVipDisMoney();
                setMoney(3);
                glod_vip_textview.setBackground(getResources().getDrawable(R.drawable.btn_pink_price));
                glod_vip_textview.setTextColor(getResources().getColor(R.color.white));

                vip_textview6.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview6.setTextColor(getResources().getColor(R.color.pink));

                vip_textview1.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview1.setTextColor(getResources().getColor(R.color.pink));

                vip_textview12.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
                vip_textview12.setTextColor(getResources().getColor(R.color.pink));
                charge_wallert.setBackground(null);
                break;


            case R.id.level_onclick:
                Intent intent = new Intent(Buy_vipActivity.this, VipPrivilegeActivity.class);
                Gson gson = new Gson();
                String dataList = gson.toJson(viewModel.viplist);
                intent.putExtra("dataList", dataList);
                startActivity(intent);
                break;
            case R.id.icon_guide1:
                guide3.setVisibility(View.VISIBLE);
                guide1.setVisibility(View.GONE);
                break;
            case R.id.icon_guide3:
                guide3.setVisibility(View.GONE);
                guide_bg.setVisibility(View.GONE);
                break;
            case R.id.btn_sure:
                Double viewModels = viewModel.money;

                if (viewModels != 0) {
                    intent = new Intent(this, PayInputMoneyActivity.class);
                    intent.putExtra("viewModel", viewModels);
                    intent.putExtra("VipId", viewModel.payVipId);
                    intent.putExtra("mType", viewModel.mType);
                    intent.putExtra("balance", viewModel.balance);
                    startActivity(intent);
                } else {
                    button_sure.setEnabled(false);
                }

                break;
            case R.id.seemoreid:

                startActivity(new Intent(Buy_vipActivity.this, VipPrivilegesActivity.class));


                break;
        }
    }

    private void setSelectone() {
        viewModel.payVipId = viewModel.viplist.get(0).getVipId();
        viewModel.level = viewModel.viplist.get(0).getVipLevel();
        viewModel.money = viewModel.viplist.get(0).getVipDisMoney();
        setMoney(0);
        vip_textview1.setBackground(getResources().getDrawable(R.drawable.btn_pink_price));
        vip_textview1.setTextColor(getResources().getColor(R.color.white));

        vip_textview6.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
        vip_textview6.setTextColor(getResources().getColor(R.color.pink));

        vip_textview12.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
        vip_textview12.setTextColor(getResources().getColor(R.color.pink));

        glod_vip_textview.setBackground(getResources().getDrawable(R.drawable.btn_pink_stoke));
        glod_vip_textview.setTextColor(getResources().getColor(R.color.pink));

        charge_wallert.setBackground(null);
    }

    @Override
    public void SuccessCallback(Object o) {
        //如果返回的是头部数据

        setSelectone();
        if (o instanceof VipInforBean) {
            vip_textview1.setText("¥" + viewModel.viplist.get(0).getVipDisMoney());
            vip_textview6.setText("¥" + viewModel.viplist.get(1).getVipDisMoney());
            vip_textview12.setText("¥" + viewModel.viplist.get(2).getVipDisMoney());
            glod_vip_textview.setText("¥" + viewModel.viplist.get(3).getVipDisMoney());

        }
        if (o instanceof String) {
            Intent intent;
            if (((String) o).contains(".jpg")) {
                intent = new Intent(Buy_vipActivity.this, ImageViewActivity.class);
            } else {
                intent = new Intent(Buy_vipActivity.this, webViewActivity.class);
                if (isUnion) {
                    intent.putExtra("hiding", "1");
                }
            }
            intent.putExtra("url", (String) o);
            intent.putExtra("payCode", viewModel.payWays);
            intent.putExtra("activityname", "购买会员");
            startActivity(intent);
        }
    }

    @Override
    public void FailCallback(String result) {

    }

    /**
     * 获取拼接的合计
     */

    private SpannableStringBuilder getMoneyTextColor(String str) {

        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        SpannableString str1 = Tools.getSpannableString("合计 ：", 0xff666666, 1.0f);
        SpannableString str2 = Tools.getSpannableString(" " + str + "元", 0xFFFF0066, 1.0f);

        spannableString.append(str1).append(str2);

        return spannableString;
    }

    /**
     * 设置会员的金钱
     */
    private void setMoney(int index) {
        int nowVipId = (int) SharedPreferencesUtils.get(this, Constants.VIP_ID, 0);

        SpannableStringBuilder spannableString1;
        viewModel.money = getPayMoney(index, viewModel.payVipId);
        if (nowVipId == 0) {
            viewModel.mType = 1;

            if (viewModel.payVipId == 5) {
                //  pay_money_text_id.setText(viewModel.money + "元/" +"永久有效" + "   立即开通");

                spannableString1 = getMoneyTextColor(viewModel.money + "");
                pay_money_text_id.setText(spannableString1);
            } else {
                /*utton_sure.setText(viewModel.money + "元/" +
                        viewModel.viplist.get(index).getVipValidity() + "天" + "   立即开通");*/
                spannableString1 = getMoneyTextColor(viewModel.money + "");
                pay_money_text_id.setText(spannableString1);
            }

        } else if (viewModel.payVipId == nowVipId) {
            viewModel.mType = 3;
            if (viewModel.payVipId == 5) {
                // button_sure.setText(viewModel.money + "元/" +"永久有效" + "    立即续费");


            } else {
              /*  button_sure.setText(viewModel.money + "元/" +
                        viewModel.viplist.get(index).getVipValidity() + "天" + "    立即续费");*/

                spannableString1 = getMoneyTextColor(viewModel.money + "");
                pay_money_text_id.setText(spannableString1);
            }

        } else {
            viewModel.mType = 2;
            // button_sure.setText(viewModel.money + "元   立即升级");
            spannableString1 = getMoneyTextColor(viewModel.money + "");
            pay_money_text_id.setText(spannableString1);
        }
        if (viewModel.payVipId < nowVipId) {
            button_sure.setEnabled(false);
            // button_sure.setText("请选择更高级会员升级或者续费");
            spannableString1 = getMoneyTextColor("请选择更高级会员升级或者续费");
            pay_money_text_id.setTextColor(0xff666666);

            pay_money_text_id.setText("请选择更高级会员升级");
        } else {
            button_sure.setEnabled(true);
        }
        //终身会员
        if (nowVipId == 5) {
            //  button_sure.setText("您已是终身会员");
            pay_money_text_id.setTextColor(0xff666666);

            pay_money_text_id.setText("您已是终身会员");
            //  tv_vip_time.setText("有效期为：终身会员");
            button_sure.setEnabled(false);
        }
        if (nowVipId == 1 && viewModel.payVipId <= 2) {
            button_sure.setEnabled(false);
            //  button_sure.setText("请选择更高级会员升级或者续费");
            spannableString1 = getMoneyTextColor("请选择更高级会员升级或者续费");
            pay_money_text_id.setTextColor(0xff666666);
            pay_money_text_id.setText("请选择更高级会员升级");
        }
    }

    /**
     * 获取实际上要支付的金额
     */

    private Double getPayMoney(int index, int level) {
        Double payMoney = 0.0;
        Double discountMoney = viewModel.viplist.get(index).getVipDisMoney();
        Double money = viewModel.viplist.get(index).getVipMoney();
        Double otherMoney = (Double) viewModel.listMoney.get(viewModel.viplist.get(index).getVipId());
        //购买会员,活动期内
        if (nowVipId == 0 || level <= nowVipId) {
            if (isInActivity(viewModel.viplist.get(index).getVipDisEndTime())) {
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
     * 拼接字符串
     */

    private SpannableStringBuilder getSpannableString(String str1, String str2) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        SpannableString spannableString1 = Tools.getSpannableString(str1, 0xff666666, 1.0f);
        SpannableString spannableString2 = Tools.getSpannableString(str2, 0xFFFF0066, 1.0f);
        spannableString.append(spannableString1).append(spannableString2);
        return spannableString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*GitdView 的监听事件*/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        ChenQianLog.i("viewGriditem :" + i);

    }

}
