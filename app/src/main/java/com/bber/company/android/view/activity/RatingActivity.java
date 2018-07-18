package com.bber.company.android.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.preferenceConstants;
import com.bber.company.android.db.DBcolumns;
import com.bber.company.android.db.SellerUserDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.FlowTagLayout;
import com.bber.customview.adapter.TagAdapter;
import com.bber.customview.listener.OnTagSelectListener;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RatingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private SimpleDraweeView user_icon;
    private TextView name;
    private EditText et_suggest;
    private TagAdapter<String> mfeatureTagAdapter;
    private FlowTagLayout feature_tag;
    private Button btn_over;
    private RatingBar rating;
    private String comment;
    private String toStaff;
    private int ID, sellerId, buyerId, status;
    private SellerUserDao selleruserdao;
    private SellerUserVo sellerUserVo;
    private boolean favariteStatus;
    private String[] colors = new String[]{"#f3267e", "#fe7979", "#04edbe", "#6cdeff", "#ffd708", "#ffb108"};

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_rating;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
        initModelTag();
    }

    private void initViews() {
        title.setText(R.string.rating);
        user_icon = findViewById(R.id.user_icon);
        name = findViewById(R.id.name);
        et_suggest = findViewById(R.id.et_suggest);
        feature_tag = findViewById(R.id.feature_tag);
        btn_over = findViewById(R.id.btn_over);
        rating = findViewById(R.id.rating);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setListeners() {
        btn_over.setOnClickListener(this);
    }

    private void initModelTag() {
        //移动研发标签
        mfeatureTagAdapter = new TagAdapter<String>(this);
        feature_tag.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
        feature_tag.setTagCheckedMax(3);
        feature_tag.setTagLineSum(4);
        feature_tag.setAdapter(mfeatureTagAdapter);
        feature_tag.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    comment = "";
                    for (int i : selectedList) {
                        comment = comment + preferenceConstants.FEATURE_RATING_LIST[i] + ",";
                    }
                }
            }
        });
        addToList();
    }

    private void addToList() {
        List<String> featuredataSource = new ArrayList<>();
        for (int i = 0; i < preferenceConstants.FEATURE_RATING_LIST.length; i++) {
            featuredataSource.add(preferenceConstants.FEATURE_RATING_LIST[i]);
        }
        mfeatureTagAdapter.onlyAddAll(featuredataSource);
    }

    private void initData() {
        ID = getIntent().getIntExtra("ID", -1);
        sellerId = getIntent().getIntExtra("sellerId", -1);
        buyerId = getIntent().getIntExtra("buyerId", -1);
        String sellerName = getIntent().getStringExtra("sellerName");
        String sellerHead = getIntent().getStringExtra("sellerHead");

        name.setText(sellerName);

        int index = (int) (Math.random() * colors.length);
        RoundingParams roundingParams =
                user_icon.getHierarchy().getRoundingParams();
        roundingParams.setBorder(Color.parseColor(colors[index]), 4);
        roundingParams.setRoundAsCircle(true);
        user_icon.getHierarchy().setRoundingParams(roundingParams);

        if (!Tools.isEmpty(sellerHead)) {
            Uri uri = Uri.parse(sellerHead);
            user_icon.setImageURI(uri);
        }

        status = getIntent().getIntExtra("status", 4);
        if (status == 6) {
            rating.setVisibility(View.GONE);
        } else {
            rating.setVisibility(View.VISIBLE);
        }
        selleruserdao = new SellerUserDao(this);
        sellerUserVo = new SellerUserVo();
        sellerUserVo.setuSeller(sellerId);
        sellerUserVo.setUsName(sellerName);
        sellerUserVo.setUsHeadm(sellerHead);
        getSellerInfo(sellerId);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_over:
                if (status == 4 && rating.getRating() < 1) {
                    MyToast.makeTextAnim(RatingActivity.this, R.string.no_rating, 0, R.style.PopToast).show();
                    return;
                }
                if (comment == null) {
                    MyToast.makeTextAnim(RatingActivity.this, R.string.no_tag, 0, R.style.PopToast).show();
                    return;
                }
                if (!netWork.isNetworkConnected()) {
                    MyToast.makeTextAnim(RatingActivity.this, R.string.no_network, 0, R.style.PopToast).show();
                    return;
                }
                DialogTool.createProgressDialog(this, true);
                final JsonUtil jsonUtil = new JsonUtil(this);
                RequestParams params = new RequestParams();
                String head = jsonUtil.httpHeadToJson(this);
                int score;
                if (status == 4) {
                    score = (int) rating.getRating();
                } else {//如果是取消订单的评价 则 默认评分为5
                    score = 5;
                }
                toStaff = et_suggest.getText() + "";
                params.put("head", head);
                params.put("orderId", ID);
                params.put("sellerId", sellerId);
                params.put("buyerId", buyerId);
                params.put("score", score);
                params.put("comment", comment);
                params.put("commentToStaff", toStaff);
                final String str = ID + "" + sellerId + "" + buyerId + "" + score + "" + comment;
                String key = Tools.md5(str);
                params.put("key", key);
                Log.e("eeeeeeeeeeee", "commentOrder ID:" + ID);
                Log.e("eeeeeeeeeeee", "commentOrder" + Constants.getInstance().commentOrder);
                HttpUtil.post(Constants.getInstance().commentOrder, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                        Log.e("eeeeeeeeeeee", "commentOrder onSuccess--jsonObject:" + jsonObject);
                        if (Tools.jsonResult(RatingActivity.this, jsonObject, null)) {
                            return;
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Log.e("eeeeeeeeeeee", "commentOrder onFailure--throwable:" + throwable);
                        MyToast.makeTextAnim(RatingActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
                    }

                    @Override
                    public void onFinish() {
                        DialogTool.dismiss(RatingActivity.this);
                    }
                });
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rating, menu);
        MenuItem action_msg = menu.findItem(R.id.action_menu);
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
        boolean isEixt = selleruserdao.isExist(DBcolumns.TABLE_FAVORITES, String.valueOf(sellerId));
        int tableItemCount = selleruserdao.tableItemCount(DBcolumns.TABLE_FAVORITES);
        if (isEixt == false && favariteStatus == false) {
            if (tableItemCount >= 10) {//收藏不能超过10个
                MyToast.makeTextAnim(RatingActivity.this, R.string.over_10item, 0, R.style.PopToast).show();
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


    private void getSellerInfo(int sellerID) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("uSeller", sellerID);
        HttpUtil.get(Constants.getInstance().getSellerInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("dataCollection");
                    sellerUserVo = jsonUtil.jsonToSellerUserVo(jsonObject1.toString());
                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getSellerInfo JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getSellerInfo onFailure--throwable:" + throwable);
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }
}
