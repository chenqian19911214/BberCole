package com.bber.company.android.view.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bber.company.android.ActivityCustomerBinding;
import com.bber.company.android.R;
import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.ChatMsgDao;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.view.adapter.ChatItemAdapter;
import com.bber.company.android.viewmodel.HeaderBarViewModel;
import com.bber.company.android.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vencent on 2017/3/22.
 */

public class CustomerActivity extends BaseActivity implements IactionListener<Object>, View.OnClickListener {


    public ChatInfo chatInfo;
    private ActivityCustomerBinding binding;
    private MessageViewModel messageViewModel;
    private ChatMsgDao msgDao;
    private List<ChatInfo> chatInfos;//私聊的历史纪录
    private RecyclerView recyclerView;
    private ChatItemAdapter chatItemAdapter;
    //切换到主线程
    android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (chatItemAdapter == null) {
                        chatItemAdapter = new ChatItemAdapter(CustomerActivity.this, chatInfos);
                        recyclerView.setAdapter(chatItemAdapter);
                    } else {
                        chatItemAdapter.setdata(chatInfo);
                    }
//            chat_edit.setText("");
//            chat_list.setSelection(chatInfos.size());

                    //针对数据库取聊天记录操作
                    chatInfo.setFromUser(messageViewModel.Other);
                    chatInfo.setSellerId(messageViewModel.sellerId);
                    chatInfo.setToUser(messageViewModel.I);
                    msgDao.insert(chatInfo);
                    messageViewModel.offset = chatInfos.size();
                    messageViewModel.updateSession(chatInfo.getMsg_type(), chatInfo.getContent() + "");
                    break;

                default:
                    break;
            }
        }

    };
    private Intent intent;
    private SessionDao sessionDao;
    private TextView text_respond;
    private TextView contacter;
    private TextView about_us;
    /***
     * 广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean setPresences = intent.getBooleanExtra("setPresences", false);

            //本地数据接收回来，然后改变界面
            String from = intent.getStringExtra("from");
            String seller = intent.getStringExtra("seller");
            if (Tools.isEmpty(from) || !from.equals(messageViewModel.Other)
                    || !seller.equals(String.valueOf(messageViewModel.sellerId))) {//推送消息时判断发送者是否对应
                return;
            }
            if (chatInfos == null) {
                chatInfos = new ArrayList<>();
            }
            ChatInfo chatInfo = (ChatInfo) intent.getSerializableExtra("chatInfo");
            chatInfos.add(chatInfo);
            Log.e("eeeeeeeeeeeeee", "chatInfos:" + chatInfos.size());
            if (chatItemAdapter == null) {
                chatItemAdapter = new ChatItemAdapter(CustomerActivity.this, chatInfos);
                recyclerView.setAdapter(chatItemAdapter);
            } else {
                chatItemAdapter.notifyItemInserted(chatItemAdapter.getItemCount());
            }
            recyclerView.smoothScrollToPosition(chatItemAdapter.getItemCount());
            Intent intent_ = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
            sendBroadcast(intent_);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer);
        messageViewModel = new MessageViewModel(this);
        messageViewModel.setActionListener(this);
        binding.setViewModel(messageViewModel);
        binding.setHeaderBarViewModel(headerBarViewModel);

        getIntentData();
        initView();
        initData();
        registerBoradcastReceiver();
    }

    private void getIntentData() {
        intent = getIntent();
        messageViewModel.Other = intent.getStringExtra("from");
        messageViewModel.sellerId = intent.getIntExtra("sellerId", -1);
        messageViewModel.sellerName = intent.getStringExtra("sellerName");
        messageViewModel.sellerHead = intent.getStringExtra("sellerHead");
        messageViewModel.buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        messageViewModel.I = messageViewModel.buyerId + "buyer";
    }

    private void initView() {
        msgDao = new ChatMsgDao(this);
        recyclerView = binding.customRecyclerview;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        messageViewModel.offset = 0;
        chatInfos = msgDao.queryMsg(String.valueOf(messageViewModel.sellerId), messageViewModel.I, messageViewModel.offset);
        chatItemAdapter = new ChatItemAdapter(this, chatInfos);
        recyclerView.setAdapter(chatItemAdapter);
        recyclerView.smoothScrollToPosition(chatItemAdapter.getItemCount());
        sessionDao = new SessionDao(this);

        text_respond = binding.textRespond;
        contacter = binding.contacter;
        about_us = binding.aboutUs;
        text_respond.setOnClickListener(this);
        contacter.setOnClickListener(this);
        about_us.setOnClickListener(this);
    }

    private void initData() {

        sessionDao.updateNoReadCount(String.valueOf(messageViewModel.sellerId), messageViewModel.I);//更新未读消息为0

        //XMPP协议增加添加好友
        MyXMPP.getInstance().addRosterItem(messageViewModel.Other, messageViewModel.Other, null);
//        messageViewModel.sendMsgText(getResources().getString(R.string.send_first_info));
        //买家选择就这个的时候调用增加意向数量
        messageViewModel.incrConnectCount();
        //插入一条系统提示
        SellerUserVo sellerUserVo = (SellerUserVo) intent.getSerializableExtra("sellerUserVo");
        messageViewModel.insertMsgToDao(sellerUserVo);

        Intent intent2 = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
        intent2.putExtra("isUpdate", true);
        sendBroadcast(intent2);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);         // 通知;
        mNotificationManager.cancel(Constants.NOTIFY_ID);
    }

    /***
     * 注册广播
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_MSG);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /***
     * 监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_change:
            case R.id.text_respond:
                Intent intent = new Intent(CustomerActivity.this, ResponseActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                Intent intent1 = new Intent(CustomerActivity.this, AboutUsActivity.class);
                startActivity(intent1);
                break;
            case R.id.contacter:
                //获取3212的妹子信息
//                messageViewModel.getGrilID();
                intent = new Intent(CustomerActivity.this, WebViewGuestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isHaveValue", true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("系统消息");
        headerBarViewModel.setOnClickListener(new HeaderBarViewModel.headerclickListener() {
            @Override
            public void leftClickListener(View v) {
                onBackPressed();
            }

            @Override
            public void rightClickListener(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    /***
     * 第一次返回信息改变UI界面
     *
     * @param o
     */
    @Override
    public void SuccessCallback(Object o) {
        //当时ChatInfo的时候，这时候应该是发送的信息，然后返回的数据
        if (o instanceof ChatInfo) {
            chatInfo = (ChatInfo) o;
            if (chatInfos == null) {
                chatInfos = new ArrayList<>();
            }
            chatInfos.add(chatInfo);
            Message msg = new Message();
            //给message对象赋值
            msg.what = 0;
            handler.sendMessage(msg);
//            if (!isAvailable) {//对方离线 需要发送推送到后台
//                Log.e("TAG", "用户发出一条离线消息");
//                //                       send(chatInfo.getContent());
//                buyerPushMessage(Other);
//            }

        }
        //当联系客服的时候，返回一个客服的SellerUserVo对象让我跳转chatActivity
        if (o instanceof SellerUserVo) {
            Intent intent = new Intent(CustomerActivity.this, ChatActivity.class);
            intent.putExtra("sellerUserVo", (SellerUserVo) o);
            intent.putExtra("fromProfile", false);
            startActivity(intent);
        }
    }

    @Override
    public void FailCallback(String result) {

    }

}
