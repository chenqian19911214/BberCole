package com.bber.company.android.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BuyerUserEntity;
import com.bber.company.android.bean.VoucherBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.activity.ADActivity;
import com.bber.company.android.view.activity.RegistereSecActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于登陆的所有逻辑
 */

public class UserViewModel extends BaseViewModel {
    public RequestParams params = new RequestParams();
    public BuyerUserEntity buyerUserEntity;
    private String head;
    private JsonUtil jsonUtil;
    //  ProgressDialog progressDialog;
    private String userName;
    private String psd;


    public UserViewModel(Context _context) {
        super(_context);
        //检测网络是否可用
        if (!appContext.isNetworkConnected()) {
            MyToast.makeTextAnim(_context, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        jsonUtil = new JsonUtil(gContext);
        //携带参数
        head = jsonUtil.httpHeadToJson(gContext);
        LogUtils.e("head" + head);
        params.put("head", head);
//        String buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";
//        params.put("userId", buyerId);
    }

    /***
     * 请求用户数据
     */
    public void initUserInfo() {

        params.put("id", SharedPreferencesUtils.get(gContext, Constants.USERID, ""));
        LogUtils.e("请求过这里 userid" + SharedPreferencesUtils.get(gContext, Constants.USERID, ""));
        HttpUtil.get(Constants.getInstance().getUserInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                    String data = dataCollection.getString("BuyerUserEntity");
                    buyerUserEntity = jsonUtil.jsonToBuyerUserEntity(data);
                    SharedPreferencesUtils.put(gContext, Constants.USERNAME, buyerUserEntity.getUbName());
                    SharedPreferencesUtils.put(gContext, Constants.UBSEX, buyerUserEntity.getUbSex());
                    SharedPreferencesUtils.put(gContext, Constants.BIRTHDAY, buyerUserEntity.getuBirthday());
                    SharedPreferencesUtils.put(gContext, Constants.VIP_ID, buyerUserEntity.getVipId());
                    SharedPreferencesUtils.put(gContext, Constants.USERICON, buyerUserEntity.getUbHeadm());
                    SharedPreferencesUtils.put(gContext, Constants.VIP_NAME, buyerUserEntity.getVipName());
                    SharedPreferencesUtils.put(gContext, Constants.VIP_START_TIME, buyerUserEntity.getVipStartTime());
                    SharedPreferencesUtils.put(gContext, Constants.VIP_END_TIEM, buyerUserEntity.getVipEndTime());
                    SharedPreferencesUtils.put(gContext, Constants.USER_MONEY, buyerUserEntity.getUbMoney());
                    SharedPreferencesUtils.put(gContext, Constants.INVITECODE, buyerUserEntity.getInviteCode());
                    SharedPreferencesUtils.put(gContext, Constants.GAMEFREEMONEY, buyerUserEntity.getGameFreeMoney());
                    SharedPreferencesUtils.put(gContext, Constants.WITHDRAWSTATUS, buyerUserEntity.getWithdrawStatus());

//                    money = buyerUserEntity.getUbMoney();
//                    tv_moneybag.setText(money+"");
                    MyApplication.getContext().setBalance(buyerUserEntity.getUbMoney());
//                    setVipInfor();

                    iactionListener.SuccessCallback(buyerUserEntity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUserInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * 获取现金券的list接口
     */
    public void getCashCardList() {

        String buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";
        params.put("buyerId", buyerId);
        HttpUtil.post(Constants.getInstance().getCashCardListByBuyerId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    //请求现金券的数量
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    List<VoucherBean> voucherList = jsonUtil.jsonToVoucherBean(dataCollection.toString());
                    iactionListener.SuccessCallback(voucherList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /***
     * 注册新用户
     */
    public void register(String userName, String psd, String city, String key, Date datetime, int userSex, String recommendBuyerName) {
        DialogTool.createProgressDialog(gContext, true);
        this.userName = userName;
        this.psd = psd;
        RequestParams params = new RequestParams();
        params.put("userName", userName);
        params.put("password", psd);
        params.put("city", city);
        params.put("inviteCode", key);
        params.put("userAge", datetime);
        params.put("userSex", userSex);
        params.put("recommendBuyerName", recommendBuyerName);
        params.put("apkSource", Constants.APK_CHANNEL_SOURCE_ID);
        HttpUtil.getLogin(Constants.getInstance().registerBuyerByName, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "registerBuyerByName onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                int resultCode = 0;
                try {
                    resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        login(null);
                    } else {
                        //推荐人错误
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "registerBuyerByName onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }
        });
    }


    /***
     * 登陆
     *
     * @param progressDialog
     */
    private void login(final ProgressDialog progressDialog) {

        RequestParams params = new RequestParams();
        params.put("userName", userName);
        params.put("password", psd);
        HttpUtil.get(Constants.getInstance().buyerLoginByName, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "buyerLogin onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");

                    String userId = dataCollection.getString(Constants.USERID);
                    String session = dataCollection.getString(Constants.SESSION);
                    String name = dataCollection.getString(Constants.USERNAME);
                    String url = dataCollection.getString(Constants.HEADURL);
                    SharedPreferencesUtils.put(gContext, Constants.USERNAME, name);
                    SharedPreferencesUtils.put(gContext, Constants.HEADURL, url);
                    SharedPreferencesUtils.put(gContext, Constants.USERID, userId);
                    SharedPreferencesUtils.put(gContext, Constants.SESSION, session);


                    Intent intent = new Intent(gContext, ADActivity.class);
                    intent.putExtra("chen", "7");

                    Bundle bundle = new Bundle();
                    intent.putExtra("isLogin", true);
                    gContext.startActivity(intent);
                    MyApplication.getContext().finishAllactiity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "buyerLogin onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }
        });

    }


    /***
     * 登陆
     *
     * @param
     */
    public void checkNameByName(final String city, final String key, final String user_name, final String psd) {
        // if (progressDialog == null) {
        DialogTool.createProgressDialog(gContext, true);
        // }
        params.put("userName", user_name);

        HttpUtil.get(Constants.getInstance().checkName, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "buyerLogin onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        Intent intent = new Intent(gContext, RegistereSecActivity.class);
                        intent.putExtra("city", city);
                        intent.putExtra("inviteCode", key);
                        intent.putExtra("username", user_name);
                        intent.putExtra("password", psd);
                        gContext.startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "buyerLogin onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }
        });

    }


    /**
     * 获取QQ号码
     */
    public void getQQinfo() {
        RequestParams params = new RequestParams();
        params.put("type", 7);
        HttpUtil.get(Constants.getInstance().getQQNumber, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    String resultMessage = jsonObject.getString("resultMessage");
                    if (resultCode == 1) {
                        iactionListener.SuccessCallback(resultMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });

    }
}
