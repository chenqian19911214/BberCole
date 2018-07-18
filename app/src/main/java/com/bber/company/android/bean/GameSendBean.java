package com.bber.company.android.bean;

/**
 * 银行卡号码
 * Created by Vencent on 2016/02/22 0024.
 * C端获取详细银行卡返回的bean类型
 */
public class GameSendBean {
    public String token;    //与GAME游戏服务器建立链接成功后返回的token
    public String gameCode;    //游戏编码
    public String gameTableNumber;    //游戏桌台号
    public String gameNumber;    //购彩号码，必须在接口2中返回的最大购买号码数内
    public int betNumber;
    public Double betAmount;
    public int type;
    public String sign;

}
