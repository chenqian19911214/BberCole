package com.bber.company.android.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
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
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.NoDoubleClickListener;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.widget.MyToast;

import java.io.File;
import java.io.FileOutputStream;

public class webViewPostActivity extends BaseAppCompatActivity {

    public boolean isQQ;
    private WebView webview;
    private String urlParameter;
    private String url, hiding;
    //    private ProgressDialog progressDialog;
    private String eventName;
    private int payCode;
    //    private LinearLayout loading;
    private TextView saveImage;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWebView();
    }

    private void setWebView() {
        urlParameter = getIntent().getStringExtra("urlParameter");
        url = getIntent().getStringExtra("url");
        hiding = getIntent().getStringExtra("hiding");


        title.setText(eventName);
        //实例化WebView对象
        webview = findViewById(R.id.webView);
        saveImage = findViewById(R.id.saveImage);
        if (hiding != null && hiding.equals("1")) {
            saveImage.setVisibility(View.GONE);
        }
        //loading = (LinearLayout) findViewById(R.id.loading_lay);
        //加载需要显示的网页
        webview.postUrl(url, urlParameter == null ? null : urlParameter.getBytes());
        //progressDialog = DialogTool.createProgressDialog(webViewPostActivity.this, "加载中，请稍后...");
        WebSettings webSettings = webview.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");//设置网页默认编码
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url); //url为你要链接的地址
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //loading.setVisibility(View.GONE);
                //progressDialog.dismiss();
            }
        });
        webview.setWebChromeClient(new MyWebChromeClient());// 设置浏览器可弹窗
        webview.setDownloadListener(new MyWebViewDownLoadListener());

        saveImage.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DialogTool tool = new DialogTool();
                LayoutInflater inflater = LayoutInflater.from(webViewPostActivity.this);
                View layout = inflater.inflate(R.layout.alertdialog_start_activity, null);
                tool.createDialogStart(webViewPostActivity.this, layout, R.string.cancal, R.string.open);
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
//        int webViewHeight = (int) (snapShot.getContentHeight()*scale);
        Bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(Bmp);
        snapShot.draw(canvas);

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

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
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
     * 浏览器可弹窗
     *
     * @author Administrator
     */
    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            new AlertDialog.Builder(webViewPostActivity.this)
                    .setTitle("App Titler")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    result.cancel();
                                }
                            }).create().show();

            return true;
        }
    }

    /**
     * 内部类，全部在本地连接中打开
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.postUrl(url, urlParameter.getBytes());
            return super.shouldOverrideUrlLoading(view, url);
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
//            progressDialog.dismiss();
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
