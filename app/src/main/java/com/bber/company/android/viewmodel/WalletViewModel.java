package com.bber.company.android.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BindBankBean;
import com.bber.company.android.bean.BindBankInfoBean;
import com.bber.company.android.bean.ChzfBean;
import com.bber.company.android.bean.VipInforBean;
import com.bber.company.android.bean.VipRechargeBean;
import com.bber.company.android.bean.VipServiceBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.activity.webViewPostActivity;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于钱包的所有逻辑
 */

public class WalletViewModel extends BaseViewModel {

    //银行卡 银行ID 银行名字
    public List<BindBankBean> bankCardInfors;
    public ObservableField<String> Title = new ObservableField<>();

    //会员充值的死数据
    public ObservableList<VipRechargeBean> vipRechargeBeanList = new ObservableArrayList<>();
    //支付方式的死数据
    public ObservableList<VipRechargeBean> payforBeanList = new ObservableArrayList<>();
    public RequestParams params = new RequestParams();
    public BindBankBean bindBankBean = new BindBankBean();
    public String objarry;
    public List<VipInforBean> viplist = new ArrayList<>();
    //购买的会员ID
    public int payVipId = 1;
    public int payWays = 51;//默认钱包的支付方式
    public int level = 0;//会员自己的等级
    public int mType = 0;
    public int resId[] = {R.mipmap.vip_big_normal, R.mipmap.vip_big_normal, R.mipmap.vip_big_golden,
            R.mipmap.vip_big_demon, R.mipmap.vip_big_crown, R.mipmap.vip_big_crown,
            R.mipmap.vip_big_crown, R.mipmap.vip_big_crown, R.mipmap.vip_big_crown};
    public ProgressDialog progressDialog;
    /**
     * 解析数据
     *
     * @param data
     */
    public VipInforBean presentData;
    public Double money;
    public HashMap listMoney = new HashMap();
    public double balance;
    private String head;
    private JsonUtil jsonUtil;

    public WalletViewModel(Context _context) {
        super(_context);
        //检测网络是否可用
        if (!appContext.isNetworkConnected()) {
            MyToast.makeTextAnim(_context, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        //携带参数
        jsonUtil = new JsonUtil(gContext);
        head = new JsonUtil(gContext).httpHeadToJson(gContext);
        LogUtils.e("head" + head);
        params.put("head", head);
        String buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";
        params.put("userId", buyerId);
    }

    /***
     * 获取银行卡列表
     */
    public synchronized void getCard() {

        HttpUtil.post(Constants.getInstance().getBuyerBankCard, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                JSONObject dataCollection = null;
                try {
                    Gson gson = new Gson();
                    dataCollection = jsonObject.getJSONObject("dataCollection");
                    bindBankBean = gson.fromJson(dataCollection.toString(), BindBankBean.class);
//                    String data = dataCollection.getString("bankId");
                    bankCardInfors.clear();
                    bankCardInfors.add(bindBankBean);
                    String result = jsonObject.toString();
                    LogUtils.e("getCard数据" + result);
                    BindBankInfoBean bindBankInfoBean = gson.fromJson(result, BindBankInfoBean.class);
                    iactionListener.SuccessCallback(bindBankBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 鐎瑰本鍨氶崥搴ょ殶閻㈩煉绱濇径杈Е閿涘本鍨氶崝鐕傜礉闁?燁洣鐠嬪啰鏁?

                // DialogTool.dismiss(WalletViewModel.this);
            }
        });
    }

    /***
     * 钱包里面的提现接口。
     */
    public synchronized void presentCash(Double cash) {

        String buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";
        params.put("buyerUserId", buyerId);
        params.put("bank_id", bindBankBean.getBankId());
        params.put("amount", cash);
        params.put("card_num", bindBankBean.getBankCard());
        params.put("card_name", bindBankBean.getCardName());

        final String str = buyerId + bindBankBean.getBankId() + cash + bindBankBean.getBankCard() + bindBankBean.getCardName() + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        DialogTool.createProgressDialog(gContext, true);
        LogUtils.e(buyerId + "," + bindBankBean.getBankId() + "," + cash + "," + bindBankBean.getBankCard() + "," + bindBankBean.getCardName());

        HttpUtil.post(Constants.getInstance().manualDraw, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                //除了成功  处理其他的code方式
                LogUtils.e(jsonObject.toString());
                String transaction_charge = null;
                try {
                    MyApplication.isRestartInitData = true;
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    LogUtils.e(dataCollection.toString());
//                    JSONObject dp_msg = dataCollection.getJSONObject("dp_msg");
//                    transaction_charge = dp_msg.getString("transaction_charge");
                    transaction_charge = dataCollection.getString("transaction_charge");
                    String substatus = dataCollection.getString("subStatus");
                    iactionListener.SuccessCallback(transaction_charge.toString());
                    Constants.ISGETDATA = true;
                    MyToast.makeTextAnim(gContext, "申请成功,资金稍后到账", 0, R.style.PopToast).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                LogUtils.e(transaction_charge.toString());
                //成功返回的弹框

//                MyToast.makeTextAnim(gContext, "申请成功,资金稍后到账", 0, R.style.PopToast).show();
//                AppManager.getAppManager().currentActivity().finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }
        });
    }

    /***
     * 更改银行卡
     */
    public void updateBank(String bankid, String cardName, String bankCard) {
        params.put("bankId", bankid);
        params.put("bankCard", bankCard);
        params.put("cardName", cardName);
        LogUtils.e("," + bindBankBean.getBankId() + "," + bindBankBean.getBankCard() + "," + bindBankBean.getCardName());
        HttpUtil.post(Constants.getInstance().updateBuyerBankCard, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }

                JSONObject dataCollection = null;
                Gson gson = new Gson();
                String result = jsonObject.toString();
                BindBankInfoBean bindBankInfoBean = gson.fromJson(result, BindBankInfoBean.class);
                if (bindBankInfoBean.getResultCode().equals("1")) {
                    iactionListener.SuccessCallback(bindBankInfoBean);
                } else {
                    iactionListener.FailCallback("网络失去连接");
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }
        });
    }

