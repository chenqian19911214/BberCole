package com.bber.company.android.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.BroadcasFragmentClassificationBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.adapter.LiveBroadcastClassificationListViewAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.view.customcontrolview.PullToRefreshLayout;
import com.bber.company.android.widget.MyToast;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类Fragment
 * Created by carlo.c on 2018/4/11.
 */

public class LiveBroadcastClassificationFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {


    private ListView listView;
    private PullToRefreshLayout ptrl;
    private BroadcasFragmentClassificationBean broadcasFragmentClassificationBean;
    private LiveBroadcastClassificationListViewAdapter adapter;
    private List<BroadcasFragmentClassificationBean.DataCollectionBean> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_livebroadcast_classification_layout, container, false);

        getRecommendBroadcast();
        inintView(view);
        initListView();
        return view;
    }

    /**
     * 获取主播
     */
    private void getRecommendBroadcast() {

       /* boolean isnet = netWork.isNetworkConnected();
        if (!isnet) {
            MyToast.makeTextAnim(getContext(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }*/
        DialogView.show(getContext(), true);
        RequestParams params = new RequestParams();
        String head = new JsonUtil(getContext()).httpHeadToJson(getContext());
        params.put("head", head);
        params.put("type", 1);
      //  params.put("page", page);

        HttpUtil.post(Constants.getInstance().getAnchor, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    ChenQianLog.i("分类fragement：json：" + jsonObject);
                    if (resultCode != 0) {

                        broadcasFragmentClassificationBean = new Gson().fromJson(jsonObject.toString(), BroadcasFragmentClassificationBean.class);

                        data.clear();
                        int currentPage = jsonObject.getInt("currentPage");


                        //if (currentPage != 0) {
                        for (BroadcasFragmentClassificationBean.DataCollectionBean dataCollectionBean : broadcasFragmentClassificationBean.getDataCollection()) {

                            data.add(dataCollectionBean);
                        }
                        adapter.notifyDataSetChanged();
                        //} else {
                        //    MyToast.makeTextAnim(getContext(), "已经加载全部模特", 0, R.style.PopToast).show();

                        //}
                        //// initListView();
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
     * 设置 Adapter
     */
    private void initListView() {

        data = new ArrayList<>();
        adapter = new LiveBroadcastClassificationListViewAdapter(getContext(), data);
        listView.setAdapter(adapter);
    }


    private void inintView(View view) {
        ptrl = view.findViewById(R.id.refresh_view);
        ptrl.setOnRefreshListener(this);
        listView = view.findViewById(R.id.content_view);

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

        getRecommendBroadcast();
        adapter.notifyDataSetChanged();
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

       // page++;
     //   getRecommendBroadcast(1);
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

    }

}
