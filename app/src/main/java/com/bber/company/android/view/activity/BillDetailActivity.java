package com.bber.company.android.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Order;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;


public class BillDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_order_pirlf, rl_anyDiscount;
    private Order order;
    private int sellerID;
    private int finalPrice;
    private SimpleDraweeView user_icon;
    private TextView tv_order_status, user_name;
    private TextView tv_status_step1, tv_status_step2, tv_status_step3;
    private TextView tv_order_dot1, tv_order_dot2, tv_order_dot3;
    private TextView tv_order_price, tv_discount_name, tv_discount_price;
    private String[] colors = new String[]{"#f3267e", "#fe7979", "#04edbe", "#6cdeff", "#ffd708", "#ffb108"};
    private TextView tv_order_id, tv_order_phone, tv_order_time, tv_order_method, tv_final_price;
    private String orderstatus[] = {"", "订单发送", "", "订单开始", "等待评价", "订单结束", "订单取消", "订单取消", "", "争议订单"};

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_bill_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        tv_order_status = findViewById(R.id.tv_order_status);
        user_icon = findViewById(R.id.user_icon);
        user_name = findViewById(R.id.user_name);
        rl_order_pirlf = findViewById(R.id.rl_order_pirlf);
        tv_status_step1 = findViewById(R.id.tv_status_step1);
        tv_status_step2 = findViewById(R.id.tv_status_step2);
        tv_status_step3 = findViewById(R.id.tv_status_step3);
        tv_order_dot1 = findViewById(R.id.tv_order_dot1);
        tv_order_dot2 = findViewById(R.id.tv_order_dot2);
        tv_order_dot3 = findViewById(R.id.tv_order_dot3);

        tv_order_price = findViewById(R.id.tv_order_price);
        tv_discount_price = findViewById(R.id.tv_discount_price);
        tv_discount_name = findViewById(R.id.tv_discount_name);
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_order_phone = findViewById(R.id.tv_order_phone);
        tv_order_time = findViewById(R.id.tv_order_time);
        tv_order_method = findViewById(R.id.tv_order_method);
        rl_order_pirlf = findViewById(R.id.rl_order_pirlf);
        tv_final_price = findViewById(R.id.tv_final_price);
        rl_anyDiscount = findViewById(R.id.rl_anyDiscount);
    }

    private void setListeners() {
        rl_order_pirlf.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.order_detail);
        order = (Order) getIntent().getBundleExtra("data").getSerializable("order");

        int index = (int) (Math.random() * colors.length);
        RoundingParams roundingParams =
                user_icon.getHierarchy().getRoundingParams();
        roundingParams.setBorder(Color.parseColor(colors[index]), 4);
        roundingParams.setRoundAsCircle(true);
        user_icon.getHierarchy().setRoundingParams(roundingParams);

        user_icon.setImageURI(order.getSellerHead());
        user_name.setText(order.getSellerNickName());
        tv_order_status.setText(orderstatus[order.getStatus()]);

        sellerID = order.getSellerId();
        tv_order_price.setText("¥" + order.getMoney());
        String disountMoney = "0";
        if ("1".equals(order.getEventStatus())
                || "5".equals(order.getEventStatus())
                || "2".equals(order.getEventStatus())) {
            tv_discount_price.setText("-¥" + order.getEventMoney());
            tv_discount_name.setText("活动优惠");
            disountMoney = order.getEventMoney();
        } else if (Tools.StringToInt(order.getCashCardMoney()) > 0) {
            tv_discount_price.setText("-¥" + order.getCashCardMoney());
            tv_discount_name.setText(order.getCashCardName());
            disountMoney = order.getCashCardMoney();
        } else {
            rl_anyDiscount.setVisibility(View.GONE);
        }
        tv_order_id.setText(order.getBusinessCode());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = new Long(order.getCreateTime());
        String dayTime = format.format(time);
        tv_order_time.setText(dayTime);
        tv_order_method.setText("线下支付");
        finalPrice = order.getMoney() - Tools.StringToInt(disountMoney);
        finalPrice = finalPrice > 0 ? finalPrice : 0;
        tv_final_price.setText("¥" + finalPrice);

        setOrderstatus(order.getStatus());
    }

    public void setOrderstatus(int status) {
        tv_status_step1.setTextColor(getResources().getColor(R.color.sub_text));
        tv_order_dot1.setTextColor(getResources().getColor(R.color.sub_text));
        tv_status_step2.setTextColor(getResources().getColor(R.color.sub_text));
        tv_order_dot2.setTextColor(getResources().getColor(R.color.sub_text));
        tv_status_step3.setTextColor(getResources().getColor(R.color.sub_text));
        tv_order_dot3.setTextColor(getResources().getColor(R.color.sub_text));

        if (status < 3) {
            tv_status_step1.setTextColor(getResources().getColor(R.color.pink));
            tv_order_dot1.setTextColor(getResources().getColor(R.color.pink));
        } else if (status < 5) {
            tv_status_step2.setTextColor(getResources().getColor(R.color.pink));
            tv_order_dot2.setTextColor(getResources().getColor(R.color.pink));
        } else {
            tv_status_step3.setTextColor(getResources().getColor(R.color.pink));
            tv_order_dot3.setTextColor(getResources().getColor(R.color.pink));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_order_pirlf:
                Intent intent = new Intent(BillDetailActivity.this, GirlProfileActivity.class);
                intent.putExtra("from", 1);
                intent.putExtra("sellerId", sellerID);
                startActivity(intent);
                break;
        }
    }
}
