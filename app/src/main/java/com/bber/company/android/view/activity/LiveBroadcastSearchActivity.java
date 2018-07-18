package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.BroadcaseLabelBean;
import com.bber.company.android.bean.livebroadcast.BroadcaseSearchSingleBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.country.ListCache;
import com.bber.company.android.util.country.SpLocalCache;
import com.bber.company.android.view.adapter.BroadcastSearchGridViewAdapter;
import com.bber.company.android.view.adapter.BroadcastSearchReyclerViewAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
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
 * 主播搜索 Activivy
 */
public class LiveBroadcastSearchActivity extends BaseAppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private EditText search_edit_id;
    private TextView search_button;
    private RecyclerView search_recyvlerview;
    private GridView search_grviewview;
    private BroadcaseLabelBean broadcaseLabelBean;

    private BroadcaseSearchSingleBean broadcaseSearchSingleBean;
    private List<BroadcaseSearchSingleBean.DataCollectionBean> listdata;
    private List<BroadcaseSearchSingleBean.DataCollectionBean> modelList = new ArrayList<>();

    private BroadcaseSearchSingleBean.DataCollectionBean dataCollectionBean;
    private SpLocalCache spLocalCache;
    private BroadcastSearchReyclerViewAdapter reyclerViewAdapter;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_live_broadcast_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_live_broadcast_search);
        anchorInfo();
        initView();
        initListener();
    }

    private void initListener() {
        search_button.setOnClickListener(this);
    }

    private void initView() {

        listdata = new ArrayList<>();
        spLocalCache = new SpLocalCache(ListCache.class, BroadcaseSearchSingleBean.DataCollectionBean.class);

        ListCache<BroadcaseSearchSingleBean.DataCollectionBean> mListCache = new ListCache<>();
        mListCache.setObjList(modelList);

        spLocalCache.read(getApplicationContext(), new SpLocalCache.LocalCacheCallBack() {
            @Override
            public void readCacheComplete(Object obj) {
               List<BroadcaseSearchSingleBean.DataCollectionBean>  slistdatas = (List<BroadcaseSearchSingleBean.DataCollectionBean>) obj;
              //  listdata.clear();
                for (BroadcaseSearchSingleBean.DataCollectionBean data:slistdatas) {
                    listdata.add(data);
                }
                reyclerViewAdapter.notifyDataSetChanged();
            }
        });

        search_edit_id = findViewById(R.id.search_edit_id);
        search_button = findViewById(R.id.search_button);
        search_recyvlerview = findViewById(R.id.search_recyvlerview);
        search_grviewview = findViewById(R.id.search_grviewview);
        search_grviewview.setOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        search_recyvlerview.setLayoutManager(linearLayoutManager);
        //设置 Adapter
        reyclerViewAdapter = new BroadcastSearchReyclerViewAdapter(getApplicationContext(), listdata);
        search_recyvlerview.setAdapter(reyclerViewAdapter);

        reyclerViewAdapter.setOnItemClickListener(new BroadcastSearchReyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(LiveBroadcastSearchActivity.this, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", listdata.get(position));
                intent.putExtra("fragmentname", "search");
                startActivity(intent);
            }
        });
    }

    private void anchorInfo() {
        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().anchorInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i1, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("获取主播标签：" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        broadcaseLabelBean = new Gson().fromJson(jsonObject.toString(), BroadcaseLabelBean.class);
                        search_grviewview.setAdapter(new BroadcastSearchGridViewAdapter(getApplicationContext(), broadcaseLabelBean.getDataCollection()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUserInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(getApplicationContext(), R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogView.dismiss(LiveBroadcastSearchActivity.this);
            }
        });
    }

    private void submit() {
        // validate
        String searchtext = search_edit_id.getText().toString().trim();
        if (TextUtils.isEmpty(searchtext)) {
            Toast.makeText(this, "搜索最近浏览", Toast.LENGTH_SHORT).show();
            return;
        }
        //搜索主播
        getRecommendBroadcast(5, searchtext);

    }


    //GridView 监听
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String type = broadcaseLabelBean.getDataCollection().get(i);
        Intent intent = new Intent(getApplicationContext(), BroadcasrClassificationActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * 获取搜索主播 信息
     */
    private void getRecommendBroadcast(int type, String str) {
       /* boolean isnet = netWork.isNetworkConnected();
        if (!isnet) {
            MyToast.makeTextAnim(getContext(), R.string.no_network, 0, R.style.PopToast).show();
            return;
        }*/
        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        String head = new JsonUtil(getApplicationContext()).httpHeadToJson(getApplicationContext());
        params.put("head", head);
        params.put("type", type);
        params.put("keyword", str);
        HttpUtil.post(Constants.getInstance().getAnchor, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    ChenQianLog.i("搜索界面：json：" + jsonObject);
                    if (resultCode != 0) {

                        broadcaseSearchSingleBean = new Gson().fromJson(jsonObject.toString(), BroadcaseSearchSingleBean.class);
                        int currentPage = broadcaseSearchSingleBean.getCurrentPage();
                        if (currentPage == 0) {
                            MyToast.makeTextAnim(getApplicationContext(), broadcaseSearchSingleBean.getResultMessage(), 0, R.style.PopToast).show();
                        } else {

                            dataCollectionBean = broadcaseSearchSingleBean.getDataCollection().get(0);
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), LiveBroadcastDetailsActivity.class);
                            intent.putExtra("fragmentname", "search");
                            intent.putExtra("data", dataCollectionBean);

                            listdata.add(0, dataCollectionBean);

                            spLocalCache.save(getApplicationContext(), listdata);

                            spLocalCache.read(getApplicationContext(), new SpLocalCache.LocalCacheCallBack() {
                                @Override
                                public void readCacheComplete(Object obj) {
                                    listdata.clear();
                                    List<BroadcaseSearchSingleBean.DataCollectionBean>  slistdatas = (List<BroadcaseSearchSingleBean.DataCollectionBean>) obj;

                                    for (BroadcaseSearchSingleBean.DataCollectionBean data:slistdatas) {
                                        listdata.add(data);
                                    }

                                    reyclerViewAdapter.notifyDataSetChanged();
                                }
                            });

                            startActivity(intent);
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

                DialogView.dismiss(LiveBroadcastSearchActivity.this);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.search_button:
                submit();
                break;
        }
    }
}
