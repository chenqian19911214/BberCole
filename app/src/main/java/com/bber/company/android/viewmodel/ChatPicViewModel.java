package com.bber.company.android.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.bean.DiscussBean;
import com.bber.company.android.bean.DiscussDetailBean;
import com.bber.company.android.bean.MessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.imageload.AudioGetFromHttp;
import com.bber.company.android.util.Bimp;
import com.bber.company.android.util.MyXMPP;
import com.bber.company.android.view.activity.ChatImageActivity;
import com.bber.company.android.view.activity.ChatImageCreateActivity;
import com.bber.company.android.view.activity.PhotoPickerActivity;
import com.bber.company.android.view.activity.PreviewPhotoActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.RecordButton;
import com.bber.company.android.widget.RecyclerItemClickListener;
import com.bber.customview.utils.LogUtils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于登陆的所有逻辑
 */

public class ChatPicViewModel extends BaseViewModel {

    /**
     * 是否显示相机
     */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /**
     * 照片选择模式
     */
    public final static String EXTRA_SELECT_MODE = "select_mode";
    /**
     * 最大选择数量
     */
    public final static String EXTRA_MAX_MUN = "max_num";
    /**
     * 总共选择的图片数量
     */
    public final static String TOTAL_MAX_MUN = "total_num";
    /**
     * 单选
     */
    public final static int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public final static int MODE_MULTI = 1;
    /**
     * 默认最大选择数量
     */
    public final static int DEFAULT_NUM = 3;
    private final static String ALL_PHOTO = "所有图片";
    private static final int PICK_PHOTO = 1;
    //请求聊图列表得数据
    public List<DiscussBean> discussBeen;
    public RecordButton chat_voice;
    //首页新闻列表
    public ObservableList<DiscussBean> allnewslist = new ObservableArrayList<DiscussBean>();
    public ObservableList<DiscussBean> mmslist = new ObservableArrayList<DiscussBean>();
    public ObservableList<DiscussBean> allhotslist = new ObservableArrayList<DiscussBean>();
    public ObservableList<DiscussBean> allshakelist = new ObservableArrayList<DiscussBean>();
    //创建聊图的bean
    public ObservableList<Bitmap> chatCreatnewslist = new ObservableArrayList<Bitmap>();
    public String datatime = "";
    //是否可看聊图的状态
    public ObservableInt tpfStatus = new ObservableInt();
    //是否可以
    public ObservableBoolean isShow = new ObservableBoolean(false);
    //回调的时候要不要重新调用申请方法。讲道理只有验证了手机号才重新吊
    public ObservableBoolean istpfStatus = new ObservableBoolean(false);
    /***
     * 这个是最新聊图的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener onClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            //这边将每一个聊天室的viewpager的图片传递过去
            //mucid没有的话，没进入聊图界面
            if (allnewslist.size() > position) {
                String room_id = allnewslist.get(position).getMucId();
                int mucFlag = allnewslist.get(position).getMucFlag();
                String[] keywords = allnewslist.get(position).getKeywords();
                String fontColor = allnewslist.get(position).getFontColor();

                List<String> list = new ArrayList<>();
                for (DiscussDetailBean discuss : allnewslist.get(position).getImg()) {
                    if (!Tools.isEmpty(discuss.getSource())) {
                        list.add(discuss.getSource());
                    }
                }

                int size = list.size();

                String[] images = list.toArray(new String[size]);
                String url = allnewslist.get(position).getUrl();

                if (allnewslist.get(position).getUrl().equals("")) {
                    //当状态值等于1可看 2未验证手机 3试看过期
                    if (StringUtils.isEmpty(allnewslist.get(position).getTitle())) {
                        Intent intent = new Intent(gContext, ChatImageActivity.class);
                        intent.putExtra("images", images);
                        String img_title = allnewslist.get(position).getTitle();
                        intent.putExtra("title", img_title);
                        intent.putExtra("room_id", room_id);
                        intent.putExtra("mucFlag", mucFlag);
                        intent.putExtra("keywords", keywords);
                        intent.putExtra("fontColor", fontColor);
                        //增加阅读次数
                        addReadTimes(allnewslist.get(position).getId());
                        gContext.startActivity(intent);
                    } else {
                        if (tpfStatus.get() == 1) {
                            Intent intent = new Intent(gContext, ChatImageActivity.class);
                            intent.putExtra("images", images);
                            String img_title = allnewslist.get(position).getTitle();
                            intent.putExtra("title", img_title);
                            intent.putExtra("room_id", room_id);
                            intent.putExtra("mucFlag", mucFlag);
                            intent.putExtra("keywords", keywords);
                            intent.putExtra("fontColor", fontColor);
                            //增加阅读次数
                            addReadTimes(allnewslist.get(position).getId());
                            gContext.startActivity(intent);
                        } else if (tpfStatus.get() == 2) {
                            isShow.set(true);
                        } else if (tpfStatus.get() == 3) {
                            isShow.set(true);
                        } else if (tpfStatus.get() == 4) {
                            isShow.set(true);
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    if (!allnewslist.get(position).getUrl().contains("http")) {
                        allnewslist.get(position).setUrl("http://" + allnewslist.get(position).getUrl());
                    }
                    //增加阅读次数
                    addReadTimes(allnewslist.get(position).getId());
                    Uri content_url = Uri.parse(allnewslist.get(position).getUrl());
                    intent.setData(content_url);
                    gContext.startActivity(intent);
                }


                return true;
            }

            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    /***
     * 这个是MMS聊图的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener mmsOnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            //这边将每一个聊天室的viewpager的图片传递过去
            //mucid没有的话，没进入聊图界面
            if (mmslist.size() > position) {
                String room_id = mmslist.get(position).getMucId();
                int mucFlag = mmslist.get(position).getMucFlag();
                String[] keywords = mmslist.get(position).getKeywords();
                String fontColor = mmslist.get(position).getFontColor();

                List<String> list = new ArrayList<>();
                for (DiscussDetailBean discuss : mmslist.get(position).getImg()) {
                    if (!Tools.isEmpty(discuss.getSource())) {
                        list.add(discuss.getSource());
                    }
                }

                int size = list.size();

                String[] images = list.toArray(new String[size]);
                String url = mmslist.get(position).getUrl();

                if (mmslist.get(position).getUrl().equals("")) {
                    //当状态值等于1可看 2未验证手机 3试看过期
                    if (StringUtils.isEmpty(mmslist.get(position).getTitle())) {
                        Intent intent = new Intent(gContext, ChatImageActivity.class);
                        intent.putExtra("images", images);
                        String img_title = mmslist.get(position).getTitle();
                        intent.putExtra("title", img_title);
                        intent.putExtra("room_id", room_id);
                        intent.putExtra("mucFlag", mucFlag);
                        intent.putExtra("keywords", keywords);
                        intent.putExtra("fontColor", fontColor);
                        //增加阅读次数
                        addReadTimes(mmslist.get(position).getId());
                        gContext.startActivity(intent);
                    } else {
                        if (tpfStatus.get() == 1) {
                            Intent intent = new Intent(gContext, ChatImageActivity.class);
                            intent.putExtra("images", images);
                            String img_title = mmslist.get(position).getTitle();
                            intent.putExtra("title", img_title);
                            intent.putExtra("room_id", room_id);
                            intent.putExtra("mucFlag", mucFlag);
                            intent.putExtra("keywords", keywords);
                            intent.putExtra("fontColor", fontColor);
                            //增加阅读次数
                            addReadTimes(mmslist.get(position).getId());
                            gContext.startActivity(intent);
                        } else if (tpfStatus.get() == 2) {
                            isShow.set(true);
                        } else if (tpfStatus.get() == 3) {
                            isShow.set(true);
                        } else if (tpfStatus.get() == 4) {
                            isShow.set(true);
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    if (!mmslist.get(position).getUrl().contains("http")) {
                        mmslist.get(position).setUrl("http://" + mmslist.get(position).getUrl());
                    }
                    //增加阅读次数
                    addReadTimes(mmslist.get(position).getId());
                    Uri content_url = Uri.parse(mmslist.get(position).getUrl());
                    intent.setData(content_url);
                    gContext.startActivity(intent);
                }


                return true;
            }

            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    /***
     * 这个是聊图的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener hotOnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            //这边将每一个聊天室的viewpager的图片传递过去
            //mucid没有的话，没进入聊图界面
            if (allhotslist.size() > position) {
                String room_id = allhotslist.get(position).getMucId();
                int mucFlag = allhotslist.get(position).getMucFlag();
                String[] keywords = allhotslist.get(position).getKeywords();
                String fontColor = allhotslist.get(position).getFontColor();

                List<String> list = new ArrayList<>();
                for (DiscussDetailBean discuss : allhotslist.get(position).getImg()) {
                    if (!Tools.isEmpty(discuss.getSource())) {
                        list.add(discuss.getSource());
                    }
                }

                int size = list.size();

                String[] images = list.toArray(new String[size]);
                LogUtils.e(images.toString());

                if (allhotslist.get(position).getUrl().equals("")) {
                    if (StringUtils.isEmpty(allhotslist.get(position).getTitle())) {
                        Intent intent = new Intent(gContext, ChatImageActivity.class);
                        intent.putExtra("images", images);
                        String img_title = allhotslist.get(position).getTitle();
                        intent.putExtra("title", img_title);
                        intent.putExtra("room_id", room_id);
                        intent.putExtra("mucFlag", mucFlag);
                        intent.putExtra("keywords", keywords);
                        intent.putExtra("fontColor", fontColor);
                        //增加阅读次数
                        addReadTimes(allnewslist.get(position).getId());
                        gContext.startActivity(intent);
                    } else {
                        //当状态值等于1可看 2未验证手机 3试看过期
                        if (tpfStatus.get() == 1) {
                            Intent intent = new Intent(gContext, ChatImageActivity.class);
                            intent.putExtra("images", images);
                            String img_title = allhotslist.get(position).getTitle();
                            intent.putExtra("title", img_title);
                            intent.putExtra("room_id", room_id);
                            intent.putExtra("mucFlag", mucFlag);
                            intent.putExtra("keywords", keywords);
                            intent.putExtra("fontColor", fontColor);
                            //增加阅读次数
                            addReadTimes(allhotslist.get(position).getId());
                            gContext.startActivity(intent);
                        } else if (tpfStatus.get() == 2) {
                            isShow.set(true);
                        } else if (tpfStatus.get() == 3) {
                            isShow.set(true);
                        } else if (tpfStatus.get() == 4) {
                            isShow.set(true);
                        }
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    if (!allhotslist.get(position).getUrl().contains("http")) {
                        allhotslist.get(position).setUrl("http://" + allhotslist.get(position).getUrl());
                    }
                    //增加阅读次数
                    addReadTimes(allhotslist.get(position).getId());
                    Uri content_url = Uri.parse(allhotslist.get(position).getUrl());
                    intent.setData(content_url);
                    gContext.startActivity(intent);
                }


                return true;
            }

            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    /***
     * 这个是摇图的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener shakeonClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            //这边将每一个聊天室的viewpager的图片传递过去
            //mucid没有的话，没进入聊图界面
            if (position != -1) {
                if (allshakelist.size() > position) {

                    String room_id = allshakelist.get(position).getMucId();
                    int mucFlag = allshakelist.get(position).getMucFlag();
                    String[] keywords = allshakelist.get(position).getKeywords();
                    String fontColor = allshakelist.get(position).getFontColor();

                    List<String> list = new ArrayList<>();
                    for (DiscussDetailBean discuss : allshakelist.get(position).getImg()) {
                        if (!Tools.isEmpty(discuss.getSource())) {
                            list.add(discuss.getSource());
                        }
                    }

                    int size = list.size();

                    String[] images = list.toArray(new String[size]);
                    LogUtils.e(images.toString());

                    if (allshakelist.get(position).getUrl().equals("")) {
                        if (StringUtils.isEmpty(allshakelist.get(position).getTitle())) {
                            Intent intent = new Intent(gContext, ChatImageActivity.class);
                            intent.putExtra("images", images);
                            String img_title = allshakelist.get(position).getTitle();
                            intent.putExtra("title", img_title);
                            intent.putExtra("room_id", room_id);
                            intent.putExtra("mucFlag", mucFlag);
                            intent.putExtra("keywords", keywords);
                            intent.putExtra("fontColor", fontColor);
                            //增加阅读次数
                            addReadTimes(allnewslist.get(position).getId());
                            gContext.startActivity(intent);
                        } else {
                            //当状态值等于1可看 2未验证手机 3试看过期
                            if (tpfStatus.get() == 1) {
                                Intent intent = new Intent(gContext, ChatImageActivity.class);
                                intent.putExtra("images", images);
                                String img_title = allshakelist.get(position).getTitle();
                                intent.putExtra("title", img_title);
                                intent.putExtra("room_id", room_id);
                                intent.putExtra("mucFlag", mucFlag);
                                intent.putExtra("keywords", keywords);
                                intent.putExtra("fontColor", fontColor);
                                //增加阅读次数
                                addReadTimes(allshakelist.get(position).getId());
                                gContext.startActivity(intent);
                            } else if (tpfStatus.get() == 2) {
                                isShow.set(true);
                            } else if (tpfStatus.get() == 3) {
                                isShow.set(true);
                            } else if (tpfStatus.get() == 4) {
                                isShow.set(true);
                            }
                        }

                    } else {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        if (!allshakelist.get(position).getUrl().contains("http")) {
                            allshakelist.get(position).setUrl("http://" + allshakelist.get(position).getUrl());
                        }
                        //增加阅读次数
                        addReadTimes(allshakelist.get(position).getId());
                        Uri content_url = Uri.parse(allshakelist.get(position).getUrl());
                        intent.setData(content_url);
                        gContext.startActivity(intent);
                    }
                    return true;
                }
            }


            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    private int m_Page = 1;
    private int m_Rows = 10;
    private String buyerId;
    private AudioGetFromHttp aduio = new AudioGetFromHttp(gContext);
    /**
     * 是否显示相机，默认不显示
     */
    private boolean mIsShowCamera = false;
    /**
     * 照片选择模式，默认是单选模式
     */
    private int mSelectMode = 1;
    /**
     * 最大选择数量，仅多选模式有用
     */
    private int mMaxNum;
    /**
     * 总共选择的图片数量
     */
    private int mTotalNum;
    private Context mContext;
    /***
     * 这个是创建聊图的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener chatCreatonClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (position == Bimp.tempSelectBitmap.size()) {
                //如果是加号，那么就打开相册
                Intent intent = new Intent(gContext, PhotoPickerActivity.class);
                intent.putExtra(EXTRA_SHOW_CAMERA, true);
                intent.putExtra(EXTRA_SELECT_MODE, MODE_MULTI);
                intent.putExtra(EXTRA_MAX_MUN, DEFAULT_NUM);
                // 总共选择的图片数量
                intent.putExtra(TOTAL_MAX_MUN, Bimp.tempSelectBitmap.size());
                ((ChatImageCreateActivity) mContext).startActivityForResult(intent, PICK_PHOTO);
            } else {
                //如果不是加号，那就预览大图
                Intent intent = new Intent(gContext,
                        PreviewPhotoActivity.class);
                intent.putExtra("position", "1");
                intent.putExtra("ID", position);
                mContext.startActivity(intent);
            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };


    public ChatPicViewModel(Context _context) {
        super(_context);
        mContext = _context;
        //检测网络是否可用
        if (!appContext.isNetworkConnected()) {
            MyToast.makeTextAnim(_context, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";

    }

    /**
     * 摇图
     *
     * @param type
     */
    public void getShakePicture(int id, final String type) {
        if (id == 0) {
            m_Page = 1;
        } else {
            m_Page++;
        }

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);

        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("page", m_Page);
        params.put("rows", m_Rows);
        params.put("type", type);
        params.put("time", datatime);
        allshakelist.clear();
        HttpUtil.post(Constants.getInstance().getTalkPicture, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    String totalPage = jsonObject.getString("totalPage");
                    discussBeen = jsonUtil.jsonToDiscussBean(dataCollection.toString());
                    allshakelist.addAll(discussBeen);
                    //如果页数超过最后一页
                    if (Integer.parseInt(totalPage) <= m_Page) {
                        iactionListener.SuccessCallback("shake");
                    }

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

    /**
     * 添加最新聊图
     *
     * @param type
     */
    public void getTalkPicture(int id, final String type) {
        if (id == 0) {
            m_Page = 1;
        } else {
            m_Page++;
        }

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);

        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("page", m_Page);
        params.put("rows", m_Rows);
        params.put("type", type);
        params.put("time", datatime);

        HttpUtil.post(Constants.getInstance().getTalkPicture, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    String totalPage = jsonObject.getString("totalPage");
                    discussBeen = jsonUtil.jsonToDiscussBean(dataCollection.toString());
                    if (discussBeen.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                    } else {
                        if (m_Page == 1) {
                            allnewslist.clear();
                        }
                    }
                    allnewslist.addAll(discussBeen);
                    for (int j = 0; j < allnewslist.size(); j++) {
                        allnewslist.get(j).notifyChange();
                    }
                    //如果页数超过最后一页
                    if (Integer.parseInt(totalPage) <= m_Page) {
                        iactionListener.SuccessCallback("new");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("添加聊图+getTalkPicture+new");
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 添加萌萌说聊图
     *
     * @param type
     */
    public void getMmsPicture(int id, final String type) {
        if (id == 0) {
            m_Page = 1;
        } else {
            m_Page++;
        }

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);

        params.put("head", head);
        params.put("buyerId", buyerId);
        params.put("page", m_Page);
        params.put("rows", m_Rows);
        params.put("type", type);
        params.put("time", datatime);

        HttpUtil.post(Constants.getInstance().getTalkPicture, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    String totalPage = jsonObject.getString("totalPage");
                    discussBeen = jsonUtil.jsonToDiscussBean(dataCollection.toString());
                    if (discussBeen.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                    } else {
                        if (m_Page == 1) {
                            mmslist.clear();
                        }
                    }
                    mmslist.addAll(discussBeen);
                    for (int j = 0; j < mmslist.size(); j++) {
                        mmslist.get(j).notifyChange();
                    }
                    //如果页数超过最后一页
                    if (Integer.parseInt(totalPage) <= m_Page) {
                        iactionListener.SuccessCallback("mms");
                    }

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

    /**
     * 添加最热聊图
     *
     * @param type
     */
    public void getChatPicture(int id, final String type) {
        if (id == 0) {
            m_Page = 1;
        } else {
            m_Page++;
        }

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);

        params.put("head", head);
        LogUtils.e("head" + head);
        params.put("buyerId", buyerId);
        LogUtils.e("buyerId" + buyerId);
        params.put("page", m_Page);
        LogUtils.e("page" + m_Page);
        params.put("rows", m_Rows);
        LogUtils.e("rows" + m_Rows);
        params.put("type", type);
        LogUtils.e("type" + type);
        params.put("time", datatime);
        LogUtils.e("time" + datatime);
        HttpUtil.post(Constants.getInstance().getTalkPicture, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                    String totalPage = jsonObject.getString("totalPage");
                    discussBeen = jsonUtil.jsonToDiscussBean(dataCollection.toString());

                    if (discussBeen.size() == 0 && m_Page == 1) {
                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                    } else {
                        if (m_Page == 1) {
                            allhotslist.clear();
                        }
                    }
                    allhotslist.addAll(discussBeen);
                    for (int j = 0; j < allhotslist.size(); j++) {
                        allhotslist.get(j).notifyChange();
                    }
                    //如果页数超过最后一页
                    if (Integer.parseInt(totalPage) <= m_Page) {
                        iactionListener.SuccessCallback("hot");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                LogUtils.e("添加聊图+getTalkPicture+hot");
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }


    /**
     * 增加阅读次数
     */
    private void addReadTimes(int m_ClickID) {
        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        params.put("head", head);
        params.put("id", m_ClickID);
        HttpUtil.post(Constants.getInstance().addReadTimes, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    /**
     * 聊图试看接口
     */
    public void applyForTalkPictureFree() {
        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().applyForTalkPictureFree, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }

                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        tpfStatus.set(dataCollection.getInt("tpfStatus"));
                        istpfStatus.set(true);
                    } else {
                        tpfStatus.set(1);
                        istpfStatus.set(true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "sendImage onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() {
            }
        });
    }


    /**
     * 发送的信息
     *
     * @return
     */

    public void getChatInfoTo(String message, String msgtype) {

        //封装一个message左右发送的消息的json类型
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
        String time = sd.format(new Date());
        MessageBean msg = new MessageBean();
        msg.fromUserId = buyerId;
        msg.toUserId = "";
        int ubsex = (int) SharedPreferencesUtils.get(gContext, Constants.UBSEX, 1);
        msg.gender = ubsex;
        String headurl = (String) SharedPreferencesUtils.get(gContext, Constants.HEADURL, "");
        msg.fromUserHead = headurl;
        msg.fromUserName = (String) SharedPreferencesUtils.get(gContext, Constants.USERNAME, "游客");
        msg.viplevel = (int) SharedPreferencesUtils.get(gContext, Constants.VIP_LEVEL, 0);
        msg.msgType = msgtype;
        msg.sendDate = time;
        msg.mapBean = null;
        if (msgtype.equals(Constants.MSG_TYPE_IMG)) {
            msg.msgContent = message;
//            msg.imageHeight = height;
//            msg.setWidth(width);
        } else if (msgtype.equals(Constants.MSG_TYPE_TEXT)) {
            msg.msgContent = message;
        } else if (msgtype.equals(Constants.MSG_TYPE_CARD)) {
            msg.msgContent = message;
        } else if (msgtype.equals(Constants.MSG_TYPE_VOICE)) {
            msg.voiceTime = chat_voice.getVoicetTime();
            msg.sourcePath = message;
        } else if (msgtype.equals(Constants.SYSTEM_TIP)) {
            msg.msgContent = message;
        }

        Gson gson = new Gson();
        JsonUtil jsonUtil = new JsonUtil(gContext);

        String messageStr = jsonUtil.MessageBeanToString(msg);
        //直接发送群聊消息
        MyXMPP.getInstance().sendGroupMessage(messageStr);

    }

    /**
     * 上传文件
     * *
     */
    public void uploadFile(final File uploadFile) {
        DialogTool.createProgressDialog(gContext, true);
        RequestParams params = new RequestParams();
        try {
            params.put("file", uploadFile);
        } catch (FileNotFoundException e) {
            ToastUtils.showToast(R.string.no_file_fail, 0);
            DialogTool.dismiss(gContext);
            return;
        }
        HttpUtil.post(Constants.getInstance().sendFile, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "sendImage onSuccess--jsonObject:" + jsonObject);
                try {
                    String responsecode = jsonObject.getString("responsecode");
                    if (!responsecode.equals("SUCCESS")) {
                        MyToast.makeTextAnim(gContext, R.string.send_fail, 0, R.style.PopToast).show();
                        return;
                    }
                    //发送语音消息
                    String URL = jsonObject.getString("data");
                    getChatInfoTo(URL, Constants.MSG_TYPE_VOICE);
                    uploadFile.delete();
                    aduio.saveFileToCache(uploadFile, URL);
                } catch (JSONException e) {
                    MyToast.makeTextAnim(gContext, R.string.operate_fail, 0, R.style.PopToast).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "sendImage onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(gContext, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(gContext);
            }
        });
    }


}
