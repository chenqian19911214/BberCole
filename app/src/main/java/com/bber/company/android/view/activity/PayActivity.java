package com.bber.company.android.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bber.company.android.R;
import com.bber.company.android.tools.ToastUtils;

public class PayActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.pay_tip);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void setListeners() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //页面下载完毕,并不代表页面渲染完毕显示出来
                if (webView.getContentHeight() != 0) {
                    //这个时候网页才显示
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("eeeeeeeeeeeeeeeeee", "---shouldOverrideUrlLoading:" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }


    private void initData() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.showToast(R.string.no_network, 0);
            return;
        }
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
        webView.reload();


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(Activity.RESULT_OK);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.call:
//                break;

        }
    }
}
