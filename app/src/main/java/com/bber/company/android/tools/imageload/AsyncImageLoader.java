package com.bber.company.android.tools.imageload;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bber.company.android.R;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.ToastUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2015-05-11.
 */
public class AsyncImageLoader {

    private NetWork netWork;
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
    private Activity activity;

    public AsyncImageLoader(Activity mactivity) {
        activity = mactivity;
        netWork = new NetWork(activity);
        memoryCache = new ImageMemoryCache(mactivity);
        fileCache = new ImageFileCache();
    }

    /**
     * 286.
     * 创建子线程加载图片
     * 287.
     * 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
     * 288.
     * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
     *
     * @param imageUrl       ：须要加载的图片url
     * @param imageCallback：
     * @return
     */
    public Bitmap loadDrawable(final String imageUrl, final ProgressBar progress_wheel,
                               final ImageCallback imageCallback) {

        /**
         * 在主线程里执行回调，更新视图
         */
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        if (progress_wheel != null) {
                            ToastUtils.showToast(R.string.download_image_fail, 0);
                            progress_wheel.setVisibility(View.GONE);
                        }
                        break;
                    case 0:
                        imageCallback.imageLoaded((Bitmap) msg.obj, imageUrl);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        };

        // 从内存缓存中获取图片
        Bitmap finalBitmap = memoryCache.getBitmapFromCache(imageUrl);
        Log.e("eeeeeeeeeeee", "缓存中获取:" + finalBitmap);
        if (finalBitmap != null) {
            imageCallback.imageLoaded(finalBitmap, imageUrl);//执行回调
            return finalBitmap;
        } else {
            // 文件缓存中获取
            finalBitmap = fileCache.getImage(imageUrl);
            Log.e("eeeeeeeeeeee", "文件中获取:" + finalBitmap);
            if (finalBitmap != null) {
                memoryCache.addBitmapToCache(imageUrl, finalBitmap);
                imageCallback.imageLoaded(finalBitmap, imageUrl);//执行回调
                return finalBitmap;
            }
        }


/**
 * 创建子线程访问网络并加载图片 ，把结果交给handler处理
 */
        if (netWork.isNetworkConnected()) {
            Log.e("eeeeeeeeeeee", "从网络获取:" + imageUrl);
            if (progress_wheel != null) {
                progress_wheel.setVisibility(View.VISIBLE);
            }

            new Thread() {
                @Override
                public void run() {
                    // 从网络获取
                    Bitmap finalBitmap = ImageGetFromHttp.downloadBitmap(imageUrl);
                    if (finalBitmap != null) {
                        Bitmap bitmap = comp(finalBitmap);
                        fileCache.saveBitmap(bitmap, imageUrl);
                        memoryCache.addBitmapToCache(imageUrl, bitmap);
                        handler.obtainMessage(0, bitmap).sendToTarget();
                    } else {
                        handler.obtainMessage(-1).sendToTarget();
                    }
                }
            }.start();
        } else {
            ToastUtils.showToast(R.string.no_network, 0);
        }
        return null;
    }

    /**
     * 压缩下载完的图片
     * <p/>
     * *
     */
    private Bitmap comp(Bitmap mbitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mbitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//            be = (int) (newOpts.outWidth / ww);
            be = (int) Math.rint(newOpts.outWidth / ww);

        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//            be = (int) (newOpts.outHeight / hh);
            be = (int) Math.rint(newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        Log.e("eeeeeeeeeeee", "图片压缩倍数be:" + be);
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }


    /**
     * 定义回调接口
     */
    public interface ImageCallback {
        void imageLoaded(Bitmap bitmap, String imageUrl);
    }
}
