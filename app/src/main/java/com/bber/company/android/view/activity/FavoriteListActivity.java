package com.bber.company.android.view.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.BusinessBean;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.DBcolumns;
import com.bber.company.android.db.SellerUserDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.adapter.BusinessModelAdapter;
import com.bber.company.android.view.adapter.FavoriteModelAdapter;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class FavoriteListActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private final int SHOW_MODEL = 0;
    private final int SHOW_SHOP = 1;
    private RecyclerView recyclerView;
    private LinearLayout view_no_favorite;
    private FavoriteModelAdapter mModelAdapter;
    private BusinessModelAdapter mBusinessAdapter;
    private List<SellerUserVo> sellerUser;
    private List<BusinessBean> mBusinessList;
    private SellerUserDao sellerUseDao;
    private int offset;
    private TextView tv_model, tv_shop;
    /**
     * 注册用户
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_FAVORITE)) {
                initData();
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_favorite;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initData();
        setListeners();

    }

    private void setListeners() {
        tv_model.setOnClickListener(this);
        tv_shop.setOnClickListener(this);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        view_no_favorite = findViewById(R.id.view_no_favorite);
        recyclerView = findViewById(R.id.recyclerView);
        tv_shop = findViewById(R.id.tv_shop);
        tv_model = findViewById(R.id.tv_model);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
    }

    public void initData() {
        title.setText(R.string.my_favarite);
        offset = 0;
        sellerUseDao = new SellerUserDao(this);
        sellerUser = sellerUseDao.queryTableItem(DBcolumns.TABLE_FAVORITES, offset, 10);
        offset = sellerUser.size();
        mBusinessAdapter = new BusinessModelAdapter(mBusinessList);
        mModelAdapter = new FavoriteModelAdapter(this, sellerUser);

        tv_model.setBackgroundResource(R.mipmap.image_sliding_block);
        tv_model.setTextColor(getResources().getColor(R.color.pink));

        mBusinessAdapter.setOnItemClickListener(new BusinessModelAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, BusinessBean data) {
                data.setId(data.getShopId());
                Intent intent = new Intent(FavoriteListActivity.this, BussinessDetailActivity.class);
                intent.putExtra("isFavorite", true);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        mModelAdapter.setOnItemClickListener(new FavoriteModelAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, SellerUserVo sellerUserVo) {
                Intent intent = new Intent(FavoriteListActivity.this, GirlProfileActivity.class);
                intent.putExtra("from", Constants.COME_FROM_HISTORY_LIST);
                intent.putExtra("sellerId", sellerUserVo.getuSeller());
                startActivity(intent);
            }
        });

        mModelAdapter.setOnItemLongClickListener(new FavoriteModelAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final SellerUserVo data) {
                LayoutInflater inflater = LayoutInflater.from(FavoriteListActivity.this);
                View layout = inflater.inflate(R.layout.custom_alertdialog_del, null);
                TextView dialogText = layout.findViewById(R.id.dialog_del);
                dialogText.setText(R.string.cancel_this_model);
                final AlertDialog dialog = DialogTool.createDel(FavoriteListActivity.this, layout);
                layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sellerUseDao.deleteTableItemById(DBcolumns.TABLE_FAVORITES, data.getuSeller().toString());
                        sellerUser.remove(data);
                        mModelAdapter.notifyDataSetChanged();
                        setView(SHOW_MODEL);
                    }
                });
            }
        });
        setView(SHOW_MODEL);

        registerBoradcastReceiver();
        getShopFavoriteById();
    }

    /**
     * 设置
     */
    private void setView(int type) {
        if (type == 0) {
            if (sellerUser != null && sellerUser.size() == 0) {
                view_no_favorite.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                view_no_favorite.setVisibility(View.GONE);
                recyclerView.setAdapter(mModelAdapter);
                mModelAdapter.updateListView(sellerUser);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mBusinessList != null && mBusinessList.size() == 0) {
                view_no_favorite.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                view_no_favorite.setVisibility(View.GONE);
                recyclerView.setAdapter(mBusinessAdapter);
                mBusinessAdapter.updateListView(mBusinessList);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 注册用户
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_FAVORITE);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onClick(View v) {
        setAllBackGround();
        switch (v.getId()) {
            case R.id.tv_shop:
                tv_shop.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_shop.setTextColor(getResources().getColor(R.color.pink));
                recyclerView.setAdapter(mBusinessAdapter);
                setView(SHOW_SHOP);
                break;
            case R.id.tv_model:
                tv_model.setBackgroundResource(R.mipmap.image_sliding_block);
                tv_model.setTextColor(getResources().getColor(R.color.pink));
                recyclerView.setAdapter(mModelAdapter);
                setView(SHOW_MODEL);
                break;
        }
    }

    /**
     *
     */
    private void setAllBackGround() {
        tv_model.setBackgroundResource(R.color.transparent);
        tv_shop.setBackgroundResource(R.color.transparent);
        tv_model.setTextColor(getResources().getColor(R.color.main_text));
        tv_shop.setTextColor(getResources().getColor(R.color.main_text));
    }


    /**
     * 获取商户评论列表
     */

    private void getShopFavoriteById() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        HttpUtil.post(Constants.getInstance().getShopFavoriteById, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    mBusinessList = jsonUtil.jsonToBusinessBean(dataCollection);
                } catch (JSONException e) {
                    e.printStackTrace();
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
}
