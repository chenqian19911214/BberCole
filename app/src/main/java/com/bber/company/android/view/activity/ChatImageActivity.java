package com.bber.company.android.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.ActivityChatImageBinding;
import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.MessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.preferenceConstants;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.BitmapUtils;
import com.bber.company.android.util.CircleProgress;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.view.adapter.ChatImageItemAdapter;
import com.bber.company.android.view.adapter.ViewPagerAdapter;
import com.bber.company.android.viewmodel.ChatPicViewModel;
import com.bber.company.android.viewmodel.HeaderBarViewModel;
import com.bber.company.android.widget.ConfirmDialog;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.RecordButton;
import com.bber.company.android.widget.RecyclerViewDivider;
import com.bber.customview.utils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览聊天和原图界面
 * Created by Vencent on 17-3-7.
 */
public class ChatImageActivity extends BaseActivity implements View.OnClickListener {


    private static final int MSG_SUCCESS = 0;//可以保存图片成功的标识
    private static final int MSG_FAILURE = 1;//不可以保存图片失败的标识
    public String title_str = "";//标题
    public String[] keywords;
    public String fontColor;
    public MessageBean messageBean;
    private ActivityChatImageBinding binding;
    private ViewPager chat_viewpager;//背景图的切换图片的控件
    private List<View> imageViews = null;
    private String[] projectImages;
    private ChatPicViewModel viewmodel;
    private RelativeLayout view_edit_linear; //页面输入框层
    private SwipeRefreshLayout refresh_chat_layout;
    private RecyclerView recyclerView;
    private EditText chat_edit;//打字输入框
    private List<MessageBean> messageBeanList = null;
    private ChatImageItemAdapter adapter;
    private ImageView image_chat, btn_record;
    private CheckBox btn_gone_chat;
    private LinearLayout view_second, view_third;
    private RelativeLayout view_focus;
    private EditText edittext_chat;
    private Button chat_send;
    private TextView tv_picnum;
    private boolean isShowBrrage = true;//有弹幕
    //页面传递过来的mucid
    private String room_id = "";
    private int mucFlag = 0;
    private LinearLayout refresh_chat_lin;
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_SUCCESS:
                    MyToast.makeTextAnim(ChatImageActivity.this, "保存成功", 0, R.style.PopToast).show();
                    break;

                case MSG_FAILURE:
                    MyToast.makeTextAnim(ChatImageActivity.this, "升级黄金会员开通此功能", 0, R.style.PopToast).show();
                    break;
            }
        }
    };
    private File uploadFile;//上传文件
    /**
     * 按住录音监听
     * *
     */
    RecordButton.OnFinishedRecordListener finishedRecordListener = new RecordButton.OnFinishedRecordListener() {
        @Override
        public void onFinishedRecord(String audioPath) {
            Log.e("eeeeeeeeeeeeee", "viewmodel.chat_voice.mFileName:" + viewmodel.chat_voice.mFileName);
            if (!Tools.isEmpty(viewmodel.chat_voice.mFileName)) {
                File file = new File(viewmodel.chat_voice.mFileName);
                if (!file.exists()) {
                    MyToast.makeTextAnim(ChatImageActivity.this, R.string.record_failed, 0, R.style.PopToast).show();
                    view_focus.setVisibility(View.VISIBLE);
                    view_third.setVisibility(View.GONE);
                    return;
                }
                uploadFile = file;
                viewmodel.uploadFile(uploadFile);

                view_focus.setVisibility(View.VISIBLE);
                view_third.setVisibility(View.GONE);
            }
        }
    };
    /***
     * 注册一个广播，便于聊天XMMP接受回来的聊天信息改变界面
     */
    private BroadcastReceiver gBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            messageBean = (MessageBean) intent.getSerializableExtra("MESSAGEGROUP");
            adapter.setdata(messageBean);
            if (adapter.getItemCount() != 0) {
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载databinding的双向绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_image);
        viewmodel = new ChatPicViewModel(this);
        binding.setViewModel(viewmodel);
        binding.setHeaderBarViewModel(headerBarViewModel);
        //注册广播
        registerBoradcastReceiver();
        //获取上个页面传递的数据
        getIntentdata();
        //加载页面层
        initViews();

        //获取数据层
        initData();

