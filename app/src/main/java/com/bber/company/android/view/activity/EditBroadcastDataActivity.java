package com.bber.company.android.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.livebroadcast.BroadcastDataBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.util.BitmapUtils;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.LocalImageHelper;
import com.bber.company.android.util.country.PhotoUtils;
import com.bber.company.android.view.adapter.AlbumAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.view.customcontrolview.MyGridView;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.TimePickerView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.netty.handler.stream.ChunkedNioFile;

import static com.bber.company.android.view.activity.myProfileSettingActivity.hasSdcard;

/**
 * 编辑主播资料
 */
public class EditBroadcastDataActivity extends BaseAppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AlbumAdapter.OnClickListenerImge {

    private static final int MY_PERMISSIONS_WRITE = 101;
    private final int requestCodeNAME = 10010;
    private final int requestCodeJIAN = 10011;
    private final int requestCodeBIAO = 10012;
    private FrameLayout details_back_id;
    private FrameLayout details_save_id;
    private MyGridView gridview_id;
    private LinearLayout view_delete;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private File fileCropUris = new ImageFileCache().initUploadcropFile();
    private Uri cropImageUri, imageUri;
    private File fileUris;

    private static final int CODE_RESULT_REQUEST = 0xa2;
    private boolean isLongClick = false;
    private File imageFile;
    private ImageFileCache imageFileCache;
    private AlbumAdapter albumAdapter;

    private List<File> photofileList;
    private File file1Itme;
    /**
     * 需要上传文件的图片文件数组
     */
    private File[] files;
    /**
     * 需要显示在本地框的 图片集合
     */
    private List<File> photolists;

    private String dataname, datajian, databiao, dataage;
    private List<LocalImageHelper.LocalFile> localFiles;
    private List<String> broadcaseopoto;
    private int filesize;
    private RelativeLayout edit_Name_layout;
    private RelativeLayout edit_age_layout;
    private RelativeLayout edit_jianjie_layout;
    private RelativeLayout edit_biaoqian_layout;
    private TextView edit_name_text;
    private TextView edit_age;
    private TextView edit_biaoqian;
    private TextView edit_jianjie_name;
    /**
     * 时间选择器
     */
    private TimePickerView pvOptions;
    private Date mDate;
    private BroadcastDataBean broadcastDataBean;

    private String photouri;
    int index = 0;
    private File deletefile;