    /**
     * 获取VIP的信息
     */
    public void getVipListInfor() {
        DialogTool.createProgressDialog(gContext, true);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getVipList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    JSONArray data = jsonObject.getJSONArray("dataCollection");
                    getVipListData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    public void getVipListData(JSONArray data) {
        JsonUtil jsonUtil = new JsonUtil(gContext);
        for (int i = 0; i < data.length(); i++) {
            try {
                VipInforBean vipInforBean = new VipInforBean();

                JSONObject obj = (JSONObject) data.get(i);
                vipInforBean.setVipId(obj.getInt("vipId"));
                vipInforBean.setVipName(obj.getString("vipName"));
                vipInforBean.setVipValidity(obj.getString("vipValidity"));
                vipInforBean.setVipMoney(obj.getDouble("vipMoney"));
                vipInforBean.setVipLevel(obj.getInt("vipLevel"));
                vipInforBean.setVipDisMoney(obj.getDouble("vipDisMoney"));
                vipInforBean.setVipDisStartTime(obj.getString("vipDisStartTime"));
                vipInforBean.setVipDisEndTime(obj.getString("vipDisEndTime"));
                objarry = obj.getString("vipDetail");
                vipInforBean.vipDetailStr = objarry;
                //找到对应VIP游客的信息
                int nowVipId = (int) SharedPreferencesUtils.get(gContext, Constants.VIP_ID, 0);

                vipInforBean.setVipResId(R.mipmap.vip_big_golden);
                List<VipServiceBean> vipInforBeanList = new ArrayList<>();
                //数组大于0才开始进行下一步的解析
                if (objarry.length() > 0) {
                    vipInforBeanList = jsonUtil.jsonToVipServiceBean(objarry);
                }
                vipInforBean.setVipDetail(vipInforBeanList);
                presentData = new VipInforBean();
                //过滤掉会员等级为0的情况，表示基础会员
                viplist.add(vipInforBean);
                if (nowVipId == obj.getInt("vipId")) {
                    //找到这个人的VIP信息，然后开始回调他的信息

                    presentData = vipInforBean;

//                    setNoVipInfor(obj.getInt("vipId"),vipInforBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        iactionListener.SuccessCallback(presentData);
        DialogTool.dismiss(gContext);
//        setVipInfor();
    }

    /**
     * BN充值会员接口
     */
    public void rechargeVip(final Context mcontext, int payCode) {
        final String userId = SharedPreferencesUtils.get(gContext, Constants.USERID, "") + "";
        DialogView.show(mcontext, true);
        params.put("buyerUserId", userId);
        params.put("vipId", payVipId);
        params.put("code", payCode);
        params.put("type", mType);
        params.put("money", money);
        final String str = userId + "" + money + "" + payCode + "" + payVipId + "" + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.buyerUserPayVip, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                try {
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("1")) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        //获取当前的支付状态，2是BN支付（url需拼接），1是DP支付（url不需要拼接）
                        //当resultCode不为1时,就是错误返回。
                        String status = dataCollection.getString("status");
                        String massage = dataCollection.getString("massage");
                        String path;
                        if (!massage.equals("0")) {
                            //DP
                            if (status.equals("1")) {
                                if (iactionListener != null) {
                                    path = massage;
                                    iactionListener.SuccessCallback(path);
                                }
                            } else if (status.equals("2")) {
                                path = SharedPreferencesUtils.get(gContext, "IMAGE_FILE", "") + massage;
                                iactionListener.SuccessCallback(path);
                            } else if (status.equals("3")) {
                                String chzfMessage = dataCollection.getString("chzfMessage");
                                ChzfBean chzfBean = jsonUtil.jsonTochzf(chzfMessage);
                                Intent intent;
                                intent = new Intent(gContext, webViewPostActivity.class);
                                //获得数据的URL
                                String urlParameter = setCHZFPay(chzfBean);
                                intent.putExtra("url", chzfBean.chzf_url);
                                intent.putExtra("hiding", "1");
                                intent.putExtra("urlParameter", urlParameter);
                                gContext.startActivity(intent);
                            } else {
                                MyToast.makeTextAnim(gContext, massage, 0, R.style.PopToast).show();
                            }
                        } else {
                            MyToast.makeTextAnim(gContext, massage, 0, R.style.PopToast).show();
                        }

                    } else {
                        MyToast.makeTextAnim(gContext, "网络繁忙,请稍后再试", 0, R.style.PopToast).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogView.dismiss(mcontext);
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

    /**
     * 会员升级
     */
    public void getVipUpgradeMoney() {
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "");
        params.put("buyerUserId", buyerId);
        final String str = buyerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);

        HttpUtil.post(Constants.getInstance().getVipUpgradeMoney, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    JSONArray UpgradeMoney = dataCollection.getJSONArray("UpgradeMoney");
                    for (int j = 0; j < UpgradeMoney.length(); j++) {
                        JSONObject obj = (JSONObject) UpgradeMoney.get(j);
                        int vipId = obj.getInt("vipId");
                        double money = obj.getDouble("money");
                        listMoney.put(vipId, money);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 购买VIP
     * *
     */
    public void buyerUserVipByBalance(final Context context) {
        final String userId = SharedPreferencesUtils.get(gContext, Constants.USERID, "") + "";
        //  final ProgressDialog progressDialog = DialogTool.createProgressDialog(gContext, "验证中...");
        DialogView.show(context, true);
        int buyerId = Tools.StringToInt(SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "");
        params.put("buyerUserId", buyerId);
        params.put("money", money);
        params.put("vipId", payVipId);
        params.put("type", mType);
        final String str = userId + "" + money + "" + payVipId + "" + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().buyerUserVipByBalance, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    String massage = jsonObject.getJSONObject("dataCollection").getString("massage");
                    int status = jsonObject.getJSONObject("dataCollection").getInt("status");
                    if (status == 1) {
                        //一旦成功，减少表面层的金钱与后台一致,这里前段的钱只能靠自己减少吗
                        SharedPreferencesUtils.put(gContext, Constants.USER_MONEY, balance - money);
                        MyToast.makeTextAnim(gContext, "恭喜，购买成功", 0, R.style.PopToast).show();
                        Constants.ISGETDATA = true;
                        //AppManager.getAppManager().finishActivityForLength(2);
                        //AppManager.getAppManager().finishActivity();
                        //重新拿会员信息？
                        //getVipListInfor();
                        //app.newActivity.add(PayVipActivity.this);
                        //app.closeListActivity();
                    } else {
                        MyToast.makeTextAnim(gContext, massage, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                // progressDialog.dismiss();
                DialogView.dismiss(context);
            }
        });
    }
}
