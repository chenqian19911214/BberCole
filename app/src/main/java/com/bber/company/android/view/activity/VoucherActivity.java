package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.bean.VoucherBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.adapter.VoucherAdapter;
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


public class VoucherActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private VoucherAdapter voucherAdapter;
    private LinearLayout view_no_voucher;
    private List<VoucherBean> voucherList;
    private VoucherBean voucherActivity;
    private int sellerId;
    private int enventStatus = 0;
    private int fromActivity = 0;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_voucher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.coupon);
        view_no_voucher = findViewById(R.id.view_no_voucher);
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
        view_no_voucher.setOnClickListener(this);
    }

    private void initData() {
        enventStatus = 0;
        sellerId = getIntent().getIntExtra("sellerId", 0);
        fromActivity = getIntent().getIntExtra(Constants.VOUCHERFROM, Constants.VOUCHER_FROM_ME);
        voucherList = new ArrayList<>();
        voucherAdapter = new VoucherAdapter(this, voucherList);
        recyclerView.setAdapter(voucherAdapter);
        voucherAdapter.setOnItemClickListener(new VoucherAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, VoucherBean data) {
                if ("活动优惠".equals(data.getCashCardName())) {
                    enventStatus = 1;
                }
                if (fromActivity == Constants.VOUCHER_FROM_CONFIRM_ORDER) {
                    Intent mIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("voucher", data);
                    mBundle.putInt("enventStatus", enventStatus);
                    mIntent.putExtras(mBundle);
                    setResult(RESULT_OK, mIntent);
                    finish();
                }
            }
        });
        getCashCardList();
        getDiscountInfor();
        setView();
    }

    private void setView() {
        if (voucherList != null && voucherList.size() == 0) {
            view_no_voucher.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_voucher.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getCashCardList() {
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
        HttpUtil.post(Constants.getInstance().getCashCardListByBuyerId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(VoucherActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    voucherList = jsonUtil.jsonToVoucherBean(dataCollection.toString());

                    Intent mIntent = new Intent(Constants.ACTION_SETTING);
                    mIntent.putExtra("type", "voucher");
                    mIntent.putExtra("voucheNum", voucherList.size() + "");
                    sendBroadcast(mIntent);
                    if (voucherActivity != null) {
                        voucherList.add(voucherActivity);
                    }
                    voucherAdapter.updateListView(voucherList);
                    setView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(VoucherActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * bruce
     * 2016.8.9
     * 获取当前当地区是否有活动
     */
    private void getDiscountInfor() {
        final JsonUtil jsonUtil = new JsonUtil(this);
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("sellerId", sellerId);
        HttpUtil.post(Constants.getInstance().getDiscount, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        int discountMoney = dataCollection.getInt("discountC");
                        if (discountMoney > 0) {
                            voucherActivity = new VoucherBean();
                            voucherActivity.setCashCardMoney(discountMoney + "");
                            voucherActivity.setCashCardName("活动优惠");
                            voucherActivity.setCashCardState("0");
                            voucherList.add(voucherActivity);
                            voucherAdapter.updateListView(voucherList);
                            setView();
                        }
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getOrderById onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(VoucherActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_change:
                break;
        }
    }
}
