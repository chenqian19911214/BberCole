package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.InviteFriend;
import com.bber.company.android.bean.VipInfor_Bean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.view.adapter.KeyListAdapter;
import com.bber.company.android.widget.CopyDialog;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KeyActivity extends BaseAppCompatActivity implements View.OnClickListener {

    // public ProgressDialog progressDialog;
    public RequestParams params = new RequestParams();
    private LinearLayout no_data, list_data;
    private RecyclerView recyclerView;
    private KeyListAdapter adapter;
    private String mInviteCode;
    //分享链接
    private String shareUrl;
    private int nextCount;
    private String CashCardNumber;
    private String CashCardName;
    private int intitedFriendsNum;
    private TextView tv_key_invite, tv_show_tip;
    private List<InviteFriend> inviteFriends;
    private TextView month_text, quarter_text, year_text, all_text, month_activity, quarter_activity, year_activity, all_activity;
    private String head;
    private JsonUtil jsonUtil;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_key;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData(getIntent());
    }

    private void initViews() {
        title.setText(R.string.key_tip);
        tv_key_invite = findViewById(R.id.tv_key_invite);

        month_text = findViewById(R.id.month_text);
        quarter_text = findViewById(R.id.quarter_text);
        year_text = findViewById(R.id.year_text);
        all_text = findViewById(R.id.all_text);
        month_activity = findViewById(R.id.month_activity);
        quarter_activity = findViewById(R.id.quarter_activity);
        year_activity = findViewById(R.id.year_activity);
        all_activity = findViewById(R.id.all_activity);

    }

    private void setListeners() {
    }

    //粉红色的按钮点击 ， 复制链接
    public void onClickCopy(View view) {
        CopyDialog copyDialog = new CopyDialog(KeyActivity.this, shareUrl, mInviteCode);
        copyDialog.show();
    }

    private void initData(Intent intent) {
        inviteFriends = new ArrayList<>();
//        adapter = new KeyListAdapter(KeyActivity.this, inviteFriends);
//        recyclerView.setAdapter(adapter);
        Bundle bundle = intent.getExtras();
        bundle.getString("inviteCode");
        mInviteCode = intent.getStringExtra("inviteCode");
        mInviteCode = bundle.getString("inviteCode");
        tv_key_invite.setText("一键分享");
        getVipListInfor();

    }

    /**
     * 获取VIP的信息
     */
    public void getVipListInfor() {
        DialogTool.createProgressDialog(this, true);
        jsonUtil = new JsonUtil(this);
        head = jsonUtil.httpHeadToJson(this);
        String buyerId = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        params.put("userId", buyerId);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getVipList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    String data = jsonObject.getString("dataCollection");
                    VipInfor_Bean vipInforBean = new VipInfor_Bean();
                    List<VipInfor_Bean> vipInforBeanList = jsonUtil.jsonToVipinforBean(data);
                    for (int j = 0; j < vipInforBeanList.size(); j++) {
                        vipInforBean = vipInforBeanList.get(j);
                        if (vipInforBean.getVipId() == 2) { //月费黄金会员
                            String str = "获得<font color='#FE7504'>" + vipInforBean.getReferralsReward() + "</font>元现金";
                            String str1 = "获得<font color='#FE7504'>" + vipInforBean.getInviteeReward() + "</font>个活动点";
                            month_text.setText(Html.fromHtml(str));
                            month_activity.setText(Html.fromHtml(str1));
                        } else if (vipInforBean.getVipId() == 3) { //半年黄金会员
                            String str = "获得<font color='#FE7504'>" + vipInforBean.getReferralsReward() + "</font>元现金";
                            String str1 = "获得<font color='#FE7504'>" + vipInforBean.getInviteeReward() + "</font>个活动点";
                            quarter_text.setText(Html.fromHtml(str));
                            quarter_activity.setText(Html.fromHtml(str1));
                        } else if (vipInforBean.getVipId() == 4) { //年度黄金会员
                            String str = "获得<font color='#FE7504'>" + vipInforBean.getReferralsReward() + "</font>元现金";
                            String str1 = "获得<font color='#FE7504'>" + vipInforBean.getInviteeReward() + "</font>个活动点";
                            year_text.setText(Html.fromHtml(str));
                            year_activity.setText(Html.fromHtml(str1));
                        } else if (vipInforBean.getVipId() == 5) { //终身黄金会员
                            String str = "获得<font color='#FE7504'>" + vipInforBean.getReferralsReward() + "</font>元现金";
                            String str1 = "获得<font color='#FE7504'>" + vipInforBean.getInviteeReward() + "</font>个活动点";
                            all_text.setText(Html.fromHtml(str));
                            all_activity.setText(Html.fromHtml(str1));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(KeyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(KeyActivity.this);
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }
    }
}