//        setHeaderBar();
    }

    /***
     * //获取上个页面传递的数据
     */
    private void getIntentdata() {
        //获取上个页面的图片数据
        imageViews = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        projectImages = getIntent().getStringArrayExtra("images");
        room_id = getIntent().getStringExtra("room_id");
        mucFlag = getIntent().getIntExtra("mucFlag", 0);
        title_str = getIntent().getStringExtra("title");
        keywords = getIntent().getStringArrayExtra("keywords");
        fontColor = getIntent().getStringExtra("fontColor");

        final int vip_id = (int) SharedPreferencesUtils.get(ChatImageActivity.this, Constants.VIP_ID, 0);
        headerBarViewModel.setBarTitle(title_str);
        if (projectImages != null) {
            for (int i = 0; i < projectImages.length; i++) {
                //加载每个页面的图片然后完成viewpager的每个页面
                final View view = inflater.inflate(R.layout.browse_item, null);
                final int finalI = i;
                final String url = projectImages[i];
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (headerBarViewModel.isShowAll.get()) {
                            headerBarViewModel.isShowAll.set(false);
                        } else {
                            headerBarViewModel.isShowAll.set(true);
                        }
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ConfirmDialog confirmDialog = new ConfirmDialog(ChatImageActivity.this);
                        confirmDialog.show();
                        confirmDialog.setActionListener(new IactionListener() {
                            @Override
                            public void SuccessCallback(final Object o) {
                                //返回保存图片的数值   1的话是保存1张，2的话是保存一套
                                new Thread() {
                                    public void run() {
                                        if (o.equals("1")) {
                                            BitmapUtils.savePicture(BitmapUtils.getHttpBitmap(projectImages[finalI]));
                                            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();//保存图片成功。
                                        } else {
                                            if (vip_id >= 2) {
                                                for (int j = 0; j < projectImages.length; j++) {
                                                    //保存从url上面拿到的图片
                                                    BitmapUtils.savePicture(BitmapUtils.getHttpBitmap(projectImages[j]));
                                                }
                                                mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();//保存图片成功。
                                            } else {
                                                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();//保存图片失败。
                                            }
                                        }
                                    }
                                }.start();

                            }

                            @Override
                            public void FailCallback(String result) {

                            }
                        });
                        return false;
                    }
                });

                final SimpleDraweeView imageView = view.findViewById(R.id.image_back);
                final ImageView imageview_back = view.findViewById(R.id.imageview_back);
                generateProgress(imageView);
                Glide.with(this)//activty
                        .load(url)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                // Do something with bitmap here.
                                bitmap.getHeight(); //获取bitmap信息，可赋值给外部变量操作，也可在此时行操作。
                                bitmap.getWidth();
                                if (bitmap.getHeight() < bitmap.getWidth()) {
                                    imageview_back.setVisibility(View.VISIBLE);
                                    imageView.setVisibility(View.GONE);

//                                    imageview_back.setImageBitmap(BitmapUtils.getHttpBitmap(url));
                                    Glide.with(view.getContext()).load(url).dontAnimate().into(imageview_back);
                                } else {
                                    imageview_back.setVisibility(View.GONE);
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageURI(url);

                                }
                            }
                        });
//

                imageViews.add(view);
            }
        }
    }

    private void generateProgress(SimpleDraweeView image) {
        CircleProgress.Builder builder = new CircleProgress.Builder();
        builder.build();
        builder.build().injectFresco(image);
    }

    /**
     * 加载控件页面
     */
    private void initViews() {
        //页面覆盖的viewpager层
        chat_viewpager = binding.chatViewpager;
        //自定义一个PageAdapter,并传入
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageViews);
        chat_viewpager.setAdapter(viewPagerAdapter);
        // 设置一个监听器，当ViewPager中的页面改变时调用
        chat_viewpager.setOnPageChangeListener(new MyPageChangeListener());
        //聊天界面的加载
        refresh_chat_layout = binding.refreshChatLayout;
        recyclerView = binding.refreshChatLoadmore;
        refresh_chat_lin = binding.refreshChatLin;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //添加每个item的分割线
        recyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, 12, getResources().getColor(R.color.transparent)));
        //开启加载更多
//        recyclerView.setLoadMoreEnable(false);
        messageBeanList = new ArrayList<>();
        messageBeanList.clear();
        adapter = new ChatImageItemAdapter(this, messageBeanList, fontColor, keywords);
        //设置adapter
        recyclerView.setAdapter(adapter);
        //设置下拉回调
        refresh_chat_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_chat_layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh_chat_layout.setRefreshing(false);
                        //数据请求
