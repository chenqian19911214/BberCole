package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * 群聊信息bean
 * Created by Vencent on 2016/02/22 0024.
 * C端获取群聊信息的MEG
 * String fromUserId;//发送者ID
 * String toUserId;//接收者ID，没有可以为空，私聊
 * String fromUserHead;//发送者的头像
 * String fromUserName;//发送者的昵称
 * String msgType;//信息类型 text:文本 image:图片 voice：语音  order：订单 location：位置
 * String msgContent;//信息内容
 * String sendDate;//发送时间
 * String sourcePath;//资源位置：如头像，视频，语音
 * MapBean mapBean;//位置信息，包含的是高德地图的相应信息
 * int voiceTime;//语音时长
 * int imageIeight;//图片高度
 * int imageWidth;//图片宽度
 */
public class MessageBean implements Serializable {

    public String fromUserId;//发送者ID
    public String toUserId;//接收者ID，没有可以为空，私聊
    public String fromUserHead;//发送者的头像
    public String fromUserName;//发送者的昵称
    public String msgType;//信息类型 text:文本 image:图片 voice：语音  order：订单 location：位置
    public String msgContent;//信息内容
    public String sendDate;//发送时间
    public String sourcePath;//资源位置：如头像，视频，语音
    public MapBean mapBean;//位置信息，包含的是高德地图的相应信息
    public int voiceTime;//语音时长
    public int imageHeight;//图片高度
    public int imageWidth;//图片宽度
    public int viplevel = 0;//VIP等级
    public int gender; //性别 1表示男 2表示女

    @Override
    public String toString() {
        return "{" +
                "fromUserId:'" + fromUserId + '\'' +
                ", toUserId:'" + toUserId + '\'' +
                ", fromUserHead:'" + fromUserHead + '\'' +
                ", fromUserName:'" + fromUserName + '\'' +
                ", msgType:'" + msgType + '\'' +
                ", msgContent:'" + msgContent + '\'' +
                ", sendDate:'" + sendDate + '\'' +
                ", sourcePath:'" + sourcePath + '\'' +
                ", mapBean:" + mapBean +
                ", voiceTime:" + voiceTime +
                ", imageHeight:" + imageHeight +
                ", imageWidth:" + imageWidth +
                '}';
    }
}
