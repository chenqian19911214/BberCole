package com.bber.company.android.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.bean.Order;
import com.bber.company.android.bean.Session;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.ChatMsgDao;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.imageload.AudioGetFromHttp;
import com.bber.company.android.view.activity.ChatActivity;
import com.bber.company.android.view.activity.WelcomeActivity;
import com.bber.customview.utils.LogUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.ReaderListener;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author baiyuliang
 */

@SuppressWarnings("static-access")
public class MsgListener implements ChatMessageListener, ReaderListener {

    PendingIntent pendingIntent;
    private Context context;
    private NotificationManager mNotificationManager;
    private JsonUtil jsonUtil;
    private ChatMsgDao msgDao;
    private SessionDao sessionDao;
    private Notification mNotification;
    private Vibrator vibrator;
    private SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
    private AudioGetFromHttp aduio = new AudioGetFromHttp(context);


    public MsgListener(Context context) {
        this.context = context;
        jsonUtil = new JsonUtil(context);
        msgDao = new ChatMsgDao(context);
        sessionDao = new SessionDao(context);
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(android.content.Context.NOTIFICATION_SERVICE);         // 通知

    }

    /*收到的系统消息*/
    @Override
    public void processMessage(Chat arg0, Message message) {
        try {
            String msgBody = message.getBody();
            LogUtils.e("eeeeeeeeeeeeeeee" + "收到消息message" + message.toXML());
            if (TextUtils.isEmpty(msgBody))
                return;
            String time = sd.format(new Date());
            LogUtils.e("eeeeeeeeeeeeeeee" + "收到消息--解密前Body" + msgBody);
            String from = message.getFrom().toString().split("@")[0];
            String to = message.getTo().toString().split("@")[0];
            LogUtils.e("eeeeeeeeeeeeeeee" + "收到消息--解密后Body" + msgBody);
            JSONObject dataJson = new JSONObject(msgBody);
            String msg_type = dataJson.getString("msg_type");
            if (msg_type.equals("order")) {
                //发送广播  通知聊天页刷新数据
                Intent mIntent = new Intent(Constants.ACTION_MSG);
                int status = dataJson.getInt("status");
                int sellerId = dataJson.getInt("sellerId");
                if (!dataJson.isNull("content")) {
                    String content = dataJson.getString("content") + "";
                    Order order = jsonUtil.jsonToOrder(content);
                    mIntent.putExtra("order", order);
                    Log.e("eeeeeeeeeeeeeee", "---收到订单信息 content:" + content);
                }
                mIntent.putExtra("isOrder", 1);//1标记为订单
                mIntent.putExtra("status", status);//
                mIntent.putExtra("sellerId", sellerId);//
                context.sendBroadcast(mIntent);
                return;
            }
            ChatInfo chatInfo = jsonUtil.jsonToChatInfo(msgBody);
            if (chatInfo == null) {
                return;
            }

            Log.e("eeeeeeeeeeeeeee", "---收到消息 content:" + chatInfo.getContent() + "--type:" + chatInfo.getMsg_type());
            if (chatInfo.getDate() == null) {
                chatInfo.setDate(time);
            }
            chatInfo.setIsReaded("1");//暂且默认为已读
            chatInfo.setFromUser(from);
            chatInfo.setToUser(to);
            chatInfo.setIsComing(0);

            Session session = new Session();
            session.setFrom(from);
            session.setTo(to);

            String sellerid = String.valueOf(chatInfo.getSellerId());
            boolean charOnline = (Boolean) SharedPreferencesUtils.get(context, "chatOnline", false);
            String nowsellerId = (String) SharedPreferencesUtils.get(context, Constants.CHAT_SELLER_ID, "");
            if (!charOnline || !nowsellerId.equals(sellerid)) {//chat页未打开,或者不在当前页面
                session.setNotReadCount(1);//未读消息数量
                Intent mIntent = new Intent(Constants.ACTION_MSG);
                mIntent.putExtra("isUpdate", true);
                context.sendBroadcast(mIntent);
            } else {
                session.setNotReadCount(0);
            }
            session.setTime(time);
            session.setType(chatInfo.getMsg_type());

            if (chatInfo.getMsg_type().equals(Constants.MSG_TYPE_TEXT)) {
                session.setContent(chatInfo.getContent() + "");
                session.setHeadURL(chatInfo.getSellerHead());
                session.setName(chatInfo.getSellerName());
                session.setSellerId(chatInfo.getSellerId());
            } else if (chatInfo.getMsg_type().equals(Constants.MSG_TYPE_IMG)) {
                session.setContent("[图片]");
                session.setHeadURL(chatInfo.getSellerHead());
                session.setName(chatInfo.getSellerName());
                session.setSellerId(chatInfo.getSellerId());
            } else if (chatInfo.getMsg_type().equals(Constants.MSG_TYPE_CARD)) {
                session.setContent("[名片]");
                session.setHeadURL(chatInfo.getSellerHead());
                session.setName(chatInfo.getSellerName());
                session.setSellerId(chatInfo.getSellerId());
            } else if (chatInfo.getMsg_type().equals(Constants.MSG_TYPE_VOICE)) {
                session.setContent("[语音]");
                session.setHeadURL(chatInfo.getSellerHead());
                session.setName(chatInfo.getSellerName());
                session.setSellerId(chatInfo.getSellerId());
            } else {
                session.setContent("[位置]");
                session.setHeadURL(chatInfo.getSellerHead());
                session.setName(chatInfo.getSellerName());
                session.setSellerId(chatInfo.getSellerId());
            }

            //如果seller为系统消息，那么更新添加消息
            if ("10000".equals(session.getFrom())) {
                session.setSellerId(10000);
                sellerid = String.valueOf(10000);
                chatInfo.setSellerId(10000);

                if (!dataJson.isNull("type")) {
                    int type = dataJson.getInt("type");
                    Intent mIntent;
                    if (type == 1 || type == 2) {//1 充值成功    通知刷新数据个人中心页
                        mIntent = new Intent(Constants.ACTION_SETTING);
                        mIntent.putExtra("type", "charge");
                        context.sendBroadcast(mIntent);
                    } else if (type == 3) {//Vip
                        mIntent = new Intent(Constants.ACTION_SETTING);
                        mIntent.putExtra("type", "update");
                        context.sendBroadcast(mIntent);
                    }
                }
            }

            if (chatInfo.getMsg_type().equals(Constants.MSG_TYPE_VOICE)) {
                aduio.asyncDownAudio(chatInfo.getVoicePath());
            }
            //存入数据库
            msgDao.insert(chatInfo);
            if (sessionDao.isContent(sellerid, to)) {//判断最近联系人列表是否已存在记录
                sessionDao.updateSession(session);
            } else {
                sessionDao.insertSession(session);
            }
            Intent mIntent;
//            if ("10000".equals(session.getFrom())){
////                mIntent   = new Intent(Constants.ACTION_CUSTOMER);
//                mIntent   = new Intent(Constants.ACTION_MSG);
//            }else {
//                mIntent   = new Intent(Constants.ACTION_MSG);
//            }
            mIntent = new Intent(Constants.ACTION_MSG);
            //发送广播  通知聊天页刷新数据
            mIntent.putExtra("from", from);
            mIntent.putExtra("seller", sellerid);
            mIntent.putExtra("chatInfo", chatInfo);
            context.sendBroadcast(mIntent);

            boolean chatPause = (Boolean) SharedPreferencesUtils.get(context, "chatPause", true);
            if (chatPause) {//chat页处于后台
                showNotice(session);
            } else {
                vibrator.vibrate(500);
            }

            if (msgBody.contains("您已成功通过主播审核，请愉快的开始主播之旅~")) {
                SharedPreferencesUtils.put(context, Constants.BROADCAST_LEVEL, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showNotice(Session session) {
        String newMsg = context.getResources().getString(R.string.new_msg);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("啪啪")
                .setContentText(newMsg)
//  .setNumber(number) //设置通知集合的数量
                .setAutoCancel(true)
                .setTicker(newMsg) //通知首次出现在通知栏，带上升动画效果的
                .setContentIntent(getDefalutIntent(session))//设置通知栏点击意图
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_tittle);//设置通知小ICON

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 500;
        notification.ledOffMS = 1000;
        mNotificationManager.notify(Constants.NOTIFY_ID, mBuilder.build());// 通知
    }

    public PendingIntent getDefalutIntent(Session session) {
        Intent intent;
        if (MyApplication.getContext().isAppRunning()) {
            intent = new Intent(context, ChatActivity.class);
            intent.putExtra("from", session.getFrom());
            intent.putExtra("sellerId", session.getSellerId());
            intent.putExtra("sellerName", session.getName());
            intent.putExtra("sellerHead", session.getHeadURL());
        } else {
            intent = new Intent(context, WelcomeActivity.class);
        }
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    @Override
    public void read(String str) {

    }
}

