package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.VoucherBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class ConfirmOrderActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private Button btn_confirm_order;
    private RelativeLayout rl_order_price, rl_coupon;
    private TextView tv_confirm_price, tv_confirm_vouche, my_balance;
    private ImageView iv_voucher_arrow;
    private VoucherBean mVoucher;
    private int finalprice;
    private int totalPrice;
    private int sellerId;
    private int discount_price = 0;
    private String businessCode;
    private String cashCardId = "";
    private int enventStatus = 0;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_confirmorder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app.newActivity.add(this);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        rl_order_price = findViewById(R.id.rl_order_price);
        rl_coupon = findViewById(R.id.rl_coupon);
        btn_confirm_order = findViewById(R.id.btn_confirm_order);
        tv_confirm_price = findViewById(R.id.tv_confirm_price);
        iv_voucher_arrow = findViewById(R.id.iv_voucher_arrow);
        tv_confirm_vouche = findViewById(R.id.tv_confirm_vouche);
        my_balance = findViewById(R.id.my_balance);
    }

    private void setListeners() {
        rl_coupon.setOnClickListener(this);
        btn_confirm_order.setOnClickListener(this);
        rl_order_price.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.choose_pay);
        totalPrice = getIntent().getIntExtra("totalPrice", 0);
        businessCode = getIntent().getStringExtra("businessCode");
        sellerId = getIntent().getIntExtra("sellerId", 0);
        my_balance.setText(totalPrice + "");
        finalprice = totalPrice;
        tv_confirm_price.setText(finalprice + "");
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_coupon:
                Intent intent = new Intent(ConfirmOrderActivity.this, VoucherActivity.class);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra(Constants.VOUCHERFROM, Constants.VOUCHER_FROM_CONFIRM_ORDER);
                startActivityForResult(intent, Constants.REQUEST_CODE_VOUCHE_RESULT);
                break;
            case R.id.rl_order_price:
                break;
            case R.id.btn_confirm_order:
                updateOrder();
                break;
            default:
                break;
        }
    }

    /**
     * 推送消息
     * *
     */
    private void updateOrder() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        final String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        params.put("head", head);
        params.put("businessCode", businessCode);
        params.put("buyerId", userId);
        params.put("money", totalPrice);
        params.put("cashCardId", cashCardId);
        params.put("eventStatus", enventStatus);
        params.put("eventMoney", discount_price);
        params.put("finalPayment", finalprice);
        HttpUtil.post(Constants.getInstance().updateOrder, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(ConfirmOrderActivity.this, jsonObject, null)) {
                    return;
                }
                Intent intent = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
                intent.putExtra("isUpdate", false);
                intent.putExtra("isOrder", 2);
                sendBroadcast(intent);

                app.closeListActivity();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(ConfirmOrderActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_VOUCHE_RESULT:
                    mVoucher = (VoucherBean) data.getSerializableExtra("voucher");
                    enventStatus = data.getIntExtra("enventStatus", 1);
                    tv_confirm_vouche.setVisibility(View.VISIBLE);
                    tv_confirm_vouche.setText(mVoucher.getCashCardName());
                    iv_voucher_arrow.setVisibility(View.GONE);
                    discount_price = Tools.StringToInt(mVoucher.getCashCardMoney());
                    finalprice = totalPrice;
                    finalprice -= discount_price;
                    finalprice = finalprice > 0 ? finalprice : 0;
                    tv_confirm_price.setText(finalprice + "");
                    cashCardId = mVoucher.getCashCardId();
                    break;
            }
        } else {
            enventStatus = 0;
            tv_confirm_vouche.setText("");
            tv_confirm_vouche.setVisibility(View.GONE);
            finalprice = totalPrice;
            finalprice = finalprice > 0 ? finalprice : 0;
            iv_voucher_arrow.setVisibility(View.VISIBLE);
            tv_confirm_price.setText(finalprice + "");
        }
    }
}
