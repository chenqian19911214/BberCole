package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Balance;
import com.bber.company.android.bean.DpPayBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.adapter.PayBillListAdapter;
import com.bber.company.android.view.adapter.RoyaltyListAdapter;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PayBillListActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private LinearLayout view_no_item;
    private RecyclerView recyclerView;
    private PayBillListAdapter doingAdapter;
    private RoyaltyListAdapter finishAdapter;
    private List<Balance> balanceList;
    private List<DpPayBean> payBilllist;
    private TextView tv_bill_doing, tv_bill_finish, tv_vip_activity_time;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_pay_bill_list;
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
        tv_bill_doing = findViewById(R.id.tv_bill_doing);
        tv_bill_finish = findViewById(R.id.tv_bill_finish);
        tv_vip_activity_time = findViewById(R.id.tv_vip_activity_time);
        tv_vip_activity_time.setVisibility(View.GONE);

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
        tv_bill_doing.setOnClickListener(this);
        tv_bill_finish.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.my_bill_detail);
        payBilllist = new ArrayList<>();
        balanceList = new ArrayList<>();
        doingAdapter = new PayBillListAdapter(payBilllist);
        finishAdapter = new RoyaltyListAdapter(this, balanceList);

        doingAdapter.setOnItemClickListener(new PayBillListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, DpPayBean data) {
                Intent intent = new Intent(PayBillListActivity.this, PayBillDetailActivity.class);
                Bundle mbundle = new Bundle();
                mbundle.putSerializable("dpbean", data);
                intent.putExtra("data", mbundle);
                startActivity(intent);
            }
        });

        getBillByBuyerUser();
        getDetails();
//        getQQinfo();
        tv_bill_doing.setBackgroundResource(R.mipmap.image_sliding_block);
        tv_bill_doing.setTextColor(getResources().getColor(R.color.pink));

        recyclerView.setAdapter(doingAdapter);
        setView(payBilllist.size());
    }

    private void setView(int listSize) {
        if (listSize == 0) {
            view_no_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_item.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取用户正在运用的订单信息
     */
    private void getBillByBuyerUser() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        final JsonUtil jsonUtil = new JsonUtil(this);
        String buyerId = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        params.put("head", head);
        params.put("buyerUserId", buyerId);
        final String str = buyerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().getBillByBuyerUser, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(PayBillListActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    payBilllist = jsonUtil.jsonToBalanceBean(dataCollection.toString());
                    if (payBilllist != null) {

                        //排序，按照降序排列
                        Collections.sort(payBilllist, new Comparator<DpPayBean>() {

                            @Override
                            public int compare(DpPayBean lhs, DpPayBean rhs) {
                                int result = rhs.getCreateDate().compareTo(lhs.getCreateDate());
                                return result;
                            }

                        });
                        doingAdapter.updateListView(payBilllist);
                        setView(payBilllist.size());
                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(PayBillListActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 获取所有的消费明细
     */
    private void getDetails() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(PayBillListActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String buyerUserId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerUserId", buyerUserId);
        final String str = buyerUserId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().getBillCList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "getBillList onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(PayBillListActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    balanceList = jsonUtil.jsonToBalances(dataCollection);
                    if (balanceList != null) {

                        //排序，按照降序排列
                        Collections.sort(balanceList, new Comparator<Balance>() {

                            @Override
                            public int compare(Balance lhs, Balance rhs) {
                                String slhs = lhs.getCreateTime() + "";
                                String srhs = rhs.getCreateTime() + "";
                                int result = srhs.compareTo(slhs);
                                return result;
                            }

                        });
                        finishAdapter.update(balanceList);
                        setView(balanceList.size());
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(PayBillListActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });

    }

    @Override
    public void onClick(View v) {

        setUnchooseItem();
        switch (v.getId()) {
            case R.id.tv_bill_doing:
                tv_bill_doing.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_bill_doing.setTextColor(getResources().getColor(R.color.pink));
                setView(payBilllist.size());
                recyclerView.setAdapter(doingAdapter);
                break;
            case R.id.tv_bill_finish:
                tv_bill_finish.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_bill_finish.setTextColor(getResources().getColor(R.color.pink));
                setView(balanceList.size());
                recyclerView.setAdapter(finishAdapter);
                break;
            default:
                break;
        }
    }

    /**
     * 讲设置灰色不选择
     */
    private void setUnchooseItem() {
        tv_bill_doing.setBackgroundResource(R.color.transparent);
        tv_bill_doing.setTextColor(getResources().getColor(R.color.main_theme));
        tv_bill_finish.setBackgroundResource(R.color.transparent);
        tv_bill_finish.setTextColor(getResources().getColor(R.color.main_theme));
    }

    /**
     * 获取QQ号码
     */
    private void getQQinfo() {
        RequestParams params = new RequestParams();
        params.put("type", 5);
        HttpUtil.get(Constants.getInstance().getQQNumber, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        tv_vip_activity_time.setText(resultMessage);
                        tv_vip_activity_time.setVisibility(View.VISIBLE);
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
}