//                        chatPicViewModel.getTalkPicture(0);
//                        recyclerView.getAdapter().notifyDataSetChanged();
//                        recyclerView.refreshCompleted();
                    }
                }, 100);
            }
        });
        view_edit_linear = binding.viewEdit;

        view_second = binding.viewSecond;//聊天第二层
        view_third = binding.viewThird;//聊天第三层
        view_focus = binding.viewFocus;//聊天的第一层
        image_chat = binding.imageChat;//聊天界面
        btn_record = binding.btnRecord;//录音按钮
        btn_gone_chat = binding.btnGoneChat;//弹幕按钮
        edittext_chat = binding.edittextChat;//输入框
        chat_send = binding.chatSend;//发送框
        tv_picnum = binding.tvPicnum;

        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatMsg = edittext_chat.getText() + "";
                if (Tools.IsContainKeyWord(chatMsg, preferenceConstants.KEY_WORD_CHAT)) {
                    ToastUtils.showToast(R.string.illege_key_word_chat, 0);
                    return;
                }
                if (Tools.isEmpty(chatMsg)) {
                    MyToast.makeTextAnim(ChatImageActivity.this, "输入不能为空", 0, R.style.PopToast).show();
                    return;
                }
                if (!keywords.equals("[]")) {
                    for (int i = 0; i < keywords.length; i++) {
                        if (chatMsg.contains(keywords[i])) {
                            chatMsg = chatMsg.replace(keywords[i].trim().toString(), replaceLength(keywords[i]));
                        }
                    }
                }
                if (Tools.isEmpty(chatMsg)) {
                    MyToast.makeTextAnim(ChatImageActivity.this, "输入不能为空", 0, R.style.PopToast).show();
                    return;
                }

                viewmodel.getChatInfoTo(chatMsg, Constants.MSG_TYPE_TEXT);
                LogUtils.e("聊天内容" + edittext_chat.getText().toString());
                edittext_chat.setText("");
                view_focus.setVisibility(View.VISIBLE);
                view_second.setVisibility(View.GONE);
            }
        });
        viewmodel.chat_voice = binding.chatVoice;
        image_chat.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_gone_chat.setOnClickListener(this);
        viewmodel.chat_voice.setOnFinishedRecordListener(finishedRecordListener);
        adapter.setRecoreButton(viewmodel.chat_voice);

        final String username = SharedPreferencesUtils.get(this, Constants.USERNAME, "") + "";
        int length = room_id.length();
        LogUtils.e("进入聊天室之前  room_id = " + room_id + "mucFlag = " + mucFlag);
        if (length != 0 && mucFlag == 0) {
            LogUtils.e("进入聊天室吗  room_id = " + room_id + "mucFlag = " + mucFlag);
            MyXMPP.getInstance().joinAroom(room_id, username);
        }

        //如果没有聊天室ID，则只加载聊图
        if (room_id.equals("") || mucFlag == 1) {
            refresh_chat_lin.setVisibility(View.GONE);
            LogUtils.e("隐藏");
//            view_edit_linear.setVisibility(View.GONE);
        } else {

            //加载VIP等级和是否验证手机号
            int level = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.VIP_LEVEL, 0);
            boolean isvirified = (boolean) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.CHECK_PHONE, false);
            int checkPhone = 0;
            if (isvirified) {
                checkPhone = 1;
            } else {
                checkPhone = 0;
            }
            if (level > 1 || checkPhone == 1) {
                view_edit_linear.setVisibility(View.VISIBLE);
            } else {
                MyToast.makeTextAnim(this, "只有会员或者验证手机号才能聊天", 0, R.style.PopToast).show();
            }
        }


    }

    //替换符号是几个*号
    private String replaceLength(String str) {
        if (str.length() < 2) {
            return "*";
        } else {
            return "**";
        }
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_chat:
                //弹出软键盘
                edittext_chat.requestFocus();
                InputMethodManager imm = (InputMethodManager) edittext_chat.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                view_focus.setVisibility(View.GONE);
                view_second.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_record:
                view_focus.setVisibility(View.GONE);
                view_third.setVisibility(View.VISIBLE);
                break;
            //弹幕
            case R.id.btn_gone_chat:
                if (!isShowBrrage) {
                    image_chat.setVisibility(View.VISIBLE);
                    tv_picnum.setVisibility(View.GONE);
                    btn_record.setVisibility(View.VISIBLE);
                    refresh_chat_lin.setVisibility(View.VISIBLE);
                    headerBarViewModel.setAllDisable(!isShowBrrage);
                    isShowBrrage = !isShowBrrage;
                } else {
                    tv_picnum.setVisibility(View.VISIBLE);
                    image_chat.setVisibility(View.GONE);
                    btn_record.setVisibility(View.GONE);
                    refresh_chat_lin.setVisibility(View.GONE);
                    headerBarViewModel.setAllDisable(!isShowBrrage);
                    isShowBrrage = !isShowBrrage;
                }
                break;
        }
    }

    /***
     * 在Avtivity里面注册
     */
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.ACTION_CHAT_IMG);
        //注册广播
        registerReceiver(gBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageBeanList.clear();
        if (room_id.length() != 0) {
            MyXMPP.getInstance().leveTheRoom(room_id);
        }

        unregisterReceiver(gBroadcastReceiver);
    }

    //设置头部
    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle(title_str);
        headerBarViewModel.setBackColorResourceId(R.color.transparent);
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


    //ViewPager滑动监听
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        public void onPageSelected(int position) {
//            setCurrentBlood(position);
//
//            setCurrentTriangle(position);
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            tv_picnum.setText(arg0 + 1 + "/" + projectImages.length);
        }
    }
}
