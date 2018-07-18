package com.bber.company.android.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.UploadImage;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.widget.MyToast;
import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusinessRatingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageButton ib_upload_pic;
    private EditText edit_detail;
    private Button btn_over;
    private int mShopId;
    private ImageFileCache imageFileCache;
    private List<File> mUploadFile;
    private TextView tv_show_e, tv_show_s, tv_show_i;
    private RatingBar rb_rating_e, rb_rating_s, rb_rating_i;
    private List<String> images;
    private SampleAdapter sampleAdapter;
    private RecyclerView recyclerView;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    sampleAdapter.update(images);
                    break;
            }
        }
    };


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_business_rating;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initData() {
        imageFileCache = new ImageFileCache();
        images = new ArrayList<>();
        mUploadFile = new ArrayList<>();
        mShopId = getIntent().getIntExtra("shopid", 0);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        sampleAdapter = new SampleAdapter();
        recyclerView.setAdapter(sampleAdapter);
    }


    private void initViews() {
        title.setText(R.string.rating_business);
        ib_upload_pic = findViewById(R.id.ib_upload_pic);
        edit_detail = findViewById(R.id.edit_detail);
        btn_over = findViewById(R.id.btn_over);
        rb_rating_e = findViewById(R.id.rb_rating_e);
        rb_rating_s = findViewById(R.id.rb_rating_s);
        rb_rating_i = findViewById(R.id.rb_rating_i);
        tv_show_e = findViewById(R.id.tv_show_e);
        tv_show_s = findViewById(R.id.tv_show_s);
        tv_show_i = findViewById(R.id.tv_show_i);
        recyclerView = findViewById(R.id.recyclerView);

    }

    private void setListeners() {
        btn_over.setOnClickListener(this);
        ib_upload_pic.setOnClickListener(this);
        rb_rating_e.setOnClickListener(this);
        rb_rating_s.setOnClickListener(this);
        rb_rating_i.setOnClickListener(this);
        rb_rating_e.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setTextView(tv_show_e, rating);
            }
        });
        rb_rating_s.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setTextView(tv_show_s, rating);
            }
        });
        rb_rating_i.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setTextView(tv_show_i, rating);
            }
        });
    }

    /**
     * 获取商户评论列表
     */

    private void insertShopComment(int score1, int score2, int score3, String note) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        File[] files = mUploadFile.toArray(new File[mUploadFile.size()]);
        params.put("head", head);
        params.put("shopId", mShopId);
        params.put("buyerId", buyerId);
        params.put("commentScore1", score1);
        params.put("commentScore2", score2);
        params.put("commentScore3", score3);
        params.put("commentTotalScore", (score1 + score2 + score3) / 3);
        params.put("commentMessage", note);
        try {
            params.put("commentPhoto", files);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HttpUtil.post(Constants.getInstance().insertShopComment, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }

                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        finish();
                    } else {
                        MyToast.makeTextAnim(BusinessRatingActivity.this, resultMessage, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BusinessRatingActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 选择本地手机图片
     */

    private void ChoosePhonePic() {
        if (images.size() >= 3) {
            MyToast.makeTextAnim(this, R.string.upload_limit3, 0, R.style.PopToast).show();
            return;
        }
        final View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_upload_bottom, null);
        final AlertDialog dialog = DialogTool.createUploadDialog(this, view);
        view.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UploadImage.upload(BusinessRatingActivity.this, 0);
            }
        });
        view.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                UploadImage.upload(BusinessRatingActivity.this, 1);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_over:
                int score1 = (int) rb_rating_e.getRating();
                int score2 = (int) rb_rating_s.getRating();
                int score3 = (int) rb_rating_i.getRating();
                String note = edit_detail.getText() + "";
                insertShopComment(score1, score2, score3, note);
                break;
            case R.id.ib_upload_pic:
                ChoosePhonePic();
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_PHOTO_ALBUM:
                    if (data != null) {
                        String path = null;
                        Uri selectedImage = data.getData();
                        selectedImage = Tools.getImageuri(this, selectedImage);
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);
                        cursor.close();
                        if (path == null) {
                            return;
                        }
                        comp(path);
                    }
                    break;
                case Constants.REQUEST_CODE_PHOTO_GRAPH:
                    break;
            }
        }
    }

    /**
     * 设置相关
     */
    private void setTextView(TextView textView, float Score) {
        if (Score < 2) {
            textView.setText("较差");
        } else if (Score < 4) {
            textView.setText("一般");
        } else {
            textView.setText("较好");
        }

    }

    /**
     * 压缩要上传的图片
     */
    private void comp(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                float hh = 800f;//这里设置高度为800f
                float ww = 480f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outWidth / ww);

                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;//设置缩放比例
                Log.e("eeeeeeeeeeee", "图片压缩倍数be:" + be);
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(path, newOpts);
                int digree = Tools.getImageDigree(path);
                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), m, true);
                }
                if (bitmap != null) {
                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();
                }

                File file = imageFileCache.initUploadFile(new Date().getTime() + "");
                mUploadFile.add(file);
                saveBitmap(bitmap, file);
                handler.sendEmptyMessage(0);
                images.add(file.getPath());

            }
        }).start();
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
        }

    }

    private class SampleAdapter extends RecyclerView.Adapter<SampleHolder> {


        private List<String> ItemList = new ArrayList<>();

        public void update(List<String> ItemList) {
            this.ItemList = ItemList;
            notifyDataSetChanged();
        }

        @Override
        public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ImageView imageView = new ImageView(BusinessRatingActivity.this);
            int w = (parent.getRight() - parent.getLeft()) / 3;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            imageView.setPadding(2, 2, 2, 2);
            return new SampleHolder(imageView);
        }

        @Override
        public int getItemCount() {
            return ItemList.size();
        }

        @Override
        public void onBindViewHolder(SampleHolder holder, final int position) {

            Glide.with(getApplication())
                    .load(ItemList.get(position))
                    .placeholder(R.mipmap.default_icon)
                    .centerCrop()
                    .into((ImageView) holder.itemView);
        }

    }

    class SampleHolder extends RecyclerView.ViewHolder {

        public SampleHolder(View itemView) {
            super(itemView);
        }
    }
}