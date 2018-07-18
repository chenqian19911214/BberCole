package com.bber.company.android.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.NoDoubleClickListener;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;

import java.io.File;
import java.io.FileOutputStream;

import static android.webkit.WebView.enableSlowWholeDocumentDraw;

public class webViewActivity extends BaseAppCompatActivity {

    public boolean isQQ;
    private WebView webview;
    private String url;
    private ProgressDialog progressDialog;
    private String eventName, hiding;
    private int payCode;
    //    private LinearLayout loading;
    private TextView saveImage;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableSlowWholeDocumentDraw();
        super.onCreate(savedInstanceState);
        setWebView();
    }

    private void setWebView() {
        url = getIntent().getStringExtra("url");
        eventName = getIntent().getStringExtra("activityname");
        payCode = getIntent().getIntExtra("payCode", 0);
        isQQ = getIntent().getBooleanExtra("isQQ", false);
        hiding = getIntent().getStringExtra("hiding");
        title.setText(eventName);

        webview = findViewById(R.id.webView);
        saveImage = findViewById(R.id.saveImage);
        if (!Tools.isEmpty(hiding)) {
            if (hiding.equals("1")) {
                saveImage.setVisibility(View.GONE);
            }
        }
//        loading = (LinearLayout) findViewById(R.id.loading_lay);
        //加载需要显示的网页
        webview.loadUrl(url);
        DialogTool.createProgressDialog(webViewActivity.this, true);
        webview.setDrawingCacheEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //关键点
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(true);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);

        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webview.setDownloadListener(new MyWebViewDownLoadListener());

        saveImage.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DialogTool tool = new DialogTool();
                LayoutInflater inflater = LayoutInflater.from(webViewActivity.this);
                View layout = inflater.inflate(R.layout.alertdialog_start_activity, null);
                tool.createDialogStart(webViewActivity.this, layout, R.string.cancal, R.string.open);
                tool.setActionListener(new IactionListener() {
                    @Override
                    public void SuccessCallback(Object o) {
                        saveCurrentImage();
                    }

                    @Override
                    public void FailCallback(String result) {
                    }
                });
            }
        });
        //设置Web视图
//        webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    progressDialog.dismiss();
//                }
//            }
//        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            setResult(Activity.RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean shouldUpRecreateTask(Intent targetIntent) {
        return super.shouldUpRecreateTask(targetIntent);
    }

    /**
     * 是否要调用新的app
     *
     * @param url
     * @return
     */
    public boolean parseScheme(String url) {
        return url.contains("intent://platformapi/startapp?");
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    private Bitmap captureWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();

        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }

    /**
     * 获取和保存当前屏幕的截图 并且有一个跳转的链接
     */
    public void saveCurrentImage() {
        //1.构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //2.获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
//        Bmp = decorview.getDrawingCache();
        //截取显示出来的webview
//        Bmp = webview.getDrawingCache();
        //截取全部出来的webview 显示和未显示的
        Picture snapShot = webview.capturePicture();
        //获取webview缩放率
        float scale = webview.getScale();
//得到缩放后webview内容的高度
        int webViewHeight = (int) (webview.getContentHeight() * scale);
        Bmp = Bitmap.createBitmap(webview.getWidth(), webViewHeight, Bitmap.Config.ARGB_8888);
//        Bmp = Bitmap.createBitmap(snapShot.getWidth(),snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(Bmp);
        webview.draw(canvas);

        String SavePath = getSDCardPath() + "/DCIM/ScreenImage";

        //3.保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            if (!path.exists()) {
                path.mkdirs();
            }
            String filepath = null;
            File[] pathFileData = path.listFiles();
            filepath = SavePath + "/Screen_" + 0 + ".png";
            for (int i = 0; i < pathFileData.length; i++) {
                filepath = SavePath + "/Screen_" + i + 1 + ".png";
            }
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();

                //保存本地截图，并且发送广播到系统相册强制刷新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
//                MediaScannerConnection.scanFile(this, new String[]{SavePath.toString()}, null, null);
//                Toast.makeText(this, "截屏文件已保存至SDCard/DCIM/ScreenImage/下", Toast.LENGTH_LONG).show();
                MyToast.makeTextAnim(this, "截屏文件已保存至SDCard/DCIM/ScreenImage/下", 0, R.style.PopToast).show();
            }

            //这里是做的跳转链接,跳转到支付宝或者微信
            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent();
            try {
                if (payCode == Constants.ALIPAY_CODE) {
                    intent = packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                } else if (payCode == Constants.UNIONPAY_CODE) {

                } else if (payCode == Constants.WECHATPAY_CODE) {
                    intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                } else if (isQQ) {
                    intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
                }
                if (payCode == 0) {
                    MyToast.makeTextAnim(this, "支付方式错误，请重新选择", 0, R.style.PopToast).show();
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                MyToast.makeTextAnim(this, "请下载QQ/支付宝/微信或者选择别的支付方式", 0, R.style.PopToast).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 保存当前屏幕图片
     * @param v
     */
//    public synchronized void saveImage(View v){
//        v.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                DialogTool tool = new DialogTool();
//                LayoutInflater inflater = LayoutInflater.from(webViewActivity.this);
//                View layout = inflater.inflate(R.layout.alertdialog_start_activity, null);
//                tool.createDialogStart(webViewActivity.this,layout,R.string.cancal,R.string.open);
//                tool.setActionListener(new IactionListener() {
//                    @Override
//                    public void SuccessCallback(Object o) {
//                        saveCurrentImage();
//                    }
//
//                    @Override
//                    public void FailCallback(String result) {
//                    }
//                });
//            }
//        });
//
//    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    /**
     * 内部类，全部在本地连接中打开
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
           /* if (otherBrowser == true){
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else {
                webview.loadUrl(url);
                if (progressDialog.isShowing() == false ) {
                    progressDialog = DialogTool.createProgressDialog(webViewActivity.this, "加载中，请稍后...");
                }
                webview.reload();
            }
            return true;
            */
            if (parseScheme(url)) {
                try {
                    Intent intent;
                    intent = Intent.parseUri(url,
                            Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    startActivity(intent);

                } catch (Exception e) {

                }
            } else {
                view.loadUrl(url);
//                if (progressDialog.isShowing() == false ) {
//                    progressDialog = DialogTool.createProgressDialog(webViewActivity.this, "加载中，请稍后...");
//                }
            }

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
            DialogTool.dismiss(webViewActivity.this);
        }
    }

    /**
     * 下载监听器
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

}
