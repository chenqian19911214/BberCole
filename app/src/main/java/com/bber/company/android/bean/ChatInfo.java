package com.bber.company.android.bean;


import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/10 0010.
 */
public class ChatInfo implements Serializable {
    public static final int MESSAGE_FROM = 0;
    public static final int MESSAGE_TO = 1;
    private String msgID;
    private Integer status;//订单状态 1:创建订单完成  2：买家已付款(接受订单) 3：开始服务 4：服务结束未评价  5：买家已评价
    private String orderID;//订单
    private String fromUser;//发送者
    private String toUser;//接收者
    private String buyerHead;//自己的头像
    private String buyerName;//自己的昵称
    private String msg_type;//信息类型       card:表示名片
    private String content;//信息内容
    private int isComing;//0表接收的消息，1表发送的消息
    private String date;//时间
    private String isReaded;//是否已读
    private Integer sellerId; //妹子ID
    private String sellerName;//妹子昵称
    private String sellerHead;//妹子头像
    private String imagePath;//图片路径
    private String voicePath;//语音路径
    private MapBean mapBean;//位置
    private int voiceTime;//语音时长
    private int height;//图片高度
    private int width;//图片宽度


    public ChatInfo() {
    }


    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getBuyerHead() {
        return buyerHead;
    }

    public void setBuyerHead(String buyerHead) {
        this.buyerHead = buyerHead;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerHead() {
        return sellerHead;
    }

    public void setSellerHead(String sellerHead) {
        this.sellerHead = sellerHead;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsComing() {
        return isComing;
    }

    public void setIsComing(int isComing) {
        this.isComing = isComing;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public int getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(int voiceTime) {
        this.voiceTime = voiceTime;
    }

    public MapBean getMapBean() {
        return mapBean;
    }

    public void setMapBean(MapBean poiItem) {
        this.mapBean = poiItem;
    }


}
