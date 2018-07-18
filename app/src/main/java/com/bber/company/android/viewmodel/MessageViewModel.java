package com.bber.company.android.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.bean.Session;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.ChatMsgDao;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

//import org.jxmpp.jid.BareJid;

/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于私聊的所有逻辑
 */

public class MessageViewModel extends BaseViewModel {
    public RequestParams params = new RequestParams();
    public SimpleDateFormat sd;
    public String I, Other;//为了好区分，I就是自己，Other就是对方
    // public BareJid jid;
    public int sellerId, buyerId;
    public int offset;//聊天记录
    public String orderID;
    public int height, width;
    public String buyerName, buyerHead, sellerName, sellerHead;
    private String head;
    private JsonUtil jsonUtil;
    private ChatMsgDao msgDao;
    private SessionDao sessionDao;

    public MessageViewModel(Context _context) {
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
        msgDao = new ChatMsgDao(gContext);
        sessionDao = new SessionDao(gContext);
    }

    /***
     * 增加妹子的意向数量
     */
    public void incrConnectCount() {
        params.put("head", head);
        params.put("sellerId", sellerId);
        final String str = sellerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().incrConnectCount, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
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


    /**
     * * 执行发送消息 文本类型
     */
    public void sendMsgText(String content) {
        final ChatInfo chatInfo = getChatInfoTo(content, Constants.MSG_TYPE_TEXT);
        if (chatInfo == null) {
            return;
        }
        final String message = jsonUtil.chatInfoToString(chatInfo);
        Log.e("eeeeeeeeeeeeeee", "发送的message:" + message);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = MyXMPP.getInstance().sendMessage(message, Other);
                    if (result) {
//                        handler.obtainMessage(1, chatInfo).sendToTarget();
                        //回掉监听，回到主线程改变界面
                        iactionListener.SuccessCallback(chatInfo);
                    } else {
                        Looper.prepare();
                        MyToast.makeTextAnim(gContext, R.string.send_fail, 0, R.style.PopToast).show();
                    }

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Looper.loop();
                    MyToast.makeTextAnim(gContext, R.string.send_fail, 0, R.style.PopToast).show();
                }
            }
        }).start();
    }


    /**
     * 发送的信息
     *
     * @return
     */
    private ChatInfo getChatInfoTo(String message, String msgtype) {
        sd = new SimpleDateFormat("MM-dd HH:mm");
        String time = sd.format(new Date());
        ChatInfo msg = new ChatInfo();
        msg.setFromUser(I);
        msg.setToUser(Other);
        msg.setMsg_type(msgtype);
        msg.setIsComing(1);
        msg.setDate(time);
        msg.setOrderID(orderID);
        msg.setSellerId(sellerId);
        msg.setBuyerName(buyerName);
        msg.setBuyerHead(buyerHead);
        msg.setSellerName(sellerName);
        msg.setSellerHead(sellerHead);
        if (msgtype.equals(Constants.MSG_TYPE_IMG)) {
            msg.setContent(message);
//            msg.setHeight(height);
//            msg.setWidth(width);
        } else if (msgtype.equals(Constants.MSG_TYPE_TEXT)) {
            msg.setContent(message);
        } else if (msgtype.equals(Constants.MSG_TYPE_CARD)) {
            msg.setContent(message);
        } else if (msgtype.equals(Constants.MSG_TYPE_VOICE)) {
//            msg.setVoiceTime(chat_voice.getVoicetTime());
//            msg.setVoicePath(chat_voice.mFileName);
        } else if (msgtype.equals(Constants.MSG_TYPE_ORDER)) {
//            msg.setStatus(status);
//            msg.setContent(message);
        } else if (msgtype.equals(Constants.MSG_TYPE_LOCATION)) {
//            msg.setContent(message);
//            msg.setMapBean(mMapBean);
        } else if (msgtype.equals(Constants.SYSTEM_TIP)) {
            msg.setContent(message);
        }
        return msg;
    }

    /***
     * 插入一条消息
     * @param sellerUserVo
     */
    public void insertMsgToDao(SellerUserVo sellerUserVo) {
        if (sellerUserVo != null) {
            String message = "";
            message = "温馨提示：请使用本平台发起订单，可设置使用优惠券减免";
            ChatInfo chatInfo = getChatInfoTo(message, Constants.SYSTEM_TIP);
            chatInfo.setFromUser(Other);
            chatInfo.setSellerId(sellerId);
            chatInfo.setToUser(I);
            msgDao.insert(chatInfo);
            updateSession(Constants.SYSTEM_TIP, message);
        }
    }

    /***
     * 更改session
     * @param type
     * @param content
     */
    public void updateSession(String type, String content) {
        Session session = new Session();
        session.setFrom(Other);
        session.setTo(I);
        session.setNotReadCount(0);//未读消息数量
        session.setTime(sd.format(new Date()));
        session.setType(type);
        session.setSellerId(sellerId);
        session.setName(sellerName);
        session.setHeadURL(sellerHead);

        switch (type) {
            case Constants.MSG_TYPE_TEXT:
                session.setContent(content);
                break;
            case Constants.MSG_TYPE_IMG:
                session.setContent("[图片]");
                break;
            case Constants.MSG_TYPE_VOICE:
                session.setContent("[语音]");
                break;
            case Constants.MSG_TYPE_CARD:
                session.setContent("[名片]");
                break;
            case Constants.MSG_TYPE_LOCATION:
                session.setContent("[位置]");
                break;
            case Constants.SYSTEM_TIP:
                session.setContent("[系统提示]");
                break;
        }

        if (sessionDao.isContent(String.valueOf(sellerId), I)) {
            sessionDao.updateSession(session);
        } else {
            sessionDao.insertSession(session);
        }
        Intent intent = new Intent(Constants.ACTION_CUSTOMER);//发送广播，通知消息界面更新
        intent.putExtra("isCurrActivity", true);
        gContext.sendBroadcast(intent);
    }

    /***
     * 通过一个ID获取一个妹子的信息
     * @param
     */
    public void getGrilID() {
        DialogTool.createProgressDialog(gContext, true);
        HttpUtil.post(Constants.getInstance().onlineservice, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    String jsonObject1 = jsonObject.getString("resultMessage");
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("1")) {
                        SellerUserVo sellerUserVo = jsonUtil.jsonToSellerUserVo(jsonObject1.toString());
                        if (sellerUserVo != null) {
                            iactionListener.SuccessCallback(sellerUserVo);
//                        setInfo();
                        }
                    } else {
                        MyToast.makeTextAnim(gContext, jsonObject1, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getSellerInfo JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getSellerInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }


        });
    }

    /***
     * 通过一个ID获取一个妹子的信息
     * @param sellerID
     */
    public void getSellerInfo(int sellerID) {
        DialogTool.createProgressDialog(gContext, true);

        params.put("uSeller", sellerID);
        HttpUtil.get(Constants.getInstance().getSellerInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("dataCollection");
                    SellerUserVo sellerUserVo = jsonUtil.jsonToSellerUserVo(jsonObject1.toString());
                    String lables = jsonObject1.getString("lables");
//                    tags = jsonUtil.jsonToTags(lables);
                    if (sellerUserVo != null) {
                        iactionListener.SuccessCallback(sellerUserVo);
//                        setInfo();
                    }

                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getSellerInfo JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getSellerInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }


        });
    }

    /***
     * 用户的意见反馈
     * @param
     */
    public void postContent(String data) {
        DialogTool.createProgressDialog(gContext, true);

        params.put("info", data);
        HttpUtil.post(Constants.getInstance().setFeedback, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        MyToast.makeTextAnim(gContext, "提交成功", 0, R.style.PopToast).show();
                        AppManager.getAppManager().currentActivity().finish();
                    }

                } catch (JSONException e) {
                    Log.e("eeeeeeeeeeee", "getSellerInfo JSONException" + e.getMessage());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getSellerInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }


        });
    }
}
