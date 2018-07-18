package com.bber.company.android.bean;

import java.util.List;

/**
 * 银行卡号码
 * Created by Vencent on 2016/02/22 0024.
 * C端获取详细银行卡返回的bean类型
 */
public class GameEnterBean {
    public String resultCode;    //银请求结果：0.失败 1.成功
    public String resultMessage;    //当resultCode为0时，回传讯息为错
    public String token;    //用户登录后的标识
    public String sign;    //token+md5key
    public String type;
    public List<GameMessageEnterBean> massage;

}
