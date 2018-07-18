package com.bber.company.android.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.PrivilegesReyclerVewData;
import com.bber.company.android.view.adapter.VipPrivilegeViewPgerAdapter;
import com.bber.company.android.view.adapter.VipPrivilegesReyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.bber.company.android.R.layout.activity_vip_privileges;

public class VipPrivilegesActivity extends BaseAppCompatActivity implements ViewPager.OnPageChangeListener,
        VipPrivilegesReyclerViewAdapter.OnItemClickListener {

    private RecyclerView vip_priviler_reyclerview_id;
    private List<PrivilegesReyclerVewData> listdata;

    private VipPrivilegesReyclerViewAdapter vipPrivilegesReyclerViewAdapter;
    private ImageView introduction_image_id;
    private TextView introduction_text_id;
    private String[] jiestext;
    private int[] jiesimage;
    private ViewPager viewPager;
    private List<View> views = new ArrayList<View>();


    @Override
    protected int getContentViewLayoutId() {
        return activity_vip_privileges;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(R.string.viprivileges);

        getlistData();
        initView();
        setListener();
    }

    private void getlistData() {

        listdata = new ArrayList<>();

        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_advertising, "免广告"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_seefilm, "优先看片"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_map, "套图下载"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_hi, "高清VR"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_download, "专属缓存"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_akira, "撩人声优"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_broadcast, "直播1V1"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_audition, "海选上门"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_customer, "专属客服"));
        listdata.add(new PrivilegesReyclerVewData(R.mipmap.viprivileges_share, "分享有礼"));

        //   jiestext = getResources().getStringArray(R.array.introduction_privileges);

        jiesimage = new int[]{R.mipmap.alipay};

        View view1 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_ttem_layout, null);
        View view2 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_two_layout, null);
        View view3 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_three_layout, null);
        View view4 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_four_layout, null);
        View view5 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_five_layout, null);
        View view6 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_six_layout, null);
        View view7 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_seven_layout, null);
        View view8 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_eight_layout, null);
        View view9 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_nine_layout, null);
        View view10 = View.inflate(getApplicationContext(), R.layout.viewpager_vipprivilegs_item_ten_layout, null);

        // introduction_image_id = view1.findViewById(R.id.introduction_image_id);
        // introduction_text_id = view1.findViewById(R.id.introduction_text_id);

        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        views.add(view6);
        views.add(view7);
        views.add(view8);
        views.add(view9);
        views.add(view10);
    }

    private void initView() {

        vip_priviler_reyclerview_id = findViewById(R.id.vip_priviler_reyclerview_id);
        vipPrivilegesReyclerViewAdapter = new VipPrivilegesReyclerViewAdapter(this, listdata);
        vip_priviler_reyclerview_id.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        vip_priviler_reyclerview_id.setAdapter(vipPrivilegesReyclerViewAdapter);


        viewPager = findViewById(R.id.vip_privige_viewpager_id);

        viewPager.setAdapter(new VipPrivilegeViewPgerAdapter(views, getApplicationContext()));
        viewPager.setOnPageChangeListener(this);

        int ints = getIntent().getIntExtra("position", 100);

        if (ints < 15) {
            // introduction_text_id.setText(jiestext[ints]);
            vipPrivilegesReyclerViewAdapter.setMenuItemsIsSelect(getMenuItemsSelectState(ints));
            vip_priviler_reyclerview_id.smoothScrollToPosition(ints);
            viewPager.setCurrentItem(ints);//viewpager 默认页面
        } else {
            //introduction_text_id.setText(jiestext[0]);
            vipPrivilegesReyclerViewAdapter.setMenuItemsIsSelect(getMenuItemsSelectState(0));
            viewPager.setCurrentItem(0);

        }
    }

    private boolean[] getMenuItemsSelectState(int position) {

        boolean[] selsctStates = new boolean[listdata.size()];
        for (int j = 0; j < selsctStates.length; j++) {
            selsctStates[j] = false;
        }
        selsctStates[position] = true;
        return selsctStates;
    }

    private void setListener() {
        vipPrivilegesReyclerViewAdapter.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(View view, int position) {

        //  introduction_text_id.setText(jiestext[position]);
        vipPrivilegesReyclerViewAdapter.changeMenuItemSelectState(position);
        vipPrivilegesReyclerViewAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    ///选择监听
    @Override
    public void onPageSelected(int position) {
        vipPrivilegesReyclerViewAdapter.changeMenuItemSelectState(position);
        vip_priviler_reyclerview_id.smoothScrollToPosition(position);
        vipPrivilegesReyclerViewAdapter.notifyDataSetChanged();


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
