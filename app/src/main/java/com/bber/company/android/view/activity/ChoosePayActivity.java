package com.bber.company.android.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class ChoosePayActivity extends BaseAppCompatActivity implements View.OnClickListener {

    public SimpleDraweeView user_icon;
    private RelativeLayout rl_choose_online, rl_choose_outline, rl_choose_cancle;
    private int totalPrice;
    private int sellerId, ID;
    private String sellerName, sellerHead, businessCode;
    private TextView tv_userName, tv_price;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_choosepay;
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
        rl_choose_online = findViewById(R.id.rl_choose_online);
        rl_choose_outline = findViewById(R.id.rl_choose_outline);
        rl_choose_cancle = findViewById(R.id.rl_choose_cancle);
        tv_userName = findViewById(R.id.user_name);
        tv_price = findViewById(R.id.tv_price);
        user_icon = findViewById(R.id.user_icon);
    }

    private void setListeners() {
        rl_choose_online.setOnClickListener(this);
        rl_choose_outline.setOnClickListener(this);
        rl_choose_cancle.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.pay_method);
        totalPrice = getIntent().getIntExtra("totalPrice", 0);
        sellerId = getIntent().getIntExtra("sellerId", 0);
        sellerName = getIntent().getStringExtra("sellerName");
        sellerHead = getIntent().getStringExtra("sellerHead");
        businessCode = getIntent().getStringExtra("businessCode");
        ID = getIntent().getIntExtra("ID", 0);

        if (!Tools.isEmpty(sellerHead)) {
            Uri uri = Uri.parse(sellerHead);
            user_icon.setImageURI(uri);
        }
        tv_userName.setText(sellerName);
        tv_price.setText(totalPrice + "");
    }

    private void cancleOrder() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(ChoosePayActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        DialogTool.createProgressDialog(this, true);
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("id", ID);
        final String str = ID + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().cancleOrder, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "cancleOrder onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(ChoosePayActivity.this, jsonObject, null)) {
                    return;
                }
                //通知首页刷新聊天会话列表
                Intent mIntent = new Intent(Constants.ACTION_HOME);
                mIntent.putExtra("type", Constants.UPDATE_MSG);
                sendBroadcast(mIntent);

                Intent intent = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
                intent.putExtra("isUpdate", false);
                intent.putExtra("isOrder", 2);
                sendBroadcast(intent);

                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(ChoosePayActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                DialogTool.dismiss(ChoosePayActivity.this);
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.user_icon:
            case R.id.username:
                intent = new Intent(this, GirlProfileActivity.class);
                intent.putExtra("from", Constants.COME_FROM_CHAT_ROOM);
                intent.putExtra("sellerId", sellerId);
                startActivity(intent);
                break;
            case R.id.rl_choose_online:

                break;
            case R.id.rl_choose_outline:
                intent = new Intent(ChoosePayActivity.this, ConfirmOrderActivity.class);
                intent.putExtra("totalPrice", totalPrice);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("businessCode", businessCode);
                startActivity(intent);
                break;
            case R.id.rl_choose_cancle:
                cancleOrder();
                break;
            default:
                break;
        }
    }
}
