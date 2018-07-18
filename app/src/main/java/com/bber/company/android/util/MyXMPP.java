package com.bber.company.android.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.MessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.GroupMsgListener;
import com.bber.company.android.listener.MsgListener;
import com.bber.company.android.service.MsfService;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.view.activity.BaseAppCompatActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.netty.handler.codec.EncoderException;

/**
 * Created by Ankit on 10/3/2015.
 */
public class MyXMPP implements PingFailedListener {

    private static MyXMPP instance;
    private final String TAG = "MyXMPP";
    public MultiUserChatManager manager;
    public MultiUserChat multiUserChat;
    AbstractXMPPConnection connection;
    ChatManager chatmanager;
    Chat newChat;
    XMPPConnectionListener connectionListener = new XMPPConnectionListener();
    Collection<RosterEntry> entries;
    /**
     * 判断好友是否在线
     * return  true:在线   false:下线
     * *
     */
    boolean isAvailable;
    private String userName = "";
    private String passWord = "";
    private boolean connected;
    private boolean chat_created;
    private boolean loggedin;
    private boolean connecting;
    private Context context;
    private GroupMsgListener mGroupMsgListener;
    //private EntityBareJid entityBareJid;
    private XMPPTCPConnectionConfiguration.Builder configBuilder;
    private Roster roster;
    private PingManager pingManager;
    private boolean startPing = true;

    //初始化
    public static MyXMPP getInstance() {
        if (instance == null) {
            instance = new MyXMPP();
        }
        return instance;
    }

    /**
     * 查询会议室成员名字
     *
     * @param muc
     */
    public static List<String> findMulitUser(MultiUserChat muc) {
        List<String> listUser = new ArrayList<String>();
        listUser = muc.getOccupants();
        //遍历出聊天室人员名称
//        while (it.hasNext()) {
//            // 聊天室成员名字
////            String name = Str.parseResource(it.next());
//            String name = it.next();
//            LogUtils.e("名字"+name);
//            listUser.add(name);
//        }

        for (int i = 0; i < listUser.size(); i++) {
            LogUtils.e("名字" + listUser.get(i));
        }
        return listUser;
    }