    private int broadcastage;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            ChenQianLog.i("");
            switch (msg.what) {
                case 1:
                    index++;
                    Bitmap bitmap = (Bitmap) msg.obj; //拿到Bitmap
                    file1Itme = imageFileCache.initUploadFiles(index); //得到要保存的文件名
                    // uploadFiles[i] = file1;
                    saveBitmap(bitmap, file1Itme); //保存文件到本地

                    //   Bitmap fileBitmap = BitmapFactory.decodeFile(file1Itme.getPath()); //从本地文件中获取Bitmap

                    photofileList.add(file1Itme);
                    photolists.add(0, file1Itme); //加载到GridView
                    albumAdapter.notifyDataSetChanged();
                    break;
                case 2:

                    deletefile = (File) msg.obj;
                    if (deletefile.exists()) {
                        deletefile.delete();
                        ChenQianLog.i("" + deletefile.getName());
                        photofileList.remove(deletefile);
                        photolists.remove(deletefile);
                        albumAdapter.notifyDataSetChanged();
                    }
                default:
                    break;
            }
        }
    };


    public  int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;
            } else {
                age--;
            }
        }
        return age;
    }


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_edit_broadcast_data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intData();
        initView();
        initListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MY_PERMISSIONS_WRITE: //读写权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    DownloadPicture(photouri); //下载头像
                } else {

                    MyToast.makeTextAnim(getApplicationContext(), "请打开存储权限", 1, R.style.PopToast).show();

                }
                break;

            case CAMERA_PERMISSIONS_REQUEST_CODE: //相机权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        fileUris = new ImageFileCache().initUploadFile();
                        imageUri = Uri.fromFile(fileUris);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".BuildConfig", fileUris);//通过FileProvider创建一个content类型的Uri
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
        }
    }

    /**
     * 检查文件存储权限
     */
    private void applicationPermissionsxie() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE);
            } else {
                //下载图片
                DownloadPicture(photouri);
            }
        }else {
            DownloadPicture(photouri);
        }

    }


    /**
     * 自动获取sdk权限 请求相册
     */

    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }

    }


    private void intData() {
        broadcastDataBean = (BroadcastDataBean) getIntent().getSerializableExtra("broadcastDataBean");

        photofileList = new ArrayList<>();
        if (broadcastDataBean != null) {
            List<String> photolists = (List<String>) broadcastDataBean.getDataCollection().getImages();
            dataname = broadcastDataBean.getDataCollection().getName();
            datajian = broadcastDataBean.getDataCollection().getDescribe();
            databiao = broadcastDataBean.getDataCollection().getType();
            long age = broadcastDataBean.getDataCollection().getAge();
            long agelog = (System.currentTimeMillis() - age) / 1000 / 60 / 60 / 24 / 365;
            dataage = agelog+"" ;

            //拿到图片地址 并下载到本地
            for (String uri : photolists) {
                this.photouri = uri;
                applicationPermissionsxie();
            }

        }

    }

    private void initListener() {
        details_back_id.setOnClickListener(this);
        details_save_id.setOnClickListener(this);
        edit_Name_layout.setOnClickListener(this);
        edit_age_layout.setOnClickListener(this);

        gridview_id.setOnItemClickListener(this);
        // gridview_id.setOnItemLongClickListener(this);
        edit_jianjie_layout.setOnClickListener(this);
        edit_biaoqian_layout.setOnClickListener(this);
        albumAdapter.setOnClickListenerImge(this);
    }

    private void initView() {
        details_back_id = findViewById(R.id.details_back_id);
        details_save_id = findViewById(R.id.details_save_id);
        gridview_id = findViewById(R.id.gridview_id);
        view_delete = findViewById(R.id.view_delete);

        imageFileCache = new ImageFileCache();
        imageFile = imageFileCache.initUploadFile();

        setGridViewData();
        albumAdapter = new AlbumAdapter(this, photolists);
        gridview_id.setAdapter(albumAdapter);
        edit_Name_layout = findViewById(R.id.edit_Name_layout);
        edit_age_layout = findViewById(R.id.edit_age_layout);
        edit_jianjie_layout = findViewById(R.id.edit_jianjie_layout);
        edit_biaoqian_layout = findViewById(R.id.edit_biaoqian_layout);
        edit_name_text = findViewById(R.id.edit_name_text);
        edit_age = findViewById(R.id.edit_age);
        edit_biaoqian = findViewById(R.id.edit_biaoqian);
        edit_jianjie_name = findViewById(R.id.edit_jianjie_name);

        //选项选择器
        pvOptions = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvOptions.setTitle("出生日期选择");
        // pvOptions.setRange(1980,2000);
        pvOptions.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {

                ChenQianLog.i("onTimeSelect:" + date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dataage = sdf.format(date);
                mDate = new Timestamp(date.getTime());
                broadcastage =  getAge(mDate);
                edit_age.setVisibility(View.VISIBLE);


                if (broadcastage<18){
                    edit_age.setText("");
                    MyToast.makeTextAnim(getApplicationContext(),"年龄不能小于18岁",0,R.style.PopToast).show();
                }else {
                    edit_age.setText(dataage);
                }
            }
        });
        if (broadcastDataBean != null) {

            long age = broadcastDataBean.getDataCollection().getAge();
            long agelog = (System.currentTimeMillis() - age) / 1000 / 60 / 60 / 24 / 365;
            edit_age.setText(agelog + "");
            edit_name_text.setText(broadcastDataBean.getDataCollection().getName());
            edit_biaoqian.setText(broadcastDataBean.getDataCollection().getType());
            edit_jianjie_name.setText(broadcastDataBean.getDataCollection().getDescribe());
            Log.i("", "");
            // broadcaseopoto
        }
    }

    /***/
    private void setGridViewData() {
        if (photolists == null) {
            photolists = new ArrayList<>();
            photolists.add(null);
        } else {
            if (photolists.size() < 8) {//为列表添加一个为空的数据，表示为"添加照片"
                photolists.add(null);
            }
        }
    }

    private void setDeleteView(int mark) {
        Animation animation;
        if (mark == 0) {
            view_delete.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
            view_delete.startAnimation(animation);
        } else {
            view_delete.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
            view_delete.startAnimation(animation);
        }

    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(EditBroadcastDataActivity.this, BroadcastEditDataActivity.class);
        switch (view.getId()) {

            case R.id.details_back_id://返回
                finish();
                break;
            case R.id.details_save_id://保存
                files = new File[photofileList.size()];
                photofileList.toArray(files);
                dataname = edit_name_text.getText().toString();
                dataage = edit_age.getText().toString();
                databiao = edit_biaoqian.getText().toString();
                datajian = edit_jianjie_name.getText().toString();
                if (dataname == null || dataname.length() < 1) {
                    MyToast.makeTextAnim(getApplicationContext(), "昵称不能为空", 1, R.style.PopToast).show();
                    break;
                }
                if (broadcastage < 18) {
                    MyToast.makeTextAnim(getApplicationContext(), "年龄不能小于18岁", 1, R.style.PopToast).show();
                    break;
                }
                if (databiao == null || databiao.length() < 1) {
                    MyToast.makeTextAnim(getApplicationContext(), "标签不能为空", 1, R.style.PopToast).show();
                    break;
                }
                if (datajian == null || datajian.length() < 1) {
                    MyToast.makeTextAnim(getApplicationContext(), "个人简介不能为空", 1, R.style.PopToast).show();
                    break;
                }
                uploadImage(files, dataname, dataage, datajian, databiao);
                break;
            case R.id.edit_Name_layout://编辑昵称

                intent.putExtra("name", "edit_Name");
                intent.putExtra("daname", "昵称");
                intent.putExtra("data", edit_name_text.getText().toString());
                startActivityForResult(intent, requestCodeNAME);
                break;
            case R.id.edit_age_layout://出生日期

                pvOptions.show();

                break;
            case R.id.edit_jianjie_layout://个人简介

                intent.putExtra("name", "edit_jianjie");
                intent.putExtra("daname", "个人简介");

                intent.putExtra("data", edit_jianjie_name.getText().toString());
                startActivityForResult(intent, requestCodeJIAN);
                break;
            case R.id.edit_biaoqian_layout://标签

                // Toast.makeText(this, "正在保存", Toast.LENGTH_SHORT).show();
                intent.putExtra("name", "edit_biaoqian");
                intent.putExtra("daname", "标签");
                intent.putExtra("data", edit_biaoqian.getText().toString());
                startActivityForResult(intent, requestCodeBIAO);
                break;
        }
    }

    /**
     * GridView Item 点击监听
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

        AlbumAdapter.ViewHolder holder = (AlbumAdapter.ViewHolder) view.getTag();
        //   Bitmap item = (Bitmap) photolists.get(position);
        if (position == photolists.size() - 1 && photolists.get(position) == null) {//点击上传图片按钮

            //上传主播图片集

            if (photofileList.size() < 7) {
                setBroadcastPhoto();
            } else {
                MyToast.makeTextAnim(getApplicationContext(), "最多7张图片", 1, R.style.PopToast).show();
            }
        }
    }

    /**
     * 上传主播相册集，最多7张
     */
    private void setBroadcastPhoto() {
        final View layout = LayoutInflater.from(EditBroadcastDataActivity.this).inflate(R.layout.custom_dialog_upload_bottom, null);
        final AlertDialog dialog = DialogTool.createUploadDialog(EditBroadcastDataActivity.this, layout);
        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//相册
                dialog.dismiss();
              /*  Intent intent = new Intent(EditBroadcastDataActivity.this, PhotoAlbumActivity.class);
                intent.putExtra("nowSize", photolists.size() - 1);
                startActivityForResult(intent, Constants.UPLOAD_IMAGE);*/

                autoObtainStoragePermission();
            }
        });
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //相机
                dialog.dismiss();
                //  UploadImage.upload(EditBroadcastDataActivity.this, 1);
                autoObtainCameraPermission();
            }
        });
    }

    /**
     * GridView Item 长按 监听
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

        // Bitmap item = (Bitmap) photolists.get(position);
        AlbumAdapter.ViewHolder holder = (AlbumAdapter.ViewHolder) view.getTag();
        if (!isLongClick && holder.view_add.getVisibility() == View.GONE) {
            isLongClick = true;
            invalidateOptionsMenu();
            setDeleteView(1);
            holder.selected_icon.setVisibility(View.VISIBLE);
            Log.e("eeeeeeeeeeeeeee", "onItemLongClick  add position:" + position);
            // selectFileLs.add(item);
            return true;
        }
        return false;
    }


    /**
     * 自动获取相机权限  头像时使用
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
                    imageUri = FileProvider.getUriForFile(this, getPackageName() + ".BuildConfig", fileUris);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                //ToastUtils.showShort(this, "设备没有SD卡！");
                MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, R.style.PopToast).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("eeeeeeeeeeeeeeee", "onActivityResult:");
        if (resultCode == RESULT_OK) {
            //从编辑资料处获取到的信息

            localFiles = LocalImageHelper.getInstance((MyApplication) getApplicationContext()).getCheckedItems();
            files = new File[]{imageFile};
            switch (requestCode) {
                case Constants.REQUEST_CODE_PHOTO_GRAPH://拍照
                    comp(files);
                    break;
                case Constants.UPLOAD_IMAGE:

                    //获取选中的图片
                    if (localFiles.size() == 0) {
                        return;
                    }
                    //添加，去重
                    boolean hasUpdate = false;
                    File[] mfiles = new File[localFiles.size()]; //要上传的file图片文件
                    for (int i = 0; i < localFiles.size(); i++) {
                        String path = localFiles.get(i).getPath();
                        //最多9张
                        if (photolists.size() == 7 && photolists.get(photolists.size() - 1) != null) {
                            // ToastUtils.showToast(R.string.image_count, 0);
                            break;
                        }
                        mfiles[i] = new File(path);
                        hasUpdate = true;
                    }
                    LocalImageHelper.getInstance((MyApplication) getApplicationContext()).clear();
                    if (hasUpdate) {
                        comp(mfiles);
                    }
                    break;
                case CODE_CAMERA_REQUEST: //相机请求回调

                    cropImageUri = Uri.fromFile(fileCropUris);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, 350, 350, CODE_RESULT_REQUEST);
                    break;

                case CODE_GALLERY_REQUEST://相册请求码

                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUris);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(this, getPackageName() + ".BuildConfig", new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, 480, 480, CODE_RESULT_REQUEST);
                    } else {
                        MyToast.makeTextAnim(getApplicationContext(), "设备没有SD卡！", 0, R.style.PopToast).show();
                    }
                    break;

                case CODE_RESULT_REQUEST: //裁剪后的回调
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        Message msg = new Message();
                        msg.obj = bitmap;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    break;
                case requestCodeNAME:
                    dataname = data.getStringExtra("dataname");

                    edit_name_text.setText(dataname);
                    //   edit_age
                    break;
                case requestCodeJIAN:
                    datajian = data.getStringExtra("dataname");
                    edit_jianjie_name.setText(datajian);
                    break;
                case requestCodeBIAO:
                    databiao = data.getStringExtra("dataname");

                    edit_biaoqian.setText(databiao);
                    break;
            }
        }
    }

    /**
     * 压缩要上传的图片
     * *
     */
    private void comp(final File[] files) {

        // filesize+=files.length;
        //   final File[] uploadFiles = new File[files.length];
        final File[] uploadFiles = new File[files.length];
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = null;
                photolists.clear();
                for (int i = 0; i < files.length; i++) {
                    path = files[i].getPath();

                    Bitmap bitmap = BitmapUtils.getBitmap(path);
                    //对Bitmap对象进行压缩处理
                    bitmap = BitmapUtils.imageZoom(bitmap, 400.00);

                    int digree = Tools.getImageDigree(path);
                    if (digree != 0) {
                        // 旋转图片
                        Matrix m = new Matrix();
                        m.postRotate(digree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), m, true);
                    }
                    File file1 = imageFileCache.initUploadFiles(i);
                    uploadFiles[i] = file1;
                    saveBitmap(bitmap, file1);
                    //  photolists.add(bitmap);
                }
                handler.obtainMessage(0, uploadFiles).sendToTarget();
            }
        }

        ).start();

    }

    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(Bitmap bitmap, File file1) {
        try {
            if (file1 == null) {
                return;
            }
            if (file1.exists()) {
                file1.delete();
            }
            file1.createNewFile();
            OutputStream outStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();


        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
            Log.w("ImageFileCache", "IOException");

        }

    }

    /**
     * 上传 主播资料
     * *
     */
    private void uploadImage(File[] files, String dataname, String dataage, String datajian, String databiao) {
       /* if (!netWork.isNetworkConnected()) {
            ToastUtils.showToast(R.string.no_network, 0);
            return;
        }*/
        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("flag", 1);//1：修改信息 2：修改在线状态
        params.put("status", 3);//状态 -1冻结 1,勿扰  2,忙碌  3,空闲
        params.put("describe", datajian);//主播自我介绍
        params.put("type", databiao);//主播类型
        params.put("name", dataname);//主播昵称
        params.put("anchorAge", dataage);//主播年龄
        try {
            params.put("files", files);
        } catch (FileNotFoundException e) {
        }
        HttpUtil.post(new Constants().uploadAnchor, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("修改主播资料 json:" + jsonObject.toString());
                try {
                    if (jsonObject.getString("resultCode").equals("1")) {
                        MyToast.makeTextAnim(getApplicationContext(), jsonObject.getString("resultMessage"), 0, R.style.PopToast).show();
                        Intent intent = new Intent(getApplicationContext(), LiveBroadcastDetailsActivity.class);
                        intent.putExtra("fragmentname", "Main");
                        startActivity(intent);
                        finish();
                    } else {
                        MyToast.makeTextAnim(getApplicationContext(), jsonObject.getString("resultMessage"), 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "uploadPhotos onFailure--throwable:" + throwable);
                ToastUtils.showToast(R.string.getData_fail, 0);
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                // progressDialog.dismiss();
                DialogView.dismiss(EditBroadcastDataActivity.this);

            }
        });
    }


    /**
     * 下载图片
     */
    private void DownloadPicture(String privtureuri) {
        String imageurltou = (String) SharedPreferencesUtils.get(getApplicationContext(), "IMAGE_FILE", "");
        final String uitt = imageurltou + privtureuri;

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                try {
                    URL url = new URL(uitt);
                    //开启连接
                    conn = (HttpURLConnection) url.openConnection();
                    //设置连接超时
                    conn.setConnectTimeout(5000);
                    //设置请求方式
                    conn.setRequestMethod("GET");
                    //conn.connect();
                    if (conn.getResponseCode() == 200) {
                        is = conn.getInputStream();
                        Bitmap b = BitmapFactory.decodeStream(is);
                        //把输入流转化成bitmap格式，以msg形式发送至主线程
                        Message msg = new Message();

                        msg.obj = b;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        //用完记得关闭
                        is.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 头像上 删除小按钮的监听
     */
    @Override
    public void onClick(View view, int position, File file) {

        //  photolists.remove(position);
        //  albumAdapter.notifyDataSetChanged();
        Message message = new Message();
        message.what = 2;
        message.arg1 = position;
        message.obj = file;
        handler.sendMessage(message);
    }
}
