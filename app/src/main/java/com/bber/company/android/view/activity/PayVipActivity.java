package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class PayVipActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView icon_user;
    private TextView tv_vipName, tv_vip_price, tv_balance, tv_payTip, tv_money;
    private Button btn_payNext;
    private int mType;
    private int payVipId;
    private int iconResId;
    private String vipname;
    private double payMoney;
    private double balance;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_payvip;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getContext();
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title = toolbar.findViewById(R.id.title);
        tv_vipName = findViewById(R.id.tv_vipName);
        btn_payNext = findViewById(R.id.btn_payNext);
        tv_money = findViewById(R.id.tv_money);
        tv_vip_price = findViewById(R.id.tv_vip_price);
        tv_balance = findViewById(R.id.tv_balance);
        tv_payTip = findViewById(R.id.tv_payTip);
        icon_user = findViewById(R.id.user_icon);
    }

    private void setListeners() {
        btn_payNext.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.buy_vip);
        payVipId = getIntent().getIntExtra(Constants.VIP_ID, 0);
        payMoney = getIntent().getDoubleExtra(Constants.PAY_MONEY, 0);
        mType = getIntent().getIntExtra("type", 0);
        iconResId = getIntent().getIntExtra("icon", 0);
        vipname = getIntent().getStringExtra("vipname");


        String userMoney = (String) SharedPreferencesUtils.get(this, Constants.USER_MONEY, "");
        balance = Double.parseDouble(userMoney);

        tv_vipName.setText(vipname);
        icon_user.setImageResource(iconResId);
        tv_money.setText(payMoney + "");
        tv_balance.setText("¥" + balance);
        tv_vip_price.setText("¥" + payMoney);
        if (balance < payMoney) {
            tv_payTip.setText(R.string.no_enough_money);
        }
    }

    /**
     * 购买VIP
     * *
     */
    private void buyerUserVipByBalance(double money) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        final String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        DialogTool.createProgressDialog(this, true);
        params.put("head", head);
        params.put("buyerUserId", userId);
        params.put("money", money);
        params.put("vipId", payVipId);
        params.put("type", mType);
        final String str = userId + "" + money + "" + payVipId + "" + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().buyerUserVipByBalance, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(PayVipActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String massage = jsonObject.getJSONObject("dataCollection").getString("massage");
                    int status = jsonObject.getJSONObject("dataCollection").getInt("status");
                    if (status == 1) {
                        //一旦成功，减少表面层的金钱与后台一致
                        SharedPreferencesUtils.put(PayVipActivity.this, Constants.USER_MONEY, balance - payMoney);
                        MyToast.makeTextAnim(PayVipActivity.this, "恭喜，购买成功", 0, R.style.PopToast).show();
                        app.newActivity.add(PayVipActivity.this);
                        app.closeListActivity();
                    } else {
                        MyToast.makeTextAnim(PayVipActivity.this, massage, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(PayVipActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(PayVipActivity.this);
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_payNext:
                if (balance < payMoney) {
                    app.closeListActivity();
                    MyToast.makeTextAnim(PayVipActivity.this, R.string.wallet_no_enough_money, 0, R.style.PopToast).show();
                } else {
                    buyerUserVipByBalance(payMoney);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            app.closeListActivity();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
