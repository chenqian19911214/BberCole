package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/19 0019.
 */
public class Order implements Serializable {

    private Integer id;            //订单ID(服务端ID)
    private String businessCode;    //业务单号，该订单的订单编号
    private Integer sellerId;        //卖家id
    private String sellerNickName;//卖家昵称
    private String sellerHead;     //卖家头像
    private Integer buyerId;        //买家id
    private String buyerNickName;    //卖家昵称
    private Integer businessType;    //业务类型
    private Integer status;        //状态
    private Integer money;            //订单金额
    private int serviceTime;        //服务时长(多少分钟)
    private String inServiceDate;    //开始服务时间
    private String organId;            //开始服务时间
    private String buyerOrderNumber;//第几单
    private String cashCardId;        //订单的id
    private String cashCardMoney;    //订单的id
    private String cashCardName;    //订单的id
    private String eventStatus;        //活动状态
    private String createTime;
    private String eventMoney;
    private String finalPayment;

    public String getEventMoney() {
        return eventMoney;
    }

    public void setEventMoney(String eventMoney) {
        this.eventMoney = eventMoney;
    }

    public String getFinalPayment() {
        return finalPayment;
    }

    public void setFinalPayment(String finalPayment) {
        this.finalPayment = finalPayment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSellerHead() {
        return sellerHead;
    }

    public void setSellerHead(String sellerHead) {
        this.sellerHead = sellerHead;
    }

    public String getInServiceDate() {
        return inServiceDate;
    }

    public void setInServiceDate(String inServiceDate) {
        this.inServiceDate = inServiceDate;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerNickName() {
        return sellerNickName;
    }

    public void setSellerNickName(String sellerNickName) {
        this.sellerNickName = sellerNickName;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerNickName() {
        return buyerNickName;
    }

    public void setBuyerNickName(String buyerNickName) {
        this.buyerNickName = buyerNickName;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getBuyerOrderNumber() {
        return buyerOrderNumber;
    }

    public void setBuyerOrderNumber(String buyerOrderNumber) {
        this.buyerOrderNumber = buyerOrderNumber;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getCashCardName() {
        return cashCardName;
    }

    public void setCashCardName(String cashCardName) {
        this.cashCardName = cashCardName;
    }

    public String getCashCardMoney() {
        return cashCardMoney;
    }

    public void setCashCardMoney(String cashCardMoney) {
        this.cashCardMoney = cashCardMoney;
    }

    public String getCashCardId() {
        return cashCardId;
    }

    public void setCashCardId(String cashCardId) {
        this.cashCardId = cashCardId;
    }

}
