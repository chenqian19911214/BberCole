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
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.DBcolumns;
import com.bber.company.android.db.SellerUserDao;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.view.adapter.HistoryModelListAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Author: Bruce
 * Date: 2016/5/9
 * Version:
 * Describe:
 */
public class HistroyActivity extends BaseAppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout view_no_model;
    private HistoryModelListAdapter adapter;
    private List<SellerUserVo> sellerUser;
    private SellerUserDao sellerUseDao;
    private int offset;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_HISTORY)) {
                initData();
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();

    }

    private void initViews() {
        view_no_model = findViewById(R.id.view_no_history);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
        sellerUseDao = new SellerUserDao(this);
    }

    public void initData() {
        title.setText(R.string.model_history_list);
        offset = 0;
        sellerUser = sellerUseDao.queryTableItem(DBcolumns.TABLE_MATCH_HISTORY, offset, 10);
        offset = sellerUser.size();
        adapter = new HistoryModelListAdapter(this, sellerUser);
        adapter.setOnItemClickListener(new HistoryModelListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, SellerUserVo sellerUserVo) {
                Intent intent = new Intent(HistroyActivity.this, GirlProfileActivity.class);
                intent.putExtra("from", Constants.COME_FROM_HISTORY_LIST);
                intent.putExtra("sellerId", sellerUserVo.getuSeller());
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new HistoryModelListAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final SellerUserVo data) {
                LayoutInflater inflater = LayoutInflater.from(HistroyActivity.this);
                View layout = inflater.inflate(R.layout.custom_alertdialog_del, null);
                TextView dialogText = layout.findViewById(R.id.dialog_del);
                dialogText.setText(R.string.delete_this_model);
                final AlertDialog dialog = DialogTool.createDel(HistroyActivity.this, layout);
                layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        sellerUseDao.deleteTableItemById(DBcolumns.TABLE_MATCH_HISTORY, data.getuSeller().toString());
                        sellerUser.remove(data);
                        adapter.notifyDataSetChanged();
                        setView();
                    }
                });
            }
        });
        setView();
        registerBoradcastReceiver();
    }

    private void setView() {
        if (sellerUser != null && sellerUser.size() == 0) {
            view_no_model.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_model.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_HISTORY);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
