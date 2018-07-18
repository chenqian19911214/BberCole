package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.Session;
import com.bber.company.android.bean.livebroadcast.BoradcastMessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.BroadcastBrowseDao;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.view.adapter.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.agora.AgoraAPIOnlySignal;

/**
 * 直播文字聊天 activity
 */
public class BroadcastChatActivity extends Activity {

    private AgoraAPIOnlySignal agoraAPI;
    private TextView textViewTitle;
    private EditText editText;
    private RecyclerView recyclerView;
    private List<BoradcastMessageBean> messageBeanList;
    private MessageAdapter adapter;

    private String username = "";
    private String myUserId = "";
    private boolean stateSingleMode = true; // single mode or channel mode
    private String account_id; //对方id
    private String ubHeadm; //对方头像
    private String json_msg;
    private String myIconuri;

    private SessionDao sessionDao;
    private BroadcastBrowseDao broadcastBrowseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_chat);
        broadcastBrowseDao = new BroadcastBrowseDao(this);
        InitUI();
        setupData();
    }

    public void onClickSend(View v) {
        String msg = editText.getText().toString();
        if (msg != null && !msg.equals("")) {
            BoradcastMessageBean messageBean = new BoradcastMessageBean(myIconuri, msg, "true");
            messageBeanList.add(messageBean);
            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
            recyclerView.scrollToPosition(messageBeanList.size() - 1);

            String jsonObject = getSendMessger(msg);

            saveDatad(msg);
            agoraAPI.messageInstantSend(account_id, 0, jsonObject, Constants.MKAgoraMessageTypeText + "");
        }
        editText.setText("");
    }

    private void saveDatad(String msg) {

        savelist(msg);

        savebroadcastmag(msg);

    }

    private void savebroadcastmag(String msg) {
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
        String time = sd.format(new Date());

        BoradcastMessageBean boradcastMessageBean = new BoradcastMessageBean();

        boradcastMessageBean.setBeSelf("true");
        boradcastMessageBean.seticonuri(myIconuri);
        boradcastMessageBean.setMessage(msg);
        boradcastMessageBean.setUserid(account_id);
        boradcastMessageBean.setTime(time);

        broadcastBrowseDao.insertSession(boradcastMessageBean);

    }

    private void savelist(String msg) {
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
        Session session = new Session();
        session.setContent(msg);
        session.setName(username); //对方名称
        session.setTime(sd.format(new Date())); // 发送时间
        session.setHeadURL(ubHeadm); // 对方头像
        session.setNotReadCount(0);//未读消息数量
        session.setType("text");
        String seller = myUserId + "seller";
        String toId = account_id + "buyer";
        session.setFrom(seller);  //自己id
        session.setTo(toId);//对方 id
        sessionDao.insertSession(session);

        Intent intent = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
        intent.putExtra("isCurrActivity", true);
        sendBroadcast(intent);
    }


    /**
     * 获取要发送的数据
     */
    private String getSendMessger(String message) {

        String username = SharedPreferencesUtils.get(this, Constants.USERNAME, "") + "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account_id", myUserId);
            jsonObject.put("messageType", Constants.MKAgoraMessageTypeText);
            jsonObject.put("message", message);
            jsonObject.put("ubName", username);
            jsonObject.put("ubHeadm", myIconuri);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 退出
     */
    public void onClickFinish(View v) {
        finish();
    }

    private void InitUI() {
        textViewTitle = (TextView) findViewById(R.id.message_title);
        editText = (EditText) findViewById(R.id.message_edittiext);
        recyclerView = (RecyclerView) findViewById(R.id.message_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
    }

    private void setupData() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MKAgoraMessageTypeText");
        registerReceiver(broadcastReceiver, intentFilter);

        sessionDao = new SessionDao(this);

        agoraAPI = MyApplication.getContext().getmAgoraAPI();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        myUserId = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        myIconuri = SharedPreferencesUtils.get(this, Constants.USERICON, "") + "";

        account_id = intent.getStringExtra("account");
        ubHeadm = intent.getStringExtra("ubHeadm");

        agoraAPI.queryUserStatus(account_id);


     /*   if (stateSingleMode) {
            MessageListBean messageListBean = Constant.getExistMesageListBean(username);
            if (messageListBean == null) {
                messageBeanList = new ArrayList<>();
            } else {
                messageBeanList = messageListBean.getMessageBeanList();
            }
        } else {
            messageBeanList = new ArrayList<>();
        }*/

        if (broadcastBrowseDao.queryAllSessions(account_id).size() > 0) {
            messageBeanList = broadcastBrowseDao.queryAllSessions(account_id);
        } else {
            messageBeanList = new ArrayList<>();
        }

        adapter = new MessageAdapter(this, messageBeanList);
        recyclerView.setAdapter(adapter);
        agoraAPI = MyApplication.getContext().getmAgoraAPI();
        if (stateSingleMode) {
            agoraAPI.queryUserStatus(username);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MKAgoraMessageTypeText")) {
                String type = intent.getStringExtra("type");
                switch (type) {
                    case "TypeText":// 接受文字消息
                        json_msg = intent.getStringExtra("json_msg");
                        try {
                            JSONObject jsonObject = new JSONObject(json_msg);
                            jsonObject.getString("ubName");

                            BoradcastMessageBean messageBean = new BoradcastMessageBean(jsonObject.getString("ubHeadm"), jsonObject.getString("message"), "false");
                            messageBeanList.add(messageBean);
                            adapter.notifyItemRangeChanged(messageBeanList.size(), 1);
                            recyclerView.scrollToPosition(messageBeanList.size() - 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "QueryUserStatus": //查询用户状态
                        String userstatus = intent.getStringExtra("status");

                        if (userstatus.equals("1")) {
                            textViewTitle.setText(username + "(" + "在线" + ")");

                        } else {
                            textViewTitle.setText(username + "(" + "离线" + ")");
                        }

                        break;
                }

            }
        }
    };
}
