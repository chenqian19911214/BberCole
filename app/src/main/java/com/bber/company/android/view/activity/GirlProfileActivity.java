package com.bber.company.android.view.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.bean.Tag;
import com.bber.company.android.bean.adsBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.DBcolumns;
import com.bber.company.android.db.SellerUserDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.util.commonActivity;
import com.bber.company.android.view.adapter.HorListAdapter;
import com.bber.company.android.view.adapter.TagAdapter;
import com.bber.company.android.widget.FullScreenVideoView;
import com.bber.company.android.widget.MyGridView;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.NotifyingScrollView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GirlProfileActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private final int TYPE_CHAT = 0;
    private final int TYPE_MOBILE = 1;
    private final int TYPE_VIP = 2;
    private final int TYPE_PRE_VIP = 3;
    private final int TYPE_GET_VIP = 4;
    private final int TYPE_CLOSE_TIP = 5;
    private ProgressBar progressBar, mp4_progressBar;
    private NotifyingScrollView scrollView;
    private TextView cup, height, detail, btn_close, tag_tittle, title_, change_screen, btn_change, btn_sure_this, rating_text, age_text;
    private ImageView btn_start, videoImage, girl_guide1, girl_guide2, girl_guide3;
    private FullScreenVideoView videoView;
    //video准备工作监听
    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Log.e("eeeeeeeeeeeeeeee", "onPrepared");
            videoView.setVideoWidth(mediaPlayer.getVideoWidth());
            videoView.setVideoHeight(mediaPlayer.getVideoHeight());
            mp4_progressBar.setVisibility(View.GONE);
        }
    };
    private int pausePos;
    private RatingBar rating;
    private LinearLayout view_bottom;
    private RelativeLayout video_rela, girl_guidebg;
    //video播放完毕
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.e("eeeeeeeeeeeeeeee", "onCompletion :");
            video_rela.setVisibility(View.GONE);
            if (!isScreenOriatationPortrait(GirlProfileActivity.this)) {// 当屏幕是横屏时
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
            }
        }
    };
    private RecyclerView recyclerView;
    private List tags;
    private SellerUserVo sellerUserVo;
    private MyGridView tag_gridview;
    private TagAdapter tagAdapter;
    private SimpleDraweeView buttomAd;
    private SellerUserDao selleruserdao;
    private int from;
    private boolean favariteStatus;
    private int sellerId;
    private List<adsBean> adsBeenList;
    private int indexAds;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    videoImage.setImageBitmap(bitmap);
                    break;
                case 1:
                    setNewAdsItem(msg.arg1);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };
    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = 200 - toolbar.getHeight() + 100;
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
//            toolbar.setBackgroundColor(getResources().getColor(R.color.item_bg));
//            toolbar.getBackground().setAlpha(newAlpha);
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_girl_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
//        title.setText("曹妮美");
        title_ = findViewById(R.id.title_);

        scrollView = findViewById(R.id.scrollView);
        recyclerView = findViewById(R.id.recyclerView);
        buttomAd = findViewById(R.id.ad_buttom);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        tag_gridview = findViewById(R.id.tag_gridview);
        progressBar = findViewById(R.id.progressBar);
        mp4_progressBar = findViewById(R.id.mp4_progressBar);
        btn_start = findViewById(R.id.btn_start);
        view_bottom = findViewById(R.id.view_bottom);
        video_rela = findViewById(R.id.video_rela);
        videoView = findViewById(R.id.video_view);
        btn_close = findViewById(R.id.btn_close);
        tag_tittle = findViewById(R.id.tag_tittle);
        change_screen = findViewById(R.id.change_screen);
        videoImage = findViewById(R.id.videoImage);
        btn_change = findViewById(R.id.btn_change);
        btn_sure_this = findViewById(R.id.btn_sure_this);
        rating = findViewById(R.id.rating);
        rating_text = findViewById(R.id.rating_text);
        cup = findViewById(R.id.cup);
        age_text = findViewById(R.id.age);
        height = findViewById(R.id.height);
        detail = findViewById(R.id.detail);
        int height = (MyApplication.screenWidth) / 4;
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        girl_guidebg = findViewById(R.id.girl_guidebg);
        girl_guide1 = findViewById(R.id.girl_guide1);
        girl_guide2 = findViewById(R.id.girl_guide2);
        girl_guide3 = findViewById(R.id.girl_guide3);

        boolean sixth = (boolean) SharedPreferencesUtils.get(this, "sixth", true);
        if (sixth) {
            girl_guidebg.setVisibility(View.VISIBLE);
            girl_guide1.setVisibility(View.VISIBLE);
//            guide_call2.setVisibility(View.VISIBLE);
//            guide_call3.setVisibility(View.VISIBLE);
            SharedPreferencesUtils.put(this, "sixth", false);
        }


    }

    private void setListeners() {
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
        btn_start.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        change_screen.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        btn_sure_this.setOnClickListener(this);
        buttomAd.setOnClickListener(this);
        girl_guide1.setOnClickListener(this);
        girl_guide2.setOnClickListener(this);
        girl_guide3.setOnClickListener(this);
        videoView.setOnCompletionListener(completionListener);
        videoView.setOnPreparedListener(preparedListener);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // 设置当前播放的位置
                return true;//如果设置true就可以防止他弹出错误的提示框！
            }
        });
    }

    private void initData() {
        getAdStatus();
        from = getIntent().getIntExtra("from", -1);
        int sellerID = 0;
        selleruserdao = new SellerUserDao(this);
        btn_sure_this.setEnabled(false);
        if (from == Constants.COME_FROM_CHAT_ROOM) {//从chat页进来
            view_bottom.setVisibility(View.GONE);
            sellerID = getIntent().getIntExtra("sellerId", -1);
            if (sellerID == -1) {
                MyToast.makeTextAnim(this, R.string.no_user, 0, R.style.PopToast).show();
                return;
            }
            getSellerInfo(sellerID);
        } else if (from == Constants.COME_FROM_VIRTUAL_GOODS) {
            btn_sure_this.setEnabled(false);
            btn_sure_this.setEnabled(false);
            sellerID = getIntent().getIntExtra("sellerId", -1);
            if (sellerID == -1) {
                MyToast.makeTextAnim(this, R.string.no_user, 0, R.style.PopToast).show();
                return;
            }
            getSellerInfo(sellerID);
        } else if (from == Constants.COME_FROM_HISTORY_LIST) {
            sellerID = getIntent().getIntExtra("sellerId", -1);
            if (sellerID == -1) {
                MyToast.makeTextAnim(this, R.string.no_user, 0, R.style.PopToast).show();
                return;
            }
            getSellerInfo(sellerID);
        } else {
            sellerUserVo = (SellerUserVo) getIntent().getSerializableExtra("sellerUserVo");
            tags = (List<Tag>) getIntent().getSerializableExtra("tags");
            setInfo();
            Tools.Vibrate(this, 600);
        }

    }

    private void setNewAdsItem(int arg1) {
        if (adsBeenList == null || adsBeenList.size() == 0) {
            return;
        }
        if (arg1 >= adsBeenList.size()) {
            arg1 = 0;
        }
        String uriTarget = adsBeenList.get(arg1).getAdPicture();
        indexAds = arg1;
        if (!Tools.isEmpty(uriTarget)) {
            Uri uri = Uri.parse(uriTarget);
            buttomAd.setImageURI(uri);
            buttomAd.setVisibility(View.VISIBLE);
        } else {
            buttomAd.setVisibility(View.GONE);
        }
        Message msg = new Message();
        msg.what = 1;
        msg.arg1 = arg1 + 1;
        handler.sendMessageDelayed(msg, 3000);
    }

    /**
     * 获取广告的状态
     */
    private void getAdStatus() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (adsBeenList != null && adsBeenList.size() > 0) {
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = 0;
            handler.sendMessageDelayed(msg, 0);
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("cityLongitude", MapSupport.longitude);
        params.put("cityLatitude", MapSupport.latitude);
        params.put("adPlace", "DETAIL");
        HttpUtil.get(Constants.getInstance().adsList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode != 0) {
                        String dataCollection = jsonObject.getString("dataCollection");
                        if (dataCollection != null) {
                            adsBeenList = jsonUtil.jsonToadsBean(dataCollection);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.arg1 = 0;
                            handler.sendMessageDelayed(msg, 0);
                        }
                    }
                } catch (JSONException e) {
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

    private void setInfo() {
        btn_sure_this.setEnabled(true);
        //设置视频
        if (!Tools.isEmpty(sellerUserVo.getUsVideo())) {
            Uri uri = Uri.parse(sellerUserVo.getUsVideo());
            //进度条
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(uri);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = createVideoThumbnail(sellerUserVo.getUsVideo());
                    handler.obtainMessage(0, bitmap).sendToTarget();
                }
            }).start();
        }
        float grade = sellerUserVo.getUsGrade() / 100;
        rating.setRating(grade);
        rating_text.setText(grade + "");
        cup.setText("胸围：" + sellerUserVo.getUsBrassiere());
        if (!Tools.isEmpty(sellerUserVo.getUsAge())) {
            age_text.setVisibility(View.VISIBLE);
            age_text.setText("年龄：" + sellerUserVo.getUsAge());
        }

        height.setText("身高：" + sellerUserVo.getUsHeight() + "cm");
        sellerId = sellerUserVo.getuSeller();
        invalidateOptionsMenu();
        if (Tools.isEmpty(sellerUserVo.getUsDescribe())) {
            detail.setText(R.string.girl_describle_defualt);
        } else {
            detail.setText(sellerUserVo.getUsDescribe());
        }
        //加载相册
        String[] images = sellerUserVo.getImages();
        if (images != null && images.length > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            HorListAdapter horListAdapter = new HorListAdapter(this, images);
            recyclerView.setAdapter(horListAdapter);
            horListAdapter.setOnItemClickListener(new HorListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(GirlProfileActivity.this, BrowseImageActivity.class);
                    intent.putExtra("images", sellerUserVo.getImages());
                    intent.putExtra("currentIndex", position);
                    startActivity(intent);
                }
            });
        }
        title.setText("1111111");
        title_.setText(sellerUserVo.getUsName() + "(id:" + sellerUserVo.getuSeller() + ")");
        //加载标签
        if (tags != null && tags.size() > 0) {
            tagAdapter = new TagAdapter(GirlProfileActivity.this, tags, 0);
            tag_gridview.setAdapter(tagAdapter);
        } else {
            tag_tittle.setVisibility(View.GONE);
        }
    }

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

    private void getSellerInfo(int sellerID) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("uSeller", sellerID);
        HttpUtil.get(Constants.getInstance().getSellerInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(GirlProfileActivity.this, jsonObject, progressBar)) {
                    return;
                }
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("dataCollection");
                    sellerUserVo = jsonUtil.jsonToSellerUserVo(jsonObject1.toString());
                    String lables = jsonObject1.getString("lables");
                    tags = jsonUtil.jsonToTags(lables);
                    if (sellerUserVo != null) {
                        setInfo();
                    }

                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getSellerInfo JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getSellerInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(GirlProfileActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                progressBar.setVisibility(View.GONE);
            }


        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (sellerUserVo == null || Tools.isEmpty(sellerUserVo.getUsVideo() + "")) {
                    MyToast.makeTextAnim(this, R.string.video_null, 0, R.style.PopToast).show();
                    return;
                }
                video_rela.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                videoView.start();
                videoView.requestFocus();
