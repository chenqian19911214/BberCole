package com.bber.company.android.util;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bber.company.android.R;
import com.bber.company.android.widget.MyToast;

import java.util.List;

/**
 * Author: admin
 * Date: 2016/9/9
 * Version:
 * Describe:
 * <p>
 * 定位工具栏
 */
public class MapSupport implements LocationSource,
        AMapLocationListener, AMap.OnMapClickListener,
        PoiSearch.OnPoiSearchListener, DistrictSearch.OnDistrictSearchListener {

    //返回定位
    public static final int POI_RETURN_TYPE_LOCATION = 0;
    //返回搜索poi
    public static final int POI_RETURN_TYPE_SEARCH = 1;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    private Context mContext;
    private AMap aMap;
    private MapView mMapView;
    private Marker mMarker;
    private LatLng mLocationPoint;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private UiSettings mUiSettings;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private LatLonPoint lp = new LatLonPoint(39.993167, 116.473274);
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据
    private myPoiOverlay poiOverlay;// poi图层
    private InterfaceMap minterfaceMap;
    private int poiType = 0;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    public MapSupport(Context context, MapView mapView, InterfaceMap interfaceMap) {
        mContext = context;
        mMapView = mapView;
        minterfaceMap = interfaceMap;
    }

    public MapSupport(Context context, InterfaceMap interfaceMap) {
        mContext = context;
        minterfaceMap = interfaceMap;
        IniteLocation();
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            if (mListener != null) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            }
            //获取位置信息
            String privince = aMapLocation.getProvince();
            String city = aMapLocation.getCity();
            String district = aMapLocation.getDistrict();

            latitude = aMapLocation.getLatitude();
            longitude = aMapLocation.getLongitude();
            // 停止定位
            locationClient.stopLocation();

            minterfaceMap.getLocation(privince, city, district, latitude, longitude);
        } else {
            // 停止定位
            locationClient.stopLocation();
            minterfaceMap.noLocation();
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 初始化AMap对象
     */
    public void setMapinit() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setOnMapClickListener(this);// 对amap添加单击地图事件监听器
            setUpMap();
        }
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
    }

    /**
     * 初始化AMap对象
     */
    public void setMapShowinit(double latitude, double longitude) {
        mLocationPoint = new LatLng(latitude, longitude);
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.addMarker(new MarkerOptions().position(mLocationPoint).icon(
                    BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            movetoNewPlace(mLocationPoint);
        }
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
    }

    /**
     * 初始化AMap对象
     */
    public void setMapSerchinit(String keyword) {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        citySearch(keyword);
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
    }


    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mLocationPoint = latLng;
        lp.setLongitude(latLng.longitude);
        lp.setLatitude(latLng.latitude);
        drawMarkers();
        doSearchQuery(POI_RETURN_TYPE_LOCATION, latLng.latitude, latLng.longitude);
        minterfaceMap.getClickLatLonPoint(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 开始进行poi搜索
     */
    private void doSearchQuery(int type, double latitude, double longitude) {
        String keyword = "汽车服务|汽车销售|" +
                "餐饮服务|购物服务|生活服务|医疗保健服务|" +
                "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|" +
                "金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        LatLonPoint mylp = new LatLonPoint(latitude, longitude);
        poiType = type;
        query = new PoiSearch.Query("", keyword, "");
        query.setPageSize(40);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        if (lp != null) {
            poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(mylp, 5000, true));//
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 开始进行poi搜索
     */
    public void doSearchQuery(String seachWord, String cityName) {
        String keyword = "汽车服务|汽车销售|" +
                "餐饮服务|购物服务|生活服务|医疗保健服务|" +
                "住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|" +
                "金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        poiType = POI_RETURN_TYPE_SEARCH;
        query = new PoiSearch.Query(seachWord, keyword, cityName);
        query.setPageSize(1);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        query.setCityLimit(true);
        poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {
                    poiResult = result;
                    poiItems = poiResult.getPois();
                    minterfaceMap.getPosSerchDate(poiItems, poiType);
                    if (poiItems != null && poiItems.size() > 0) {
                        poiOverlay = new myPoiOverlay(aMap, poiItems);
                        poiOverlay.zoomToSpan();
                    }

                    if (poiType == POI_RETURN_TYPE_SEARCH) {
                        if (poiItems != null && poiItems.size() > 0) {
                            aMap.clear();// 清理之前的图标
                            aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                            PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                            poiOverlay.removeFromMap();
                            poiOverlay.addToMap();
                            poiOverlay.zoomToSpan();
                        } else {
                            MyToast.makeTextAnim(mContext, R.string.no_location_infor, 0, R.style.PopToast).show();
                        }
                    }
                }
            }
        }
    }

    public void movetoNewPlace(LatLng point) {
        mLocationPoint = point;
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mLocationPoint, 18, 30, 0)));
        drawMarkers();
    }

    /**
     * 绘制系统默认的1种marker背景图片
     */
    public void drawMarkers() {
        aMap.clear();
        mMarker = aMap.addMarker(new MarkerOptions()
                .position(mLocationPoint)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true));
        mMarker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
        if (districtResult == null || districtResult.getDistrict() == null) {
            return;
        }
        final DistrictItem item = districtResult.getDistrict().get(0);

        if (item == null) {
            return;
        }
        LatLonPoint centerLatLng = item.getCenter();
        if (centerLatLng != null) {
            aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 11));
        }
    }

    private void citySearch(String keyword) {
        DistrictSearch search = new DistrictSearch(mContext);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(keyword);
        query.setShowBoundary(false);
        search.setQuery(query);
        search.setOnDistrictSearchListener(this);
        search.searchDistrictAnsy();

    }

    /**
     * 启动定位
     */

    public void IniteLocation() {
        //初始化定位
        locationClient = new AMapLocationClient(mContext.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationOption.setOnceLocation(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        locationClient.stopLocation();
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        locationClient.startLocation();
    }

    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    public void onDestroy() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    public void onPause() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    /**
     * poi搜索类
     */
    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;

        public myPoiOverlay(AMap amap, List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 移动镜头到当前的视角。
         *
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }
    }
}
