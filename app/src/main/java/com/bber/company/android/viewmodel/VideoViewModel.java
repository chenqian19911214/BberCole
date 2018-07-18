/*
package com.bber.company.android.viewmodel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.bean.VideoBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.activity.LandVoiceActivity;
import com.bber.company.android.view.activity.LandscapeActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.RecyclerItemClickListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

*/
/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于登陆的所有逻辑
 *//*


public class VideoViewModel extends BaseViewModel {
    //首页新闻列表
    public ObservableList<VideoBean> video1list = new ObservableArrayList<VideoBean>();
    public ObservableList<VideoBean> video2list = new ObservableArrayList<VideoBean>();
    public ObservableList<VideoBean> video3list = new ObservableArrayList<VideoBean>();
    public ObservableList<VideoBean> video4list = new ObservableArrayList<VideoBean>();
    //是否可以
    public ObservableBoolean isShow = new ObservableBoolean(false);
    public ObservableBoolean isVip = new ObservableBoolean(false);
    public ObservableField<Integer> videoStatus = new ObservableField<Integer>(2);
    //请求聊图列表得数据
    public List<VideoBean> videoBeen;
    public List<VideoBean> videotypeBeen;
    public List<VideoBean> videovoiceBeen;
    public List<VideoBean> videoVRBeen;
    private Context mContext;
    */
/***
     * 这个是视频的recycle的长按监听和单击监听
     *//*

    public RecyclerItemClickListener.OnItemClickListener video1OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (video1list.size() > position) {
                int type = video1list.get(position).getType();
                if (type == 3) {
                    Intent intent = new Intent(mContext, LandVoiceActivity.class);
                    intent.putExtra("url", video1list.get(position).getCover_path());
                    intent.putExtra("source", video1list.get(position).getVipZoneImglist().get(0).source);
                    mContext.startActivity(intent);
                } else {  // chenqian

                    ComponentName componentName = new ComponentName("com.example.gsyvideoplayer", "com.example.gsyvideoplayer.InputUrlDetailActivity");
                  */
/*  Intent intent = new Intent(mContext, LandscapeActivity.class);
                    intent.putExtra("url", video1list.get(position).getCover_path());
                    mContext.startActivity(intent);*//*


                    Intent intent = new Intent();
                    intent.setClass(mContext,LandscapeActivity.class);
                   // intent.setComponent(componentName);
                    intent.putExtra("url", video1list.get(position).getCover_path());
                    intent.putExtra("name", video1list.get(position).getTitle());
                    mContext.startActivity(intent);

                }
                addClick(video1list.get(position).getId());
            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    */
/***
     * 这个是视频的recycle的长按监听和单击监听
     *//*

    public RecyclerItemClickListener.OnItemClickListener video2OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (video2list.size() > position) { //chenqian
                addClick(video2list.get(position).getId());
                Intent intent = new Intent(mContext, LandscapeActivity.class);
                intent.putExtra("url", video2list.get(position).getCover_path());
                mContext.startActivity(intent);
            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    */
/***
     * 这个是视频的recycle的长按监听和单击监听
     *//*

    public RecyclerItemClickListener.OnItemClickListener video3OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {

            if (video3list.size() > position) {
                addClick(video3list.get(position).getId());
                Intent intent = new Intent(mContext, LandVoiceActivity.class);
                intent.putExtra("url", video3list.get(position).getCover_path());
                intent.putExtra("name", video3list.get(position).getTitle());
                intent.putExtra("source", video3list.get(position).getVipZoneImglist().get(0).source);
                mContext.startActivity(intent);
            }

            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    */
/***
     * 这个是视频的recycle的长按监听和单击监听
     *//*

    public RecyclerItemClickListener.OnItemClickListener video4OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (video4list.size() > position) {
                addClick(video4list.get(position).getId());
                Intent intent = new Intent(mContext, LandscapeActivity.class);
                intent.putExtra("url", video4list.get(position).getCover_path());
                mContext.startActivity(intent);
            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {
        }
    };
    private String buyerId;
    private int m_Page = 1;
    private int m_Rows = 10;

    public VideoViewModel(Context _context) {
        super(_context);
        mContext = _context;
        //检测网络是否可用
        if (!appContext.isNetworkConnected()) {
            MyToast.makeTextAnim(_context, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";
    }

    */
/**
     * 增加阅读次数
     *//*

    private void addClick(int m_ClickID) {
        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        params.put("head", head);
        params.put("vipZoneId", m_ClickID);
        HttpUtil.post(Constants.getInstance().addClick, params, new JsonHttpResponseHandler() {
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

    */
/**
     * 所有的视频视频
     * 0 所有
     * 1 视频
     * 2 套图
     * 3 声优
     * 4 VR
     *
     * @param
     *//*

    public void getVipZone(int id, final String allType, String keyword) {
        String type = "";
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
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("sort", 0);
        params.put("allType", allType);
        params.put("day", "");
        isShow.set(true);

        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }

                ChenQianLog.i("getVipZone:" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {

                        boolean isNull = jsonObject.isNull("dataCollection");
                        if (isNull) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                            String resultMessage = jsonObject.getString("resultMessage");

                            String code = (String) SharedPreferencesUtils.get(mContext, "code", "0");
                            String url = (String) SharedPreferencesUtils.get(mContext, "url", "");
                            if (Integer.parseInt(resultMessage) == 1) {
                                isVip.set(false);
                            } else {
                                isVip.set(true);
                                if (code.equals("1")) {
                                    videoStatus.set(4);
                                } else {
                                    videoStatus.set(Integer.parseInt(resultMessage));
                                }
                            }

                            videoBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());

                            if (video1list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                            } else {
                                if (m_Page == 1) {
                                    video1list.clear();
                                }
                            }
                            video1list.addAll(videoBeen);
                            for (int j = 0; j < video1list.size(); j++) {
                                video1list.get(j).notifyChange();
                            }
                            String totalPage = jsonObject.getString("totalPage");

                            //如果页数超过最后一页
                            if (Integer.parseInt(totalPage) <= m_Page) {
                                iactionListener.SuccessCallback("0");
                            }
                        }


                        isShow.set(false);
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

    */
/**
     * 所有的视频视频
     *
     * @param
     *//*

    public void getVipTypeZone(int id, final String allType, String type) {
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
        params.put("sort", 0);
        params.put("allType", allType);
        params.put("day", "");
        isShow.set(true);

        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        boolean isNull = jsonObject.isNull("dataCollection");
                        if (isNull) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                            videotypeBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                            if (video2list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                            } else {
                                if (m_Page == 1) {
                                    video2list.clear();
                                }
                            }
                            video2list.addAll(videotypeBeen);
                            for (int j = 0; j < video2list.size(); j++) {
                                video2list.get(j).notifyChange();
                            }
                            String totalPage = jsonObject.getString("totalPage");

                            //如果页数超过最后一页
                            if (Integer.parseInt(totalPage) <= m_Page) {
                                iactionListener.SuccessCallback("1");
                            }
                        }


                        isShow.set(false);
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


    */
/**
     * 所有的视频视频
     *
     * @param
     *//*

    public void getVipVoiceZone(int id, final String allType) {
        String type = "";
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
        params.put("sort", 0);
        params.put("allType", allType);
        params.put("day", "");


        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                        videovoiceBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                        if (video3list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                        } else {
                            if (m_Page == 1) {
                                video3list.clear();
                            }
                        }
                        video3list.addAll(videovoiceBeen);
                        for (int j = 0; j < video3list.size(); j++) {
                            video3list.get(j).notifyChange();
                        }
                        String totalPage = jsonObject.getString("totalPage");

                        //如果页数超过最后一页
                        if (Integer.parseInt(totalPage) <= m_Page) {
                            iactionListener.SuccessCallback("3");
                        }

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


    */
/**
     * 所有的视频视频
     *
     * @param
     *//*

    public void getVipVRZone(int id, final String allType) {
        String type = "";
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
        params.put("sort", 1);
        params.put("allType", allType);
        params.put("day", "");


        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                        videoVRBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                        if (video4list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                        } else {
                            if (m_Page == 1) {
                                video4list.clear();
                            }
                        }
                        video4list.addAll(videoVRBeen);
                        for (int j = 0; j < video4list.size(); j++) {
                            video4list.get(j).notifyChange();
                        }
                        String totalPage = jsonObject.getString("totalPage");

                        //如果页数超过最后一页
                        if (Integer.parseInt(totalPage) <= m_Page) {
                            iactionListener.SuccessCallback("4");
                        }

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

    */
/**
     * 周期热的的视频视频
     *
     * @param
     *//*

    public void getHotVideo(int id, String day) {
        String type = "";
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
        params.put("sort", 1);
        params.put("allType", 0);
        params.put("day", day);
        isShow.set(true);

        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {

                        boolean isNull = jsonObject.isNull("dataCollection");
                        if (isNull) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                            videoBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                            if (video1list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                            } else {
                                if (m_Page == 1) {
                                    video1list.clear();
                                }
                            }
                            video1list.addAll(videoBeen);
                            for (int j = 0; j < video1list.size(); j++) {
                                video1list.get(j).notifyChange();
                            }
                            String totalPage = jsonObject.getString("totalPage");

                            //如果页数超过最后一页
                            if (Integer.parseInt(totalPage) <= m_Page) {
                                iactionListener.SuccessCallback("0");
                            }
                        }

                        isShow.set(false);
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

    */
/**
     * 周期热的的视频视频
     *
     * @param
     *//*

    public void getVipZoneKeyWords() {

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getVipZoneKeyWords, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        String dataCollection = jsonObject.getString("dataCollection");
                        List<String> listStr = jsonUtil.jsonToDStringBean(dataCollection);
                        iactionListener.SuccessCallback(listStr);
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

    */
/**
     * 获取会员专区使用资格
     *
     * @param
     *//*

    public void setVipZoneFree() {

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().setVipZoneFree, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        isVip.set(false);
                    }
                    String resultMessage = jsonObject.getString("resultMessage");
                    MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
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
}
*/
package com.bber.company.android.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.bean.VideoBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.activity.StreamingMediaVideoActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.RecyclerItemClickListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于登陆的所有逻辑
 */

public class VideoViewModel extends BaseViewModel {
    //首页新闻列表
    public ObservableList<VideoBean> video1list = new ObservableArrayList<VideoBean>();
    public ObservableList<VideoBean> video2list = new ObservableArrayList<VideoBean>();
    public ObservableList<VideoBean> video3list = new ObservableArrayList<VideoBean>();
    public ObservableList<VideoBean> video4list = new ObservableArrayList<VideoBean>();
    //是否可以
    public ObservableBoolean isShow = new ObservableBoolean(false);
    public ObservableBoolean isVip = new ObservableBoolean(false);
    public ObservableField<Integer> videoStatus = new ObservableField<Integer>(2);
    //请求聊图列表得数据
    public List<VideoBean> videoBeen;
    public List<VideoBean> videotypeBeen;
    public List<VideoBean> videovoiceBeen;
    public List<VideoBean> videoVRBeen;
    private Context mContext;
    /***
     * 这个是视频的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener video1OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (video1list.size() > position) {
                //  int type = video1list.get(position).getType();
                //if (type == 3) {
                //  startVoiceActivity(video1list, position );
                //} else {

                 /*   Intent intent = new Intent(mContext, LandscapeActivity.class);
                    intent.putExtra("url",video1list.get(position).getCover_path());
                    mContext.startActivity(intent);*/
                startVoiceActivity(video1list, position);