//                Uri uri = Uri.parse(sellerUserVo.getUsVideo().trim());
////                //调用系统自带的播放器
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Log.v("URI:::::::::", uri.toString());
//                intent.setDataAndType(uri, "video/mp4");
//                startActivity(intent);
                break;
            case R.id.btn_close:
                video_rela.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                videoView.pause();
                if (!isScreenOriatationPortrait(this)) {// 当屏幕是横屏时
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
                }
                break;
            case R.id.change_screen:
                if (isScreenOriatationPortrait(this)) {// 当屏幕是竖屏时
                    // 点击后变横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置当前activity为横屏
                    // 当横屏时 把除了视频以外的都隐藏
                    //隐藏其他组件的代码
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
                    //显示其他组件
                }
                break;
            case R.id.btn_change:
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case R.id.btn_sure_this:
              /*  boolean checkVIP = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.VIPIMSWITCH, false);
                boolean checkPhon = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.PHONEIMSWITCH, false);
                if (checkVIP) {
                    int level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
                    if (level > 1) {
                        startChatActivity();
                    } else {
                        setTipDialog(R.string.mobile_charge, R.string.mobile_verify_nexttime,
                                R.string.mobile_charge_now, false, true, 0, TYPE_VIP);
                    }
                } else {

                    isVerifyMobile();
                }*/

                inputChatActivity();
                break;
            case R.id.ad_buttom:
                commonActivity.startBrowebuyURL(this, adsBeenList.get(indexAds));
                break;
            case R.id.girl_guide1:
                girl_guide1.setVisibility(View.GONE);
                girl_guide2.setVisibility(View.VISIBLE);
                break;
            case R.id.girl_guide2:
                girl_guide2.setVisibility(View.INVISIBLE);
                girl_guide3.setVisibility(View.VISIBLE);
                break;
            case R.id.girl_guide3:
                girl_guide3.setVisibility(View.GONE);
                girl_guidebg.setVisibility(View.GONE);
                break;
        }
    }

    private void inputChatActivity() {

        boolean checkVIP = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.VIPIMSWITCH, false);
        boolean checkPhon = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.PHONEIMSWITCH, false);
        boolean isPhon = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.CHECK_PHONE, false);
        int level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
        if (checkVIP && checkPhon) {

            startChatActivity();

        } else {

            if (!checkVIP && checkPhon) {

                if (level == 0) {
                    // 开通会员

                    setTipDialog(R.string.mobile_charge, R.string.mobile_verify_nexttime,
                            R.string.mobile_charge_now, true, true, 0, TYPE_VIP);
                } else {
                    startChatActivity();
                }

            } else if (!checkPhon && checkVIP) {

                if (!isPhon) {
                    // 验证手机页面
                    setTipDialog(R.string.mobile_notice, R.string.mobile_verify_nexttime,
                            R.string.mobile_verify_now, true, true, TYPE_CHAT, TYPE_MOBILE);
                } else {
                    startChatActivity();
                }
            } else if (!checkPhon && !checkVIP) {

                if (!isPhon) {

                    // 验证手机页面
                    setTipDialog(R.string.mobile_notice, R.string.mobile_verify_nexttime,
                            R.string.mobile_verify_now, true, true, TYPE_CHAT, TYPE_MOBILE);

                } else if (level == 0) {

                    // 开通会员
                    setTipDialog(R.string.mobile_charge, R.string.mobile_verify_nexttime,
                            R.string.mobile_charge_now, true, true, 0, TYPE_VIP);

                } else {
                    startChatActivity();
                }
            }
        }


    }

    /**
     * 查看是否验证了手机号码
     * 查看是否打开了手机认证才能聊天的开关
     * 查看是否有这个级别进行聊天
     */
    public void isVerifyMobile() {
        boolean checkPhone = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.CHECK_PHONE, false);
        boolean buyerPhoneIMSwitch = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.PHONEIMSWITCH, true);
        int myVipLevel = (int) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.VIP_LEVEL, 0);
        int vipLevel = sellerUserVo.getSellerVipLevel();
        //如果进行了手机认证，那么可以进行下个步骤