    //Initialize
    public void init(final Context context, String userId, String pwd) throws XmppStringprepException {
        Log.i(TAG, "Initializing!");
        this.context = context;
        this.userName = userId;
        this.passWord = pwd;
        instance = this;
        //smack的链接池,链接通讯协议

        try {
            configBuilder = XMPPTCPConnectionConfiguration.builder();

            //账号密码登陆
            configBuilder.setUsernameAndPassword(userName, passWord);
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
            configBuilder.setResource("Android");
            // DomainBareJid serviceName = JidCreate.domainBareFrom(Constants.XMPP_DOMU) ; //服务器名

            configBuilder.setServiceName(Constants.XMPP_DOMU);
            configBuilder.setResource("bber");
            configBuilder.setHost(Constants.getInstance().XMPP_HOST);
            configBuilder.setPort(Constants.XMPP_PORT);
            configBuilder.setDebuggerEnabled(false);
            configBuilder.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (EncoderException e) {

            ChenQianLog.i("qianqian::" + e.getMessage());
            e.getMessage();
        }


        //这个应该是个什么验证的，具体作用不知道
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            //这个应该是SSL握手，Android的私钥和信任证书的格式必须是BKS格式的，通过配置本地JDK，让keytool可以生成BKS格式的私钥和信任证书,java本身没有BouncyCastle密库
            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, MemorizingTrustManager.getInstanceList(context, new SecureRandom());
            sc.init(null, trustAllCerts, new SecureRandom());

            configBuilder.setCustomSSLContext(sc);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (KeyManagementException e) {
            throw new IllegalStateException(e);
        }
        //SAS的一个验证机制
        SASLAuthentication.unregisterSASLMechanism("KERBEROS_V4");
        SASLAuthentication.unregisterSASLMechanism("GSSAPI");
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        //发送方可以收到自己发出信息的消息回执
        XMPPTCPConnection.setUseStreamManagementDefault(true);

        connection = new XMPPTCPConnection(configBuilder.build());
        connection.setPacketReplyTimeout(10000);
        connection.addConnectionListener(connectionListener);

        MyApplication.connection = connection;

        ProviderManager pm = new ProviderManager();
        ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
        ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceiptRequest.Provider());
        DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(connection);
        //向传出数据包添加传递接收请求。
        deliveryReceiptManager.autoAddDeliveryReceiptRequests();
        deliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        //聊天管理器
        chatmanager = ChatManager.getInstanceFor(connection);
        chatmanager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new MsgListener(context));
            }
        });
    }

    // Disconnect Function
    public void disconnectConnection() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    // 登陆没有连接上，重新登陆IM
    public void ReconnectConnection(final Boolean isLogin) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 3);
                    connectConnection(isLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void connectConnection(final Boolean isLogin) {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {
                // Create a connection
                Log.e(TAG, "connectConnection");
                try {
                    connection.connect();

                    ChenQianLog.i("链接成功");
                    if (connection.isConnected()) {
                        if (isLogin) {
                            login();
                        } else {
                            register();
                        }
                        connected = true;
                    } else {
                        ReconnectConnection(isLogin);
                    }

                } catch (IOException e) {
                    ReconnectConnection(isLogin);
                    Log.e("eeeeeeeeeeeeeeeeeeeee", "----connect IOException" + e.getMessage());
                } catch (SmackException e) {
                    if (connection != null && connection.isConnected() == false) {
                        ReconnectConnection(isLogin);
                    }
                    Log.e("eeeeeeeeeeeeeeeeeeeee", "----connect SmackException" + e.getMessage());
                    if (e instanceof SmackException.NoResponseException) {
                        Log.e("eeeeeeeeeeeeeeeeeeeee", "----connect SmackException NoResponseException:" + e.getMessage());
//                        connectConnection(isLogin);
                    }

                } catch (XMPPException e) {
                    ReconnectConnection(isLogin);
                    Log.e("eeeeeeeeeeeeeeeeeeeee", "----connect XMPPException" + e.getMessage());
                }

                return null;
            }
        };
        connectionThread.execute();
    }

    public void login() {
        Log.e(TAG, "----login start userName:" + userName + "---passWord:" + passWord);
        try {
            connection.login(userName, passWord);
            //Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");
            Log.e(TAG, "----login succ");
            pingManager = PingManager.getInstanceFor(connection);
            new Thread(new ThreadShow()).start();
            registerRosterListener();
        } catch (XMPPException | SmackException | IOException e) {
            Log.e(TAG, "----login XMPPException:" + e.getMessage());
            e.printStackTrace();
//            register();
            MsfService.getInstance().initXMPPTask(false);
        } catch (Exception e) {
            Log.e(TAG, "----login Exception:" + e.getMessage());
            MsfService.getInstance().initXMPPTask(false);
        }

    }

    public void register() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", userName);
        map.put("password", passWord);
        AccountManager accountManager = AccountManager.getInstance(connection);
        accountManager.sensitiveOperationOverInsecureConnection(true);
        try {
//            accountManager.supportsAccountCreation();
            accountManager.createAccount(userName, passWord);
            Log.e(TAG, "----register succ");
            login();
        } catch (XMPPException | SmackException e) {
            Log.e(TAG, "----register XMPPException:" + e.getMessage());
            e.printStackTrace();
            login();
        } catch (Exception e) {
            Log.e(TAG, "----register Exception:" + e.getMessage());
        }
    }

    private void registerRosterListener() {
        roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        entries = roster.getEntries();

        roster.addRosterListener(new RosterListener() {

            @Override
            public void entriesAdded(Collection<String> addresses) {

                Log.e(TAG, "---通讯录用户添加");
                getPresences();
            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {
                Log.e(TAG, "---通讯录用户变更");
                getPresences();
            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {
                Log.e(TAG, "---通讯录用户删除");
                getPresences();
            }

            @Override
            public void presenceChanged(Presence presence) {
                getPresences();
                Log.e(TAG, "--通讯录用户presence变化:" + presence.toXML());

            }
        });
    }

    /**
     * 获取好友信息 ，同时发送广播通知其他页面改变好友状态
     * <p>
     * *
     */
    public void getPresences() {
        entries = roster.getEntries();
        Intent intent = new Intent(Constants.ACTION_MSG);
        intent.putExtra("setPresences", true);
        context.sendBroadcast(intent);
    }

    public boolean isAvailable(final String user) {
        if (user == null) {
            return false;
        }
        isAvailable = false;

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        if (entries == null || entries.size() == 0) {
            if (roster == null) {
                registerRosterListener();
            } else {
                entries = roster.getEntries();
            }

        }


        for (RosterEntry entry : entries) {

            //  entry.getJid();
            if (user.equals(entry.getName())) {
                Presence presence = roster.getPresence(entry.getUser());
                isAvailable = presence.isAvailable();
                Log.e(TAG, "---isAvailable:" + isAvailable);
                LogUtils.e("Name :" + entry.getName());
                break;
            } else {
                addRosterItem(user, user, null);
            }
        }
//            }
//        }).start();
        return isAvailable;
    }

    /**
     * 添加好友
     *
     * @param user   用户jid
     * @param name   添加好友备注名称
     * @param groups 好友添加到的分组,可以过个组
     * @throws Exception
     */

    public void addRosterItem(final String user, final String name, final String[] groups) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //entityBareJid = JidCreate.entityBareFrom();

                    roster.createEntry(user + "@" + Constants.XMPP_DOMU, name, groups);

                    Log.e(TAG, "----addRosterItem 添加好友成功");
                } catch (XMPPException | SmackException e) {
                    Log.e(TAG, "----addRosterItem XMPPException:" + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e(TAG, "----addRosterItem Exception:" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 删除好友
     */

    public void removeRosterItem(final String user) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    //   entityBareJid = JidCreate.entityBareFrom();

                    RosterEntry entry = roster.getEntry(user + "@" + Constants.XMPP_DOMU);
                    roster.removeEntry(entry);
                    Log.e(TAG, "----removeRosterItem 删除好友成功");
                } catch (Exception e) {
                    Log.e(TAG, "----removeRosterItem Exception:" + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void pingFailed() {
        Log.i(TAG, "pingFailed()");
    }

    private void checkSession() {
        RequestParams params = new RequestParams();
        final String userId = SharedPreferencesUtils.get(context, Constants.USERID, "") + "";
        String session = SharedPreferencesUtils.get(context, Constants.SESSION, "") + "";
        params.put("userId", userId);
        params.put("session", session);
        params.put("type", "1");
        HttpUtil.post(Constants.getInstance().sessionIsInvalid, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e(TAG, "sessionIsInvalid onSuccess--jsonObject:" + jsonObject);
                if (jsonObject == null) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 0) {
                    } else if (resultCode == 2) {
                        Intent s = new Intent(context, MsfService.class);
                        context.stopService(s);
                        String runningActivity = getRunningActivityName();
                        if (BaseAppCompatActivity.instance != null && runningActivity.contains(BaseAppCompatActivity.instance.getLocalClassName())) {
                            LayoutInflater inflater = LayoutInflater.from(BaseAppCompatActivity.instance);
                            View layout = inflater.inflate(R.layout.custom_alertdialog_warn, null);
                            DialogTool.createDialogWarn(BaseAppCompatActivity.instance, layout, R.string.relogin, R.string.sure_tip);
                        }

                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e(TAG, "sessionIsInvalid onFailure--throwable:" + throwable);
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用

            }
        });
    }

    private String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.e(TAG, "sessionIsInvalid 当前activity:" + runningActivity);
        return runningActivity;
    }

    /**
     * 发送消息
     *
     * @param content
     * @param touser
     * @throws XMPPException
     */

    public boolean sendMessage(String content, String touser) throws XMPPException {
        if (!connection.isConnected()) {
            connectConnection(true);
            return false;
        }
        if (chatmanager == null) {
            chatmanager = ChatManager.getInstanceFor(connection);
        }

        try {
            //entityBareJid = JidCreate.entityBareFrom();
            newChat = chatmanager.createChat(touser + "@" + Constants.XMPP_DOMU);
//            content = OtrNormal.getInstance().transform(content,touser);
//            if(content==null){
//                Log.e(TAG, "发送加密失败");
//            }else{
            newChat.sendMessage(content);
            Log.e(TAG, "发送成功");
//            }

            return true;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e(TAG, "发送失败 NotConnectedException:" + e.getMessage());
            return false;
        }
    }

    /**
     * 发送订单
     *
     * @param content
     * @param touser
     * @throws XMPPException
     */
    public void sendOrder(String content, String touser) throws XMPPException {
        if (!connection.isConnected()) {
            connectConnection(true);
        }
        if (chatmanager == null) {
            chatmanager = ChatManager.getInstanceFor(connection);
        }

        try {

            //entityBareJid = JidCreate.entityBareFrom();

            newChat = chatmanager.createChat(touser + "@" + Constants.XMPP_DOMU);
            newChat.sendMessage(content);
            Log.e(TAG, "发送成功");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e(TAG, "发送失败 NotConnectedException:" + e.getMessage());
        }
    }

    public void destroy() {
        startPing = false;
        connection.disconnect();
        connection.removeConnectionListener(connectionListener);
    }

    /**
     * 创建房间
     *
     * @param user
     * @param roomName
     * @param password
     * @return
     */
    public MultiUserChat createRoom(String user, String roomName, String password) {
        MultiUserChat muc;
        try {
            muc = MultiUserChatManager.getInstanceFor(connection)
                    .getMultiUserChat(roomName + "@" + "bber.cc");

            muc.createOrJoin(roomName);

            Form form = muc.getConfigurationForm();
            Form submitForm = form.createAnswerForm();
            List<FormField> formFields = form.getFields();
            Iterator<FormField> fields = formFields.iterator();
            while (fields.hasNext()) {
                FormField field = fields.next();
                if (!FormField.FORM_TYPE.equals(field.getType()) && field.getVariable() != null) {
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }

            List<String> owners = new ArrayList<String>();
            owners.add(String.valueOf(connection.getUser()));
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
            if (!password.equals("")) {
                // 进入是否需要密码
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
                        true);
                // 设置进入密码
                submitForm.setAnswer("muc#roomconfig_roomsecret", password);
            }
            // 能够发现占有者真实 JID 的角色
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
            // 登录房间对话
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
            // 仅允许注册的昵称登录
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
            // 允许使用者修改昵称
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
            // 允许用户注册房间
            submitForm.setAnswer("x-muc#roomconfig_registration", false);
            muc.sendConfigurationForm(submitForm);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return muc;
    }

    /**
     * 创建一个聊天的房间
     *
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException
     */

    public void createAMUCroom(String roomName) throws XMPPException.XMPPErrorException, SmackException {
        // Get the MultiUserChatManager
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        // Create a MultiUserChat using an XMPPConnection for a room
        MultiUserChat muc = manager.getMultiUserChat("mytestroom" + "@" + "conference." + Constants.XMPP_DOMU);
        // Create the room
        muc.create(roomName);
        // Get the the room's configuration form
        Form form = muc.getConfigurationForm();
        // Create a new form to submit based on the original form
        Form submitForm = form.createAnswerForm();
        // Add default answers to the form to submit
        // Sets the new owner of the room
        List owners = new ArrayList();
        owners.add("2876buyer@bber.cc");
        submitForm.setAnswer("muc#roomconfig_roomowners", owners);
        // Send the completed form (with default values) to the server to configure the room
        muc.sendConfigurationForm(submitForm);
    }

    /**
     * 加入一个带名字的房间
     */
    public void joinAroom(String roomNackName, String userName) {
        // Get the MultiUserChatManager
        manager = MultiUserChatManager.getInstanceFor(connection);
        // Create a MultiUserChat using an XMPPConnection for a room
        multiUserChat = manager.getMultiUserChat(roomNackName + "@muc." + Constants.XMPP_DOMU);
        // The room service will decide the amount of history to send
        try {
            //用户的昵称
            multiUserChat.join(userName);
//            multiUserChat.add
            final List<MessageBean> messageBeanList = new ArrayList<>();
            //加入群聊的监听，然后可以监听到返回的信息，用广播传递回去改变界面
            mGroupMsgListener = new GroupMsgListener(context);
            multiUserChat.addMessageListener(mGroupMsgListener);

//            findMulitUser(multiUserChat);
//            getRoomInfor(roomNackName);
//            multiUserChat.sendMessage("dafasdf asdf sdaf ");
        } catch (SmackException.NoResponseException e) {
            //在加入房间的时候超时间
            MyToast.makeTextAnim(MyApplication.getContext(), "加入聊图失败,请重新加入", 0, R.style.PopToast).show();
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            //在加入房间的时候超时间
            MyToast.makeTextAnim(MyApplication.getContext(), "加入聊图失败,请重新加入", 0, R.style.PopToast).show();
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            //在加入房间的时候超时间
            MyToast.makeTextAnim(MyApplication.getContext(), "加入聊图失败,请重新加入", 0, R.style.PopToast).show();
            e.printStackTrace();
        }

    }

    /**
     * 离开一个带名字的房间
     */
    public void leveTheRoom(String roomNackName) {
        // Get the MultiUserChatManager
        manager = MultiUserChatManager.getInstanceFor(connection);
        // Create a MultiUserChat using an XMPPConnection for a room
        multiUserChat = manager.getMultiUserChat(roomNackName + "@muc." + Constants.XMPP_DOMU);

        try {
            multiUserChat.leave();
            multiUserChat.removeMessageListener(mGroupMsgListener);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /***
     * 发送群聊的消息
     * @param contont
     */
    public void sendGroupMessage(String contont) {
        try {
            if (multiUserChat != null) {
                multiUserChat.sendMessage(contont);
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    /***
     * 获取房间信息
     */
    public void getRoomInfor(String roomNackName) {
        // Get the MultiUserChatManager
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        RoomInfo info = null;
        try {
            info = manager.getRoomInfo(roomNackName + "@" + "muc." + Constants.XMPP_DOMU);
//            info.
            LogUtils.e("Number of occupants:" + info.getOccupantsCount());
            LogUtils.e("Room Subject:" + info.getSubject());
            LogUtils.e("Room getDescription:" + info.getDescription());
            LogUtils.e("Room getName:" + info.getName());
            LogUtils.e("Room getRoom:" + info.getRoom());
            LogUtils.e("Room getLang:" + info.getLang());
            LogUtils.e("Room getLdapGroup:" + info.getLdapGroup());
            LogUtils.e("Room getPubSub:" + info.getPubSub());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    class ThreadShow implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(60 * 1000);
                    if (!startPing) {
                        return;
                    }
                    if (!connection.isConnected()) {
                        connection.connect();
                    }
                    boolean ping = pingManager.pingMyServer();
                    Log.e(TAG, "pingMyServer ping： " + ping);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e(TAG, "pingMyServer Exception： " + e.getMessage());
                }
            }
        }
    }

    //Connection Listener to check connection state
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.e(TAG, "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
//                login();
            }
        }

        @Override
        public void connectionClosed() {
            Log.e(TAG, "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.e(TAG, "ConnectionClosedOn Error!");
            if (e.getMessage().contains("conflict")) { // 被挤掉线
                Log.e(TAG, "connectionClosedOnError  被挤下线");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        checkSession();
                    }
                });

                // 关闭连接，由于是被人挤下线，可能是用户自己，承以关闭连接，让用户重新登录是丿个比较好的鿉择
//			XmppTool.getInstance().closeConnection();
                // 接下来你可以通过发鿁一个广播，提示用户被挤下线，重连很箿单，就是重新登录
            } else if (e.getMessage().contains("Connection timed out")) {// 连接超时
                // 不做任何操作，会实现自动重连
                Log.e(TAG, "connectionClosedOnError  连接超时");
            }
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.e(TAG, "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Log.e(TAG, "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            Log.e(TAG, "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.e(TAG, "Authenticated!");
            loggedin = true;

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }
}
