package com.bber.company.android.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.country.PhotoUtils;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.view.customcontrolview.MovieRecorderView;
import com.bber.company.android.widget.MyToast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import static com.bber.company.android.R.id;
import static com.bber.company.android.R.layout;
import static com.bber.company.android.R.style;
import static com.bber.company.android.view.activity.myProfileSettingActivity.hasSdcard;

/**
 * 主播认证 上传头像视频
 * A login screen that offers login via email/password.
 */
public class LiveBroadcastAuthenticationPhotoActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int CAMERA_PERMISSIONS_REQUEST_VOID = 0x05;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;
    private SimpleDraweeView upload_photo_image;
    private ImageView upload_photo_image_button;
    private SimpleDraweeView upload_voideo_image;
    private ImageView upload_photo_devoid_button;
    private ImageView upload_start_button;
    private File imageFile, voidefile;
    private MovieRecorderView movieRecorderView;
    /**
     * 拍照后的保存文件
     */
    private File fileUris;
    private Uri imageUri, croppedImage, uri;
    private Uri cropImageUri;
    private File fileCropUris = new ImageFileCache().initUploadcropFile();
    private String usVideonumber, touurl, viodeourl;
    /**
     * 头像的 bitmap
     */
    private Bitmap bitmap, Videobitmap;
    private int MY_PERMISSIONS_CAMERA = 1;
    private int MY_PERMISSIONS_WRITE = 2;

    @Override
    protected int getContentViewLayoutId() {
        return layout.activity_live_broadcast_authentication_photo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText("主播认证");

        initdata();
        initView();
        initLinstener();
    }

    private void initdata() {

        touurl = (String) SharedPreferencesUtils.get(getApplicationContext(), "touurl", "");
        viodeourl = (String) SharedPreferencesUtils.get(getApplicationContext(), "uploadVideouri", "");

        imageFile = new ImageFileCache().initUploadFile();
        Intent intent = getIntent();
        usVideonumber = intent.getStringExtra("usVideonumber");
        if (usVideonumber != null) {
            movieRecorderView = new MovieRecorderView(getApplicationContext());
            voidefile = new File(movieRecorderView.getmVecordFile());
            MyToast.makeTextAnim(getApplicationContext(), usVideonumber, 0, style.PopToast).show();
            Videobitmap = getVideoThumbnail(voidefile.getPath());

            try {
                uploadVoideo(voidefile, usVideonumber);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //}
        }

    }

    private void initLinstener() {
        upload_photo_image_button.setOnClickListener(this);
        upload_photo_devoid_button.setOnClickListener(this);
        upload_start_button.setOnClickListener(this);
    }

    private void initView() {
        upload_photo_image = findViewById(id.upload_photo_image);
        upload_photo_image_button = findViewById(id.upload_photo_image_button);
        upload_voideo_image = findViewById(id.upload_voideo_image);
        upload_photo_devoid_button = findViewById(id.upload_photo_devoid_button);
        upload_start_button = findViewById(id.upload_start_button);

       /* *//**s设置视频*//*
        if (Videobitmap != null) {

              upload_voideo_image.setImageBitmap(Videobitmap);
        }*/
        /**设置图片*/
        if (touurl != null) {
            setUploadPhoto(touurl, upload_photo_image);
        }

        if (viodeourl != null) {
            upload_voideo_image.setImageBitmap(createVideoThumbnail(viodeourl));
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case id.upload_photo_image_button://上传头像

                final View views = LayoutInflater.from(this).inflate(layout.custom_dialog_upload_bottom, null);
                final AlertDialog dialog = DialogTool.createUploadDialog(this, views);
                views.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        try {  //相册
                            autoObtainStoragePermission();  //

                        } catch (Exception e) {

                            MyToast.makeTextAnim(getApplicationContext(), "调用相机失败，请到设置中打开相机权限", 1, style.PopToast).show();
                        }
                    }
                });
                views.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();//相机
                        autoObtainCameraPermission();
                    }
                });

                break;
            case id.upload_photo_devoid_button://上传 视频

                autoObtainCameraPermissionvidoe();
                break;
            case id.upload_start_button://开始认证
                submissionAudit();
                break;

        }
    }


    /**
     * 自动获取相机存储  视频时权限
     */
    private void autoObtainCameraPermissionvidoe() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                MyToast.makeTextAnim(getApplicationContext(), "您已经拒绝过一次", 0, style.PopToast).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, CAMERA_PERMISSIONS_REQUEST_VOID);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {


                Intent intent = new Intent(this, RECActivity.class); //跳转到录制视频
                // intent.putExtra(Constants.SELLERID, uSellerID);
                startActivity(intent);
                finish();

            } else {

                MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, style.PopToast).show();

            }
        }
    }

    /**
     * 获取本地视频截图
     */
    private Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获取网络视频截图
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap createVideoThumbnail(String url) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, MyApplication.screenWidth, Tools.dip2px(this, 200),
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    /**
     * 自动获取相机权限  头像时使用
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                MyToast.makeTextAnim(getApplicationContext(), "您已经拒绝过一次", 0, style.PopToast).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                fileUris = new ImageFileCache().initUploadFile();
                imageUri = Uri.fromFile(fileUris);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(LiveBroadcastAuthenticationPhotoActivity.this, getPackageName() + ".BuildConfig", fileUris);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                //ToastUtils.showShort(this, "设备没有SD卡！");

                MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, style.PopToast).show();

            }
        }
    }

    /**
     * 自动获取sdk权限
     */

    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }

    }

    /**
     * 检查 存储权限
     */
    private void applicationPermissionsxie() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE);
            } else {
                saveBitmap(bitmap);
            }
        }

    }

    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(Bitmap bitmap) {

        if (imageFile.exists()) {
            // imageFile.mkdir();
            imageFile.delete();
        }
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            // upload_photo_image.setImageBitmap(bitmap);

            uploadImage(imageFile);
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }

    }

    private void uploadImage(File imageFile) throws FileNotFoundException {

        DialogView.show(LiveBroadcastAuthenticationPhotoActivity.this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        params.put("file", imageFile);

        HttpUtil.post(Constants.getInstance().uploadheadm, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);


                        if (jsonObject.getInt("resultCode") == 1) {
                            String touurl = jsonObject.getString("dataCollection");
                            String resultMessage = jsonObject.getString("resultMessage");
                            setUploadPhoto(touurl, upload_photo_image);
                            SharedPreferencesUtils.put(getApplicationContext(), "touurl", touurl);
                            MyToast.makeTextAnim(getApplicationContext(), resultMessage, 0, style.PopToast).show();
                            SharedPreferencesUtils.put(getApplicationContext(), Constants.BROADCAST_LEVEL, 1);

                        }


                        ChenQianLog.i("上传主播头像json:" + requst);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(LiveBroadcastAuthenticationPhotoActivity.this);

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });



    }

    /**
     * 上传视频
     */
    private void uploadVoideo(File imageFile, String videonumber) throws FileNotFoundException {

        DialogView.show(LiveBroadcastAuthenticationPhotoActivity.this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        params.put("file", imageFile);
        params.put("videonumber", videonumber);

        HttpUtil.post(Constants.getInstance().uploadVideo, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);


                        if (jsonObject.getInt("resultCode") == 1) {
                            String viodeourl = jsonObject.getString("dataCollection");
                            String resultMessage = jsonObject.getString("resultMessage");
                            if (viodeourl != null) {
                                upload_voideo_image.setImageBitmap(createVideoThumbnail(viodeourl));
                            }
                            SharedPreferencesUtils.put(getApplicationContext(), "uploadVideouri", viodeourl);
                            SharedPreferencesUtils.put(getApplicationContext(), Constants.BROADCAST_LEVEL, 1);
                            MyToast.makeTextAnim(getApplicationContext(), resultMessage, 0, style.PopToast).show();
                        }
                        ChenQianLog.i("上传主播视频json:" + requst);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(LiveBroadcastAuthenticationPhotoActivity.this);

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });


    }

    /**
     * 提交
     */
    private void submissionAudit() {

        DialogView.show(LiveBroadcastAuthenticationPhotoActivity.this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);

        HttpUtil.post(Constants.getInstance().checkAnchor, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");
                        JSONObject jsonObject = new JSONObject(requst);


                        if (jsonObject.getInt("resultCode") == 1) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            SharedPreferencesUtils.put(getApplicationContext(), Constants.BROADCAST_LEVEL, 2);
                            MyToast.makeTextAnim(getApplicationContext(), resultMessage, 0, style.PopToast).show();

                            finish();
                        }
                        ChenQianLog.i("上传审核json:" + requst);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                DialogView.dismiss(LiveBroadcastAuthenticationPhotoActivity.this);
            }
        });

    }

    private void setUploadPhoto(String touurl, SimpleDraweeView imageview) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(Uri.parse(touurl));
        imagePipeline.evictFromDiskCache(Uri.parse(touurl));
        // combines above two lines
        imagePipeline.evictFromCache(Uri.parse(touurl));

        imageview.setImageURI(touurl);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                //拍照完成回调
                case CODE_CAMERA_REQUEST:
                    cropImageUri = Uri.fromFile(fileCropUris);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    break;
                //访问相册完成回调
                case CODE_GALLERY_REQUEST:

                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUris);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(this, getPackageName() + ".BuildConfig", new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    } else {
                        MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, style.PopToast).show();
                    }

                    //   if (hasSdcard()) {