//        if (myVipLevel < vipLevel) {
//            startChooseVIPActivity();
//        }else {
        if (checkPhone == true) {
            startChatActivity();
        } else {
            if (buyerPhoneIMSwitch) {
                setTipDialog(R.string.mobile_notice, R.string.mobile_verify_nexttime,
                        R.string.mobile_verify_now, true, true, TYPE_CHAT, TYPE_MOBILE);
            } else {
                //不可以可以跳过聊天直接进入聊天界面
                setTipDialog(R.string.mobile_notice, R.string.mobile_verify_nexttime,
                        R.string.mobile_verify_now, false, true, 0, TYPE_MOBILE);
            }
        }
//        }
    }

    /**
     * 设置对话框的接口
     *
     * @param contentRId     内容的字符串ID
     * @param LeftButtonRId  左安建的ID
     * @param rightButtonRId 右按键的id
     * @param leftVisable    左安建是否可见
     * @param rightVisable   右按键是否可见
     * @param rightType      进入那个Activity 的标签
     */
    private void setTipDialog(int contentRId, int LeftButtonRId, int rightButtonRId,
                              boolean leftVisable, boolean rightVisable, final int leftType, final int rightType) {
        //如果没有认证，需要判断是否需要手机认证才能聊天
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.custom_alertdialog_mobile, null);
        final AlertDialog dialog = DialogTool.createSelectDialog(this, layout, contentRId, LeftButtonRId, rightButtonRId);
        if (leftVisable == false) {
            layout.findViewWithTag(0).setVisibility(View.GONE);
        }
        if (rightVisable == false) {
            layout.findViewWithTag(1).setVisibility(View.GONE);
        }
        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                startNewActivity(leftType);
            }
        });
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startNewActivity(rightType);
            }
        });
    }

    /**
     * 进入新的activity
     *
     * @param type
     */
    private void startNewActivity(int type) {
        switch (type) {
            case TYPE_CHAT://进入聊天界面
                startChatActivity();
                break;
            case TYPE_MOBILE://进入手机认证界面
                startMobileVerifyActivity();
                break;
            case TYPE_VIP://进入会员充值
                startGetVipActivity();
                break;
            case TYPE_PRE_VIP:
                startChooseVIPActivity();
                break;
            case TYPE_GET_VIP:
                getBuyerUserStatus();
                break;
            case TYPE_CLOSE_TIP:
                break;
        }
    }


    /**
     * create by brucs
     * version V1.6.0
     * date:2016/6/29
     * describle:获取用户界面的状态
     */

    private void getBuyerUserStatus() {
        //  SharedPreferencesUtils.put(GirlProfileActivity.this, Constants.PHONEIMSWITCH, true);
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(GirlProfileActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }

        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerUserId", buyerId);
        final String str = buyerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().setBuyerUserVip, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    String massage = jsonObject.getJSONObject("dataCollection").getString("massage");
                    boolean isVerify = jsonObject.getJSONObject("dataCollection").getBoolean("status");
                    if (isVerify == true) {
                        startChatActivity();
                    } else {
                        MyToast.makeTextAnim(GirlProfileActivity.this, massage, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(GirlProfileActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 查询是否参与过活动
     */
    private void startChooseVIPActivity() {
        //不可以可以跳过聊天直接进入聊天界面
        boolean getFreeVip = (boolean) SharedPreferencesUtils.get(GirlProfileActivity.this, Constants.FREEVIP, true);
        if (getFreeVip == true) {
            setTipDialog(R.string.vip_notice, R.string.get_free_vip,
                    R.string.vip_get_now, true, true, TYPE_GET_VIP, TYPE_VIP);
        } else {
            setTipDialog(R.string.vip_notice, R.string.mobile_verify_nexttime,
                    R.string.vip_get_now, true, true, TYPE_CLOSE_TIP, TYPE_VIP);
        }
    }

    /**
     * 进入聊天界面
     */
    private void startChatActivity() {
        //进入65800
//        Intent intent = new Intent(this, WebViewGuestActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isHaveValue", true);
//        bundle.putString("pretty", sellerUserVo.getUsName());
//        intent.putExtras(bundle);
//        startActivity(intent);

        boolean isReminding = (boolean) SharedPreferencesUtils.get(this, Constants.ISRMINDING, false);


        if (!isReminding) {

            View view = LayoutInflater.from(this).inflate(R.layout.custom_alertdialog_remind, null, false);
            final Dialog dialog = DialogTool.createCheckboxDialog(this, view);

            CheckBox checkBox = view.findViewWithTag(0);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheckBox) {
                    if (isCheckBox) {

                        SharedPreferencesUtils.put(GirlProfileActivity.this, Constants.ISRMINDING, true);
                    } else {
                        SharedPreferencesUtils.put(GirlProfileActivity.this, Constants.ISRMINDING, false);

                    }
                }
            });
            view.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GirlProfileActivity.this, ChatActivity.class);
                    intent.putExtra("sellerUserVo", sellerUserVo);
                    intent.putExtra("fromProfile", true);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

        } else {
            Intent intent = new Intent(GirlProfileActivity.this, ChatActivity.class);
            intent.putExtra("sellerUserVo", sellerUserVo);
            intent.putExtra("fromProfile", true);
            startActivity(intent);
        }

    }

    /**
     * 进入手机认证界面
     */
    private void startMobileVerifyActivity() {
        Intent intent = new Intent(GirlProfileActivity.this, MobileVerifyActivity.class);
        startActivity(intent);
    }

    /**
     * 进入会员充值
     */
    private void startGetVipActivity() {
        Intent intent = new Intent(GirlProfileActivity.this, Buy_vipActivity.class);
        startActivity(intent);
    }


    /**
     * 获取屏幕的状态
     *
     * @param context
     * @return
     */
    public boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            pausePos = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    @Override
    protected void onRestart() {
        Log.e("eeeeeeeeeeeeeeee", "onRestart");
        super.onRestart();
        if (video_rela.getVisibility() == View.VISIBLE && pausePos >= 0) {
            videoView.seekTo(pausePos);

            pausePos = -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (video_rela.getVisibility() == View.VISIBLE) {
                video_rela.setVisibility(View.GONE);
                videoView.pause();
                if (!isScreenOriatationPortrait(this)) {// 当屏幕是横屏时
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
                }
            } else {
                finish();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_rating, menu);
        MenuItem action_msg = menu.findItem(R.id.action_menu);
        if (from == Constants.COME_FROM_VIRTUAL_GOODS) {
            action_msg.setVisible(false);
            return true;
        }
        boolean isEixt = selleruserdao.isExist(DBcolumns.TABLE_FAVORITES, String.valueOf(sellerId));
        favariteStatus = isEixt;
        if (isEixt == false) {
            action_msg.setIcon(R.mipmap.heart_small_none);
        } else {
            action_msg.setIcon(R.mipmap.heart_small);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
        if (sellerUserVo == null) {
            return false;
        }
        boolean isEixt = selleruserdao.isExist(DBcolumns.TABLE_FAVORITES, String.valueOf(sellerId));
        int tableItemCount = selleruserdao.tableItemCount(DBcolumns.TABLE_FAVORITES);
        if (isEixt == false && favariteStatus == false) {
            if (tableItemCount >= 10) {//收藏不能超过10个
                MyToast.makeTextAnim(GirlProfileActivity.this, R.string.over_10item, 0, R.style.PopToast).show();
                return false;
            }
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String time = dateFormat.format(now);
            sellerUserVo.setNowTime(time);
            selleruserdao.insert(sellerUserVo, DBcolumns.TABLE_FAVORITES);

            //通知首页刷新聊天会话列表
            Intent mIntent1 = new Intent(Constants.ACTION_FAVORITE);
            sendBroadcast(mIntent1);
            invalidateOptionsMenu();
        }
        if (isEixt == true && favariteStatus == true) {
            selleruserdao.deleteTableItemById(DBcolumns.TABLE_FAVORITES, String.valueOf(sellerId));
            //通知首页刷新聊天会话列表
            Intent mIntent1 = new Intent(Constants.ACTION_FAVORITE);
            sendBroadcast(mIntent1);
            invalidateOptionsMenu();
        }
        return true;
    }
}
