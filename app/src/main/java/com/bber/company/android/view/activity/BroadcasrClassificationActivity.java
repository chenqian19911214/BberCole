package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.BroadcaseClasssificationBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.adapter.LiveBoadcastClassificationGridViewAdapter;
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
 * 主播分类Actiity
 */
public class BroadcasrClassificationActivity extends BaseAppCompatActivity implements PullToRefreshLayout.OnRefreshListener {

    private PullToRefreshLayout pullToRefreshLayout;
    private GridView gridView;
    private String broadcasetype;
    private BroadcaseClasssificationBean broadcaseClasssificationBean;
    private List<BroadcaseClasssificationBean.DataCollectionBean> items;
    private LiveBoadcastClassificationGridViewAdapter adapter;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_broadcasr_classification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
    }

    private void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            broadcasetype = intent.getStringExtra("type");
        }
    }

    /**
     * GridView初始化方法
     */
    private void initGridView() {

        items = new ArrayList<>();
        adapter = new LiveBoadcastClassificationGridViewAdapter(getApplicationContext(), items);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BroadcaseClasssificationBean.DataCollectionBean data = items.get(position);

                Intent intent = new Intent(getApplicationContext(), LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", data);
                intent.putExtra("fragmentname", "broadcastcalssfiftion");
                startActivity(intent);
            }
        });
    }

    private void initView() {
        gridView = findViewById(R.id.nearby_gridview);
        gridView.setNumColumns(2);
        pullToRefreshLayout = findViewById(R.id.refresh_view);
        title.setText(broadcasetype);
        pullToRefreshLayout.setOnRefreshListener(this);
        initGridView();
        getRecommendBroadcast(broadcasetype,0);


    }

    /**
     * 根据标签搜索主播
     */
    private void getRecommendBroadcast(String acType, final  int pages) {

       /* boolean isnet = netWork.isNetworkConnected();
        if (!isnet) {
            MyToast.makeTextAnim(getContext(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }*/

        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        String head = new JsonUtil(getApplicationContext()).httpHeadToJson(getApplicationContext());
        params.put("head", head);
        params.put("type", 4);
        params.put("acType", acType);
        if (pages!=0){
            params.put("page", pages);
        }

        HttpUtil.post(Constants.getInstance().getAnchor, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    int currentPage = jsonObject.getInt("currentPage");

                    ChenQianLog.i("分类主播：json：" + jsonObject);
                    if (resultCode != 0) {
                        broadcaseClasssificationBean = new Gson().fromJson(jsonObject.toString(), BroadcaseClasssificationBean.class);

                        if (currentPage != 0) {

                            if (pages==0){
                                items.clear();
                                page=1;
                            }
                            for (BroadcaseClasssificationBean.DataCollectionBean dataCollectionBean : broadcaseClasssificationBean.getDataCollection()) {

                                items.add(dataCollectionBean);
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            MyToast.makeTextAnim(getApplicationContext(), "已经加载全部模特", 0, R.style.PopToast).show();

                        }

                    }
                } catch (JSONException e) {

                    e.getMessage();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用

                DialogView.dismiss(BroadcasrClassificationActivity.this);
            }
        });
    }

    //下拉回调
    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getRecommendBroadcast(broadcasetype,0);
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    //上拉回调
    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        page++;
        getRecommendBroadcast(broadcasetype ,page);
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }

    private int  page=1;
}
