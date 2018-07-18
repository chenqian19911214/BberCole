package com.bber.company.android.service;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.util.SocketHelp;
import com.bber.company.android.view.activity.VideoCallActivityActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;

/**
 * 服务
 */
public class MsfService extends Service {

    private static MsfService mInstance = null;
    private final IBinder binder = new MyBinder();
    public String mUserName, mPassword;
    private boolean isLogin = true;//false:表示注册， true:表示登录
    private MyXMPP xmpp;
    private String userid, appid, token;

    private AgoraAPIOnlySignal agoraAPIOnlySignal;

    private SocketHelp socketHelp;
  //  private Socket socket;

    public static MsfService getInstance() {
        return mInstance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        userid = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        this.mUserName = userid + "buyer";
        this.mPassword = Tools.md5("c" + mUserName);
        mInstance = this;
        xmpp = new MyXMPP();
        appid = getString(R.string.agora_app_id);
        agoraAPIOnlySignal = MyApplication.getContext().getmAgoraAPI();
        agoraAPIOnlySignal.login2(appid, userid, "_no_need_token", 0, "", 30, 3);

        getbberToken();
        addCallback();

    }

    /**
     * 获取Token
     */
    private void getbberToken() {
        //  DialogView.show(getContext(), true);
        RequestParams params = new RequestParams();
        String head = new JsonUtil(getApplication()).httpHeadToJson(getApplication());
        params.put("head", head);
        params.put("appId", appid);
        params.put("account", userid);
        HttpUtil.post(Constants.getInstance().getToken, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    //    ChenQianLog.i("appkoken：json：" + jsonObject);
                    if (resultCode != 0) {
                        token = jsonObject.getString("dataCollection");
                        if (token != null) {

                            SharedPreferencesUtils.put(getApplicationContext(), Constants.AGORAKOKEN, token);
                            // agoraAPIOnlySignal.login2(appid, userid, token, 0, "", 30, 3);

                        }
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用

                //  DialogView.dismiss();
            }
        });
    }

    /**
     * 登录回调接口
     */
    private void addCallback() {
        agoraAPIOnlySignal.callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                ChenQianLog.i("onLoginSuccess uuid:" + uid + "  fd:" + fd);

            }

            @Override
            public void onInviteMsg(String channelID, String account, int uid, String msgType, String msgData, String extra) {
                super.onInviteMsg(channelID, account, uid, msgType, msgData, extra);

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("channelID:" + channelID + "\n");
                stringBuffer.append("account:" + account + "\n");
                stringBuffer.append("uid:" + uid + "\n");
                stringBuffer.append("msgType:" + msgType + "\n");
                stringBuffer.append("msgData:" + msgData + "\n");
                stringBuffer.append("extra:" + extra);

                ChenQianLog.i("stringBuffer：" + stringBuffer);
            }

            /**消息发送 成功*/
            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
                ChenQianLog.i("成功：自己收到 ICallBack 类的 onMessageSendSuccess() 回调");
            }

            /**文字接受 后回调*/
            @Override
            public void onMessageInstantReceive(String account, int uid, String msg) {
                super.onMessageInstantReceive(account, uid, msg);

                ChenQianLog.i("有消息：" + msg);
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String messageType = jsonObject.getString("messageType");
                    int intytpe = Integer.parseInt(messageType);
                    Intent intent = new Intent();

                    switch (intytpe) {

                        case Constants.MKAgoraMessageTypeUnknown:  // 未知消息

                            break;
                        case Constants.MKAgoraMessageTypeText: // 文本信息

                            Intent intent1 = new Intent();
                            intent1.setAction("MKAgoraMessageTypeText");
                            intent1.putExtra("type","TypeText");
                            intent1.putExtra("json_msg",msg);
                            sendBroadcast(intent1);

                            break;
                        case Constants.MKAgoraMessageTypeSendRequestLive:   // 发送视频请求

                            String username = jsonObject.getString("ubName");
                            String userids = jsonObject.getString("account");
                            createServiceDialog(username, userids);
                            break;
                        case Constants.MKAgoraMessageTypeResponseRequestLive: // 回应视频请求

                            String channelName = jsonObject.getString("chanelName");
                            String userid = jsonObject.getString("account");

                            intent.setAction("broadcest_setvice");
                            intent.putExtra("state", "allow_video");
                            intent.putExtra("channelName", channelName);
                            intent.putExtra("userid", userid);
                            sendBroadcast(intent);

                            break;
                        case Constants.MKAgoraMessageTypeResponseRequestLiveReject: // 拒绝视频请求

                            intent = new Intent();
                            intent.setAction("broadcest_setvice");
                            intent.putExtra("state", "refuse_video");
                            intent.putExtra("usetid", account);
                            sendBroadcast(intent);
                            break;
                        case Constants.MKAgoraMessageTypeGuestCloseVideo:  // 用户关闭视频

                            intent = new Intent();
                            intent.setAction("broadcest_setvice");
                            intent.putExtra("state", "guest_close_video");
                            sendBroadcast(intent);

                            break;
                        case Constants.MKAgoraMessageTypeAnchorCloseVideo:   // 主播关闭视频

                            intent = new Intent();
                            intent.setAction("broadcest_setvice");
                            intent.putExtra("state", "anchor_close_video");
                            sendBroadcast(intent);

                            break;

                        case Constants.MKAgoraMessageTypeGuestCloseVideoNoMoney: // 没钱

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //当收到呼叫邀请时触发此回调。
            @Override
            public void onInviteReceived(String channelID, String account, int uid, String extra) {
                super.onInviteReceived(channelID, account, uid, extra);

                ChenQianLog.i("当收到呼叫邀请：" + channelID);

            }

            /**消息发送失败*/
            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
                ChenQianLog.i("请求失败：" + ecode);

            }

            //当呼叫被对方收到时触发。
            @Override
            public void onInviteReceivedByPeer(String channelID, String account, int uid) {
                super.onInviteReceivedByPeer(channelID, account, uid);
                ChenQianLog.i("当呼叫被对方收到时：" + channelID);

            }

            //当呼叫被对方接受时触发此回调。
            @Override
            public void onInviteAcceptedByPeer(String channelID, String account, int uid, String extra) {
                super.onInviteAcceptedByPeer(channelID, account, uid, extra);
                ChenQianLog.i("当呼叫被对方接受时触发：" + channelID);

            }

            //对方已拒绝呼叫回调
            @Override
            public void onInviteRefusedByPeer(String channelID, String account, int uid, String extra) {
                super.onInviteRefusedByPeer(channelID, account, uid, extra);
                ChenQianLog.i("对方已拒绝呼叫回调：" + channelID);

            }

            // 呼叫失败回调 当呼叫失败时触发
            @Override
            public void onInviteFailed(String channelID, String account, int uid, int ecode, String extra) {
                super.onInviteFailed(channelID, account, uid, ecode, extra);
                ChenQianLog.i("呼叫失败回调：" + channelID);

            }

            //对方已结束呼叫回调 当呼叫被对方结束时触发此回调。
            @Override
            public void onInviteEndByPeer(String channelID, String account, int uid, String extra) {
                super.onInviteEndByPeer(channelID, account, uid, extra);
                ChenQianLog.i("对方已结束呼叫回调：" + channelID);
            }

            //本地已结束呼叫回调  当呼叫被自己结束时触发。
            @Override
            public void onInviteEndByMyself(String channelID, String account, int uid) {
                super.onInviteEndByMyself(channelID, account, uid);
                ChenQianLog.i("本地已结束呼叫回调：" + channelID);
            }

            @Override
            public void onLoginFailed(final int i) {
                ChenQianLog.i("onLoginFailed " + i);
            }

            @Override
            public void onError(String s, int i, String s1) {
                ChenQianLog.i("onError s:" + s + " s1:" + s1);
            }

            /**用户状态查询*/
            @Override
            public void onQueryUserStatusResult(String name, String status) {
                super.onQueryUserStatusResult(name, status);
                Intent intent1 = new Intent();
                intent1.setAction("MKAgoraMessageTypeText");
                intent1.putExtra("type","QueryUserStatus");
                intent1.putExtra("status",status);
                sendBroadcast(intent1);
            }
        });
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("eeeeeeeeeeeeeeeeeeeee", "onStartCommand");
        if (intent != null) {
            this.isLogin = intent.getBooleanExtra("isLogin", true);
            String userid = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
            this.mUserName = userid + "buyer";
            this.mPassword = Tools.md5("c" + mUserName);
        }
        initXMPPTask(isLogin);
        return START_STICKY;
    }

    /**
     * 初始化xmpp和完成后台登录
     */
    public void initXMPPTask(final boolean isLogin) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    xmpp.init(mInstance, mUserName, mPassword);
                    xmpp.connectConnection(isLogin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        Log.e("eeeeeeeeeeeeeeeeeeeeeee", "---- service onDestroy");
        try {
            new Thread() {
                @Override
                public void run() {
                    xmpp.destroy();
//                    if (MyApplication.connection != null) {
//                        MyApplication.connection.disconnect();
//                        MyApplication.connection = null;
//                    }
                }
            }.start();

            if (agoraAPIOnlySignal != null) {
                agoraAPIOnlySignal.logout();
            }
            if (socketHelp != null) {
                socketHelp.closeSocketService();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("eeeeeeeeeeeeeeeeeeeeeee", "---- service onDestroy Exception:" + e.getMessage());
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public MsfService getService() {
            return MsfService.this;
        }
    }

    /**
     * 在服务里创建 Dialog
     */
    public void createServiceDialog(final String usetname, final String userid) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_alertdialog_createcast_request, null, false);
                        TextView namemame = view.findViewById(R.id.dialog_broadecase_test);
                        TextView dialog_broadecase_on = view.findViewById(R.id.dialog_broadecase_on);
                        TextView dialog_broadecase_ok = view.findViewById(R.id.dialog_broadecase_ok);
                        final AlertDialog dialog = new AlertDialog.Builder(getApplicationContext()).create();

                        namemame.setText(usetname + "请求与你视频通话");
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setView(view);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_TOAST));
                        } else {
                            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

                        }
                        dialog.show();
                        dialog_broadecase_on.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                refuseVoide(userid);//拒绝通话
                                dialog.dismiss();
                            }
                        });

                        dialog_broadecase_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                allowVoide(userid);//接受通话
                                dialog.dismiss();

                            }
                        });
                       /* if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));


                            //service对话框
                        }else {
                            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                           // dialog.show();

                        }*/

                    }
                });
            }
        }).start();
    }


    /**
     * 接受 视频请求
     */

    private void allowVoide(String account) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", userid);
            jsonObject.put("messageType", Constants.MKAgoraMessageTypeResponseRequestLive);
            jsonObject.put("ubHeadm", "");
            jsonObject.put("unName", "");
            jsonObject.put("chanelName", userid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyApplication.getContext().getmAgoraAPI().messageInstantSend(account, 0, jsonObject.toString(), Constants.MKAgoraMessageTypeResponseRequestLive + "");

        Intent allowintent = new Intent(getApplicationContext(), VideoCallActivityActivity.class);
        allowintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        allowintent.putExtra("channelName", userid);
        allowintent.putExtra("userid", account);
        startActivity(allowintent);
    }

    private void refuseVoide(String account) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("messageType", Constants.MKAgoraMessageTypeResponseRequestLiveReject);
            jsonObject.put("ubHeadm", "");
            jsonObject.put("unName", "");
            jsonObject.put("chanelName", userid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyApplication.getContext().getmAgoraAPI().messageInstantSend(account, 0, jsonObject.toString(), Constants.MKAgoraMessageTypeResponseRequestLiveReject + "");
    }
}
