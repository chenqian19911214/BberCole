package com.bber.company.android.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BuyerUserEntity;
import com.bber.company.android.bean.VoucherBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.activity.BillListActivity;
import com.bber.company.android.view.activity.BusinessActivity;
import com.bber.company.android.view.activity.FavoriteListActivity;
import com.bber.company.android.view.activity.GetKeyActivity;
import com.bber.company.android.view.activity.KeyActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.view.activity.MyWalletActivity;
import com.bber.company.android.view.activity.PayBillListActivity;
import com.bber.company.android.view.activity.RegPreferenceActivity;
import com.bber.company.android.view.activity.SettingActivity;
import com.bber.company.android.view.activity.VoucherActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author: Bruce
 * Date: 2016/5/9
 * Version:
 * Describe:
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private RelativeLayout btn_preference, btn_setting, btn_invatecode;
    private RelativeLayout btn_my_order, rl_bill_detail, rl_favorite, rl_shop;
    private TextView key_count, btn_sign, btn_verify, uer_name, tv_invitecode;
    private TextView tv_coupon, tv_vip_getvip, tv_vip_name, tv_moneybag;
    private LinearLayout ll_myprofile, ll_mymoney, ll_voucher;
    private SimpleDraweeView user_icon;
    private String oldHeadIcon;
    private int keyCount;
    private NetWork netWork;
    private MainActivity meActivity;
    private String mInviteCode;
    private List<VoucherBean> voucherList;
    private BuyerUserEntity buyerUserEntity;
    private double money;
    private int vipcolor[] = {R.color.vip_none, R.color.vip_normal, R.color.vip_golden,
            R.color.vip_demon, R.color.vip_crown};
    private int vipIcon[] = {R.mipmap.vip_small_none, R.mipmap.vip_small_normal, R.mipmap.vip_small_golden,
            R.mipmap.vip_small_demon, R.mipmap.vip_small_crown};
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String type = intent.getStringExtra("type");
            if (action.equals(Constants.ACTION_SETTING)) {
                if ("addkey".equals(type)) {
                    keyCount = ((MainActivity) getActivity()).getUserTotalKey();
                    key_count.setText(keyCount + "");
                } else if ("uicon".equals(type)) {
                    oldHeadIcon = intent.getStringExtra("url");
                    if (!Tools.isEmpty(oldHeadIcon)) {
                        Uri uri = Uri.parse(oldHeadIcon);
                        user_icon.setImageURI(uri);
                        buyerUserEntity.setUbHeadm(oldHeadIcon);
                    }
                } else if ("voucher".equals(type)) {
                    String voucheNum = intent.getStringExtra("voucheNum");
                    int num = Tools.StringToInt(voucheNum);
                    tv_coupon.setText(num + "");
                } else if ("update".equals(type)) {
                    initUserInfo();
                } else {
                    reCheckPhone();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, null);
        meActivity = (MainActivity) getActivity();
        netWork = new NetWork(meActivity);
        LogUtils.e("MeFragment");
        initViews();
        initSetting();
        setListeners();
        initData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //标记是否刷新。改变主要的钱
        if (MyApplication.isRestartInitData) {
            initData();
            MyApplication.isRestartInitData = false;
        }

    }

    private void initViews() {
        toolbar = view.findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        user_icon = view.findViewById(R.id.user_icon);
        btn_sign = view.findViewById(R.id.btn_sign);
        btn_preference = view.findViewById(R.id.btn_preference);
        btn_setting = view.findViewById(R.id.btn_setting);
        btn_invatecode = view.findViewById(R.id.btn_invatecode);
        btn_my_order = view.findViewById(R.id.btn_my_order);
        btn_verify = view.findViewById(R.id.tv_isVerify);
        key_count = view.findViewById(R.id.key_count);
        uer_name = view.findViewById(R.id.uer_name);
        tv_invitecode = view.findViewById(R.id.tv_invitecode);
        ll_myprofile = view.findViewById(R.id.ll_myprofile);
        ll_mymoney = view.findViewById(R.id.ll_mymoney);
        rl_bill_detail = view.findViewById(R.id.rl_bill_detail);
        rl_favorite = view.findViewById(R.id.rl_favorite);
        ll_voucher = view.findViewById(R.id.ll_voucher);
        tv_coupon = view.findViewById(R.id.tv_coupon);
        tv_vip_name = view.findViewById(R.id.vip_name);
        tv_vip_getvip = view.findViewById(R.id.tv_getvip);
        tv_moneybag = view.findViewById(R.id.tv_moneybag);
        rl_shop = view.findViewById(R.id.rl_shop);
    }

    private void initSetting() {
        buyerUserEntity = new BuyerUserEntity();
    }

    private void setListeners() {
        user_icon.setOnClickListener(this);
        btn_sign.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_invatecode.setOnClickListener(this);
        btn_my_order.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
        btn_preference.setOnClickListener(this);
        tv_invitecode.setOnClickListener(this);
        ll_myprofile.setOnClickListener(this);
        ll_mymoney.setOnClickListener(this);
        ll_voucher.setOnClickListener(this);
        rl_bill_detail.setOnClickListener(this);
        rl_favorite.setOnClickListener(this);
        rl_shop.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.myprifle);
        keyCount = ((MainActivity) getActivity()).getUserTotalKey();
        key_count.setText(keyCount + "");
        user_icon.setVisibility(View.VISIBLE);
        initUserInfo();
        reCheckPhone();
        getCashCardList();
        registerBoradcastReceiver();
    }

    /**
     * 初始化用户数据，从网络获取用户的相关信息
     */
    private void initUserInfo() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(meActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(meActivity);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(meActivity);
        params.put("head", head);
        params.put("id", SharedPreferencesUtils.get(meActivity, Constants.USERID, ""));
        HttpUtil.get(Constants.getInstance().getUserInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(meActivity, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    String data = dataCollection.getString("BuyerUserEntity");
                    buyerUserEntity = jsonUtil.jsonToBuyerUserEntity(data);
                    SharedPreferencesUtils.put(meActivity, Constants.USERNAME, buyerUserEntity.getUbName());
                    SharedPreferencesUtils.put(meActivity, Constants.VIP_ID, buyerUserEntity.getVipId());
                    SharedPreferencesUtils.put(meActivity, Constants.USERICON, buyerUserEntity.getUbHeadm());
                    SharedPreferencesUtils.put(meActivity, Constants.VIP_START_TIME, buyerUserEntity.getVipStartTime());
                    SharedPreferencesUtils.put(meActivity, Constants.VIP_END_TIEM, buyerUserEntity.getVipEndTime());
                    uer_name.setText(buyerUserEntity.getUbName());
                    mInviteCode = buyerUserEntity.getInviteCode();
                    tv_invitecode.setText(mInviteCode);
                    //加载头像
                    if (!Tools.isEmpty(buyerUserEntity.getUbHeadm())) {
                        oldHeadIcon = buyerUserEntity.getUbHeadm();
                        SharedPreferencesUtils.put(meActivity, Constants.HEADURL, oldHeadIcon);
                        Uri uri = Uri.parse(buyerUserEntity.getUbHeadm());
                        user_icon.setImageURI(uri);
                    }
                    SharedPreferencesUtils.put(meActivity, Constants.USER_MONEY, buyerUserEntity.getUbMoney());
                    money = buyerUserEntity.getUbMoney();
                    tv_moneybag.setText(money + "");
                    MyApplication.getContext().setBalance(money);
                    setVipInfor();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUserInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(meActivity, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 设置VIP相关的信息
     */
    private void setVipInfor() {
        int level = buyerUserEntity.getVipLevel();
        SharedPreferencesUtils.put(meActivity, Constants.VIP_LEVEL, level);
        if (level >= vipcolor.length || level >= vipIcon.length) {
            level = 0;
        }
        int color = getResources().getColor(vipcolor[level]);
        tv_vip_name.setText(buyerUserEntity.getVipName());
        tv_vip_name.setTextColor(color);
        Drawable drawable = getResources().getDrawable(vipIcon[level]);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_vip_name.setCompoundDrawables(drawable, null, null, null);
        //会员等级
        tv_vip_getvip.setBackgroundResource(R.drawable.btn_corner);
        GradientDrawable mGrad = (GradientDrawable) tv_vip_getvip.getBackground();
        mGrad.setStroke(1, color);
        tv_vip_getvip.setTextColor(color);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    int addCount = data.getIntExtra("addCount", 0);
                    key_count.setText(keyCount + addCount + "");
                    break;
            }
        }
    }

    /**
     * 获取现金券的list接口
     */
    private void getCashCardList() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(meActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(meActivity).httpHeadToJson(meActivity);
        final JsonUtil jsonUtil = new JsonUtil(meActivity);
        String buyerId = SharedPreferencesUtils.get(meActivity, Constants.USERID, "-1") + "";
        params.put("head", head);
        params.put("buyerId", buyerId);
        HttpUtil.post(Constants.getInstance().getCashCardListByBuyerId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(meActivity, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    voucherList = jsonUtil.jsonToVoucherBean(dataCollection.toString());
                    tv_coupon.setText(voucherList.size() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(meActivity, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_myprofile:
            case R.id.user_icon:
            case R.id.tv_isVerify:
            case R.id.uer_name:
               /* if (buyerUserEntity != null) {
                    intent = new Intent(meActivity, myProfileSettingActivity.class);
                    intent.putExtra("nickName", buyerUserEntity.getUbName());
                    intent.putExtra("nickheadPic", buyerUserEntity.getUbHeadm());
                    intent.putExtra("vipName", buyerUserEntity.getVipName());
                    startActivityForResult(intent, 1);
                }*/

                MyToast.makeTextAnim(getContext(), "toast", 0, R.style.PopToast).show();
                break;
            case R.id.btn_sign:
                intent = new Intent(meActivity, GetKeyActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.ll_mymoney:
                intent = new Intent(meActivity, MyWalletActivity.class);
                intent.putExtra("money", money);
                startActivity(intent);
                //MyToast.makeTextAnim(meActivity, R.string.function_nono, 0, R.style.PopToast).show();
                break;
            case R.id.tv_invitecode:
                String key = tv_invitecode.getText() + "";
                ClipboardManager cmb = (ClipboardManager) meActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(key);
                MyToast.makeTextAnim(meActivity, R.string.copy_key_tip, 0, R.style.PopToast).show();
                break;
            case R.id.btn_setting:
                intent = new Intent(meActivity, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_invatecode:
                intent = new Intent(meActivity, KeyActivity.class);
                intent.putExtra("inviteCode", mInviteCode);
                startActivity(intent);
                break;
            case R.id.btn_my_order:
                intent = new Intent(meActivity, BillListActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_preference:
                intent = new Intent(meActivity, RegPreferenceActivity.class);
                intent.putExtra("isregister", false);
                startActivity(intent);
                break;
            case R.id.ll_voucher:
                intent = new Intent(meActivity, VoucherActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_bill_detail:
                intent = new Intent(meActivity, PayBillListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_favorite:
                intent = new Intent(meActivity, FavoriteListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_shop:
                intent = new Intent(meActivity, BusinessActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void reCheckPhone() {
        /*如果认证的话，那么这个按钮就按不了，不用再次认证了*/
        String userphone = (String) SharedPreferencesUtils.get(meActivity, Constants.USER_PHONE, "");
        Log.i("", "");
        if (userphone.length() > 5) {
            btn_verify.setText(R.string.mobile_verified);
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_SETTING);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
