package com.bber.company.android.view.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.databinding.ActivityAboutUsBinding;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vencent on 2017/3/24.
 */

public class AboutUsActivity extends BaseActivity {

    private ActivityAboutUsBinding binding;
    private boolean isadver = false;
    private TextView text_service, text_cooperation, text_ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        binding.setHeaderBarViewModel(headerBarViewModel);

        initheadView();

    }

    private void initheadView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isadver = bundle.getBoolean("isadver");
            if (isadver == false) {
                headerBarViewModel.setBarTitle("商务合作");
            }
        }
        text_service = binding.textService;
        text_cooperation = binding.textCooperation;
        text_ads = binding.textAds;
        getQQinfo();

        text_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(text_service.getText());
                MyToast.makeTextAnim(AboutUsActivity.this, "客服QQ已复制", 0, R.style.PopToast).show();
            }
        });
        text_cooperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(text_cooperation.getText());
                MyToast.makeTextAnim(AboutUsActivity.this, "加盟QQ已复制", 0, R.style.PopToast).show();
            }
        });
        text_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(text_ads.getText());
                MyToast.makeTextAnim(AboutUsActivity.this, "广告QQ已复制", 0, R.style.PopToast).show();
            }
        });
    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("关于我们");
    }

    private void getQQinfo() {
        RequestParams params = new RequestParams();
        params.put("type", 2);
        HttpUtil.get(Constants.getInstance().getQQNumber, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");

                    JSONObject jsonObject1 = new JSONObject(resultMessage);
                    String service = jsonObject1.getString("service");
                    String cooperation = jsonObject1.getString("cooperation");
                    String ads = jsonObject1.getString("ads");
                    if (resultCode == 1) {
                        text_service.setText(service);
                        text_cooperation.setText(cooperation);
                        text_ads.setText(ads);
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

