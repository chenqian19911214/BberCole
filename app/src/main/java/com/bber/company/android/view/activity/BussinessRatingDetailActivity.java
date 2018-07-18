package com.bber.company.android.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.bean.BusinessRatingBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.view.adapter.RatingListAdapter;
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


public class BussinessRatingDetailActivity extends BaseAppCompatActivity {

    private final int LOAD_MORE = 1;
    private final int LOAD_FRESH = 0;
    private RecyclerView mRV_Rating;
    private RatingListAdapter mRatingAdapter;
    private List<BusinessRatingBean> mRatingList;
    private int shopId;
    private int page = 1;
    private int pageNumber = 15;
    private XRefreshView xRefreshView;
    private LinearLayout view_no_item;


    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_business_rating_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.rating_business);
        mRV_Rating = findViewById(R.id.rv_rating);
        xRefreshView = findViewById(R.id.xrefreshview);
        view_no_item = findViewById(R.id.view_no_item);
    }

    private void setListeners() {
    }

    private void initData() {

        mRatingList = new ArrayList<>();
        mRatingAdapter = new RatingListAdapter(this, mRatingList);
        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(true);
        mRatingAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
                getShopCommentList(LOAD_FRESH);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                getShopCommentList(LOAD_MORE);
            }
        });

        shopId = getIntent().getIntExtra("shopid", 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRV_Rating.setLayoutManager(layoutManager);
        mRV_Rating.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());

        //获取评论列表
        getShopCommentList(LOAD_FRESH);

        mRV_Rating.setAdapter(mRatingAdapter);

    }

    /**
     * 获取商户评论列表
     */

    private void getShopCommentList(final int type) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (type == 0) {
            page = 1;
            mRatingList.clear();
        } else {
            page++;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("page", page);
        params.put("pageNumber", pageNumber);
        params.put("shopId", shopId);
        HttpUtil.post(Constants.getInstance().getShopCommentList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    List<BusinessRatingBean> newBusinessList = jsonUtil.jsonToBusinessRatingBean(dataCollection);
                    setView(type, newBusinessList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(BussinessRatingDetailActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }


    private void setView(int type, List<BusinessRatingBean> businessList) {
        if (type == LOAD_FRESH) {//刷新列表
            xRefreshView.stopRefresh();
        } else {
            xRefreshView.stopLoadMore();
        }
        if (businessList != null) {
            mRatingList.addAll(businessList);
        }
        if (mRatingList != null && mRatingList.size() == 0) {
            view_no_item.setVisibility(View.VISIBLE);
            mRV_Rating.setVisibility(View.GONE);
        } else {
            mRV_Rating.setVisibility(View.VISIBLE);
            view_no_item.setVisibility(View.GONE);
            mRatingAdapter.update(mRatingList);
        }
    }

}
