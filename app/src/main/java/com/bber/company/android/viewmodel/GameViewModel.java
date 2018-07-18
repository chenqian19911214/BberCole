package com.bber.company.android.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.GameBean;
import com.bber.company.android.bean.GameBeenSoldBean;
import com.bber.company.android.bean.GameGirlsBean;
import com.bber.company.android.bean.GameMessageEnterBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.NettyClientBootstrap;
import com.bber.company.android.listener.NettyListener;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.activity.GameActivity;
import com.bber.company.android.view.activity.MainActivity;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.RecyclerItemClickListener;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketResponsePacket;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Vencent on 2017/2/20.
 * 处理的逻辑页面
 * 关于登陆的所有逻辑
 */

public class GameViewModel extends BaseViewModel implements NettyListener {
    public SocketClient socketClient;
    //：游戏可下注金额
    public String gameBetLimit;
    //当前游戏局号
    public String gameNumber = "";
    public String hotAndCold = "";
    public int wait = 0;
    public String winNumberArray = "";
    public Double jackpotBalance;
    public Double jackpotAmount;
    public NettyClientBootstrap nettyStart;
    //这个是桌台信息
    public ObservableList<GameMessageEnterBean> datalist = new ObservableArrayList<GameMessageEnterBean>();
    public String currentToken;
    //游戏编码
    public String gameCode;
    //游戏桌台号
    public String gameTableNumber;
    public String gameLuckymmImg;
    //所有图片集合
    public List<GameGirlsBean> girlspicList;
    public List<GameBeenSoldBean> beanlist;
    public String gameTpye2data;
    public String notOpenImg;
    public String picList;
    private JsonUtil jsonUtil;
    private String head;
    private String MD5key = "aa455bf23756e5f8168b54e0dde38bf3";
    //最大下注号码数量
    private String gameMaxNumber;
    private String sendData;
    /***
     * 这个是聊图的recycle的长按监听和单击监听
     */
    public RecyclerItemClickListener.OnItemClickListener onClick = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public boolean onItemClick(View view, final int position) {
            if (MyApplication.getContext().isNetworkConnected()) {
                if (datalist.size() > position) {

                    Intent intent = new Intent(gContext, GameActivity.class);
                    intent.putExtra("gameTpye2data", gameTpye2data);
                    intent.putExtra("currentToken", currentToken);
                    intent.putExtra("position", position);
                    gContext.startActivity(intent);
                    //设置音乐停止
                    ((MainActivity) gContext).setMusicSt(false);

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            enterGameDesk(datalist.get(position));
                        }
                    }, 2000);
                    return true;
                }
            } else {
                MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
            }


            return false;
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };


    public GameViewModel(Context _context) {
        super(_context);
        /**
         * 设置自动转换String类型到byte[]类型的编码
         * 如未设置（默认为null），将不能使用{@link SocketClient#sendString(String)}发送消息
         * 如设置为非null（如UTF-8），在接受消息时会自动尝试在接收线程（非主线程）将接收的byte[]数据依照编码转换为String，在{@link SocketResponsePacket#getMessage()}读取
         */
//        setSocket();
        jsonUtil = new JsonUtil(gContext);
        head = jsonUtil.httpHeadToJson(gContext);
    }

    //socket长链接
    public void setSocket() {
//        socketClient = new SocketClient(new SocketClientAddress("10.14.2.6", 8812));

        try {
            if (nettyStart == null) {
                nettyStart = NettyClientBootstrap.getInstance();
                nettyStart.startNetty();
            } else {
                nettyStart = NettyClientBootstrap.getInstance();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /***
     * 与GAME游戏服务建立链接
     */
    public void loginSocket() {
        String paramForSign = "head=" + head + "#type=" + 1 + MD5key;
        final String sign = Tools.md5(paramForSign);

        GameBean gameBean = new GameBean();
        gameBean.head = head;
        gameBean.sign = sign;
        //Type1的时候登陆,2的时候进入大厅,3的时候进入游戏桌台,4的时候下单购买号码
        gameBean.type = 1;
        sendData = jsonUtil.GameBeanToString(gameBean);
        NettyClientBootstrap.getInstance().sendMsg(sendData + "\n");
        NettyClientBootstrap.getInstance().setInfoListener(this);
    }

    /***
     * 拼接sign的加密字符
     */
    private String spellSign(String token, String type) {
        return Tools.md5("token=" + token + "#type=" + type + MD5key);
    }

    /***
     * 拼接sign的加密字符
     */
    public String spellSign(String token, String gameCode, String gameTableNumber, String gameNumber, int betNumber, Double betAmount, int type) {
        return Tools.md5("token=" + token + "#gameCode=" + gameCode + "#gameTableNumber=" + gameTableNumber + "#gameNumber=" + gameNumber + "#betNumber=" + betNumber + "#betAmount=" + betAmount + "#type=" + type + MD5key);
    }

    /***
     * 拼接sign的加密字符
     */
    private String spellSign(String token, String gameCode, String gameTableNumber, String type) {
        return Tools.md5("token=" + token + "#gameCode=" + gameCode + "#gameTableNumber=" + gameTableNumber + "#type=" + type + MD5key);
    }

    /***
     * 进入游戏大厅
     */
    public void enterGameRoom(String token) {
        GameBean gameBean = new GameBean();
        gameBean.sign = spellSign(token, "2");
        gameBean.token = token;
        currentToken = token;
        //Type1的时候登陆,2的时候进入大厅,3的时候进入游戏桌台,4的时候下单购买号码
        gameBean.type = 2;
        sendData = jsonUtil.GameBeanToString(gameBean);
        try {
            nettyStart.sendMsg(sendData + "\n");
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /***
     * 进入游戏卓台
     */
    public void enterGameDesk(GameMessageEnterBean bean) {
        GameBean gameBean = new GameBean();
        gameBean.sign = spellSign(currentToken, bean.gameCode, bean.gameTableNumber, "3");
        gameCode = bean.gameCode;
        gameBean.token = currentToken;
        gameBean.gameCode = bean.gameCode;
        gameBean.gameTableNumber = bean.gameTableNumber;
        gameTableNumber = bean.gameTableNumber;
        gameBetLimit = bean.gameBetLimit;
        gameMaxNumber = bean.gameMaxNumber;
        //Type1的时候登陆,2的时候进入大厅,3的时候进入游戏桌台,4的时候下单购买号码
        gameBean.type = 3;
        sendData = jsonUtil.GameBeanToString(gameBean);
        nettyStart.sendMsg(sendData + "\n");
    }

    @Override
    public void onDisConnected(ChannelHandlerContext ctx) {
        Constants.ISCONTACT = false;
        try {
            if (!MyApplication.getContext().isNetworkConnected()) {
                MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), R.string.no_network, 0, R.style.PopToast).show();
                return;
            }
            nettyStart.startNetty();
            loginSocket();
            MyToast.makeTextAnim(gContext, "网络不稳定，为您重连...", 0, R.style.PopToast).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExceptionconnected(ChannelHandlerContext ctx) {
        Constants.ISCONTACT = false;
        LogUtils.e("过不过的来啊！！！！！！！！！！！！！！");
        MyToast.makeTextAnim(gContext, "网络不稳定，为您重连...", 0, R.style.PopToast).show();
    }

    @Override
    public void onResponse(ChannelHandlerContext ctx, final String msg) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.e(msg);
                String message = msg; // 获取按默认设置的编码转化的String，可能为null
                String type = "";
                String resultCode = "";
                String token = "";
                String sign = "";
                try {
                    JSONObject dataJson = new JSONObject(message);
                    type = dataJson.getString("type");
                    resultCode = dataJson.getString("resultCode");
                    token = dataJson.getString("token");
                    sign = dataJson.getString("sign");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (resultCode.equals("0")) {
                    Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                    intent.putExtra("message", message);
                    intent.putExtra("type", 0);
                    gContext.sendBroadcast(intent);
                }

                //resultCode为1的时候成功返回
                if (resultCode.equals("1")) {
                    //type为1的时候证明是链接


                    if (type.equals("1")) {
                        //验证返回的sign是不是同一个人
//                            GameLoginBean gameLoginBean = jsonUtil.GameLoginBeanToString(message);
                        if (sign.equals(spellSign(token, "1"))) {
                            enterGameRoom(token);
                        } else {
                            MyToast.makeTextAnim(gContext, "服务器连接超时", 0, R.style.PopToast).show();
                        }
//                            进入游戏大厅
                    } else if (type.equals("2")) {
                        if (sign.equals(spellSign(currentToken, "2"))) {
                            String currentData = "";
                            try {
                                JSONObject dataJson = new JSONObject(message);
                                currentData = dataJson.getString("massage");
                                gameTpye2data = currentData;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            datalist.clear();
                            List<GameMessageEnterBean> beanlist = jsonUtil.GameMessageEnterBeanToString(currentData);
                            datalist.addAll(beanlist);
                            for (int i = 0; i < datalist.size(); i++) {
                                datalist.get(i).notifyChange();
                            }

                            iactionListener.SuccessCallback(2);
                            LogUtils.e("重新获取到的新的大厅信息" + currentData);

                        } else {
                            MyToast.makeTextAnim(gContext, "服务器连接超时", 0, R.style.PopToast).show();
                        }
//                            进入游戏桌台
                    } else if (type.equals("3")) {
                        String currentData = "";
                        String currentMassage = "";
                        String beenSold = "";
                        String hot = "";
                        List<String> stringList = new ArrayList<String>();
                        try {
                            JSONObject dataJson = new JSONObject(message);
                            currentData = dataJson.getString("massage");
                            JSONObject currentDataJson = new JSONObject(currentData);
//                    currentMassage = currentDataJson.getString("massage");
//                    JSONObject soldData = new JSONObject(currentMassage);

                            beenSold = currentDataJson.getString("beenSold");

                            gameNumber = currentDataJson.getString("gameNumber");
                            gameLuckymmImg = currentDataJson.getString("gameLuckymmImg");
                            hotAndCold = currentDataJson.getString("hotAndCold");
                            wait = currentDataJson.getInt("wait");
                            winNumberArray = currentDataJson.getString("winNumberArray");
                            jackpotBalance = currentDataJson.getDouble("jackpotBalance");
//                            jackpotAmount = currentDataJson.getDouble("jackpotAmount");

//                            JSONObject hotAndColdJson = new JSONObject(hotAndCold);
//                            hot = hotAndColdJson.getString("hot");
//                            stringList = jsonUtil.jsonToDStringBean(hot);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!beenSold.equals("[]")) {
                            beanlist = jsonUtil.GameSoldToString(beenSold);
                            //将已购买
                        }
                        if (!Tools.isEmpty(gameLuckymmImg)) {
                            //所有图片集合
                            girlspicList = jsonUtil.GameGilrsToString(gameLuckymmImg);
                        }
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 3);
                        intent.putExtra("beenSold", beenSold);
                        intent.putExtra("hotAndCold", hotAndCold);
                        intent.putExtra("wait", wait);
                        intent.putExtra("jackpotBalance", jackpotBalance);
                        intent.putExtra("gameNumber", gameNumber);
//                        intent.putExtra("jackpotAmount", jackpotAmount);
                        intent.putExtra("winNumberArray", winNumberArray);
                        intent.putExtra("gameLuckymmImg", gameLuckymmImg);//

                        gContext.sendBroadcast(intent);

//                        MyToast.makeTextAnim(gContext, "加入成功", 0, R.style.PopToast).show();
//                            下单购买号码
                    } else if (type.equals("4")) {
                        String currentData = "";
                        String currentMassage = "";
                        String beenSold = "";
                        double ubMoney = 0.0;
                        String gameFreeMoney = "";
                        int driveDay = 0;
                        try {
                            JSONObject dataJson = new JSONObject(message);
                            currentData = dataJson.getString("massage");
                            JSONObject dataJson2 = new JSONObject(currentData);
                            beenSold = dataJson2.getString("beenSold");
                            ubMoney = dataJson2.getDouble("ubMoney");
                            gameFreeMoney = dataJson2.getString("gameFreeMoney");
                            driveDay = dataJson2.getInt("driveDay");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //将已购买
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 4);
                        intent.putExtra("message", beenSold);
                        intent.putExtra("gameFreeMoney", gameFreeMoney);
                        intent.putExtra("ubMoney", ubMoney);
                        intent.putExtra("driveDay", driveDay);

                        gContext.sendBroadcast(intent);

                    } else if (type.equals("5")) {

                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 5);
                        intent.putExtra("message", message);

                        gContext.sendBroadcast(intent);


                    } else if (type.equals("99")) {

//                            100的时候是开奖
                    } else if (type.equals("100")) {
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 100);
                        intent.putExtra("message", message);

                        gContext.sendBroadcast(intent);
//                          已购买号码推送
                    } else if (type.equals("101")) {
                        String currentData = "";
                        String currentMassage = "";
                        String beenSold = "";

                        try {
                            JSONObject dataJson = new JSONObject(message);
                            currentData = dataJson.getString("massage");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!beenSold.equals("[]")) {
                            List<GameBeenSoldBean> beanlist = jsonUtil.GameSoldToString(currentData);

//                            iactionListener.SuccessCallback(beanlist);
                        }
                        //将已购买
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 101);
                        intent.putExtra("message", currentData);

                        gContext.sendBroadcast(intent);
//                    看的人数推送
                    } else if (type.equals("102")) {
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 102);
                        intent.putExtra("message", message);

                        gContext.sendBroadcast(intent);

                    } else if (type.equals("103")) {
                        String intendata = "";
                        try {
                            JSONObject dataJson = new JSONObject(message);
                            intendata = dataJson.getString("massage");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<GameMessageEnterBean> wacthList = jsonUtil.GameMessageEnterBeanToString(intendata);
                        if (datalist.size() == wacthList.size()) {
                            for (int i = 0; i < datalist.size(); i++) {
                                datalist.get(i).setGameCode(wacthList.get(i).gameCode);
                                datalist.get(i).setGameTableNumber(wacthList.get(i).gameTableNumber);
                                datalist.get(i).setWatching(wacthList.get(i).watching);
                                datalist.get(i).setJackpotBalance(wacthList.get(i).jackpotBalance);
                                datalist.get(i).notifyChange();
                            }
                        }
                        LogUtils.e("datalist" + datalist.size());
                        LogUtils.e("wacthList" + wacthList.size());
                        iactionListener.SuccessCallback(3);
//                        iactionListener.SuccessCallback(wacthList);
                    }
                    //其他推送
                    else if (type.equals("199")) {
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 199);
                        intent.putExtra("message", message);
                        String intendata = "";
                        JSONObject dataJson = null;
                        try {
                            dataJson = new JSONObject(message);
                            intendata = dataJson.getString("massage");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        iactionListener.SuccessCallback(intendata);
                        gContext.sendBroadcast(intent);
//                  当购买重复的时候
                    } else if (type.equals("200")) {
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 200);
                        intent.putExtra("message", message);
                        gContext.sendBroadcast(intent);
                    } else if (type.equals("201")) {
                        Intent intent = new Intent(Constants.ACTION_GAME_TYPE3);
                        intent.putExtra("type", 201);
                        gContext.sendBroadcast(intent);
                    } else if (type.equals("300")) {
                        NettyClientBootstrap.getInstance().sendMsg(message + "\n");
                    }
                } else if (resultCode.equals("2")) {
                    if (type.equals("2")) {
                        String currentData = "";
                        try {
                            JSONObject dataJson = new JSONObject(message);
                            currentData = dataJson.getString("massage");
                            JSONObject dataJson1 = new JSONObject(currentData);
                            String path = (String) SharedPreferencesUtils.get(gContext, "IMAGE_FILE", "");
                            notOpenImg = path + dataJson1.getString("notOpenImg");

                            datalist.clear();

                            iactionListener.FailCallback("1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    /***
     * 判断是否会员
     */
    public void setGameLinmit() {

        RequestParams params = new RequestParams();
        String head = new JsonUtil(gContext).httpHeadToJson(gContext);
        final JsonUtil jsonUtil = new JsonUtil(gContext);

        params.put("head", head);

        HttpUtil.post(Constants.getInstance().getGameVipFlag, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(gContext, jsonObject, null)) {
                    return;
                }
                try {
                    //0 不能进入游戏大厅
                    //1 能进入大厅

                    String resultMessage = jsonObject.getString("resultMessage");
                    String path = (String) SharedPreferencesUtils.get(gContext, "IMAGE_FILE", "");
                    if (jsonObject.getString("dataCollection") != null) {
                        picList = path + jsonObject.getString("dataCollection");
                    }

                    if (resultMessage.equals("0")) {
                        iactionListener.SuccessCallback(0);
                    } else {
                        iactionListener.SuccessCallback(1);
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
}
