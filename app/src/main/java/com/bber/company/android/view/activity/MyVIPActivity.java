package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.viewmodel.MessageViewModel;

import java.text.DecimalFormat;


/***
 * 我的钱包  充值体现
 */
public class MyVIPActivity extends BaseAppCompatActivity implements View.OnClickListener, IactionListener<Object> {

    private TextView HistoryList;
    private RelativeLayout rl_recharge, rl_withdraw, rl_bank_card, walert_relatlayout, vash_relat;
    private double money;
    private TextView my_balance;
    private MessageViewModel messageViewModel;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_my_vip;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        HistoryList = toolbar.findViewById(R.id.btn_change);
        rl_recharge = findViewById(R.id.rl_recharge);
        rl_withdraw = findViewById(R.id.rl_withdraw);

        int withdrawStatus = (int) SharedPreferencesUtils.get(this, Constants.WITHDRAWSTATUS, 0);

        if (withdrawStatus == 0) {
            rl_withdraw.setVisibility(View.GONE);
        }
        rl_bank_card = findViewById(R.id.rl_bank_card);
        vash_relat = findViewById(R.id.vash_relat);
        walert_relatlayout = findViewById(R.id.walert_relatlayout);
        my_balance = findViewById(R.id.my_balance);

        messageViewModel = new MessageViewModel(this);
        messageViewModel.setActionListener(this);
    }

    private void setListeners() {
        HistoryList.setOnClickListener(this);
        rl_recharge.setOnClickListener(this);
        rl_withdraw.setOnClickListener(this);
        rl_bank_card.setOnClickListener(this);
        vash_relat.setOnClickListener(this);
        walert_relatlayout.setOnClickListener(this);
    }

    private void initData() {
        title.setText(R.string.my_wallet);
        HistoryList.setVisibility(View.VISIBLE);
        HistoryList.setText(R.string.my_bill_detail);
//        money = getIntent().getDoubleExtra("money",0);
        String userMoney = (String) SharedPreferencesUtils.get(this, Constants.USER_MONEY, "");
        money = Double.parseDouble(userMoney);
        String format = new DecimalFormat("0.00").format(money);
        my_balance.setText(format);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //我的会员
            case R.id.btn_change:
                intent = new Intent(MyVIPActivity.this, PayBillListActivity.class);
                startActivity(intent);
                break;
            //购买会员
            case R.id.rl_recharge:
                intent = new Intent(MyVIPActivity.this, Buy_vipActivity.class);
                startActivity(intent);
                break;
            //钱包充值
            case R.id.walert_relatlayout:
                intent = new Intent(MyVIPActivity.this, PayInputMoneyActivity.class);
                startActivity(intent);
                break;
            //现金券
            case R.id.vash_relat:
                intent = new Intent(MyVIPActivity.this, VoucherActivity.class);
                startActivity(intent);
                break;
            //游戏币提现
            case R.id.rl_withdraw:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isBank", false);
                intent = new Intent(MyVIPActivity.this, BankCardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
//                MyToast.makeTextAnim(MyWalletActivity.this, R.string.function_nono, 0, R.style.PopToast).show();
                break;
            //常见问题
            case R.id.rl_bank_card:
                intent = new Intent(MyVIPActivity.this, CommonProblemActivity.class);
                bundle = new Bundle();
                bundle.putBoolean("isHaveValue", true);
                intent.putExtras(bundle);
                startActivity(intent);
//                messageViewModel.getGrilID();
//                MyToast.makeTextAnim(MyVIPActivity.this, "联系客服", 0, R.style.PopToast).show();
                break;
        }
    }

    @Override
    public void SuccessCallback(Object o) {
        //当联系客服的时候，返回一个客服的SellerUserVo对象让我跳转chatActivity
        if (o instanceof SellerUserVo) {
            Intent intent = new Intent(MyVIPActivity.this, ChatActivity.class);
            intent.putExtra("sellerUserVo", (SellerUserVo) o);
            intent.putExtra("fromProfile", false);
            startActivity(intent);
        }
    }

    @Override
    public void FailCallback(String result) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
