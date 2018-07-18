package com.bber.company.android.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.NearbyBroadcastAdvertisementBean;
import com.bber.company.android.bean.livebroadcast.NearbyBroadcastBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.InterfaceMap;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.view.activity.LiveBroadcastDetailsActivity;
import com.bber.company.android.view.adapter.LiveBoadcastNearbyGridViewAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.view.customcontrolview.PullToRefreshLayout;
import com.bber.company.android.widget.MyToast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * 附近Fragment
 * Created by carlo.c on 2018/4/11.
 */

public class LiveBroadcastNearbyFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {

    private BGABanner carousell_figure;
    private GridView gridView;
    private PullToRefreshLayout pullToRefreshLayout;
    private Context context;
    private MapSupport mapSupport;
    private NearbyBroadcastBean recommendBroadcastdata;
    private List<NearbyBroadcastBean.DataCollectionBean> items;
    private List<String> datalist = new ArrayList<>();
    private List<String> datalisturl = new ArrayList<>();
    private String city;
    private LiveBoadcastNearbyGridViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_livebroadcast_nearby_layout, container, false);
        context = getContext();
        getLocation();
        initView(view);
        ininListener();
        return view;
    }

    private void initView(View view) {
        carousell_figure = view.findViewById(R.id.nearby_carousell_figure_nearby_id);
        gridView = view.findViewById(R.id.nearby_gridview);
        gridView.setNumColumns(2);
        pullToRefreshLayout = view.findViewById(R.id.refresh_view);

        getRecommendBroadcast(0);
        initGridView();
    }

    private void setAdvertisment() {
        carousell_figure.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, final int position) {
                Glide.with(getContext()).load(model)/*.error(R.mipmap.icon_pay)*/.into(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            goTOView(datalisturl.get(position));

                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                });
            }
        });
        carousell_figure.setData(datalist, null);
        carousell_figure.setAutoPlayInterval(3000);
    }

    /**
     * 到本地浏览器
     */
    private void goTOView(String uristr) {
        String httpurl;
        if (uristr.length() > 4) {
            String http = uristr.substring(0, 4);
            if (!http.equals("http")) {
                httpurl = "http://" + uristr;
            } else {
                httpurl = uristr;
            }
            Uri uri = Uri.parse(httpurl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }


    private void ininListener() {

        pullToRefreshLayout.setOnRefreshListener(this);

    }

    private void getLocation() {
        mapSupport = new MapSupport(context, new InterfaceMap() {
            @Override
            public void getPosSerchDate(List<PoiItem> poiItems, int type) {

            }

            @Override
            public void getClickLatLonPoint(double latitude, double longitude) {

            }

            @Override
            public void getLocation(String privince, String city, String district, double lat, double lng) {

                LiveBroadcastNearbyFragment.this.city = city;
                getAdStatus(lat, lng);
            }


            @Override
            public void noLocation() {

            }
        });

        mapSupport.startLocation();
    }


    /**
     * 获取广告
     */
    private void getAdStatus(double latitude, double longitude) {

             /* boolean isnet = netWork.isNetworkConnected();
        if (!isnet) {
            MyToast.makeTextAnim(getContext(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }*/
        RequestParams params = new RequestParams();
        String head = new JsonUtil(context).httpHeadToJson(context);
        params.put("head", head);
        params.put("adPlace", "ANCHOR_NEAR");
        params.put("cityLongitude", longitude);
        params.put("cityLatitude", latitude);
        HttpUtil.get(Constants.getInstance().adsList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i2, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    ChenQianLog.i("主播附近广告：json：" + jsonObject);

                    if (resultCode != 0) {
                        NearbyBroadcastAdvertisementBean nearbyBroadcastAdvertisementBean = new Gson().fromJson(jsonObject.toString(), NearbyBroadcastAdvertisementBean.class);

                        datalist.clear();
                        datalisturl.clear();
                        List<NearbyBroadcastAdvertisementBean.DataCollectionBean> data = nearbyBroadcastAdvertisementBean.getDataCollection();
                        for (int i = 0; i < data.size(); i++) {

                            datalist.add(data.get(i).getAdPicture());
                            datalisturl.add(data.get(i).getAdTarget());
                        }
                        setAdvertisment();

                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
//                handler.sendEmptyMessage(gotoSeccion);
            }
        });
    }

    /**
     * GridView初始化方法
     */
    private void initGridView() {

        items = new ArrayList<>();
        adapter = new LiveBoadcastNearbyGridViewAdapter(getContext(), items);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NearbyBroadcastBean.DataCollectionBean data = items.get(position);
                Intent intent = new Intent(getContext(), LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", data);
                intent.putExtra("fragmentname", "Nearby");
                startActivity(intent);
            }
        });
    }

    /**
     * 获取附近的主播
     */
    private void getRecommendBroadcast(final int pages) {

       /* boolean isnet = netWork.isNetworkConnected();
        if (!isnet) {
            MyToast.makeTextAnim(getContext(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }*/

        DialogView.show(getContext(), true);
        final RequestParams params = new RequestParams();
        String head = new JsonUtil(context).httpHeadToJson(context);
        params.put("head", head);
        params.put("type", 3);
        params.put("city", city);
        if (pages!=0) {
            params.put("page", pages);
        }
        HttpUtil.post(Constants.getInstance().getAnchor, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    ChenQianLog.i("附近主播：json：" + jsonObject);
                    if (resultCode != 0) {

                        recommendBroadcastdata = new Gson().fromJson(jsonObject.toString(), NearbyBroadcastBean.class);
                        int currentPage = jsonObject.getInt("currentPage");

                        if (currentPage!=0) {

                            if (pages==0) {
                                items.clear();
                                page = 1;
                            }
                            for (NearbyBroadcastBean.DataCollectionBean dataCollectionBean : recommendBroadcastdata.getDataCollection()) {
                                items.add(dataCollectionBean);
                            }

                            adapter.notifyDataSetChanged();
                        }else {
                            MyToast.makeTextAnim(getContext(), "已经加载全部模特", 0, R.style.PopToast).show();

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

                DialogView.dismiss(getContext());
            }
        });
    }


    /**
     * 下拉刷新回调
     */
    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getRecommendBroadcast(0);
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    /**
     * 上拉刷新回调
     */
    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

        page++;
        getRecommendBroadcast(page);
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    private int page;
}
