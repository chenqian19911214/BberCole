package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;
import com.bber.company.android.bean.MapBean;
import com.bber.company.android.util.InterfaceMap;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.view.adapter.MapPlaceListAdapter;
import com.bber.company.android.widget.MyToast;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MaplocationActivity extends BaseAppCompatActivity {

    private MapView mMapView;
    private RecyclerView recyclerView;
    private MapPlaceListAdapter adapter;
    private LinearLayout view_no_model;
    private LinearLayout showInfor;
    private MapBean mMapBean;
    private TextView tv_mittle, tv_address, tv_advance;
    private List<PoiItem> mPoiItems;// poi数据
    private int mPosition;// poi图层
    private LatLng mLocationPoint;
    private MapSupport mapSupport;
    private InterfaceMap minterfaceMap;
    private int showType = 0;
    private String cityName;
    private String keyWord;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_maplocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = findViewById(R.id.view_location_map);
        mMapView.onCreate(savedInstanceState);
        setViews();
        initData();
    }

    private void initData() {
        mPosition = -1;
        mPoiItems = new ArrayList<>();
        adapter = new MapPlaceListAdapter(this, mPoiItems, mPosition);

        minterfaceMap = new InterfaceMap() {
            @Override
            public void getPosSerchDate(List<PoiItem> poiItems, int type) {
                mPoiItems = poiItems;
                setView();
            }

            @Override
            public void getClickLatLonPoint(double latitude, double longitude) {

            }

            @Override
            public void getLocation(String privince, String city, String district, double lat, double lng) {

            }

            @Override
            public void noLocation() {

            }
        };
        mapSupport = new MapSupport(this, mMapView, minterfaceMap);

        adapter.setOnItemClickListener(new MapPlaceListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView image = view.findViewById(R.id.view_this);
                mLocationPoint = new LatLng(mPoiItems.get(position).getLatLonPoint().getLatitude(),
                        mPoiItems.get(position).getLatLonPoint().getLongitude());
                image.setVisibility(View.VISIBLE);
                mPosition = position;
                mapSupport.movetoNewPlace(mLocationPoint);
                adapter.updatePosition(mPosition);
            }
        });
        showType = getIntent().getIntExtra("showType", 0);
        mMapBean = (MapBean) getIntent().getSerializableExtra("mapbean");
        keyWord = getIntent().getStringExtra("keyWord");
        cityName = getIntent().getStringExtra("cityName");

        if (showType == 0) { //按照经纬度定位用户
            title.setText(R.string.location);
            tv_mittle.setText(mMapBean.getmTitle());
            String address = mMapBean.getCityName()
                    + "," + mMapBean.getBusinessArea()
                    + "," + mMapBean.getmSnippet();
            tv_address.setText(address);
            tv_advance.setVisibility(View.GONE);

            mapSupport.setMapShowinit(mMapBean.getLatitude(), mMapBean.getLonPoint());
        } else if (showType == 1) {
            title.setText(R.string.send_location);
            tv_advance.setText(R.string.action_send);
            tv_advance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPoiItems != null && mPosition != -1) {
                        Intent mIntent = new Intent();
                        Bundle mBundle = new Bundle();
                        mBundle.putParcelable("poiItem", mPoiItems.get(mPosition));
                        mIntent.putExtras(mBundle);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    } else {
                        MyToast.makeTextAnim(MaplocationActivity.this, R.string.send_a_add, 0, R.style.PopToast).show();
                    }
                }
            });
            mapSupport.setMapinit();
        } else {//按照关键字搜索用户
            title.setText(R.string.location);
            mapSupport.doSearchQuery(keyWord, cityName);
            tv_mittle.setText(mMapBean.getmTitle());
            String address = mMapBean.getCityName()
                    + "," + mMapBean.getBusinessArea()
                    + "," + mMapBean.getmSnippet();
            tv_address.setText(address);
            tv_advance.setVisibility(View.GONE);
            mapSupport.setMapSerchinit(cityName);
        }

        setView();
    }

    private void setViews() {
        view_no_model = findViewById(R.id.view_no_list);
        showInfor = findViewById(R.id.view_userinfo);
        showInfor = findViewById(R.id.view_userinfo);
        recyclerView = findViewById(R.id.recyclerView);
        tv_advance = toolbar.findViewById(R.id.btn_advance);
        tv_mittle = findViewById(R.id.palceName);
        tv_address = findViewById(R.id.address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
    }

    private void setView() {
        if (showType == 0 || showType == 2) {
            recyclerView.setVisibility(View.GONE);
            view_no_model.setVisibility(View.GONE);
            showInfor.setVisibility(View.VISIBLE);
            return;
        }
        if (mPoiItems.size() == 0) {
            view_no_model.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_model.setVisibility(View.GONE);
            adapter.updateListView(mPoiItems, 0);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mapSupport.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mapSupport.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mapSupport.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


}
