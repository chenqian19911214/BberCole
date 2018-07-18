package com.bber.company.android.view.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;
import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.bean.MapBean;
import com.bber.company.android.bean.Order;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.bean.Session;
import com.bber.company.android.bean.adsBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.replyConstants;
import com.bber.company.android.db.ChatMsgDao;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.UploadImage;
import com.bber.company.android.tools.imageload.AudioGetFromHttp;
import com.bber.company.android.tools.imageload.ImageFileCache;
import com.bber.company.android.util.MapSupport;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.util.commonActivity;
import com.bber.company.android.view.adapter.ChatListAdapter;
import com.bber.company.android.view.adapter.ReplyListAdapter;
import com.bber.company.android.widget.DropdownListView;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.RecordButton;
import com.bber.customview.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.apache.http.Header;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChatActivity extends BaseAppCompatActivity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {

    private static List<adsBean> adsBeenList;
    private final String TAG = "ChatActivity";
    public RecordButton chat_voice;
    EditText.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                if (view_add.getVisibility() == View.VISIBLE) {
                    view_add.setVisibility(View.GONE);
                }
            }
        }
    };
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Tools.hideKb(ChatActivity.this);
            if (view_add.getVisibility() == View.VISIBLE) {
                view_add.setVisibility(View.GONE);
            }
            return false;
        }
    };
    /**
     * * 执行发送消息 发送订单(隐形发送，不显示在页面)
     */
    JSONObject jsonObject = null;
    private ImageView btn_add, chat_type, btn_reply;
    private TextView btn_send, old_price, new_price, activity_name, sum_order, btn_pay, tv_status, activity_tittle;
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!Tools.isEmpty(s + "")) {
                if (btn_send.getVisibility() == View.GONE) {
                    btn_send.setVisibility(View.VISIBLE);
                }
                if (btn_add.getVisibility() == View.VISIBLE) {
                    btn_add.setVisibility(View.GONE);
                }
            } else {
                if (btn_send.getVisibility() == View.VISIBLE) {
                    btn_send.setVisibility(View.GONE);
                }
                if (btn_add.getVisibility() == View.GONE) {
                    btn_add.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private LinearLayout view_edit, view_add, view_album, view_location, lv_price;
    private RelativeLayout view_order, view_focus;
    private EditText chat_edit;
    private DropdownListView chat_list;
    private List<ChatInfo> chatInfos;
    private ChatListAdapter chatListAdapter;
    private RecyclerView recyclerView;
    private ReplyListAdapter replyListAdapter;
    private String I, Other;//为了好区分，I就是自己，Other就是对方
    private SimpleDateFormat sd;
    private JsonUtil jsonUtil;
    private File uploadFile;
    private ImageFileCache imageFileCache;
    private ChatMsgDao msgDao;
    private SessionDao sessionDao;
    private int offset;
    private String orderID;
    private int ID;
    private int status;////订单状态 1:创建订单完成  2：买家已付款(接受订单) 3：开始服务 4：服务结束未评价 5：买家已评价
    private int money, finalPrice;
    private String buyerOrderNumber;
    private int sellerId, buyerId;
    private String buyerName, buyerHead, sellerName, sellerHead;
    private Order order;
    private boolean isAvailable;
    private int height, width;
    private PoiItem mPoiItem;//poi结果
    private MapBean mMapBean;//poi结果
    private String sendMsgType;
    private AudioGetFromHttp aduio = new AudioGetFromHttp(this);
    private int discountMoney = 0;
    private String activityname = "";
    private boolean isOrderTittle = false;
    private boolean canClickTittle = true;
    //设置录音存放路径
//    private String fileName;
    private String eventStatus = "0";
    private ImageView iv_ads_close;
    private RelativeLayout rl_ads;
    private SimpleDraweeView ads_picture;
    private int indexAds;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    uploadFile();
                    break;
                case 1:
                    ChatInfo chatInfo = (ChatInfo) msg.obj;
                    if (chatInfos == null) {
                        chatInfos = new ArrayList<>();
                    }
                    chatInfos.add(chatInfo);
                    if (chatListAdapter == null) {
                        chatListAdapter = new ChatListAdapter(ChatActivity.this, chatInfos);
                        chat_list.setAdapter(chatListAdapter);
                    } else {
                        chatListAdapter.updateListView(chatInfos);
                    }
                    chat_edit.setText("");
                    chat_list.setSelection(chatInfos.size());

                    //针对数据库取聊天记录操作
                    chatInfo.setFromUser(Other);
                    chatInfo.setSellerId(sellerId);
                    chatInfo.setToUser(I);
//                    if (!sessionDao.isContent("571",)){
//
//                    }
                    msgDao.insert(chatInfo);
                    offset = chatInfos.size();
                    updateSession(chatInfo.getMsg_type(), chatInfo.getContent() + "");

                    if (!isAvailable) {//对方离线 需要发送推送到后台
                        Log.e("TAG", "用户发出一条离线消息");
                        //                       send(chatInfo.getContent());
                        buyerPushMessage(Other);
                    }
                    break;
                case 2:
                    setNewAdsItem(msg.arg1);
                    break;
                case 3:
                    ChatInfo chat = (ChatInfo) msg.obj;
                    if (chatInfos == null) {
                        chatInfos = new ArrayList<>();
                    }
                    chatInfos.add(chat);
                    if (chatListAdapter == null) {
                        chatListAdapter = new ChatListAdapter(ChatActivity.this, chatInfos);
                        chat_list.setAdapter(chatListAdapter);
                    } else {
                        chatListAdapter.updateListView(chatInfos);
                    }
                    break;
                default:
                    break;
            }
        }

    };
    /**
     * 按住录音监听
     * *
     */
    RecordButton.OnFinishedRecordListener finishedRecordListener = new RecordButton.OnFinishedRecordListener() {
        @Override
        public void onFinishedRecord(String audioPath) {
            Log.e("eeeeeeeeeeeeee", "chat_voice.mFileName:" + chat_voice.mFileName);
            if (!Tools.isEmpty(chat_voice.mFileName)) {
                File file = new File(chat_voice.mFileName);
                if (!file.exists()) {
                    MyToast.makeTextAnim(ChatActivity.this, R.string.record_failed, 0, R.style.PopToast).show();
                    return;
                }
                sendMsgType = Constants.MSG_TYPE_VOICE;
                uploadFile = file;
                handler.obtainMessage(0).sendToTarget();
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean setPresences = intent.getBooleanExtra("setPresences", false);
            if (setPresences) {//更新好友在线状态
                isAvailable = MyXMPP.getInstance().isAvailable(Other);
                if (isAvailable) {
                    status_point.setTextColor(Color.parseColor("#19D908"));
                    status_point.setText("在线");
                } else {
                    status_point.setTextColor(Color.parseColor("#BDC2C0"));
                    status_point.setText("离线");
                }
                return;
            }

            boolean isCurrActivity = intent.getBooleanExtra("isCurrActivity", false);
            if (isCurrActivity) {//当前页面发送的广播 不用处理
                return;
            }

            int isOrder = intent.getIntExtra("isOrder", -1);
            if (isOrder == 1) {//订单
                status = intent.getIntExtra("status", -1);
                order = (Order) intent.getSerializableExtra("order");
                sellerId = intent.getIntExtra("sellerId", sellerId);
                String seller = String.valueOf(sellerId);
                //如果当前订单不存在的话
                if (order != null) {
                    money = order.getMoney();
                    finalPrice = money;
                    ID = order.getId();
                    buyerOrderNumber = order.getBuyerOrderNumber();
                    sellerId = order.getSellerId();
                    orderID = order.getBusinessCode();
                }
                if (order != null && seller.equals(String.valueOf(order.getSellerId()))
                        || status == 4 || status == 6) {
                    initOrderInfo();
                }

            } else if (isOrder == 2) {
                getOrderInfo();
            } else {
                //本地数据接收回来，然后改变界面
                String from = intent.getStringExtra("from");
                String seller = intent.getStringExtra("seller");
                if (Tools.isEmpty(from) || !from.equals(Other)
                        || !seller.equals(String.valueOf(sellerId))) {//推送消息时判断发送者是否对应
                    return;
                }
                if (chatInfos == null) {
                    chatInfos = new ArrayList<>();
                }
                ChatInfo chatInfo = (ChatInfo) intent.getSerializableExtra("chatInfo");
                chatInfos.add(chatInfo);
                Log.e("eeeeeeeeeeeeee", "chatInfos:" + chatInfos.size());
                if (chatListAdapter == null) {
                    chatListAdapter = new ChatListAdapter(ChatActivity.this, chatInfos);
                    chat_list.setAdapter(chatListAdapter);
                } else {
                    chatListAdapter.updateListView(chatInfos);
                }
            }
        }

    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData(getIntent());
    }

    private void initViews() {
        view_edit = findViewById(R.id.view_edit);
        view_location = findViewById(R.id.view_location);
        old_price = findViewById(R.id.old_price);
        new_price = findViewById(R.id.new_price);
        activity_name = findViewById(R.id.activity_name);
        sum_order = findViewById(R.id.sum_order);
        chat_type = findViewById(R.id.chat_type);
        btn_reply = findViewById(R.id.btn_reply_fast);
        btn_add = findViewById(R.id.btn_add);
        btn_send = findViewById(R.id.btn_send);
        view_add = findViewById(R.id.view_add);
        view_album = findViewById(R.id.view_album);
        view_order = findViewById(R.id.view_order);
        activity_tittle = findViewById(R.id.activity_tittle);
        tv_status = findViewById(R.id.tv_status);
        lv_price = findViewById(R.id.lv_price);
        view_focus = findViewById(R.id.view_focus);
        chat_edit = findViewById(R.id.chat_edit);
        chat_voice = findViewById(R.id.chat_voice);
        chat_list = findViewById(R.id.chat_list);
        btn_pay = findViewById(R.id.btn_pay);
        ads_picture = findViewById(R.id.ads_picter);
        rl_ads = findViewById(R.id.rl_ads);
        iv_ads_close = findViewById(R.id.iv_closed);
        chat_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        jsonUtil = new JsonUtil(this);
        imageFileCache = new ImageFileCache();
        msgDao = new ChatMsgDao(this);
        sessionDao = new SessionDao(this);

        registerBoradcastReceiver();
    }

    private void setNewAdsItem(int arg1) {

        if (adsBeenList == null || adsBeenList.size() == 0) {
            return;
        }
        if (arg1 >= adsBeenList.size()) {
            arg1 = 0;
        }
        view_order.setVisibility(View.GONE);
        ads_picture.setVisibility(View.VISIBLE);
        rl_ads.setVisibility(View.VISIBLE);
        indexAds = arg1;
        String uriTarget = adsBeenList.get(arg1).getAdPicture();
        if (!Tools.isEmpty(uriTarget)) {
            Uri uri = Uri.parse(uriTarget);
            ads_picture.setImageURI(uri);
            ads_picture.setVisibility(View.VISIBLE);
            rl_ads.setVisibility(View.VISIBLE);
        } else {
            ads_picture.setVisibility(View.GONE);
            rl_ads.setVisibility(View.GONE);
        }
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = arg1 + 1;
        handler.sendMessageDelayed(msg, 3000);
    }

    private void setorderyVisible() {
        view_order.setVisibility(View.VISIBLE);
        lv_price.setVisibility(View.VISIBLE);
        sum_order.setVisibility(View.VISIBLE);
        tv_status.setVisibility(View.VISIBLE);
        btn_pay.setVisibility(View.VISIBLE);
        activity_tittle.setVisibility(View.GONE);
    }

    private void showTittleVisable() {
        view_order.setVisibility(View.VISIBLE);
        lv_price.setVisibility(View.GONE);
        sum_order.setVisibility(View.GONE);
        tv_status.setVisibility(View.GONE);
        btn_pay.setVisibility(View.GONE);
        activity_tittle.setVisibility(View.VISIBLE);
        view_order.setOnClickListener(this);
    }

    /**
     * 活动的优惠
     */
    private void setorderDisountInfo() {
        old_price.setVisibility(View.GONE);
        old_price.setText("￥" + money);
        activity_name.setVisibility(View.GONE);
        old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("sellerId", sellerId);
        HttpUtil.post(Constants.getInstance().getDiscount, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        discountMoney = dataCollection.getInt("discountC");
                        if (discountMoney > 0) {
                            old_price.setVisibility(View.VISIBLE);
                            activity_name.setVisibility(View.VISIBLE);
                            activity_name.setText(getResources().getString(R.string.give_custom_money)
                                    + discountMoney + getResources().getString(R.string.yuan));
                            finalPrice = money - discountMoney > 0 ? money - discountMoney : 0;
                            new_price.setText("" + finalPrice);
                        }
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getOrderById onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(ChatActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private void setListeners() {
        btn_add.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        chat_edit.setOnFocusChangeListener(focusChangeListener);
        chat_edit.addTextChangedListener(watcher);
        chat_list.setOnRefreshListenerHead(this);
        chat_list.setOnTouchListener(touchListener);
        btn_pay.setOnClickListener(this);
        chat_type.setOnClickListener(this);
        btn_reply.setOnClickListener(this);
        chat_voice.setOnFinishedRecordListener(finishedRecordListener);
        view_album.setOnClickListener(this);
        view_location.setOnClickListener(this);
        ads_picture.setOnClickListener(this);
        iv_ads_close.setOnClickListener(this);
    }

    private void initData(Intent intent) {
        SharedPreferencesUtils.put(this, "chatPause", false);
        SharedPreferencesUtils.put(this, "chatOnline", true);
        sd = new SimpleDateFormat("MM-dd HH:mm");
        mMapBean = new MapBean();
        buyerName = SharedPreferencesUtils.get(this, Constants.USERNAME, "") + "";
        buyerHead = SharedPreferencesUtils.get(this, Constants.HEADURL, "") + "";
        buyerId = Tools.StringToInt(SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "");
        I = buyerId + "buyer";
        SellerUserVo sellerUserVo = (SellerUserVo) intent.getSerializableExtra("sellerUserVo");
        if (sellerUserVo != null) {//从profile进来
            Other = sellerUserVo.getOrganiId() + "seller";
            sellerId = sellerUserVo.getuSeller();
            sellerName = sellerUserVo.getUsName();
            sellerHead = sellerUserVo.getUsHeadm();
        } else {//从消息列表进来
            Other = intent.getStringExtra("from");
            sellerId = intent.getIntExtra("sellerId", -1);
            sellerName = intent.getStringExtra("sellerName");
            sellerHead = intent.getStringExtra("sellerHead");
        }

        SharedPreferencesUtils.put(this, Constants.CHAT_SELLER_ID, String.valueOf(sellerId));
        Log.e("eeeeeeeeeeeeeee", "Other:" + Other);
        boolean fromProfile = intent.getBooleanExtra("fromProfile", false);
        if (fromProfile) {//如果是首次进入 则默认发送一个名片消息到B端
            MyXMPP.getInstance().addRosterItem(Other, Other, null);
//            sendMsgText(getResources().getString(R.string.send_first_info));
            //买家选择就这个的时候调用增加意向数量
            incrConnectCount();
            //插入一条系统提示
            insertMsgToDao(sellerUserVo);
        }

        //加载聊天记录
        offset = 0;
        chatInfos = msgDao.queryMsg(String.valueOf(sellerId), I, offset);
        offset = chatInfos.size();
        chatListAdapter = new ChatListAdapter(ChatActivity.this, chatInfos);
        chat_list.setAdapter(chatListAdapter);

        chatListAdapter.setRecoreButton(chat_voice);
        chat_list.setSelection(chatInfos.size());
        getLongClickItem();//长按的选项
        sessionDao.updateNoReadCount(String.valueOf(sellerId), I);//更新未读消息为0
        Intent intent2 = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
        intent2.putExtra("isUpdate", true);
        sendBroadcast(intent2);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);         // 通知;
        mNotificationManager.cancel(Constants.NOTIFY_ID);
        //  }

        if ("10000".equals(Other)) {
            view_order.setVisibility(View.GONE);
            view_edit.setVisibility(View.GONE);
            title.setText("系统消息");
            return;
        }
        Log.e("eeeeeeeeeeeeeeeee", "Other:" + Other + "--I:" + I);
        //设置录音存放路径
        // setVoicePath();
        //加载头像
        title.setText(sellerName);

        status_point.setVisibility(View.VISIBLE);
        isAvailable = MyXMPP.getInstance().isAvailable(Other);
        if (isAvailable) { //在线离线问题
            status_point.setTextColor(Color.parseColor("#19D908"));
            status_point.setText("在线");
        } else {
            status_point.setTextColor(Color.parseColor("#BDC2C0"));
            status_point.setText("离线");

        }
        // 生成订单号
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmsssss");
        int num = new Random().nextInt(9999 - 1000 + 1) + 1000;//生成4位随机数字
        orderID = format.format(new Date()) + num;
        getOrderInfo();
    }

    private void insertMsgToDao(SellerUserVo sellerUserVo) {
        if (sellerUserVo != null) {
            String message = "";
            message = "温馨提示：平台美模确保安全可靠，请使用平台发起订单，充值全款或定金预约美模，详情请咨询app内客服";
            ChatInfo chatInfo = getChatInfoTo(message, Constants.SYSTEM_TIP);
            chatInfo.setFromUser(Other);
            chatInfo.setSellerId(sellerId);
            chatInfo.setToUser(I);
            msgDao.insert(chatInfo);
            updateSession(Constants.SYSTEM_TIP, message);
        }
    }

    /**
     * 下拉加载更多
     */
    @Override
    public void onRefresh() {
        List<ChatInfo> infos = msgDao.queryMsg(String.valueOf(sellerId), I, offset);
        Log.e("eeeeeeeeeeeeeeeee", "infos:" + infos.size());
        if (infos.size() <= 0) {
            chat_list.setSelection(0);
            chat_list.onRefreshCompleteHeader();
            return;
        }
        chatInfos.addAll(0, infos);
        offset = chatInfos.size();
        chat_list.onRefreshCompleteHeader();
        chatListAdapter.notifyDataSetChanged();
        chat_list.setSelection(infos.size());
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_MSG);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private void getOrderInfo() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("sellerId", sellerId);
        params.put("buyerId", buyerId);
        HttpUtil.post(Constants.getInstance().getOrderById, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "getOrderById onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(ChatActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        getAdStatus();
                        return;
                    }
                    order = jsonUtil.jsonToOrder(dataCollection);
                    if (order != null && order.getBusinessCode() != null) {//已有订单
                        ID = order.getId();
                        orderID = order.getBusinessCode();
                        status = order.getStatus();
                        money = order.getMoney();
                        finalPrice = money;
                        buyerOrderNumber = order.getBuyerOrderNumber();
                        eventStatus = order.getEventStatus();

                        old_price.setVisibility(View.GONE);
                        old_price.setText("￥" + money);
                        activity_name.setVisibility(View.GONE);
                        old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        if ("1".equals(eventStatus)) {
                            setorderDisountInfo();
                        } else if (Tools.StringToInt(order.getCashCardMoney()) > 0) {
                            setOrderByCashCard(order.getCashCardName(), order.getCashCardMoney());
                        } else {
                            old_price.setVisibility(View.GONE);
                            old_price.setText("￥" + money);
                            activity_name.setVisibility(View.GONE);
                            old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        }

                        initOrderInfo();
                    } else {
                        getAdStatus();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getOrderById onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(ChatActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 代金券的优惠
     *
     * @param enventTittle
     * @param enventMoney
     */
    private void setOrderByCashCard(String enventTittle, String enventMoney) {
        old_price.setVisibility(View.GONE);
        old_price.setText("￥" + money);
        activity_name.setVisibility(View.GONE);
        old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        discountMoney = Tools.StringToInt(enventMoney);
        if (discountMoney > 0) {
            old_price.setVisibility(View.VISIBLE);
            activity_name.setVisibility(View.VISIBLE);
            activity_name.setText(enventTittle);
            finalPrice = money - discountMoney > 0 ? money - discountMoney : 0;
            new_price.setText("" + finalPrice);
        }
    }

    private void incrConnectCount() {
        if (!netWork.isNetworkConnected()) {
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("sellerId", sellerId);
        final String str = sellerId + "bber";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().incrConnectCount, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(ChatActivity.this, jsonObject, null)) {
                    return;
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(ChatActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /***
     * 发送短信
     * @param organiID
     */
    private void buyerPushMessage(String organiID) {
        if (organiID == null) {
            return;
        }
        String otherNum = organiID;
        otherNum = otherNum.replace("seller", "");
        final JsonUtil jsonUtil = new JsonUtil(this);
        RequestParams params = new RequestParams();
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("organiID", otherNum);
        HttpUtil.post(Constants.getInstance().buyerPushMessage, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "incrConnectCount onSuccess--jsonObject:" + jsonObject);
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "faild");
            }

            @Override
            public void onFinish() {
                Log.e("eeeeeeeeeeee", "finish");
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("eeeeeeeeeeeeeeeee", "--------------chat  onRestart");
        SharedPreferencesUtils.put(this, "chatPause", false);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);         // 通知;
        mNotificationManager.cancel(Constants.NOTIFY_ID);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("eeeeeeeeeeeeeeeee", "--------------chat  onNewIntent");
        initData(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferencesUtils.put(this, "chatPause", true);
        if (chatListAdapter != null && chatListAdapter.mPlayer != null) {
            chatListAdapter.mPlayer.stop();
            chatListAdapter.mPlayer.release();
            chatListAdapter.mPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtils.put(this, "chatOnline", false);
        SharedPreferencesUtils.put(this, Constants.CHAT_SELLER_ID, "");
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initOrderInfo() {
        setorderyVisible();
        new_price.setText("" + finalPrice);
        sum_order.setText("第" + buyerOrderNumber + "单");
        Intent intent;
        switch (status) {
            case -1://没有订单
                view_order.setVisibility(View.GONE);
                break;
            case 1://收到订单
                canClickTittle = true;
                isOrderTittle = true;
                showTittleVisable();
                activity_tittle.setText(R.string.newOrder);
                break;
            case 3://显示服务中
                canClickTittle = false;
                break;
            case 4://显示服务结束
                //删除聊天记录
                canClickTittle = false;
                msgDao.deleteMsgById(String.valueOf(sellerId), I);
                sessionDao.deleteSession(String.valueOf(sellerId), I);
                MyXMPP.getInstance().removeRosterItem(Other);
                Intent intent2 = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
                intent2.putExtra("isUpdate", true);
                intent2.putExtra("isCurrActivity", true);
                sendBroadcast(intent2);
                intent = new Intent(this, RatingActivity.class);
                intent.putExtra("status", status);
                intent.putExtra("ID", ID);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("buyerId", buyerId);
                intent.putExtra("sellerName", sellerName);
                intent.putExtra("sellerHead", sellerHead);
                startActivity(intent);
                finish();
                break;
            case 6://取消订单--跳转到评价页
                canClickTittle = false;
                msgDao.deleteMsgById(String.valueOf(sellerId), I);
                sessionDao.deleteSession(String.valueOf(sellerId), I);
                MyXMPP.getInstance().removeRosterItem(Other);
                intent = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
                intent.putExtra("isUpdate", true);
                intent.putExtra("isCurrActivity", true);
                sendBroadcast(intent);
                view_order.setVisibility(View.GONE);
                Intent intent1 = new Intent(this, RatingActivity.class);
                intent1.putExtra("status", status);
                intent1.putExtra("ID", ID);
                intent1.putExtra("sellerId", sellerId);
                intent1.putExtra("buyerId", buyerId);
                intent1.putExtra("sellerName", sellerName);
                intent1.putExtra("sellerHead", sellerHead);
                startActivity(intent1);
                finish();
                break;
//            case 1://显示付款
//                order_cancle.setVisibility(View.VISIBLE);
//                btn_start.setVisibility(View.GONE);
//                btn_pay.setText("付款");
//                btn_pay.setBackgroundResource(R.drawable.btn_gray_black);
//                break;
//            case 2://显示开始服务
//                order_cancle.setVisibility(View.VISIBLE);
//                btn_start.setVisibility(View.VISIBLE);
//                btn_pay.setVisibility(View.GONE);
//                if (payProgressDialog != null && payProgressDialog.isShowing()) {
//                    payProgressDialog.dismiss();
//                }
//                break;
        }
    }

    // ProgressDialog payProgressDialog;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.chat_type:
                if (chat_type.isSelected()) {//切换为键盘输入
                    chat_type.setSelected(false);
                    chat_voice.setVisibility(View.GONE);
                    chat_edit.setVisibility(View.VISIBLE);
                    view_focus.setFocusable(false);
                    view_focus.setFocusableInTouchMode(false);
                    view_focus.clearFocus();

                    chat_edit.setFocusable(true);
                    chat_edit.setFocusableInTouchMode(true);
                    chat_edit.requestFocus();
                    Tools.showKb(chat_edit);
                } else {//切换为语音输入
                    chat_type.setSelected(true);
                    chat_voice.setVisibility(View.VISIBLE);
                    chat_edit.setVisibility(View.GONE);
                    view_focus.setFocusable(true);
                    view_focus.setFocusableInTouchMode(true);
                    view_focus.requestFocus();
                    Tools.hideKb(this);

                }
                break;
            case R.id.btn_send:
                if (!netWork.isNetworkConnected()) {
                    ToastUtils.showToast(R.string.no_network, 0);
                    return;
                }
                String chatMsg = chat_edit.getText() + "";
                LogUtils.e("聊天内容：" + chatMsg);
//                if (Tools.IsContainKeyWord(chatMsg, preferenceConstants.KEY_WORD_CHAT)) {
//                    ToastUtils.showToast(R.string.illege_key_word_chat, 0);
//                    return;
//                }

                sendMsgText(chatMsg);
                break;
            case R.id.btn_add:
                if (view_add.getVisibility() == View.GONE) {
                    view_add.setVisibility(View.VISIBLE);
                    Log.e("eeeeeeeeeeeeee", "chat_edit.isFocused(:" + chat_edit.isFocused());
                    if (chat_edit.isFocused()) {
                        view_focus.setFocusable(true);
                        view_focus.setFocusableInTouchMode(true);
                        view_focus.requestFocus();
                        Tools.hideKb(this);
                    }
                } else {
                    view_add.setVisibility(View.GONE);

                    if (chat_voice.getVisibility() == View.VISIBLE) {
                        chat_voice.setVisibility(View.GONE);
                    }

                    if (chat_edit.getVisibility() == View.GONE) {
                        chat_edit.setVisibility(View.VISIBLE);
                    }
                    view_focus.setFocusable(false);
                    view_focus.setFocusableInTouchMode(false);
                    view_focus.clearFocus();
                    chat_edit.setFocusable(true);
                    chat_edit.setFocusableInTouchMode(true);
                    chat_edit.requestFocus();

                    chat_type.setSelected(false);
                    Tools.showKb(chat_edit);

                }

                break;
            case R.id.view_album:
                uploadFile = imageFileCache.initUploadFile();
                if (uploadFile != null && uploadFile.exists()) {
                    uploadFile.delete();
                }
                view_add.setVisibility(View.GONE);
                final View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_upload_bottom, null);
                final AlertDialog dialog = DialogTool.createUploadDialog(this, view);
                view.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        UploadImage.upload(ChatActivity.this, 0);
                    }
                });
                view.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        UploadImage.upload(ChatActivity.this, 1);
                    }
                });
                break;
            case R.id.view_location:
                Intent intentMap = new Intent(this, MaplocationActivity.class);
                intentMap.putExtra("showType", 1);
                startActivityForResult(intentMap, Constants.REQUEST_CODE_SEND_LOCATION);
                break;
            case R.id.btn_reply_fast:
                setFastReplyAlertDialog();
                break;
            case R.id.view_order:
                if (canClickTittle == false) {
                    return;
                }
                if (isOrderTittle == false) {
                    String infoURL = (String) SharedPreferencesUtils.get(this, "infoURL", "");
                    if (!Tools.isEmpty(infoURL)) {
                        Intent intent = new Intent(ChatActivity.this, webViewActivity.class);
                        intent.putExtra("url", infoURL);
                        intent.putExtra("activityname", activityname);
                        startActivity(intent);
                    }
                } else {
                    Intent intent1 = new Intent(ChatActivity.this, ChoosePayActivity.class);
                    intent1.putExtra("totalPrice", finalPrice);
                    intent1.putExtra("businessCode", orderID);
                    intent1.putExtra("sellerId", sellerId);
                    intent1.putExtra("sellerName", sellerName);
                    intent1.putExtra("sellerHead", sellerHead);
                    intent1.putExtra("ID", ID);
                    startActivity(intent1);
                }
                break;
            case R.id.ads_picter:
                commonActivity.startBrowebuyURL(this, adsBeenList.get(indexAds));
                break;
            case R.id.iv_closed:
                rl_ads.setVisibility(View.GONE);
                handler.removeMessages(2);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_PHOTO_ALBUM:
                    if (data != null) {
                        String path = null;
                        Uri selectedImage = data.getData();
                        selectedImage = Tools.getImageuri(this, selectedImage);
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("eeeeeeeeeeeeeeeeee", "path:" + path);
                        if (path == null) {
                            return;
                        }
                        comp(path);
                    }
                    break;
                case Constants.REQUEST_CODE_PHOTO_GRAPH:
                    uploadFile = imageFileCache.initUploadFile();
                    Log.e("eeeeeeeeeeeeeeeeee", "imageFile:" + uploadFile);
                    comp(uploadFile.getPath());
                    break;
                case 1:
                    Log.e("eeeeeeeeeeeeee", "onActivityResult");
                    DialogTool.createProgressDialog(this, true);
                    break;
                case Constants.REQUEST_CODE_SEND_LOCATION:
                    mPoiItem = data.getParcelableExtra("poiItem");
                    poiFormatToMapBean();
                    String address = Tools.connectAddress(mPoiItem.getCityName())
                            + Tools.connectAddress(mPoiItem.getAdName())
                            + Tools.connectAddress(mPoiItem.getBusinessArea())
                            + Tools.connectAddress(mPoiItem.getSnippet())
                            + mPoiItem.getTitle();
                    sendMsgLocation(address);
            }
        }
    }

    /**
     * 转换成mapbean
     */
    private void poiFormatToMapBean() {
        mMapBean.setLatitude(mPoiItem.getLatLonPoint().getLatitude());
        mMapBean.setLonPoint(mPoiItem.getLatLonPoint().getLongitude());
        mMapBean.setadName(mPoiItem.getAdName());
        mMapBean.setBusinessArea(mPoiItem.getBusinessArea());
        mMapBean.setCityName(mPoiItem.getCityName());
        mMapBean.setmSnippet(mPoiItem.getSnippet());
        mMapBean.setmTitle(mPoiItem.getTitle());
        mMapBean.setProvinceName(mPoiItem.getProvinceName());
    }

    /**
     * 压缩要上传的图片
     */
    private void comp(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                newOpts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);//此时返回bm为空
                newOpts.inJustDecodeBounds = false;
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                float hh = 800f;//这里设置高度为800f
                float ww = 480f;//这里设置宽度为480f
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outWidth / ww);

                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) Math.rint(newOpts.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                newOpts.inSampleSize = be;//设置缩放比例
                Log.e("eeeeeeeeeeee", "图片压缩倍数be:" + be);
                //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                bitmap = BitmapFactory.decodeFile(path, newOpts);
                int digree = Tools.getImageDigree(path);
                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), m, true);
                }
                if (bitmap != null) {
                    height = bitmap.getHeight();
                    width = bitmap.getWidth();
                }

                File file1 = imageFileCache.initUploadFile();
                saveBitmap(bitmap, file1);
                sendMsgType = Constants.MSG_TYPE_IMG;
                handler.obtainMessage(0).sendToTarget();
            }
        }).start();

    }

    /**
     * 将图片存入文件 *
     */
    public void saveBitmap(Bitmap bitmap, File file1) {
        try {
            if (file1 == null) {
                return;
            }
            if (file1.exists()) {
                file1.delete();
            }
            file1.createNewFile();
            OutputStream outStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            Log.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            Log.w("ImageFileCache", "IOException");
        } finally {
        }

    }

    /**
     * 上传文件
     * *
     */
    private void uploadFile() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        DialogTool.createProgressDialog(this, true);
        RequestParams params = new RequestParams();
        try {
            params.put("file", uploadFile);
        } catch (FileNotFoundException e) {
            ToastUtils.showToast(R.string.no_file_fail, 0);
            DialogTool.dismiss(this);
            return;
        }
        HttpUtil.post(Constants.getInstance().sendFile, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "sendImage onSuccess--jsonObject:" + jsonObject);
                try {
                    String responsecode = jsonObject.getString("responsecode");
                    if (!responsecode.equals("SUCCESS")) {
                        MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                        return;
                    }
                    String URL = jsonObject.getString("data");
                    UploadFinish(URL);
                    uploadFile.delete();
                } catch (JSONException e) {
                    MyToast.makeTextAnim(ChatActivity.this, R.string.operate_fail, 0, R.style.PopToast).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "sendImage onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(ChatActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(ChatActivity.this);
            }
        });
    }

    private void UploadFinish(String URL) {
        if (Constants.MSG_TYPE_VOICE.equals(sendMsgType)) {
            chat_voice.mFileName = URL;
            sendMsgVoice(URL);
            aduio.saveFileToCache(uploadFile, URL);
        } else {
            sendMsgImage(URL);
        }
    }

    /**
     * 推送消息
     * *
     */
    private void send(String content) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("id", Other);
        params.put("msg_type", 1);
        params.put("msg", content);
        final String str = Other + "" + 1 + "" + content;
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().send, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "send onSuccess--jsonObject:" + jsonObject);
//                if (Tools.jsonResult(ChatActivity.this, jsonObject, progressDialog)) {
//                    return;
//                }
//                try {
//                    String responsecode = jsonObject.getString("responsecode");
//                    if (!responsecode.equals("SUCCESS")) {
//                        MyToast.makeTextAnim(ChatActivity.this, R.string.sendimage_fail, 0, R.style.PopToast).show();
//                        return;
//                    }
//                    String URL = jsonObject.getString("data");
//                    sendMsgImage(URL);
//                } catch (JSONException e) {
//
//                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(ChatActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
            }
        });
    }

    /**
     * * 执行发送消息 文本类型
     */
    void sendMsgText(String content) {
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
                        handler.obtainMessage(1, chatInfo).sendToTarget();
                    } else {
                        Looper.prepare();
                        MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                    }

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Looper.loop();
                    MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                }
            }
        }).start();
    }

    /**
     * * 执行发送消息 名片类型
     */
    void sendMsgCard(String content) {
        final ChatInfo chatInfo = getChatInfoTo(content, Constants.MSG_TYPE_CARD);
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
                        handler.obtainMessage(1, chatInfo).sendToTarget();
                    } else {
                        Looper.prepare();
                        MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                    }

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Looper.loop();
                    MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                }
            }
        }).start();
    }

    /**
     * * 执行发送消息
     */
    void sendMsgLocation(String content) {
        final ChatInfo chatInfo = getChatInfoTo(content, Constants.MSG_TYPE_LOCATION);
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
                        handler.obtainMessage(1, chatInfo).sendToTarget();
                    } else {
                        Looper.prepare();
                        MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                    }

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Looper.loop();
                    MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                }
            }
        }).start();
    }

    /**
     * * 执行发送消息 图片
     */
    void sendMsgImage(String URL) {
        final ChatInfo chatInfo = getChatInfoTo(URL, Constants.MSG_TYPE_IMG);
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
                        handler.obtainMessage(1, chatInfo).sendToTarget();
                    } else {
                        Looper.prepare();
                        MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                    }

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Looper.loop();
                    MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                }
            }
        }).start();
    }

    /**
     * * 执行发送消息 图片
     */
    void sendMsgVoice(String URL) {
        final ChatInfo chatInfo = getChatInfoTo(URL, Constants.MSG_TYPE_VOICE);
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
                        handler.obtainMessage(1, chatInfo).sendToTarget();
                    } else {
                        Looper.prepare();
                        MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                    }

                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Looper.loop();
                    MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                }
            }
        }).start();
    }

    void sendOrder(final JSONObject jsonObject1) {
        try {
            jsonObject = new JSONObject();
            jsonObject.put("msg_type", Constants.MSG_TYPE_ORDER);
            jsonObject.put("status", status);
            jsonObject.put("orderID", orderID);
            jsonObject.put("sellerId", sellerId);
            if (jsonObject1 != null) {
                jsonObject.put("content", jsonObject1);
            }

        } catch (JSONException e) {

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyXMPP.getInstance().sendOrder(jsonObject.toString(), Other);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    MyToast.makeTextAnim(ChatActivity.this, R.string.send_fail, 0, R.style.PopToast).show();
                    Looper.loop();
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
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(ChatActivity.this, R.string.no_network, 0, R.style.PopToast).show();
            return null;
        }
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
            msg.setHeight(height);
            msg.setWidth(width);
        } else if (msgtype.equals(Constants.MSG_TYPE_TEXT)) {
            msg.setContent(message);
        } else if (msgtype.equals(Constants.MSG_TYPE_CARD)) {
            msg.setContent(message);
        } else if (msgtype.equals(Constants.MSG_TYPE_VOICE)) {
            msg.setVoiceTime(chat_voice.getVoicetTime());
            msg.setVoicePath(chat_voice.mFileName);
        } else if (msgtype.equals(Constants.MSG_TYPE_ORDER)) {
            msg.setStatus(status);
            msg.setContent(message);
        } else if (msgtype.equals(Constants.MSG_TYPE_LOCATION)) {
            msg.setContent(message);
            msg.setMapBean(mMapBean);
        } else if (msgtype.equals(Constants.SYSTEM_TIP)) {
            msg.setContent(message);
        }
        return msg;
    }

    void updateSession(String type, String content) {
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
        Intent intent = new Intent(Constants.ACTION_MSG);//发送广播，通知消息界面更新
        intent.putExtra("isCurrActivity", true);
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (view_add.getVisibility() == View.VISIBLE) {
                view_add.setVisibility(View.GONE);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getLongClickItem() {

        chatListAdapter.setOnItemLongClickListener(new ChatListAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
                View layout = inflater.inflate(R.layout.custom_alertdialog_del, null);
                TextView dialogdel = layout.findViewById(R.id.dialog_del);
                TextView dialogcopy = layout.findViewById(R.id.dialog_copy);
                dialogdel.setVisibility(View.VISIBLE);
                dialogcopy.setVisibility(View.VISIBLE);

                if (Constants.MSG_TYPE_IMG.equals(chatInfos.get(position).getMsg_type())
                        || Constants.MSG_TYPE_VOICE.equals(chatInfos.get(position).getMsg_type())) {
                    dialogcopy.setVisibility(View.GONE);
                }
                //删除，转发，copy
                final AlertDialog dialog = DialogTool.createDel(ChatActivity.this, layout);
                layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatInfo data = chatInfos.get(position);
                        dialog.dismiss();
                        msgDao.deleteMsgByMsgId(String.valueOf(data.getSellerId()), String.valueOf(data.getToUser()), data.getMsgID());
                        chatInfos.remove(position);
                        chatListAdapter.updateListView(chatInfos);
                    }
                });
                layout.findViewWithTag(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ChatInfo data = chatInfos.get(position);
                        cm.setText(data.getContent());
                        MyToast.makeTextAnim(ChatActivity.this, R.string.copy_success, 0, R.style.PopToast).show();
                    }
                });
            }
        });
    }

    private void setFastReplyAlertDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_tips_bottom, null);
        final AlertDialog dialog = DialogTool.createReplyDialog(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        List<String> reply = Arrays.asList(new replyConstants().REPLY_LIST);
        replyListAdapter = new ReplyListAdapter(ChatActivity.this, reply);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(ChatActivity.this)
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());
        recyclerView.setAdapter(replyListAdapter);
        replyListAdapter.setOnItemClickListener(new ReplyListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String reply) {
                sendMsgText(reply);
                dialog.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rating, menu);
        MenuItem action_msg = menu.findItem(R.id.action_menu);
        if (10000 != sellerId) {
            action_msg.setIcon(R.mipmap.girlprifle);
        } else {
            action_msg.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent intent = new Intent(this, GirlProfileActivity.class);
                intent.putExtra("from", Constants.COME_FROM_CHAT_ROOM);
                intent.putExtra("sellerId", sellerId);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取广告的状态
     */
    private void getAdStatus() {


        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        if (adsBeenList != null && adsBeenList.size() > 0) {
            Message msg = new Message();
            msg.what = 2;
            msg.arg1 = 0;
            handler.sendMessageDelayed(msg, 0);
            return;
        }
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("cityLongitude", MapSupport.longitude);
        params.put("cityLatitude", MapSupport.latitude);
        params.put("adPlace", "TALK");
        HttpUtil.post(Constants.getInstance().adsList, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode != 0) {
                        String dataCollection = jsonObject.getString("dataCollection");
                        if (dataCollection != null) {
                            adsBeenList = jsonUtil.jsonToadsBean(dataCollection);
                            Message msg = new Message();
                            msg.what = 2;
                            msg.arg1 = 0;
                            handler.sendMessageDelayed(msg, 0);
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
            }
        });
    }
}