                // }
                addClick(video1list.get(position).getId());
            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    /***
     * 这个是视频的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener video2OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (video2list.size() > position) {
                addClick(video2list.get(position).getId());
              /*  Intent intent = new Intent(mContext, LandscapeActivity.class);
                intent.putExtra("url",video2list.get(position).getCover_path());
                mContext.startActivity(intent);*/
                startVoiceActivity(video2list, position);

            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    /***
     * 这个是视频的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener video3OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {

            if (video3list.size() > position) {
                addClick(video3list.get(position).getId());
              /*  Intent intent = new Intent(mContext, LandVoiceActivity.class);
                intent.putExtra("url",video3list.get(position).getCover_path());
                intent.putExtra("name", video3list.get(position).getTitle());
                intent.putExtra("source",video3list.get(position).getVipZoneImglist().get(0).source);
                mContext.startActivity(intent);*/
                startVoiceActivity(video3list, position);

            }

            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
    /***
     * 这个是视频的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener video4OnClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position) {
            if (video4list.size() > position) {
                addClick(video4list.get(position).getId());
               /* Intent intent = new Intent(mContext, LandscapeActivity.class);
                intent.putExtra("url",video4list.get(position).getCover_path());
                mContext.startActivity(intent);*/
                startVoiceActivity(video4list, position);

            }
            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {
        }
    };
    private String packagename = "com.example.chenq.videoplayerdome";
    private String packageclassname = "com.example.chenq.videoplayerdome.MainActivity";
    private String buyerId;
    private int m_Page = 1;
    private int m_Rows = 10;

    public VideoViewModel(Context _context) {
        super(_context);
        mContext = _context;
        //检测网络是否可用
        if (!appContext.isNetworkConnected()) {
            MyToast.makeTextAnim(_context, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        buyerId = SharedPreferencesUtils.get(gContext, Constants.USERID, "-1") + "";
    }

    private boolean isPkgInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void startVoiceActivity(ObservableList<VideoBean> video1list, int position) {

        int type = video1list.get(position).getType();
        //  ComponentName componentName = new ComponentName(packagename, packageclassname);
        Intent intent = new Intent(); //StreamingMediaVideoActivity  LandVoiceActivity
        intent.setClass(mContext, StreamingMediaVideoActivity.class);
        // intent.setComponent(componentName);
        intent.putExtra("url", video1list.get(position).getCover_path());
        intent.putExtra("name", video1list.get(position).getTitle());
        if (type == 3) {
            intent.putExtra("source", video1list.get(position).getVipZoneImglist().get(0).source);
        }
        mContext.startActivity(intent);

    }

    /**
     * 增加阅读次数
     */
    private void addClick(int m_ClickID) {
        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        params.put("head", head);
        params.put("vipZoneId", m_ClickID);
        HttpUtil.post(Constants.getInstance().addClick, params, new JsonHttpResponseHandler() {
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
     * 所有的视频视频
     * 0 所有
     * 1 视频
     * 2 套图
     * 3 声优
     * 4 VR
     *
     * @param
     */
    public void getVipZone(int id, final String allType, String keyword) {
        String type = "";
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
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("sort", 0);
        params.put("allType", allType);
        params.put("day", "");
        isShow.set(true);

        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }

                ChenQianLog.i("getVipZone:" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {

                        boolean isNull = jsonObject.isNull("dataCollection");
                        if (isNull) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");
                            String resultMessage = jsonObject.getString("resultMessage");

                            String code = (String) SharedPreferencesUtils.get(mContext, "code", "0");
                            String url = (String) SharedPreferencesUtils.get(mContext, "url", "");
                            if (Integer.parseInt(resultMessage) == 1) {
                                isVip.set(false);
                            } else {
                             /*   isVip.set(true);
                                if (code.equals("1")) {
                                    videoStatus.set(4);
                                } else {
                                    videoStatus.set(Integer.parseInt(resultMessage));
                                }*/
                                isVip.set(false);
                            }

                            videoBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());

                            if (video1list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                            } else {
                                if (m_Page == 1) {
                                    video1list.clear();
                                }
                            }
                            video1list.addAll(videoBeen);
                            for (int j = 0; j < video1list.size(); j++) {
                                video1list.get(j).notifyChange();
                            }
                            String totalPage = jsonObject.getString("totalPage");

                            //如果页数超过最后一页
                            if (Integer.parseInt(totalPage) <= m_Page) {
                                iactionListener.SuccessCallback("0");
                            }
                        }


                        isShow.set(false);
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
     * 所有的视频视频
     *
     * @param
     */
    public void getVipTypeZone(int id, final String allType, String type) {
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
        params.put("sort", 0);
        params.put("allType", allType);
        params.put("day", "");
        isShow.set(true);

        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        boolean isNull = jsonObject.isNull("dataCollection");
                        if (isNull) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                            videotypeBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                            if (video2list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                            } else {
                                if (m_Page == 1) {
                                    video2list.clear();
                                }
                            }
                            video2list.addAll(videotypeBeen);
                            for (int j = 0; j < video2list.size(); j++) {
                                video2list.get(j).notifyChange();
                            }
                            String totalPage = jsonObject.getString("totalPage");

                            //如果页数超过最后一页
                            if (Integer.parseInt(totalPage) <= m_Page) {
                                iactionListener.SuccessCallback("1");
                            }
                        }


                        isShow.set(false);
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
     * 所有的视频视频
     *
     * @param
     */
    public void getVipVoiceZone(int id, final String allType) {
        String type = "";
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
        params.put("sort", 0);
        params.put("allType", allType);
        params.put("day", "");


        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                        videovoiceBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                        if (video3list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                        } else {
                            if (m_Page == 1) {
                                video3list.clear();
                            }
                        }
                        video3list.addAll(videovoiceBeen);
                        for (int j = 0; j < video3list.size(); j++) {
                            video3list.get(j).notifyChange();
                        }
                        String totalPage = jsonObject.getString("totalPage");

                        //如果页数超过最后一页
                        if (Integer.parseInt(totalPage) <= m_Page) {
                            iactionListener.SuccessCallback("3");
                        }

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
     * 所有的视频视频
     *
     * @param
     */
    public void getVipVRZone(int id, final String allType) {
        String type = "";
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
        params.put("sort", 1);
        params.put("allType", allType);
        params.put("day", "");


        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                        videoVRBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                        if (video4list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                        } else {
                            if (m_Page == 1) {
                                video4list.clear();
                            }
                        }
                        video4list.addAll(videoVRBeen);
                        for (int j = 0; j < video4list.size(); j++) {
                            video4list.get(j).notifyChange();
                        }
                        String totalPage = jsonObject.getString("totalPage");

                        //如果页数超过最后一页
                        if (Integer.parseInt(totalPage) <= m_Page) {
                            iactionListener.SuccessCallback("4");
                        }

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
     * 周期热的的视频视频
     *
     * @param
     */
    public void getHotVideo(int id, String day) {
        String type = "";
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
        params.put("sort", 1);
        params.put("allType", 0);
        params.put("day", day);
        isShow.set(true);

        HttpUtil.post(Constants.getInstance().getVipZone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {

                        boolean isNull = jsonObject.isNull("dataCollection");
                        if (isNull) {
                            String resultMessage = jsonObject.getString("resultMessage");
                            MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            JSONArray dataCollection = jsonObject.getJSONArray("dataCollection");

                            videoBeen = jsonUtil.jsonToVideoBean(dataCollection.toString());


                            if (video1list.size() == 0 && m_Page == 1) {
//                        MyToast.makeTextAnim(gContext, R.string.rechoose, 0, R.style.PopToast).show();
                            } else {
                                if (m_Page == 1) {
                                    video1list.clear();
                                }
                            }
                            video1list.addAll(videoBeen);
                            for (int j = 0; j < video1list.size(); j++) {
                                video1list.get(j).notifyChange();
                            }
                            String totalPage = jsonObject.getString("totalPage");

                            //如果页数超过最后一页
                            if (Integer.parseInt(totalPage) <= m_Page) {
                                iactionListener.SuccessCallback("0");
                            }
                        }

                        isShow.set(false);
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
     * 周期热的的视频视频
     *
     * @param
     */
    public void getVipZoneKeyWords() {

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getVipZoneKeyWords, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        String dataCollection = jsonObject.getString("dataCollection");
                        List<String> listStr = jsonUtil.jsonToDStringBean(dataCollection);
                        iactionListener.SuccessCallback(listStr);
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
     * 获取会员专区使用资格
     *
     * @param
     */
    public void setVipZoneFree() {

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().setVipZoneFree, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        isVip.set(false);
                    }
                    String resultMessage = jsonObject.getString("resultMessage");
                    MyToast.makeTextAnim(gContext, resultMessage, 0, R.style.PopToast).show();
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
}
