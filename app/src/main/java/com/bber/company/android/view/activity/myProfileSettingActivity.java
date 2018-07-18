package com.bber.company.android.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.UploadImage;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.util.country.PhotoUtils;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class myProfileSettingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;
    private Uri imageUri, croppedImage, uri;
    private Uri cropImageUri;
    /**
     * 拍照后的保存文件
     */
    private File fileUris;
    private File fileCropUris = new ImageFileCache().initUploadcropFile();
    private TextView textview_verify, textView_phone, mynick_name, tv_vip_infor, vip_infor_qq, textview_sex, textview_birthday;
    private RelativeLayout btn_verify, rl_profile_pic, btn_vip, rl_qqNum;
    private SimpleDraweeView user_icon;
    private File imageFile;
    private ImageFileCache imageFileCache;
    private String oldHeadIcon;
    private String mynickName;
    private String vipName;
    private String nickheadPic;
    private int MY_PERMISSIONS_CAMERA = 1;
    private int MY_PERMISSIONS_WRITE = 2;
    /**
     * 头像的 bitmap
     */
    private Bitmap bitmap;

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_myproflie_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getContext();
        app.newActivity.add(this);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.mypriflesetting);
        user_icon = findViewById(R.id.user_icon);
        btn_verify = findViewById(R.id.btn_verify);
        rl_profile_pic = findViewById(R.id.rl_profile_pic);
        btn_vip = findViewById(R.id.btn_vip);
        textview_verify = findViewById(R.id.textview_verify);
        textView_phone = findViewById(R.id.text_phone);
        mynick_name = findViewById(R.id.mynick_name);
        tv_vip_infor = findViewById(R.id.vip_infor);
        vip_infor_qq = findViewById(R.id.vip_infor_qq);
        rl_qqNum = findViewById(R.id.rl_qqNum);
        textview_sex = findViewById(R.id.textview_sex);
        textview_birthday = findViewById(R.id.textview_birthday);
    }

    private void setListeners() {
        btn_verify.setOnClickListener(this);
        rl_profile_pic.setOnClickListener(this);
        btn_vip.setOnClickListener(this);
    }

    private void initData() {
        mynickName = (String) SharedPreferencesUtils.get(this, Constants.USERNAME, "");
        nickheadPic = (String) SharedPreferencesUtils.get(this, Constants.USERICON, "");
        vipName = (String) SharedPreferencesUtils.get(this, Constants.VIP_NAME, "");
        int vipId = (int) SharedPreferencesUtils.get(myProfileSettingActivity.this, Constants.VIP_ID, 0);
        if (vipId > 0) {
            tv_vip_infor.setText(vipName);
            getQQinfo();
        }
        user_icon.setImageURI(nickheadPic);
        mynick_name.setText(mynickName);
        imageFileCache = new ImageFileCache();
        imageFile = imageFileCache.initUploadFile();

        int ubsex = (int) SharedPreferencesUtils.get(this, Constants.UBSEX, 1);
        if (ubsex == 1) {
            textview_sex.setText("男");
        } else {
            textview_sex.setText("女");
        }

        String data = (String) SharedPreferencesUtils.get(this, Constants.BIRTHDAY, "");
        String[] birthday = data.split(" ");
        textview_birthday.setText(birthday[0]);
    }

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
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                MyToast.makeTextAnim(getApplicationContext(), "您已经拒绝过一次", 0, R.style.PopToast).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                fileUris = new ImageFileCache().initUploadFile();
                imageUri = Uri.fromFile(fileUris);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(myProfileSettingActivity.this, getPackageName() + ".BuildConfig", fileUris);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                //ToastUtils.showShort(this, "设备没有SD卡！");

                MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, R.style.PopToast).show();

            }
        }
    }

    private void applicationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
            } else {

                //imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //imageUri = FileProvider.getUriForFile(myProfileSettingActivity.this, "com.bber.company.android", fileUri);
                }

                UploadImage.upload(myProfileSettingActivity.this, 1);
            }
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
                            imageUri = FileProvider.getUriForFile(myProfileSettingActivity.this, getPackageName() + ".BuildConfig", fileUris);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        //  ToastUtils.showShort(this, "设备没有SD卡！");
                        MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, R.style.PopToast).show();

                    }
                } else {

                    // ToastUtils.showShort(this, "请允许打开相机！！");
                    MyToast.makeTextAnim(getApplicationContext(), "请允许打开相机！！", 0, R.style.PopToast).show();

                }
                break;
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {

                    MyToast.makeTextAnim(getApplicationContext(), "请允许打操作SDCard！！", 0, R.style.PopToast).show();
                }
                break;
            default:
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_profile_pic:
                final View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_upload_bottom, null);
                final AlertDialog dialog = DialogTool.createUploadDialog(this, view);
                view.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        try {  //相册
                            // UploadImage.upload(myProfileSettingActivity.this, 0);
                            autoObtainStoragePermission();  //

                        } catch (Exception e) {

                            MyToast.makeTextAnim(getApplicationContext(), "调用相机失败，请到设置中打开相机权限", 1, R.style.PopToast).show();
                        }
                    }
                });
                view.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();//相机
                        //
                        // applicationPermissions();

                        autoObtainCameraPermission();

                    }
                });
                break;
            case R.id.btn_verify:
                Intent intent = new Intent(myProfileSettingActivity.this, MobileVerifyActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_vip:
                Intent vipintent = new Intent(myProfileSettingActivity.this, Buy_vipActivity.class);
//                vipintent.putExtra("nickheadPic",nickheadPic);
                startActivity(vipintent);
                break;

        }
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
                        MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, R.style.PopToast).show();
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
    protected void onStart() {
        super.onStart();
        boolean checkPhone = (boolean) SharedPreferencesUtils.get(myProfileSettingActivity.this, Constants.CHECK_PHONE, false);
        String phone = (String) SharedPreferencesUtils.get(myProfileSettingActivity.this, Constants.PHONE, "");
        if (checkPhone == true || phone.length() > 5) {
         /*   GradientDrawable drawable = (GradientDrawable) textview_verify.getBackground();
         //   drawable.setColor(getResources().getResourceName(R.mipmap.ccertified));*/

            //  textview_verify.setText(R.string.mobile_verified);
            textview_verify.setBackgroundResource(R.mipmap.ccertified);
            btn_verify.setEnabled(false);
            textView_phone.setText(StuffPhone(phone));


            textView_phone.setVisibility(View.VISIBLE);
        }
        Intent intent = new Intent(Constants.ACTION_SETTING);
        intent.putExtra("type", "verify");
        sendBroadcast(intent);
    }

    /**
     * brucs
     * 手机号码隐藏中间4位
     *
     * @param phone
     * @return
     */
    public String StuffPhone(String phone) {
        String result = "";
        if (phone.length() != 0) {
            result = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
        } else {
            result = "";
        }
        return result;
    }

    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(Bitmap bitmap) {

        if (imageFile.exists()) {
            imageFile.mkdir();
        }
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            uploadImage(imageFile);
        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }

    }

    /**
     * 上传图片
     * *
     */
    private void uploadImage(final File bitmap) {

        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        //  final ProgressDialog progressDialog = new ProgressDialog(this);
        DialogView.show(this, true);
        // progressDialog.setMessage("正在上传...");
        // progressDialog.show();
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        Log.e("eeeeeeeeeeee", "oldHeadIcon:" + oldHeadIcon);
        params.put("headPortraitPath", oldHeadIcon == null ? "" : oldHeadIcon);
        params.put("ubuyer", SharedPreferencesUtils.get(this, Constants.USERID, ""));
        try {
            params.put("file", bitmap);
        } catch (FileNotFoundException e) {
            ToastUtils.showToast(R.string.no_imgefile_fail, 0);
            //  progressDialog.dismiss();
            DialogView.dismiss(this);
            return;
        }

        String uploadimag = Constants.getInstance().uploadImg;
        Log.i("", "");
        HttpUtil.post(uploadimag, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "uploadImg onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(myProfileSettingActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    oldHeadIcon = jsonObject.getString("dataCollection");
                    SharedPreferencesUtils.put(myProfileSettingActivity.this, Constants.HEADURL, oldHeadIcon);
                    Uri uri = Uri.parse(oldHeadIcon);
                    user_icon.setImageURI(uri);
                    Intent intent = new Intent(Constants.ACTION_SETTING);
                    intent.putExtra("type", "uicon");
                    intent.putExtra("url", oldHeadIcon);
                    SharedPreferencesUtils.put(myProfileSettingActivity.this, Constants.USERICON, oldHeadIcon);
                    sendBroadcast(intent);

                } catch (JSONException e) {

                    e.getMessage();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "uploadImg onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(myProfileSettingActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogView.dismiss(myProfileSettingActivity.this);
            }
        });
    }

    /**
     * 获取QQ号码
     */
    private void getQQinfo() {
        RequestParams params = new RequestParams();
        params.put("type", 6);
        HttpUtil.get(Constants.getInstance().getQQNumber, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");

//                    JSONObject jsonObject1 = new JSONObject(resultMessage);
//                    String service = jsonObject1.getString("service");
                    if (resultCode == 1) {
                        vip_infor_qq.setText(resultMessage);
                        rl_qqNum.setVisibility(View.VISIBLE);
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
}
