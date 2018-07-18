package com.bber.company.android.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.bean.DiscussBean;
import com.bber.company.android.bean.DiscussDetailBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.activity.BrowseImageActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.view.adapter.DiscussPicAdapter;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.view.XRefreshView;
import com.bber.customview.view.XRefreshViewFooter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONArray;
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
public class DiscussPicFragment extends BaseFragment implements View.OnClickListener {

    protected NetWork netWork;
    private View view;
    private MainActivity mainActivity;
    private LinearLayout view_no_item;
    private RecyclerView recyclerView;
    private DiscussPicAdapter adapter;
    private List<DiscussBean> discussList;
    private int m_Page = 1;
    private int m_Rows = 15;
    private int m_ClickID;
    private XRefreshView xRefreshView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discusspic, null);
        mainActivity = (MainActivity) getActivity();
        initViews();
        initData();
        return view;
    }

    private void initViews() {
        toolbar = view.findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        view_no_item = view.findViewById(R.id.view_no_item);
        xRefreshView = view.findViewById(R.id.xrefreshview);

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mainActivity)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
    }

    public void initData() {
        netWork = new NetWork(mainActivity);
        title.setText(R.string.discusspic);
        discussList = new ArrayList<>();
        adapter = new DiscussPicAdapter(getActivity(), discussList);

        recyclerView.setAdapter(adapter);

        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(mainActivity));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
                getTalkPicture(0);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                getTalkPicture(1);
            }
        });


        getTalkPicture(0);

        adapter.setOnItemClickListener(new DiscussPicAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, DiscussBean data) {
                List<String> list = new ArrayList<>();
                for (DiscussDetailBean discuss : data.getImg()) {
                    if (!Tools.isEmpty(discuss.getSource())) {
                        list.add(discuss.getSource());
                    }
                }
                int size = list.size();
                String[] images = list.toArray(new String[size]);

                Intent intent = new Intent(mainActivity, BrowseImageActivity.class);
                intent.putExtra("images", images);
                intent.putExtra("currentIndex", 0);
                intent.putExtra("discussBean", data);
                m_ClickID = data.getId();
                startActivity(intent);
                addReadTimes();
            }
        });
        setViewList();
    }

    /**
     * 添加聊图
     *
     * @param type
     */
    private void getTalkPicture(final int type) {
        if (type == 0) {
            m_Page = 1;
        } else {
            m_Page++;
        }
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(mainActivity).httpHeadToJson(mainActivity);
        final JsonUtil jsonUtil = new JsonUtil(mainActivity);
        String buyerId = SharedPreferencesUtils.get(mainActivity, Constants.USERID, "-1") + "";
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("page", m_Page);
        params.put("rows", m_Rows);
        HttpUtil.post(Constants.getInstance().getTalkPicture, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(mainActivity, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    List<DiscussBean> discussBeen = jsonUtil.jsonToDiscussBean(dataCollection.toString());
                    setView(type, discussBeen);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(mainActivity, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 添加聊图
     */
    private void addReadTimes() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(mainActivity, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(mainActivity).httpHeadToJson(mainActivity);
        params.put("head", head);
        params.put("id", m_ClickID);
        HttpUtil.post(Constants.getInstance().addReadTimes, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(mainActivity, jsonObject, null)) {
                    return;
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

    private void setView(int type, List<DiscussBean> discussBean) {

        if (type == 0) {//刷新列表
            xRefreshView.stopRefresh();
            discussList.clear();
        } else {
            xRefreshView.stopLoadMore();
        }
        if (discussBean != null) {
            discussList.addAll(discussBean);
        }
        if (discussList != null && discussList.size() == 0) {
            view_no_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_item.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.updateListView(discussList);
    }

    private void setViewList() {
        if (discussList != null && discussList.size() == 0) {
            view_no_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_item.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.updateListView(discussList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }

}
