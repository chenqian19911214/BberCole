package com.bber.company.android.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BuyerUserEntity;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.FragmentLeftMenuBinding;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.util.WightHeighUtil;
import com.bber.company.android.view.activity.Buy_vipActivity;
import com.bber.company.android.view.activity.myProfileSettingActivity;
import com.bber.company.android.view.adapter.UserItemAdapter;
import com.bber.company.android.viewmodel.UserViewModel;
import com.bber.customview.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wuwenjie
 * @date 2014/11/14
 * @description 侧边栏菜单
 */
public class LeftFragment extends BaseFragment implements IactionListener<Object> {

    protected View top_statusbar;//状态栏View
    private FragmentLeftMenuBinding binding;
    private UserViewModel userViewModel;
    private RecyclerView recycleView;
    private TextView uer_name, tv_vip_name, tv_getvip, tvVipEndTime;
    private String oldHeadIcon;
    private SimpleDraweeView user_icon;
    private SimpleDraweeView user_imformation;
    private int vipcolor[] = {R.color.vip_none, R.color.vip_normal, R.color.vip_golden,
            R.color.vip_demon, R.color.vip_crown};
    private int vipIcon[] = {R.mipmap.vip_small_none, R.mipmap.vip_small_normal, R.mipmap.vip_small_golden,
            R.mipmap.vip_small_demon, R.mipmap.vip_small_crown};
    private List<Integer> imageList; // 图片集合
    private List<String> titleList; //头像集合
    private String[] serviceactions = null;
    private UserItemAdapter adapter;
    private Activity mActivty;
    private ImageView guide_0, guide_1, guide_2, guide_3;
    private RelativeLayout hid_rel;
    private Button vip_renew_button;
    private TextView textview_verify, viplodle_id;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String type = intent.getStringExtra("type");
            if (action.equals(Constants.ACTION_SETTING)) {
                if ("uicon".equals(type)) {
                    oldHeadIcon = intent.getStringExtra("url");
                    if (!Tools.isEmpty(oldHeadIcon)) {
                        Uri uri = Uri.parse(oldHeadIcon);
                        user_icon.setImageURI(uri);
                        userViewModel.buyerUserEntity.setUbHeadm(oldHeadIcon);
                    }
                } else if ("voucher".equals(type)) {
                    String voucheNum = intent.getStringExtra("voucheNum");
                    int num = Tools.StringToInt(voucheNum);
                    adapter.setChangeData(1, num + "");
                } else if ("voucher".equals(type)) {
                    textview_verify.setText(R.string.mobile_verified);
                } else if (type.equals("charge")) {
                    userViewModel.initUserInfo();
                } else if ("update".equals(type)) {
                    userViewModel.initUserInfo();
                } else {
                    reCheckPhone();
                }
            }
        }
    };

    public LeftFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //找到databinding的控件
        mActivty = getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_left_menu, container, false);
        userViewModel = new UserViewModel(mActivty);
        binding.setViewModel(userViewModel);
        userViewModel.setActionListener(this);
        setData();
        Navigationheight();
        setRecycleView();
        userImformation();
        //请求数据
        initdata();
        //广播
        registerBoradcastReceiver();
        return binding.getRoot();
    }

    private void setData() {
        //array_service_actions 跳转监听 类
        titleList = new ArrayList<>();
        titleList.add("我的会员");
        titleList.add("我的钱包");
        // titleList.add("商户");
        titleList.add("我的收藏");
        titleList.add("我的订单");
        titleList.add("PAPA新手教程");
        //titleList.add("游戏");
        titleList.add("设置");
        titleList.add("常见问题");
        titleList.add("商务合作");
        titleList.add("关于我们");

        imageList = new ArrayList<>();
        imageList.add(R.mipmap.icon_buy_vip);
        imageList.add(R.mipmap.icon_my_wallert);
        //imageList.add(R.mipmap.icon_shop);
        imageList.add(R.mipmap.icon_love);
        imageList.add(R.mipmap.icon_appoint);
        //imageList.add(R.mipmap.icon_shaixuan);
        imageList.add(R.mipmap.icon_new_user);
        imageList.add(R.mipmap.icon_setting);
        imageList.add(R.mipmap.problem);
        imageList.add(R.mipmap.icon_business_cooperation);
        imageList.add(R.mipmap.icon_aboutus);
        serviceactions = getResources().getStringArray(R.array.array_service_actions);
    }

    private void initdata() {

        userViewModel.initUserInfo();
        userViewModel.getCashCardList();
    }

    @Override
    public void onStart() {
        super.onStart();
        String userMoney = String.valueOf(SharedPreferencesUtils.get(getActivity(), Constants.USER_MONEY, ""));
        adapter.setChangeMoney(1, userMoney);

        if (Constants.ISGETDATA) {
            Constants.ISGETDATA = false;
            userViewModel.initUserInfo();
        }
        //setVipInfor();
    }

    //设置recycleView
    private void setRecycleView() {
        recycleView = binding.userRecycle;
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivty);
        recycleView.setLayoutManager(layoutManager);

        recycleView.addItemDecoration(new DividerItemDecoration(mActivty, DividerItemDecoration.VERTICAL_LIST));
        adapter = new UserItemAdapter(mActivty, imageList, titleList);
        recycleView.setAdapter(adapter);

        //每行的监听
        adapter.setOnItemClickLitener(new UserItemAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                if (serviceactions[position].contains("RegPreferenceActivity")) {
                    bundle.putBoolean("isregister", false);
//                    bundle.putString("inviteCode",userViewModel.buyerUserEntity.getInviteCode());
                }
                if (serviceactions[position].contains("AboutUsActivity")) {
                    bundle.putBoolean("isadver", false);
//                    bundle.putString("inviteCode",userViewModel.buyerUserEntity.getInviteCode());
                }
                MyApplication.getContext().startActivityForRoot(mActivty, serviceactions[position], bundle);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        uer_name = binding.uerName;
        user_icon = binding.userIcon;
        tv_vip_name = binding.vipName;
        tv_getvip = binding.tvGetvip;
        user_imformation = binding.userIcon;
        textview_verify = binding.tvIsVerify;
        guide_0 = binding.guide0;
        guide_1 = binding.guide1;
        guide_2 = binding.guide2;
        guide_3 = binding.guide3;
        hid_rel = binding.hidRel;

        viplodle_id = binding.viplodleId;
        tvVipEndTime = binding.tvVipEndTime;

        setVipInformation();
        vip_renew_button = binding.vipRenewButtonId;
        boolean second = (boolean) SharedPreferencesUtils.get(getActivity(), "second", true);
        if (second) {
            hid_rel.setVisibility(View.VISIBLE);
            guide_0.setVisibility(View.VISIBLE);
            guide_1.setVisibility(View.GONE);
            guide_2.setVisibility(View.GONE);
            guide_3.setVisibility(View.GONE);
            SharedPreferencesUtils.put(getActivity(), "second", false);
        }
        guide_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide_0.setVisibility(View.GONE);
                hid_rel.setVisibility(View.VISIBLE);
                guide_1.setVisibility(View.VISIBLE);
            }
        });
        guide_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide_1.setVisibility(View.GONE);
                guide_2.setVisibility(View.VISIBLE);
            }
        });
        guide_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide_2.setVisibility(View.GONE);
                guide_3.setVisibility(View.VISIBLE);
            }
        });
        guide_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide_3.setVisibility(View.GONE);
                hid_rel.setVisibility(View.GONE);
            }
        });

        vip_renew_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivty, Buy_vipActivity.class));
            }
        });
    }

    /**
     * 设置vip会员信息
     */
    private void setVipInformation() {
        String nowVipName = (String) SharedPreferencesUtils.get(getContext(), Constants.VIP_NAME, "");
        viplodle_id.setText(nowVipName);
        String actiEndTime = (String) SharedPreferencesUtils.get(getContext(), Constants.VIP_END_TIEM, "");

        if (!(actiEndTime == null || actiEndTime.equals(""))) {
            String vipEndTime = Tools.dateToDate(actiEndTime, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd");

            if (vipEndTime.equals("2050/03/01")) {
                tvVipEndTime.setText("终身黄金会员");
            } else {
                tvVipEndTime.setText(vipEndTime + "到期");
            }

        } else {

            // tvVipEndTime.setText("到期");
            tvVipEndTime.setVisibility(View.GONE);
        }
    }

    /**
     * 设置VIP相关的信息
     */
    private void setVipInfor() {
        int level = userViewModel.buyerUserEntity.getVipLevel();
        SharedPreferencesUtils.put(MyApplication.getContext(), Constants.VIP_LEVEL, level);
        if (level >= vipcolor.length || level >= vipIcon.length) {
            level = 0;
        }
        int color = MyApplication.getContext().getResources().getColor(vipcolor[level]);
        tv_vip_name.setText(userViewModel.buyerUserEntity.getVipName());
        tv_vip_name.setTextColor(color);
        Drawable drawable = MyApplication.getContext().getResources().getDrawable(vipIcon[level]);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_vip_name.setCompoundDrawables(drawable, null, null, null);
        //会员等级
        tv_getvip.setBackgroundResource(R.drawable.btn_corner);
        GradientDrawable mGrad = (GradientDrawable) tv_getvip.getBackground();
        mGrad.setStroke(1, color);
        tv_getvip.setTextColor(color);

        boolean checkPhone = (boolean) SharedPreferencesUtils.get(mActivty, Constants.CHECK_PHONE, false);
        String phone = (String) SharedPreferencesUtils.get(mActivty, Constants.PHONE, "");
        if (checkPhone == true || phone.length() > 5) {
//            GradientDrawable drawable = (GradientDrawable) textview_verify.getBackground();
//            drawable.setColor(getResources().getColor(R.color.key_page));
            textview_verify.setText(R.string.mobile_verified);
//            btn_verify.setEnabled(false);
//            textView_phone.setText(StuffPhone(phone));
//            textView_phone.setVisibility(View.VISIBLE);
        }
    }

    //判断底部是否有虚拟按键，然后计算出高度。隔离开
    private void Navigationheight() {
        WightHeighUtil util = new WightHeighUtil();
        top_statusbar = binding.topStatusbar;
        if (WightHeighUtil.checkDeviceHasNavigationBar(getActivity())) {
            if (top_statusbar != null) {
                ViewGroup.LayoutParams param = top_statusbar.getLayoutParams();
                param.height = util.getNavigationBarHeight(getActivity());
                LogUtils.e("底部状态栏的高度" + param.height);
                top_statusbar.setLayoutParams(param);
            }
        }

    }

    /***
     * 设置头像的点击事件
     */
    public void userImformation() {
        user_imformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userViewModel.buyerUserEntity != null) {
                    Intent intent = new Intent();
                    intent = new Intent(mActivty, myProfileSettingActivity.class);
                    intent.putExtra("nickName", userViewModel.buyerUserEntity.getUbName());
                    intent.putExtra("nickheadPic", userViewModel.buyerUserEntity.getUbHeadm());
                    intent.putExtra("vipName", userViewModel.buyerUserEntity.getVipName());
                    startActivityForResult(intent, 1);
                }
            }
        });

    }

    private void reCheckPhone() {
        /*如果认证的话，那么这个按钮就按不了，不用再次认证了*/
        String phone = (String) SharedPreferencesUtils.get(mActivty, Constants.PHONE, "");

        boolean checkPhone = (boolean) SharedPreferencesUtils.get(getActivity(), Constants.CHECK_PHONE, false);
        if (checkPhone == true || phone.length() > 5) {
            textview_verify.setText(R.string.mobile_verified);
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_SETTING);
        mActivty.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivty.unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void SuccessCallback(Object o) {
        //成功取到了用户的数据
        if (o instanceof BuyerUserEntity) {
            //设置用户名字
            uer_name.setText(userViewModel.buyerUserEntity.getUbName());
            adapter.setChangeMoney(1, userViewModel.buyerUserEntity.getUbMoney() + "");
            //加载头像
            if (!Tools.isEmpty(userViewModel.buyerUserEntity.getUbHeadm())) {
                oldHeadIcon = userViewModel.buyerUserEntity.getUbHeadm();
                SharedPreferencesUtils.put(MyApplication.getContext(), Constants.HEADURL, oldHeadIcon);
                Uri uri = Uri.parse(userViewModel.buyerUserEntity.getUbHeadm());

                user_icon.setImageURI(uri);
            }
            setVipInfor();
        }
        if (o instanceof List) {
            //如果返回来的是现金券的数量
//            if (((List) o).size()!=0){
//                if (((List) o).get(0) instanceof VoucherBean){
//                    adapter.setChangeData(1,((List) o).size()+"");
//                }
//            }else {
//                adapter.setChangeData(1,"0");
//            }

        }
    }

    @Override
    public void FailCallback(String result) {

    }
}