/*
                       //cropImageUri = Uri.fromFile(fileCropUris);
                       cropImageUri = Uri.fromFile(fileCropUris);
                       Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                           newUri = FileProvider.getUriForFile(this, "com.zz.fileprovider", new File(newUri.getPath()));
                       }
                       PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                   } else {
                   }*/
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        this.bitmap = bitmap;
                        applicationPermissionsxie();
                    }
                    break;
                default:
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        fileUris = new ImageFileCache().initUploadFile();
                        imageUri = Uri.fromFile(fileUris);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            imageUri = FileProvider.getUriForFile(LiveBroadcastAuthenticationPhotoActivity.this, getPackageName() + ".BuildConfig", fileUris);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        //  ToastUtils.showShort(this, "设备没有SD卡！");
                        MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, style.PopToast).show();

                    }
                } else {

                    // ToastUtils.showShort(this, "请允许打开相机！！");
                    MyToast.makeTextAnim(getApplicationContext(), "请允许打开相机！！", 0, style.PopToast).show();

                }
                break;
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {

                    MyToast.makeTextAnim(getApplicationContext(), "请允许打操作SDCard！！", 0, style.PopToast).show();
                }
                break;

            case CAMERA_PERMISSIONS_REQUEST_VOID:

                if ((grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED))) {
                    if (hasSdcard()) {
                        Intent intent = new Intent(this, RECActivity.class);
                        //    intent.putExtra(Constants.SELLERID, uSellerID);
                        startActivity(intent);

                        finish();

                    } else {
                        //  ToastUtils.showShort(this, "设备没有SD卡！");
                        MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, style.PopToast).show();

                    }
                } else {
                    MyToast.makeTextAnim(getApplicationContext(), "请允许以上权限", 0, style.PopToast).show();


                }
            default:
        }

    }
}

