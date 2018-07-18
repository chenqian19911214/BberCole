package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class GetKeyActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView sign, ima_circle_1;
    private TextView sign_tip, get_key_num;
    private RelativeLayout view_sign;
    private String addKey = "0";
    private int type;// 1:获取签到状态  2：签到

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_getkey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.key_title);
        sign = findViewById(R.id.sign);
        sign_tip = findViewById(R.id.sign_tip);
        get_key_num = findViewById(R.id.get_key_num);
        ima_circle_1 = findViewById(R.id.ima_circle_1);
        view_sign = findViewById(R.id.view_sign);
    }


    private void setListeners() {
        sign.setOnClickListener(this);
    }

    private void initData() {
        type = 1;
        getInfo();
    }

    //  ProgressDialog progressDialog;

    private void getInfo() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (type == 2) {
            DialogTool.createProgressDialog(this, true);
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("type", type);
        HttpUtil.get(Constants.getInstance().signIn, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "signIn onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(GetKeyActivity.this, jsonObject, null)) {
                    return;
                }
                view_sign.setVisibility(View.VISIBLE);
                if (type == 2) {
                    sign_tip.setText("已签到");
                    ima_circle_1.setEnabled(false);
                    sign.setEnabled(false);
                    Intent mIntent = new Intent(Constants.ACTION_HOME);
                    mIntent.putExtra("type", "addkey");
                    mIntent.putExtra("addKey", addKey);
                    //发送广播  通知首页刷新列表
                    sendBroadcast(mIntent);
                } else {
                    try {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        String status = dataCollection.getString("status");
                        addKey = dataCollection.getString("keyNumber");
                        if (status != null && status.equals("1")) {
                            sign_tip.setText("已签到");
                            get_key_num.setText("+" + addKey);
                            ima_circle_1.setEnabled(false);
                            sign.setEnabled(false);
                        } else {
                            sign_tip.setText("签到");
                            get_key_num.setText("+" + addKey);
                            ima_circle_1.setEnabled(true);
                            sign.setEnabled(true);
                        }

                    } catch (JSONException e) {

                    }
                }


            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "signIn onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(GetKeyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 瀹屾垚鍚庤皟鐢紝澶辫触锛屾垚鍔燂紝閮借璋冪敤
                DialogTool.dismiss(GetKeyActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign:
                type = 2;
                getInfo();
                break;
        }
    }

}
