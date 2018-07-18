package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.util.country.BaseCommonProblem;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class CommonProblemActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private BaseCommonProblem data;
    private Button commonProblem_botton;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_common_problem;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(R.string.commonproblem);

        getCommonProblem();

        initView();
    }

    private void initView() {

        recyclerView = findViewById(R.id.commonproblemlistid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));

        commonProblem_botton = findViewById(R.id.commonProblem_botton);
        commonProblem_botton.setOnClickListener(this);
    }

    private void getCommonProblem() {
        DialogView.show(CommonProblemActivity.this, true);

        RequestParams params = new RequestParams();
        final String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        final String session = (String) SharedPreferencesUtils.get(this, Constants.SESSION, "");
        params.put("userId", userId);
        params.put("session", session);
        HttpUtil.post(Constants.commonProblem, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (i == 200) {
                    try {
                        String commondate = new String(bytes, "utf-8");
                        data = new Gson().fromJson(commondate, BaseCommonProblem.class);

                        ChenQianLog.i("qian data: " + commondate);
                        if (data != null) {
                            recyclerView.setAdapter(new RecyViewAdapter(data.getDataCollection()));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                MyToast.makeTextAnim(CommonProblemActivity.this, "数据加载失败", 1, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(CommonProblemActivity.this);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commonProblem_botton:
                startActivity(new Intent(this, WebViewGuestActivity.class));
                break;
        }
    }

    static class RecyViewAdapter extends RecyclerView.Adapter<RecyViewAdapter.ViewHolder> {
        List<BaseCommonProblem.DataCollectionBean> listdata;

        public RecyViewAdapter(List<BaseCommonProblem.DataCollectionBean> listdata) {
            this.listdata = listdata;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commonproblem_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            BaseCommonProblem.DataCollectionBean data = listdata.get(position);
            TextView question = holder.question;
            final TextView answer = holder.answer;
            question.setText(data.getQuestion());
            answer.setText(data.getAnswer());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                boolean isClick = true;

                @Override
                public void onClick(View view) {
                    if (isClick) {
                        isClick = false;
                        answer.setVisibility(View.VISIBLE);
                    } else {
                        isClick = true;
                        answer.setVisibility(View.GONE);

                    }

                }

            });
        }

        @Override
        public int getItemCount() {
            return this.listdata.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView answer;
            private TextView question;

            public ViewHolder(View itemView) {
                super(itemView);
                question = itemView.findViewById(R.id.commonproblemitmequestiontext);
                answer = itemView.findViewById(R.id.commonproblemitmeanswertext);

            }
        }


    }
}
