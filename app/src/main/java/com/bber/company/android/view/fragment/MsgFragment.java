package com.bber.company.android.view.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.bean.Session;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.db.ChatMsgDao;
import com.bber.company.android.db.SessionDao;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.view.activity.Buy_vipActivity;
import com.bber.company.android.view.activity.ChatActivity;
import com.bber.company.android.view.activity.CustomerActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.view.activity.MobileVerifyActivity;
import com.bber.company.android.view.activity.WebViewGuestActivity;
import com.bber.company.android.view.adapter.MsgListAdapter;
import com.bber.customview.utils.LogUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天 Fragment
 * Author: Bruce
 * Date: 2016/5/9
 * Version:
 * Describe:
 */
public class MsgFragment extends BaseFragment {
    private final int TYPE_CHAT = 0;
    private final int TYPE_MOBILE = 1;
    private final int TYPE_VIP = 2;
    public SimpleDateFormat sd;
    private View view;
    private View headView;
    private RecyclerView recyclerView;
    private LinearLayout view_no_msg;
    private MsgListAdapter adapter;
    private List<Session> sessions;
    private SessionDao sessionDao;
    private ChatMsgDao msgDao;
    private LinearLayout guide_bg;
    private ImageView guide;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int isOrder = intent.getIntExtra("isOrder", -1);
            boolean isUpdate = intent.getBooleanExtra("isUpdate", false);
            if (isOrder != 1 || isUpdate) {
                initData();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_msg, null);

        initViews();
        initData();
        LogUtils.e("MsgFragment");
        return view;
    }

    private void initViews() {
        toolbar = view.findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.title);
        view_no_msg = view.findViewById(R.id.view_no_msg);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        guide_bg = view.findViewById(R.id.guide_bg);
        guide = view.findViewById(R.id.guide_1);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.line_gray))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.divider_left, R.dimen.divider_right)
                .build());


        registerBoradcastReceiver();
        sessionDao = new SessionDao(getActivity());
        msgDao = new ChatMsgDao(getActivity());
        sessions = new ArrayList<>();

        boolean fourth = (boolean) SharedPreferencesUtils.get(getActivity(), "fourth", true);
        if (fourth) {
            guide_bg.setVisibility(View.VISIBLE);
            guide.setVisibility(View.VISIBLE);
//            guide_call2.setVisibility(View.VISIBLE);
//            guide_call3.setVisibility(View.VISIBLE);
            SharedPreferencesUtils.put(getActivity(), "fourth", false);
        }
        guide_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guide_bg.setVisibility(View.GONE);
            }
        });
    }

    public void initData() {
        String buyerId = SharedPreferencesUtils.get(getActivity(), Constants.USERID, "-1") + "";
        if (buyerId.equals("-1")) {
            return;
        }
        String IM_ID = buyerId + "buyer";
        title.setText(R.string.action_msg);

        if (!sessionDao.isContent("10000", IM_ID)) {

            Session sessionodata = new Session();
            sessionodata.setSellerId(10000);
            sessionodata.setName("系统消息");
            sessionodata.setContent("");
            sessionodata.setTo(IM_ID);
            sessionodata.setNotReadCount(0);//未读消息数量
            sd = new SimpleDateFormat("MM-dd HH:mm");
            sessionodata.setTime(sd.format(new Date()));
            sessionodata.setType(Constants.SYSTEM_TIP);
            sessions.add(sessionodata);
            sessionDao.insertSession(sessionodata);
        }
        if (!sessionDao.isContent("3520", IM_ID)) {

            Session sessionodata = new Session();
            sessionodata.setSellerId(3520);
            sessionodata.setName("啪啪客服");
            sessionodata.setContent("");
            sessionodata.setFrom("571seller");
            sessionodata.setTo(IM_ID);
            sessionodata.setNotReadCount(0);//未读消息数量
            sd = new SimpleDateFormat("MM-dd HH:mm");
            sessionodata.setTime(sd.format(new Date()));
            sessionodata.setType("2");
            sessions.add(sessionodata);
            sessionDao.insertSession(sessionodata);
        }
        sessions = sessionDao.queryAllSessions(IM_ID);
        //讲啪啪这个数据置顶
        for (int i = 0; i < sessions.size(); i++) {
            if (!sessions.get(0).getSellerId().equals(10000)) {
                if (sessions.get(i).getSellerId().equals(10000)) {
                    Session data = new Session();
                    data = sessions.get(i);
                    sessions.set(i, sessions.get(0));
                    sessions.set(0, data);
                }
            }
            if (!sessions.get(1).getSellerId().equals(3520)) {
                if (sessions.get(i).getSellerId().equals(3520)) {
                    Session data = new Session();
                    data = sessions.get(i);
                    sessions.set(i, sessions.get(1));
                    sessions.set(1, data);
                }
            }
        }

        adapter = new MsgListAdapter(getActivity(), sessions);
        adapter.setOnItemClickListener(new MsgListAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, Session session, int position) {
                //如果是系统消息
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), CustomerActivity.class);
                    intent.putExtra("from", session.getFrom());
                    intent.putExtra("sellerId", session.getSellerId());
                    intent.putExtra("sellerName", session.getName());
                    intent.putExtra("sellerHead", session.getHeadURL());
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), WebViewGuestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isHaveValue", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    //进入模特聊天。先判断是否是会员
                    inputChatActivity(session); /// 进入聊天界面

                }
            }
        });

        adapter.setOnItemLongClickListener(
                new MsgListAdapter.OnRecyclerViewItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, final Session data, final int position) {

                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        View layout = inflater.inflate(R.layout.custom_alertdialog_del, null);
                        final AlertDialog dialog = DialogTool.createDel(getActivity(), layout);
                        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                msgDao.deleteMsgById(data.getSellerId().toString(), data.getTo());
                                sessionDao.deleteSession(data.getSellerId().toString(), data.getTo());
                                sessions.remove(data);
                                adapter.notifyDataSetChanged();
                                setView();
                                MyXMPP.getInstance().removeRosterItem(data.getFrom());

                            }
                        });
                    }

