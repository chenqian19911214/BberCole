package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BusinessBean;
import com.bber.company.android.bean.BusinessRatingBean;
import com.bber.company.android.bean.MapBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.adapter.HorListAdapter;
import com.bber.company.android.view.adapter.RatingListAdapter;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class BussinessDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private BusinessBean shop;
    private TextView mTV_shop_name, mTV_shop_detail, mTV_price, mTV_shop_address, tv_add_rating;
    private TextView mTV_ration_mun, mTV_rating_more, mTV_feature, mTV_shop_tittle, mTV_manager_phone;
    private RecyclerView mRV_Photo;
    private RecyclerView mRV_Rating;
    private LinearLayout mLL_photo, mLL_feature, mLL_shop_address, mLL_shop_detail, mLL_manager, mLL_shop_name;
    private RatingBar mRB_rating;
    private RatingListAdapter mRatingAdapter;
    private List<BusinessRatingBean> mRatingList;
    private Boolean isFavorite;
    private Boolean isTuchable = true;
    private View view_rating_line;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_business_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.shop_detail);
        mTV_shop_detail = findViewById(R.id.tv_shop_detail);
        mTV_price = findViewById(R.id.tv_price);
        mTV_shop_name = findViewById(R.id.tv_shop_name);
        mTV_shop_address = findViewById(R.id.tv_shop_address);
        mTV_ration_mun = findViewById(R.id.tv_ration_mun);
        mTV_rating_more = findViewById(R.id.tv_rating_more);
        mTV_feature = findViewById(R.id.tv_feature);
        mTV_shop_tittle = findViewById(R.id.tv_shop_tittle);
        tv_add_rating = findViewById(R.id.tv_add_rating);
        mTV_manager_phone = findViewById(R.id.tv_manager_phone);

        mRB_rating = findViewById(R.id.rb_rating);

        mLL_photo = findViewById(R.id.ll_photo);
        mLL_shop_name = findViewById(R.id.ll_shop_name);
        mLL_feature = findViewById(R.id.ll_feature);
        mLL_shop_address = findViewById(R.id.ll_shop_address);
        mLL_shop_detail = findViewById(R.id.ll_shop_detail);
        mLL_manager = findViewById(R.id.ll_manager);

        mRV_Photo = findViewById(R.id.rv_photo);
        mRV_Rating = findViewById(R.id.rv_rating);
        view_rating_line = findViewById(R.id.view_rating_line);

    }

    private void setListeners() {
        mTV_rating_more.setOnClickListener(this);
        tv_add_rating.setOnClickListener(this);
        mLL_shop_address.setOnClickListener(this);
    }

    private void initData() {
        shop = (BusinessBean) getIntent().getSerializableExtra("data");
        isFavorite = getIntent().getBooleanExtra("isFavorite", false);
        mTV_shop_name.setText(shop.getShopName());
        mTV_ration_mun.setText("评分：" + shop.getShopScore() + "分");
        setTextInfor(mLL_feature, mTV_feature, shop.getShopNote());
        setTextInfor(mLL_shop_address, mTV_shop_tittle, shop.getShopName());
        setTextInfor(mLL_shop_address, mTV_shop_address, getAddressInfor());
        setTextInfor(mLL_shop_detail, mTV_shop_detail, getShopDetalString());
        setTextInfor(mLL_manager, mTV_manager_phone, getManegerString());
        mRB_rating.setRating(shop.getShopScore());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRV_Rating.setLayoutManager(layoutManager);
        mRV_Rating.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());

        //初始化相片列表
        initPhotos();
        //获取评论列表
        getShopCommentList();

        mRatingAdapter = new RatingListAdapter(this, mRatingList);
        mRV_Rating.setAdapter(mRatingAdapter);

    }

    /**
     * 初始化相片列表
     *
     * @return
     */
    private void initPhotos() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRV_Photo.setLayoutManager(linearLayoutManager);
        int height = (MyApplication.screenWidth) / 4;
        mRV_Photo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

        //加载相册
        String[] images = shop.getShopPhotoList();
        if (images != null && images.length > 0) {
            mLL_photo.setVisibility(View.VISIBLE);
            mRV_Photo.setVisibility(View.VISIBLE);
            HorListAdapter horListAdapter = new HorListAdapter(this, images);
            mRV_Photo.setAdapter(horListAdapter);
            horListAdapter.setOnItemClickListener(new HorListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(BussinessDetailActivity.this, BrowseImageActivity.class);
                    intent.putExtra("images", shop.getShopPhotoList());
                    intent.putExtra("currentIndex", position);
                    startActivity(intent);
                }
            });
        } else {
            mLL_photo.setVisibility(View.GONE);
        }
    }

    /**
     * 设置文字
     *
     * @param textInfor
     * @return
     */
    private void setTextInfor(LinearLayout layout, TextView textView, String textInfor) {
        layout.setVisibility(View.GONE);
        if (!Tools.isEmpty(textInfor)) {
            textView.setText(textInfor);
            layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置文字
     *
     * @return
     */
    private String getAddressInfor() {
        StringBuilder result = new StringBuilder();
        result.append(shop.getShopProvinceName()).append(" ")
                .append(shop.getShopCityName()).append(" ")
                .append(shop.getShopDistrictName()).append(" ")
                .append(shop.getShopAddress());
        return result.toString();
    }

    /**
     * 设置文字
     *
     * @return
     */
    private String getShopDetalString() {
        String result = "";
        if (!Tools.isEmpty(shop.getShopPhone())) {
            result += "商户电话：" + shop.getShopPhone() + "\n";
        }
        if (!Tools.isEmpty(shop.getShopBusinessHours())) {
            result += "营业时间：" + shop.getShopBusinessHours() + "\n";
        }
        if (shop.getShopMaxMoney() > 0) {
            result += "参考消费：¥" + shop.getShopMinMoney() + " - ¥" + shop.getShopMaxMoney();
        }
        return result;
    }

    /**
     * 设置文字
     *
     * @return
     */
    private String getManegerString() {
        String result = "";
        if (!Tools.isEmpty(shop.getShopManagerName())) {
            result += "经理人：" + shop.getShopManagerName() + "\n";
        }
        if (!Tools.isEmpty(shop.getShopManagerPhone())) {
            result += "联系方式：" + shop.getShopManagerPhone();
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_rating_more:
                intent = new Intent(BussinessDetailActivity.this, BussinessRatingDetailActivity.class);
                intent.putExtra("shopid", shop.getId());
                startActivity(intent);
                break;
            case R.id.tv_add_rating:
                intent = new Intent(BussinessDetailActivity.this, BusinessRatingActivity.class);
                intent.putExtra("shopid", shop.getId());
                startActivity(intent);
                break;
            case R.id.ll_shop_address:
                MapBean mapBean = new MapBean();
                mapBean.setadName(shop.getShopDistrictName());
                mapBean.setCityName(shop.getShopCityName());
                mapBean.setProvinceName(shop.getShopProvinceName());
                mapBean.setmTitle(shop.getShopName());
                mapBean.setmSnippet(shop.getShopAddress());
                mapBean.setLatitude(shop.getShopLatitude());
                mapBean.setLonPoint(shop.getShopLongitude());
                mapBean.setBusinessArea(shop.getShopDistrictName());

                Intent intentMap = new Intent(BussinessDetailActivity.this, MaplocationActivity.class);
                int showType = 0; //如果没有经纬度，按照关键字搜索2是表示关键字搜索
                if (shop.getShopLatitude() == 0 || shop.getShopLongitude() == 0) {
                    showType = 2;
                }
                intentMap.putExtra("showType", showType);
                intentMap.putExtra("keyWord", shop.getShopAddress() + shop.getShopName());
                intentMap.putExtra("cityName", shop.getShopCityName());
                intentMap.putExtra("mapbean", mapBean);
                startActivity(intentMap);
                break;
        }
    }


    /**
     * 获取商户评论列表
     */

    private void getShopCommentList() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("page", 1);
        params.put("pageNumber", 2);
        params.put("shopId", shop.getId());
        HttpUtil.post(Constants.getInstance().getShopCommentList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    mRatingList = jsonUtil.jsonToBusinessRatingBean(dataCollection);
                    mRatingAdapter.update(mRatingList);
                    mRV_Rating.setVisibility(View.VISIBLE);
                    if (mRatingList != null && mRatingList.size() > 0) {
                        view_rating_line.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BussinessDetailActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rating, menu);
        MenuItem action_msg = menu.findItem(R.id.action_menu);
        if (isFavorite == false) {
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
        if (isTuchable == false) {
            isTuchable = false;
            return true;
        }
        if (isFavorite == false) {
            insertShopFavorite();
        } else {
            deleteShopFavorite();
        }
        return true;
    }

    /**
     * 获取商户评论列表
     */

    private void insertShopFavorite() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("shopId", shop.getId());
        HttpUtil.post(Constants.getInstance().insertShopFavorite, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        isFavorite = true;
                        invalidateOptionsMenu();
                        Intent mIntent = new Intent(Constants.ACTION_BUSSINESS);
                        sendBroadcast(mIntent);
                    }
                    if ("商户已被收藏".equals(resultMessage)) {
                        isFavorite = true;
                        invalidateOptionsMenu();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BussinessDetailActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                isTuchable = true;
            }
        });
    }

    /**
     * 获取商户评论列表
     */

    private void deleteShopFavorite() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("shopId", shop.getId());
        HttpUtil.post(Constants.getInstance().deleteShopFavorite, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        isFavorite = false;
                        invalidateOptionsMenu();
                        Intent mIntent = new Intent(Constants.ACTION_BUSSINESS);
                        sendBroadcast(mIntent);
                    }
                    if ("商户不在收藏列表中".equals(resultMessage)) {
                        isFavorite = false;
                        invalidateOptionsMenu();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BussinessDetailActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                isTuchable = true;
            }
        });
    }
}
