package com.bber.company.android.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.bean.VipInforBean;
import com.bber.company.android.bean.VipServiceBean;
import com.bber.company.android.databinding.ActivityVipPrivilegeBinding;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.view.adapter.BuyVipAdapter;
import com.bber.company.android.view.adapter.VipItemListAdapter;
import com.bber.company.android.viewmodel.WalletViewModel;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class VipPrivilegeActivity extends BaseActivity {

    public List<VipInforBean> viplist = new ArrayList<>();
    public List<VipInforBean> datalist = new ArrayList<>();
    private ActivityVipPrivilegeBinding binding;
    private WalletViewModel viewModel;
    private RecyclerView recyclerView, recyclerView_item;
    private BuyVipAdapter adapter;
    private VipItemListAdapter vipAdapter;
    private List<VipServiceBean> vipServiiceList;
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vip_privilege);
        viewModel = new WalletViewModel(this);
        binding.setViewModel(viewModel);
        binding.setHeaderBarViewModel(headerBarViewModel);
//        viewModel.setActionListener(this);
        getIntentData();
        initViews();
        initdata();
    }

    public void getIntentData() {
        Intent intent = getIntent();
        viplist = new ArrayList<>();
        datalist = new ArrayList<>();
        String data = intent.getStringExtra("dataList");
        JsonUtil jsonUtil = new JsonUtil(this);
        viplist = jsonUtil.jsonToVipinfoBean(data);
    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("会员特权");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView_item = findViewById(R.id.recyclerView_item);
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

    private void initdata() {
        vipServiiceList = new ArrayList<>();


        adapter = new BuyVipAdapter(viplist.get(0).getVipDetail());
        datalist.clear();
        datalist.add(viplist.get(3));
        vipAdapter = new VipItemListAdapter(this, datalist);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_item.setAdapter(vipAdapter);
        recyclerView_item.setVisibility(View.VISIBLE);
//        vipAdapter.setOnItemClickListener(new VipItemListAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                mPosition = position;
////                vipAdapter.updataList(viplist);
//
//                adapter.updateListView(viplist.get(position).getVipDetail());
//            }
//        });
    }


}
