package com.bber.company.android.view.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;
import com.bber.company.android.bean.cityBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.PointState;
import com.bber.company.android.db.CityDao;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.InterfaceMap;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.view.adapter.CityListAdapter;
import com.bber.company.android.view.adapter.DistrictGridviewAdapter;
import com.bber.company.android.view.adapter.ResultListAdapter;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.SideLetterBar;

import java.util.ArrayList;
import java.util.List;

/**
 * author 2016/1/26.
 */
public class CityPickerActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private List<cityBean> mAllCities;
    private List<cityBean> mAllDistrict;
    private ListView mResultListView;
    private SideLetterBar mLetterBar;
    private ListView listView;
    private ImageView clearBtn;
    private EditText searchBox;
    private GridView gd_district;
    private CityDao dbManager;
    private ViewGroup emptyView;
    private CityListAdapter mCityAdapter;
    private ResultListAdapter mResultAdapter;
    private DistrictGridviewAdapter mDistrictAdapter;
    private TextView tv_now_city, tv_district_name, overlay, history_list;
    private LinearLayout ll_district;
    private RelativeLayout rl_choose_distirct;
    private View alpha_back;
    private int privinceCode = 0;
    private int cityCode = 0;
    private int districtCode = 0;
    private String cityName = "", districtName = "";
    private MapSupport mapSupport;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_city_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    private void initListener() {
        clearBtn.setOnClickListener(this);
        rl_choose_distirct.setOnClickListener(this);
        history_list.setOnClickListener(this);
    }


    private void initData() {
        title.setText(R.string.choose_city);
        history_list.setText(R.string.sure_tip);
        tv_now_city.setText("请选择城市");
        tv_district_name.setText("请选择区县");
        dbManager = new CityDao(this);
        mAllCities = dbManager.getAllCity();
        mAllDistrict = new ArrayList<>();
        mCityAdapter = new CityListAdapter(this, mAllCities, dbManager);
        mResultAdapter = new ResultListAdapter(this, null);
        mDistrictAdapter = new DistrictGridviewAdapter(this, mAllDistrict);

        cityBean itemNull = new cityBean();
        itemNull.setPinyin("0");
        itemNull.setName("定位");
        cityBean itemhot = new cityBean();
        itemhot.setPinyin("1");
        itemhot.setName("热门");

        cityName = getIntent().getStringExtra("cityName");
        districtName = getIntent().getStringExtra("districtName");

        setKeyWordCity(cityName);

        mAllCities.add(0, itemNull);
        mAllCities.add(1, itemhot);

        listView.setAdapter(mCityAdapter);
        mResultListView.setAdapter(mResultAdapter);
        gd_district.setAdapter(mDistrictAdapter);

        mLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                listView.setSelection(position);
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rl_choose_distirct.setVisibility(View.GONE);
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    clearBtn.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    mResultListView.setVisibility(View.GONE);
                    rl_choose_distirct.setVisibility(View.VISIBLE);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    mResultListView.setVisibility(View.VISIBLE);
                    List<cityBean> result = dbManager.searchCity(keyword);
                    if (result == null || result.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        mResultAdapter.changeData(result);
                    }
                }
            }
        });

        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCity(mResultAdapter.getItem(position));
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                mResultListView.setVisibility(View.GONE);
                rl_choose_distirct.setVisibility(View.VISIBLE);
            }
        });

        mCityAdapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {

            @Override
            public void onCityClick(cityBean city) {
                setCity(city);
            }

            @Override
            public void onLocateClick() {

            }
        });

        gd_district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAllDistrict != null) {
                    setDistrict(mAllDistrict.get(position));
                }
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
                mCityAdapter.updateLocateState(PointState.SUCCESS, city);
                setKeyWordCity(city);
                getKeyWordDistrict(district);
            }

            @Override
            public void noLocation() {

            }
        });
        mapSupport.startLocation();

    }

    private void initView() {
        listView = findViewById(R.id.listview_all_city);
        mResultListView = findViewById(R.id.listview_search_result);
        emptyView = findViewById(R.id.empty_view);
        clearBtn = findViewById(R.id.iv_search_clear);
        overlay = findViewById(R.id.tv_letter_overlay);
        tv_now_city = findViewById(R.id.tv_now_city);
        tv_district_name = findViewById(R.id.tv_district_name);
        gd_district = findViewById(R.id.gd_district);
        mLetterBar = findViewById(R.id.side_letter_bar);
        mLetterBar.setOverlay(overlay);
        searchBox = findViewById(R.id.et_search);
        rl_choose_distirct = findViewById(R.id.rl_choose_distirct);
        ll_district = findViewById(R.id.ll_district);
        alpha_back = findViewById(R.id.alpha_back);
        history_list = findViewById(R.id.sure_toolbar);
    }

    /**
     * 选择了城市名称，或者是区域的名称，返回
     *
     * @param city
     */
    private void setCity(cityBean city) {
        tv_now_city.setText("当前城市：" + city.getName());
        mAllDistrict = dbManager.getCityDistrict(city.getCitycode());
        privinceCode = getPrivinceCode(city);
        cityCode = city.getAdcode();
        cityName = city.getName();
        districtCode = 0;
        districtName = Constants.UNLIMITE_LOCATINO;
        tv_district_name.setText("请选择区县");
        cityBean citybean = new cityBean(city.getId(), 0, Constants.UNLIMITE_LOCATINO,
                city.getLat(), city.getLng(), city.getParentAdcode(), city.getCitycode(), city.getPinyin());
        mAllDistrict.add(0, citybean);
        mDistrictAdapter.changeData(mAllDistrict);
        rl_choose_distirct.setEnabled(true);
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
     * 或者是区域的名称，返回
     *
     * @param city
     */
    private void setDistrict(cityBean city) {
        tv_district_name.setText(city.getName());
        districtCode = city.getAdcode();
        districtName = city.getName();
        ll_district.setVisibility(View.GONE);
        alpha_back.setVisibility(View.GONE);
        Drawable down = getResources().getDrawable(R.mipmap.down);
        down.setBounds(0, 0, down.getMinimumWidth(), down.getMinimumHeight());
        down.getMinimumHeight();
        tv_district_name.setCompoundDrawables(null, null, down, null);
        searchBox.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_clear:
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                mResultListView.setVisibility(View.GONE);
                rl_choose_distirct.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_choose_distirct:
                if (Tools.isEmpty(cityName)) {
                    MyToast.makeTextAnim(this, R.string.please_choose_city, 0, R.style.PopToast).show();
                    break;
                }
                if (ll_district.getVisibility() == View.GONE) {
                    ll_district.setVisibility(View.VISIBLE);
                    alpha_back.setVisibility(View.VISIBLE);
                    tv_district_name.setCompoundDrawables(null, null, setDrawableResouce(R.mipmap.up), null);
                    searchBox.setEnabled(false);
                } else {
                    ll_district.setVisibility(View.GONE);
                    alpha_back.setVisibility(View.GONE);
                    tv_district_name.setCompoundDrawables(null, null, setDrawableResouce(R.mipmap.down), null);
                    searchBox.setEnabled(true);
                }
                break;
            case R.id.sure_toolbar:
                if (Tools.isEmpty(cityName)) {
                    MyToast.makeTextAnim(this, R.string.please_choose_city, 0, R.style.PopToast).show();
                    break;
                }
                Intent mIntent = new Intent();
                mIntent.putExtra("privinceCode", privinceCode);
                mIntent.putExtra("cityCode", cityCode);
                mIntent.putExtra("districtCode", districtCode);
                mIntent.putExtra("cityName", cityName);
                mIntent.putExtra("districtName", districtName);
                setResult(RESULT_OK, mIntent);
                finish();
                break;
        }
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
     * 设置左右资源的文件
     *
     * @param resouceId
     */

    private Drawable setDrawableResouce(int resouceId) {
        Drawable drawable = getResources().getDrawable(resouceId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable.getMinimumHeight();
        return drawable;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();
    }

}
