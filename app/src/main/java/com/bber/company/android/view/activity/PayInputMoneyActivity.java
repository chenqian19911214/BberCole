package com.bber.company.android.view.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.ChzfBean;
import com.bber.company.android.bean.PayQueryBean;
import com.bber.company.android.bean.VipInforBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.adapter.PayInputMoneryListViewAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.view.customcontrolview.ListViewPay;
import com.bber.company.android.viewmodel.WalletViewModel;
import com.bber.company.android.widget.MyToast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 充值方式
 */
public class PayInputMoneyActivity extends BaseAppCompatActivity implements View.OnClickListener
        , AdapterView.OnItemClickListener {

    public boolean isQQ;
    private RelativeLayout rl_bankcard;
    private EditText et_extra_phone;
    private String payName;
    private int payCode, iconResId;
    private TextView tv_payLimit;
    private EditText et_money_text;
    private ImageView img_payWay;
    private Button btn_payNext;
    private String payUrl;
    private Double payMoney;
    private int payMaxUnionpay = 0, payMaxAlipay = 0, payMaxWechat = 0;
    private String isUnionpay, isPayAlipay, isPayWechat;
    private int payMax = 0;
    private PayQueryBean payQueryBean;
    private Double monet, balance;
    /**
     * 判断是充值还开通会员
     */
    private boolean isUnion = true;
    private WalletViewModel viewModel;
    private ListViewPay payListveiw;
    private ScrollView scroll_viewid;
    private PayInputMoneryListViewAdapter payInputMoneryListViewAdapter;
    private List<PayQueryBean.DataCollectionBean> listviewdata;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_payinput;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new WalletViewModel(this);

        getpayisSuer();
        viewModel.setActionListener(new IactionListener() {
            @Override
            public void SuccessCallback(Object o) {

                //如果返回的是头部数据
                if (o instanceof VipInforBean) {
                  /*  vip_textview1.setText("¥" + viewModel.viplist.get(0).getVipDisMoney());
                    vip_textview6.setText("¥" + viewModel.viplist.get(1).getVipDisMoney());
                    vip_textview12.setText("¥" + viewModel.viplist.get(2).getVipDisMoney());
                    glod_vip_textview.setText("¥" + viewModel.viplist.get(3).getVipDisMoney());*/

                }
                if (o instanceof String) {
                    Intent intent;
                    if (((String) o).contains(".jpg")) {
                        intent = new Intent(PayInputMoneyActivity.this, ImageViewActivity.class);
                    } else {
                        intent = new Intent(PayInputMoneyActivity.this, webViewActivity.class);
                        if (isUnion) {
                            intent.putExtra("hiding", "1");
                        }
                    }
                    intent.putExtra("url", (String) o);
                    intent.putExtra("payCode", viewModel.payWays);
                    intent.putExtra("activityname", "购买会员");
                    startActivity(intent);
                }
            }

            @Override
            public void FailCallback(String result) {

            }
        });

    }

    /**
     * 移除掉不可以的钱包 item
     */
    private void removeConnotPay(List<PayQueryBean.DataCollectionBean> list, String str) {

        for (int i = list.size(); i > 0; i--) {
            if (list.get(i - 1).getPayFlag().equals(str)) {
                listviewdata.remove(i - 1);
            }
        }
    }


    /**
     * 从网络获取 可用的钱包
     */
    private void getpayisSuer() {
        RequestParams params = new RequestParams();
        final String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        final String session = (String) SharedPreferencesUtils.get(this, Constants.SESSION, "");
        params.put("userId", userId);
        params.put("session", session);
        DialogView.show(this, true);
        HttpUtil.post(Constants.getpayissuer, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                if (i == 200) {
                    try {
                        String requst = new String(bytes, "utf-8");

                        ChenQianLog.i("getpayissuerjson:" + requst);

                        payQueryBean = new Gson().fromJson(requst, PayQueryBean.class);
                        listviewdata = payQueryBean.getDataCollection();

                        if (payQueryBean.getResultCode() == 1 && listviewdata != null) {


                            removeConnotPay(listviewdata, "-1");

                            initViews();
                            setListeners();
                            initData();

                            //去除不可用的钱包

                            payInputMoneryListViewAdapter = new PayInputMoneryListViewAdapter(getApplicationContext(), listviewdata);

                            payInputMoneryListViewAdapter.setMenuItemsIsSelect(getMenuItemsSelectState());

                            payListveiw.setAdapter(payInputMoneryListViewAdapter);

                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                MyToast.makeTextAnim(PayInputMoneyActivity.this, R.string.enough_money, 0, R.style.PopToast).show();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogView.dismiss(PayInputMoneyActivity.this);

            }
        });
    }


    /**
     * 选择钱包默认的图标
     */
    private boolean[] getMenuItemsSelectState() {

        boolean[] selsctStates = new boolean[listviewdata.size()];
        int i = 1;
        selsctStates[0] = true;
        selsctStates[i++] = false;
        return selsctStates;
    }


    private void initViews() {
        payCode = Constants.PAYDEFAULT;
        payListveiw = findViewById(R.id.payintlistviewid);
        title = toolbar.findViewById(R.id.title);
        rl_bankcard = findViewById(R.id.rl_bankcard);
        img_payWay = findViewById(R.id.img_payWay);
        btn_payNext = findViewById(R.id.btn_payNext);
        et_money_text = findViewById(R.id.et_money);
        tv_payLimit = findViewById(R.id.tv_payLimit);
        scroll_viewid = findViewById(R.id.scroll_viewid);

        scroll_viewid.smoothScrollBy(0, 0);


        /**
         * 购买会员
         * 前面传过来的数据
         * */

        monet = getIntent().getDoubleExtra("viewModel", 0);
        int type = getIntent().getIntExtra("mType", 0);
        int VipId = getIntent().getIntExtra("VipId", 0);
        balance = getIntent().getDoubleExtra("balance", 0);

        if (monet != 0) {
            goSetViewMode(type, VipId);
        } else {
            removePayItem();

        }

    }

    /**
     * 设置 从购买会员的参数
     */
    private void goSetViewMode(int type, int vipId) {
        viewModel.mType = type;
        viewModel.money = monet;
        viewModel.payVipId = vipId;
        String paymaxtext = "单笔限额：5000元";
        tv_payLimit.setText(paymaxtext);
        et_money_text.setText(monet + "");
        et_money_text.setEnabled(false);
        et_money_text.setFocusable(false);
    }

    /**
     * 移除钱包item
     */
    private void removePayItem() {
        int bar = 0;

        ChenQianLog.i("item qian3:" + listviewdata.size());
        listviewdata.size();
        for (PayQueryBean.DataCollectionBean listviewdataitem : listviewdata) {
            String paynaem = listviewdataitem.getPayName();
            if (!paynaem.equals("钱包")) {
                bar++;
            } else {
                break;
            }
        }
        listviewdata.remove(bar);

        ChenQianLog.i("item qian4:" + listviewdata.size());
    }

    private void setListeners() {
        btn_payNext.setOnClickListener(this);
        payListveiw.setOnItemClickListener(this);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case 1:
//                    payCode = data.getIntExtra("payCode", 0);
//                    payName = data.getStringExtra("payName");
//                    iconResId = data.getIntExtra("iconResId", R.mipmap.icon_pay);
//                    tv_carName.setText(payName);
//                    img_payWay.setImageResource(iconResId);
//                    //     if (payCode == Constants.ALIPAY_CODE){
//                    //         ll_extra_item.setVisibility(View.VISIBLE);
//                    //     }else{
////                        ll_extra_item.setVisibility(View.GONE);
//                    //      }
//
//                    setPayMaxTextView();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//    }

    private void initData() {
        // getPayMax();
        title.setText(R.string.payMode);
        payMoney = getIntent().getDoubleExtra(Constants.PAY_MONEY, 0);
        if (payMoney > 0) {
            et_money_text.setText(payMoney + "");
            et_money_text.setEnabled(false);
        }
    }

    /**
     * 充值
     * 购买VIP
     * *
     */
    private void payCMoney(final int code, String note, double money) {


        ChenQianLog.i("购买VIP:" + code);
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }

        DialogView.show(PayInputMoneyActivity.this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        final String userId = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        params.put("head", head);
        params.put("buyerUserId", userId);
        params.put("money", money);
        params.put("code", code);
        params.put("note", note);
        final String str = userId + "" + money + "" + code + "" + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().payCMoney, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                if (Tools.jsonResult(PayInputMoneyActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    Log.e("", "支付返回的dataCollection : " + dataCollection.toString());
                    String status = dataCollection.getString("status");
                    String massage = dataCollection.getString("massage");
                    if ("1".equals(status)) {
                        payUrl = massage;
                        if (payUrl == null || payUrl.length() == 0) {
                            return;
                        }
                        Intent intent = new Intent(PayInputMoneyActivity.this, webViewActivity.class);
                        intent.putExtra("url", payUrl);
                        intent.putExtra("activityname", "充值");
                        intent.putExtra("payCode", code);
                        intent.putExtra("isQQ", isQQ);
                        if (code == Constants.UNIONPAY_SCAN_CODE || code == Constants.UNIONPAY_CODE) {
                            intent.putExtra("hiding", "1");
                        }
                        startActivity(intent);
                    } else if (status.equals("2")) {

                        String path = SharedPreferencesUtils.get(PayInputMoneyActivity.this, "IMAGE_FILE", "") + massage;
                        Intent intent;
                        if (path.contains(".jpg")) {
                            intent = new Intent(PayInputMoneyActivity.this, ImageViewActivity.class);
                        } else {
                            intent = new Intent(PayInputMoneyActivity.this, webViewActivity.class);
                        }

                        intent.putExtra("url", path);
                        intent.putExtra("activityname", "充值");
                        intent.putExtra("payCode", code);
                        intent.putExtra("isQQ", isQQ);
                        startActivity(intent);
                    } else if (status.equals("3")) {
                        if (dataCollection.has("chzfMessage")) { //所有h5支付使用之前诚和支付的参数名
                            String chzfMessage = dataCollection.getString("chzfMessage");
                            ChzfBean chzfBean = jsonUtil.jsonTochzf(chzfMessage);
                            Intent intent;
                            intent = new Intent(PayInputMoneyActivity.this, webViewPostActivity.class);
                            //获得数据的URL
                            intent.putExtra("url", chzfBean.chzf_url);
                            intent.putExtra("hiding", "1");
                            startActivity(intent);
                        }
                    } else {
                        MyToast.makeTextAnim(PayInputMoneyActivity.this, massage, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                MyToast.makeTextAnim(PayInputMoneyActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogView.dismiss(PayInputMoneyActivity.this);
            }
        });
    }

    /***
     * 成河支付
     * @param chzfBean
     */
    private String setCHZFPay(ChzfBean chzfBean) {
        String CHZFPay = "body=" + chzfBean.body + "&ip=" + chzfBean.ip + "&money=" + chzfBean.money + "&notify_url=" + chzfBean.notify_url
                + "&pay_type=" + chzfBean.pay_type + "&payment_id=" + chzfBean.payment_id + "&return_url=" + chzfBean.return_url
                + "&time=" + chzfBean.time + "&user_id=" + chzfBean.user_id + "&version=" + chzfBean.version
                + "&sign=" + chzfBean.sign + "&remark=" + chzfBean.remark;
        return CHZFPay;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_bankcard:
                //跳转选择
                intent = new Intent(PayInputMoneyActivity.this, PayModeActivity.class);
                intent.putExtra("isUnionpay", isUnionpay);
                intent.putExtra("isPayAlipay", isPayAlipay);
                intent.putExtra("isPayWechat", isPayWechat);
                startActivityForResult(intent, 1);
                break;

            case R.id.btn_payNext:

                if (payCode == 999999) { //人工充值

                    Intent intent1 = new Intent(PayInputMoneyActivity.this, WebViewGuestActivity.class);
                    startActivity(intent1);

                } else {

                    if (monet != 0) { //购买会员

                        if (payCode == 888888) { //使用钱包购买

                            ChenQianLog.i("viewModel.balance::" + balance + "  monet:" + monet);
                            if (balance < monet) {
                                MyToast.makeTextAnim(this, R.string.wallet_no_enough_money, 0, R.style.PopToast).show();
                            } else {
                                DialogTool tool = new DialogTool();
                                LayoutInflater inflater = LayoutInflater.from(this);
                                View layout = inflater.inflate(R.layout.alertdialog_pay, null);
                                tool.createDialogStart(this, layout, R.string.cancal, R.string.sure);
                                tool.setActionListener(new IactionListener() {
                                    @Override
                                    public void SuccessCallback(Object o) {
                                        viewModel.buyerUserVipByBalance(PayInputMoneyActivity.this);
                                    }

                                    @Override
                                    public void FailCallback(String result) {
                                    }
                                });
                            }
                        } else {
                            viewModel.rechargeVip(PayInputMoneyActivity.this, payCode);
                        }

                    } else { //钱包充值

                        final String note = "";
                        final Double money = Tools.StringToDouble(et_money_text.getText() + "");

                        if (payCode == 0) {
                            MyToast.makeTextAnim(this, R.string.choose_pay_mothed, 0, R.style.PopToast).show();
                            return;
                        }
                        if (payMax < money) {
                            MyToast.makeTextAnim(this, R.string.pay_over_max, 0, R.style.PopToast).show();
                            return;
                        }
                        if (money < 50) {
                            MyToast.makeTextAnim(this, R.string.pay_money, 0, R.style.PopToast).show();
                            return;
                        }
                        payCMoney(payCode, note, money);

                    }
                }


                break;
        }
    }


    /**
     * 设置单笔限额的参数
     * chen
     */
    private void setPayMaxTextView(String payMaxs) {

        this.payMax = Integer.parseInt(payMaxs);
        String paymaxtext = "单笔限额：" + payMax + "元";
        tv_payLimit.setText(paymaxtext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * listview 监听事件
     */
    @SuppressLint("NewApi")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        try {

            payCode = Integer.valueOf(listviewdata.get(position).getPayCode()).intValue();

            payInputMoneryListViewAdapter.changeMenuItemSelectState(position);
            payInputMoneryListViewAdapter.notifyDataSetChanged();

            //设置最大金额
            setPayMaxTextView(listviewdata.get(position).getPayMax());

            ChenQianLog.i("pwodke:" + position + "  payCode:" + payCode);


        } catch (NumberFormatException e) {

            e.getMessage();
        }


    }
}
