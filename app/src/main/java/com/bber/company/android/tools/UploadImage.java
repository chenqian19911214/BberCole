package com.bber.company.android.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.util.ChenQianLog;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2015/11/5 0005.
 */
public class UploadImage {

    private static Activity mactivity;

    /**
     * type 0:相册 1：拍照
     * <p>
     * 注意测试评价这块
     */
    public static void upload(Activity activity, int type) {
        mactivity = activity;
        if (type == 0) {  //相册
            Intent intent = new Intent(Intent.ACTION_PICK/*,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI*/);
            intent.setType(/*MediaStore.Images.Media.EXTERNAL_CONTENT_URI,*/"image/*");
            activity.startActivityForResult(intent, Constants.REQUEST_CODE_PHOTO_ALBUM);

        } else { //相机
            final File imageFile = new ImageFileCache().initUploadFile();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Tools.hasSDCard()) {


                ChenQianLog.i("imageFile:" + imageFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));

                activity.startActivityForResult(intent, Constants.REQUEST_CODE_PHOTO_GRAPH);

            } else {
                ToastUtils.showToast(R.string.no_sdCard, 0);
            }
        }

    }

    /**
     * 截取图片
     */
    public static void cropImage(Uri uri, Context context) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);


        //将存储图片的uri读写权限授权给剪裁工具应用
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent intent1 = Intent.createChooser(intent, "");

        mactivity.startActivityForResult(intent1, Constants.REQUEST_CODE_PHOTO_CUT);// 启动裁剪程序

    }

}
