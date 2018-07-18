package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;
import com.bber.company.android.bean.BusinessBean;
import com.bber.company.android.bean.adsBean;
import com.bber.company.android.bean.cityBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.CityDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.InterfaceMap;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.util.commonActivity;
import com.bber.company.android.view.adapter.BusinessModelAdapter;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.view.XRefreshView;
import com.bber.customview.view.XRefreshViewFooter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Bruce
 * Date: 2016/5/9
 * Version:
 * Describe:
 */
public class BusinessActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private final String[] type = {"全部\n", "商务KTV\n", "桑拿会所\n", "保健按摩\n"};
    private final String[] typeWord = {"", "A", "B", "C"};
    private RecyclerView recyclerView;
    private XRefreshView xRefreshView;
    private LinearLayout view_no_business;
    private BusinessModelAdapter adapter;
    private TextView home_top_city;
    private List<BusinessBean> mBusinessList;
    private List<BusinessBean> mFavoriteShopList;
    private int privinceCode, cityCode, districtCode;
    private String cityName, districtName;
    private int page = 1;
    private CityDao dbManager;
    private int pageNumber = 15;
    private String shopType = "";
    private int mChooseType = 0;
    private List<TextView> viewList;
    private MapSupport mapSupport;
    private EditText mET_search;
    private List<adsBean> adsBeenList;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setView(0, null);
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_BUSSINESS)) {
                getShopFavoriteById();
            }
        }
    };

    private void setNewAdsItem() {
        if (adsBeenList == null || adsBeenList.size() == 0) {
            return;
        }
        for (int i = 0; i < adsBeenList.size(); i++) {
            String uriTarget = adsBeenList.get(i).getAdPicture();
            if (!Tools.isEmpty(uriTarget)) {
                BusinessBean businessBean = new BusinessBean();
                businessBean.setShopHeadPhoto(uriTarget);
                businessBean.setViewType(1);
                businessBean.setShopId(i);
                mBusinessList.add(i, businessBean);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListener();
        initData();
    }

    private void initListener() {
        home_top_city.setOnClickListener(this);
        mET_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getShopListForBuyer(0, mET_search.getText() + "");
                return false;
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        home_top_city = toolbar.findViewById(R.id.home_top_city);
        view_no_business = findViewById(R.id.view_no_business);
        xRefreshView = findViewById(R.id.xrefreshview);
        recyclerView = findViewById(R.id.recyclerView);
        mET_search = findViewById(R.id.country_et_search);
    }

    public void initData() {
        viewList = new ArrayList<>();
        mBusinessList = new ArrayList<>();
        dbManager = new CityDao(this);
        addNewTextview();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
        adapter = new BusinessModelAdapter(mBusinessList);
        adapter.setOnItemClickListener(new BusinessModelAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, BusinessBean data) {
                if (data.getViewType() == 0) {
                    Intent intent = new Intent(BusinessActivity.this, BussinessDetailActivity.class);
                    intent.putExtra("isFavorite", getIsFavorite(data.getId()));
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    commonActivity.startBrowebuyURL(BusinessActivity.this, adsBeenList.get(data.getShopId()));
                }
            }
        });
        recyclerView.setAdapter(adapter);
        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
                getShopListForBuyer(0, "");
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                getShopListForBuyer(1, "");
            }
        });

        mapSupport = new MapSupport(this, new InterfaceMap() {
            @Override
            public void getPosSerchDate(List<PoiItem> poiItems, int type) {

            }

            @Override
            public void getClickLatLonPoint(double latitude, double longitude) {

            }

            @Override
            public void getLocation(String privince, String city, String district, double lat, double lng) {
                cityName = city;
                districtName = district;
                setKeyWordCity(cityName);
                getKeyWordDistrict(districtName);
                //重新更新一下
                getShopListForBuyer(0, "");
            }

            @Override
            public void noLocation() {

            }
        });
        mapSupport.startLocation();

        getShopFavoriteById();
        getAdStatus();
        registerBoradcastReceiver();
    }

    private void setView(int type, List<BusinessBean> business) {
        if (type == 0) {//刷新列表
            xRefreshView.stopRefresh();
            mBusinessList.clear();
            setNewAdsItem();
        } else {
            xRefreshView.stopLoadMore();
        }
        if (business != null) {
            mBusinessList.addAll(business);
        }

        if (mBusinessList != null && mBusinessList.size() == 0) {
            view_no_business.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_business.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.updateListView(mBusinessList);
    }

    /**
     * 添加新的textview
     */

    private void addNewTextview() {
        LinearLayout linearLayoutContainer = findViewById(R.id.ll_business_type);
        linearLayoutContainer.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.width = 0;
        lp.setMargins(20, 0, 20, 0);
        mChooseType = 0;
        for (int i = 0; i < type.length; i++) {
            TextView tvCategory = new TextView(this);
            tvCategory.setText(type[i]);
            tvCategory.setTextColor(getResources().getColor(R.color.black));
            tvCategory.setLayoutParams(lp);
            tvCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvCategory.setGravity(Gravity.CENTER);
            tvCategory.setTag(i);
            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChooseType == (Integer) v.getTag()) {
                        return;
                    }
                    v.setBackgroundResource(R.mipmap.image_sliding_block);
                    mChooseType = (Integer) v.getTag();
                    setBackground(mChooseType);
                    shopType = typeWord[mChooseType];
                    getShopListForBuyer(0, "");
                }
            });
            linearLayoutContainer.addView(tvCategory);
            viewList.add(tvCategory);
        }
        setBackground(0);
    }

    /**
     * 设置背景
     *
     * @param index
     */
    private void setBackground(int index) {
        for (int i = 0; i < viewList.size(); i++) {
            if (i == index) {
                viewList.get(i).setBackgroundResource(R.mipmap.image_sliding_block);
                viewList.get(i).setTextColor(getResources().getColor(R.color.pink));

            } else {
                viewList.get(i).setBackgroundResource(R.color.transparent);
                viewList.get(i).setTextColor(getResources().getColor(R.color.main_text));
            }
        }
    }

    /**
     * 获取商户的列表
     */

    private void getShopListForBuyer(final int type, String keyShopName) {
        if (type == 0) {
            page = 1;
        } else {
            page++;
        }
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        if ("".equals(shopType) == false) {
            params.put("shopType", shopType);
        }
        params.put("longitude", MapSupport.latitude);
        params.put("latitude", MapSupport.longitude);
        if (privinceCode != 0) {
            params.put("province", privinceCode);
        }
        if (cityCode != 0) {
            params.put("city", cityCode);
        }
        if (districtCode != 0) {
            params.put("district", districtCode);
        }
        params.put("page", page);
        params.put("pageNumber", pageNumber);
        params.put("sortType", "");
        params.put("shopName", keyShopName);
        HttpUtil.post(Constants.getInstance().getShopListForBuyer, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    List<BusinessBean> newBusinessList = jsonUtil.jsonToBusinessBean(dataCollection);
                    setView(type, newBusinessList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BusinessActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_top_city:
                Intent intent = new Intent(this, CityPickerActivity.class);
                startActivityForResult(intent, 1);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                privinceCode = data.getIntExtra("privinceCode", 0);
                cityCode = data.getIntExtra("cityCode", 0);
                districtCode = data.getIntExtra("districtCode", 0);
                cityName = data.getStringExtra("cityName");
                districtName = data.getStringExtra("districtName");
                home_top_city.setText(cityName);
                if (Tools.isEmpty(districtName) == false
                        && !Constants.UNLIMITE_LOCATINO.equals(districtName)) {
                    home_top_city.setText(districtName);
                }
                //重新更新一下
                getShopListForBuyer(0, "");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBroadcastReceiver);
        dbManager.closeDB();
    }

    /**
     * 设置关键字城市
     *
     * @param city
     */
    private void setKeyWordCity(String city) {
        if (!Tools.isEmpty(city) && !Constants.UNLIMITE_LOCATINO.equals(city)) {
            List<cityBean> result = dbManager.searchCity(city);
            if (result != null && result.size() > 0) {
                setCity(result.get(0));
            }
        }
    }

    /**
     * 选择了城市名称，或者是区域的名称，返回
     *
     * @param city
     */
    private void setCity(cityBean city) {
        privinceCode = getPrivinceCode(city);
        cityCode = city.getAdcode();
        cityName = city.getName();
        if (!Tools.isEmpty(cityName)) {
            home_top_city.setText(cityName);
        }
    }

    /**
     * 查询省份的编码
     *
     * @param city
     */
    private int getPrivinceCode(cityBean city) {
        int adcode = 0;
        List<cityBean> citys = dbManager.getPrivinceCode(city.getParentAdcode() + "");
        if (citys != null && citys.size() > 0) {
            adcode = citys.get(0).getAdcode();
        }
        return adcode;
    }

    /**
     * 设置关键字城市
     *
     * @param disttict
     */
    private void getKeyWordDistrict(String disttict) {
        if (!Tools.isEmpty(disttict) && !Constants.UNLIMITE_LOCATINO.equals(disttict)) {
            List<cityBean> result = dbManager.searchDistrict(disttict);
            if (result != null && result.size() > 0) {
                setDistrict(result.get(0));
            }
        }
    }

    /**
     * 或者是区域的名称，返回
     *
     * @param city
     */
    private void setDistrict(cityBean city) {
        districtCode = city.getAdcode();
        districtName = city.getName();
        if (!Tools.isEmpty(districtName)) {
            home_top_city.setText(districtName);
        }
    }

    /**
     * 获取商户评论列表
     */

    private void getShopFavoriteById() {
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
        HttpUtil.post(Constants.getInstance().getShopFavoriteById, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    mFavoriteShopList = jsonUtil.jsonToBusinessBean(dataCollection);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * @return
     */
    private boolean getIsFavorite(int shopId) {
        boolean result = false;
        for (int i = 0; mFavoriteShopList != null && i < mFavoriteShopList.size(); i++) {
            if (mFavoriteShopList.get(i).getShopId() == shopId) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_BUSSINESS);
        this.registerReceiver(mBroadcastReceiver, myIntentFilter);
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
        params.put("adPlace", "SHOP");
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
}

