package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Order;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.adapter.BillListAdapter;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BillListActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private LinearLayout view_no_item, ll_list_type;
    private RecyclerView recyclerView;
    private BillListAdapter adapter;
    private List<Order> orderlist;
    private List<Order> allorderlist;
    private TextView tv_order_all, tv_order_finish, tv_order_unpay, tv_order_unfinish, tv_order_dispute;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_bill_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        view_no_item = findViewById(R.id.view_no_item);
        ll_list_type = findViewById(R.id.ll_list_type);
        tv_order_all = findViewById(R.id.tv_order_all);
        tv_order_finish = findViewById(R.id.tv_order_finish);
        tv_order_unpay = findViewById(R.id.tv_order_unpay);
        tv_order_unfinish = findViewById(R.id.tv_order_unfinish);
        tv_order_dispute = findViewById(R.id.tv_order_dispute);


        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
    }

    private void setListeners() {
        tv_order_all.setOnClickListener(this);
        tv_order_finish.setOnClickListener(this);
        tv_order_unpay.setOnClickListener(this);
        tv_order_unfinish.setOnClickListener(this);
        tv_order_dispute.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.my_order);
        orderlist = new ArrayList<>();
        allorderlist = new ArrayList<>();
        adapter = new BillListAdapter(orderlist);
        tv_order_all.setBackgroundResource(R.mipmap.image_sliding_block);
        tv_order_all.setTextColor(getResources().getColor(R.color.pink));

        getOrderList();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BillListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Order data) {
                Intent intent = new Intent(BillListActivity.this, BillDetailActivity.class);
                Bundle mbundle = new Bundle();
                mbundle.putSerializable("order", data);
                intent.putExtra("data", mbundle);
                startActivity(intent);
            }
        });

        setView();
    }

    private void setView() {
        if (orderlist != null && orderlist.size() == 0) {
            view_no_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_item.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    private void getOrderList() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        final JsonUtil jsonUtil = new JsonUtil(this);
        String buyerId = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        params.put("head", head);
        params.put("buyerId", buyerId);
        HttpUtil.post(Constants.getInstance().getOrderListById, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(BillListActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    allorderlist = jsonUtil.jsonToOrderBean(dataCollection.toString());
                    setOderListClassify(-1);
                    setView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BillListActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        setAllBackGround();
        switch (v.getId()) {
            case R.id.tv_order_all:
                setOderListClassify(-1);
                tv_order_all.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_order_all.setTextColor(getResources().getColor(R.color.pink));
                break;
            case R.id.tv_order_finish:
                setOderListClassify(5);
                tv_order_finish.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_order_finish.setTextColor(getResources().getColor(R.color.pink));
                break;
            case R.id.tv_order_unpay:
                setOderListClassify(1);
                tv_order_unpay.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_order_unpay.setTextColor(getResources().getColor(R.color.pink));
                break;
            case R.id.tv_order_unfinish:
                setOderListClassify(4);
                tv_order_unfinish.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_order_unfinish.setTextColor(getResources().getColor(R.color.pink));
                break;
            case R.id.tv_order_dispute:
                setOderListClassify(9);
                tv_order_dispute.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_order_dispute.setTextColor(getResources().getColor(R.color.pink));
                break;
        }
        setView();
    }

    private void setAllBackGround() {
        tv_order_all.setBackgroundResource(R.color.transparent);
        tv_order_finish.setBackgroundResource(R.color.transparent);
        tv_order_unpay.setBackgroundResource(R.color.transparent);
        tv_order_unfinish.setBackgroundResource(R.color.transparent);
        tv_order_dispute.setBackgroundResource(R.color.transparent);

        tv_order_all.setTextColor(getResources().getColor(R.color.main_text));
        tv_order_finish.setTextColor(getResources().getColor(R.color.main_text));
        tv_order_unpay.setTextColor(getResources().getColor(R.color.main_text));
        tv_order_unfinish.setTextColor(getResources().getColor(R.color.main_text));
        tv_order_dispute.setTextColor(getResources().getColor(R.color.main_text));
    }

    private void setOderListClassify(int status) {
        orderlist.clear();
        if (status == -1) {
            orderlist.addAll(allorderlist);
        } else {
            for (int i = 0; i < allorderlist.size(); i++) {
                Order order = allorderlist.get(i);
                if (status == order.getStatus()) {
                    orderlist.add(order);
                }
            }
        }
        adapter.updateListView(orderlist);
    }


}
