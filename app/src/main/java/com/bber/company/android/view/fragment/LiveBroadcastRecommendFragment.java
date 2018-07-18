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
import com.bber.company.android.bean.livebroadcast.RecommendBroadcastAdvertisementBean;
import com.bber.company.android.bean.livebroadcast.RecommendBroadcastBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.InterfaceMap;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.view.activity.LiveBroadcastDetailsActivity;
import com.bber.company.android.view.adapter.LiveBoadcastRecommendGridViewAdapter;
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
 * 推荐 Fragment
 * Created by carlo.c on 2018/4/11.
 */

public class LiveBroadcastRecommendFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {

    private BGABanner carousell_figure;
    private GridView gridView;
    private PullToRefreshLayout pullToRefreshLayout;
    private Context context;
    private MapSupport mapSupport;
    private View view;
    private RecommendBroadcastBean recommendBroadcastdata;
    private RecommendBroadcastAdvertisementBean recommendBroadcastAdvertisementBean;
    private List<String> datalist = new ArrayList<>();
    private List<String> dataurl = new ArrayList<>();
    private LiveBoadcastRecommendGridViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getContext();
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_livebroadcast_recommend_layout, container, false);
        initView(view);

        // initGridView();
        ininListener();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView(view);

    }

    private void initView(View view) {
        carousell_figure = view.findViewById(R.id.nearby_carousell_figure_recommend_id);
        gridView = view.findViewById(R.id.nearby_gridview);
        gridView.setNumColumns(2);
        pullToRefreshLayout = view.findViewById(R.id.refresh_view);

        initGridView();
        /**
         * 获取定位信息
         * */
        getLocation();

        getRecommendBroadcast(0);

    }

    /**
     * 设置广告
     */
    private void setAdvertisement() {
        carousell_figure.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, final int position) {
                Glide.with(getContext()).load(model)/*.error(R.mipmap.icon_pay)*/.into(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            goTOView(dataurl.get(position));

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

                getAdStatus(lat, lng);
            }

            @Override
            public void noLocation() {

            }
        });

        mapSupport.startLocation();
    }

    List<RecommendBroadcastBean.DataCollectionBean> items = new ArrayList<>();

    private void ininListener() {

        pullToRefreshLayout.setOnRefreshListener(this);

    }

    /**
     * GridView初始化方法
     */
    private void initGridView() {
        // items = recommendBroadcastdata.getDataCollection();
        adapter = new LiveBoadcastRecommendGridViewAdapter(getContext(), items);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecommendBroadcastBean.DataCollectionBean data = items.get(position);
                Intent intent = new Intent(getContext(), LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", data);
                intent.putExtra("fragmentname", "Recommend");
                startActivity(intent);
            }
        });
    }


    /**
     * 获取广告的状态
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
        params.put("adPlace", "ANCHOR_INDEX");
        params.put("cityLongitude", longitude);
        params.put("cityLatitude", latitude);
        HttpUtil.get(Constants.getInstance().adsList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    ChenQianLog.i("主播推荐广告：json：" + jsonObject);

                    if (resultCode != 0) {
                        recommendBroadcastAdvertisementBean = new Gson().fromJson(jsonObject.toString(), RecommendBroadcastAdvertisementBean.class);
                        List<RecommendBroadcastAdvertisementBean.DataCollectionBean> datajson = recommendBroadcastAdvertisementBean.getDataCollection();

                        datalist.clear();
                        dataurl.clear();
                        for (int j = 0; j < datajson.size(); j++) {

                            datalist.add(datajson.get(j).getAdPicture());
                            dataurl.add(datajson.get(j).getAdTarget());
                        }
                        setAdvertisement();
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
     * 搜索主播
     */
    private void getRecommendBroadcast(final int pagee) {

       /* boolean isnet = netWork.isNetworkConnected();
        if (!isnet) {
            MyToast.makeTextAnim(getContext(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }*/
        DialogView.show(getContext(), true);
        final RequestParams params = new RequestParams();
        String head = new JsonUtil(context).httpHeadToJson(context);
        params.put("head", head);
        params.put("type", 2);
        if (pagee != 0) {
            params.put("page", pagee);
        }

        HttpUtil.post(Constants.getInstance().getAnchor, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    ChenQianLog.i("推荐主播：json：" + jsonObject);
                    if (resultCode != 0) {
                        int currentPage = jsonObject.getInt("currentPage");
                        if (currentPage != 0) {
                            if (pagee == 0) {
                                items.clear();
                                page = 1;
                            }
                            recommendBroadcastdata = new Gson().fromJson(jsonObject.toString(), RecommendBroadcastBean.class);
                            for (RecommendBroadcastBean.DataCollectionBean dataCollectionBean : recommendBroadcastdata.getDataCollection()) {
                                items.add(dataCollectionBean);
                            }
                            adapter.notifyDataSetChanged();
                        } else {

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

        ChenQianLog.i("page：" + page);
        getRecommendBroadcast(page);
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

    }

    private int page;
}
