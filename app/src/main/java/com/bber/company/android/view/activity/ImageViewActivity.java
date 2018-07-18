package com.bber.company.android.view.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.NoDoubleClickListener;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.widget.MyToast;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageViewActivity extends BaseAppCompatActivity {

    private WebView webview;
    private String url;
    private ProgressDialog progressDialog;
    private String eventName;
    private int payCode;
    private ImageView image_view;
    private TextView saveImage;
//    private LinearLayout loading;

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_image_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWebView();
    }


//    /***
//     * 保存当前屏幕图片
//     * @param v
//     */
//    public synchronized void saveImage(View v){
//        v.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(final View v) {
//                DialogTool tool = new DialogTool();
//                LayoutInflater inflater = LayoutInflater.from(ImageViewActivity.this);
//                View layout = inflater.inflate(R.layout.alertdialog_start_activity, null);
//                tool.createDialogStart(ImageViewActivity.this,layout,R.string.cancal,R.string.open);
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

    private void setWebView() {
        url = getIntent().getStringExtra("url");
        eventName = getIntent().getStringExtra("activityname");
        payCode = getIntent().getIntExtra("payCode", 0);
        title.setText(eventName);
        image_view = findViewById(R.id.image_view);
        saveImage = findViewById(R.id.saveImage);
        Uri image_uri = Uri.parse(url);
        Glide.with(this).load(url).into(image_view);

        saveImage.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                DialogTool tool = new DialogTool();
                LayoutInflater inflater = LayoutInflater.from(ImageViewActivity.this);
                View layout = inflater.inflate(R.layout.alertdialog_start_activity, null);
                tool.createDialogStart(ImageViewActivity.this, layout, R.string.cancal, R.string.open);
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
//        Picture snapShot = webview.capturePicture();
        //获取webview缩放率
//        float scale = webview.getScale();
//得到缩放后webview内容的高度
//        int webViewHeight = (int) (snapShot.getContentHeight()*scale);
        Bmp = Bitmap.createBitmap(image_view.getWidth(), image_view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(Bmp);
        image_view.draw(canvas);

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
                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(), filepath, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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

//                    intent = packageManager.getLaunchIntentForPackage("com.tencent.mm.ui.LauncherUI");
                    intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                }
                if (payCode == 0) {
                    MyToast.makeTextAnim(this, "支付方式错误，请重新选择", 0, R.style.PopToast).show();
                }


                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                MyToast.makeTextAnim(this, "请下载支付宝/微信或者选择别的支付方式", 0, R.style.PopToast).show();
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

}