//                    @Override
//                    public void onItemLongClick(View view, final Session data) {
//                        LayoutInflater inflater = LayoutInflater.from(getActivity());
//                        View layout = inflater.inflate(R.layout.custom_alertdialog_del, null);
//                        final AlertDialog dialog = DialogTool.createDel(getActivity(), layout);
//                        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                msgDao.deleteMsgById(data.getSellerId().toString(), data.getTo());
//                                sessionDao.deleteSession(data.getSellerId().toString(), data.getTo());
//                                sessions.remove(data);
//                                adapter.notifyDataSetChanged();
//                                setView();
//                                MyXMPP.getInstance().removeRosterItem(data.getFrom());
//                            }
//                        });
//                    }
                });
        setView();
    }

    /**
     * 判断是否是会员，和手机绑定
     */
    private void inputChatActivity(Session session) {

        int level = (int) SharedPreferencesUtils.get(getContext(), Constants.VIP_LEVEL, 0);
        if (level == 0) {
            // 开通会员
            setTipDialog(session, R.string.mobile_charge, R.string.mobile_verify_nexttime,
                    R.string.mobile_charge_now, true, true, 0, TYPE_VIP);
        } else {
            startChatActivity(session);
        }
    }

    /**
     * 设置对话框的接口
     *
     * @param contentRId     内容的字符串ID
     * @param LeftButtonRId  左安建的ID
     * @param rightButtonRId 右按键的id
     * @param leftVisable    左安建是否可见
     * @param rightVisable   右按键是否可见
     * @param rightType      进入那个Activity 的标签
     */
    private void setTipDialog(final Session session, int contentRId, int LeftButtonRId, int rightButtonRId,
                              boolean leftVisable, boolean rightVisable, final int leftType, final int rightType) {
        //如果没有认证，需要判断是否需要手机认证才能聊天
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.custom_alertdialog_mobile, null);
        final AlertDialog dialog = DialogTool.createSelectDialog(getContext(), layout, contentRId, LeftButtonRId, rightButtonRId);
        if (leftVisable == false) {
            layout.findViewWithTag(0).setVisibility(View.GONE);
        }
        if (rightVisable == false) {
            layout.findViewWithTag(1).setVisibility(View.GONE);
        }
        layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                startNewActivity(leftType);
            }
        });
        layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startNewActivity(rightType, session);
            }
        });
    }

    /**
     * 进入新的activity
     *
     * @param type
     */
    private void startNewActivity(int type, Session session) {
        switch (type) {
            case TYPE_CHAT://进入聊天界面
                startChatActivity(session);
                break;
            case TYPE_MOBILE://进入手机认证界面
                Intent intent = new Intent(getContext(), MobileVerifyActivity.class);
                startActivity(intent);
                break;
            case TYPE_VIP://进入会员充值
                Intent intents = new Intent(getContext(), Buy_vipActivity.class);
                startActivity(intents);
                break;
        }
    }

    /**
     * 到聊天页面
     */
    private void startChatActivity(Session session) {

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("from", session.getFrom());
        intent.putExtra("sellerId", session.getSellerId());
        intent.putExtra("sellerName", session.getName());
        intent.putExtra("sellerHead", session.getHeadURL());
        startActivity(intent);
    }

    private void setView() {
        if (sessions != null && sessions.size() == 0) {
            view_no_msg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            view_no_msg.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }

        int noreadNum = 0;
        for (Session session : sessions) {
            noreadNum += session.getNotReadCount();
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setPot(noreadNum);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_MSG);
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
