package com.bber.company.android.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Session implements Serializable {
    private String id;
    private Integer sellerId; //妹子ID
    private String from;        //发送人
    private String name;        //发送人昵称
    private String headURL;        //发送人头像
    private String type;        //消息类型
    private String time;        //接收时间
    private String content;        //发送内容
    private Integer notReadCount;//未读记录
    private String to;        //接收人
    private String isdispose;//是否已处理 0未处理，1已处理

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getHeadURL() {
        return headURL;
    }

    public void setHeadURL(String headURL) {
        this.headURL = headURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(Integer notReadCount) {
        this.notReadCount = notReadCount;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getIsdispose() {
        return isdispose;
    }

    public void setIsdispose(String isdispose) {
        this.isdispose = isdispose;
    }


}
