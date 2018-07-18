package com.bber.company.android.view.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.customview.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;

public class WebViewGuestActivity extends BaseAppCompatActivity {

    private final static int FILECHOOSER_RESULTCODE = 1;
    public boolean isQQ;
    public String pretty;
    private WebView webview;
    private String url;
    private ProgressDialog progressDialog;
    private String eventName, hiding;
    //    private LinearLayout loading;
    private int payCode;
    private TextView saveImage, web_title;
    private ImageView back_;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_web_guest_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableSlowWholeDocumentDraw();
        super.onCreate(savedInstanceState);


        setWebView();
        setHeader();
    }

    private void setHeader() {
        web_title = findViewById(R.id.web_title);
        back_ = findViewById(R.id.back_);
        back_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (!Tools.isEmpty(pretty)) {
            web_title.setText(pretty);
        } else {
            web_title.setText("啪啪客服");
        }
    }

    private void setWebView() {

        boolean isHaveValue = getIntent().getBooleanExtra("isHaveValue", false);
        pretty = getIntent().getStringExtra("pretty");
        String value = "";
        String userId = "0";
        String username = "注册登录页";
        int level = 0;
        //WebView加载web资源
        if (isHaveValue) {
            LogUtils.e("isHaveValue进来了 为true");
            level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
            userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
            username = (String) SharedPreferencesUtils.get(this, Constants.USERNAME, "");
            if (!Tools.isEmpty(pretty)) {
                username = username + "(" + pretty + ")";
            }
        }
        try {
            String asString = "userId=" + userId + "&name=" + username + "&memo=" + level;
            value = URLEncoder.encode(asString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtils.e("username=" + username);

        url = "https://chat56.live800.com/live800/chatClient/chatbox.jsp?companyID=896572&configID=141991&jid=1734856383&s=1&info=" + value;
        LogUtils.e("url=" + url);
        title.setText(eventName);

        webview = findViewById(R.id.webView);


//        loading = (LinearLayout) findViewById(R.id.loading_lay);
        //加载需要显示的网页
        webview.loadUrl(url);
        webview.reload();
        DialogTool.createProgressDialog(WebViewGuestActivity.this, true);
        webview.setDrawingCacheEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webview.getSettings().setSupportMultipleWindows(true);
        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setAppCacheEnabled(true);

        webview.setWebChromeClient(new WebChromeClient() {
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebViewGuestActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebViewGuestActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebViewGuestActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        WebViewGuestActivity.FILECHOOSER_RESULTCODE);

            }

            // For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebViewGuestActivity.this.startActivityForResult(Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
                return true;
            }
        });
        webview.loadUrl(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {

            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }

    /**
     * 内部类，全部在本地连接中打开
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                webview.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            loading.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            loading.setVisibility(View.GONE);
            DialogTool.dismiss(WebViewGuestActivity.this);
        }


    }


}
