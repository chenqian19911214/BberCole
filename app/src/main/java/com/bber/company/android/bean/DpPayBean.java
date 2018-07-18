package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class DpPayBean implements Serializable {

    private int Id;
    private int orderId;
    private int organId;
    private Double money;
    private String payCode;
    private String dpCode;
    private int dpStatus;
    private String errorMsg;
    private int status;
    private int code;
    private int type;
    private String createDate;
    private String responseUpdateDate;
    private String resultUpdateDate;
    private int buyerUserId;
    private Double actualMoney;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Double getActualMoney() {
        return actualMoney;
    }

    public void setActualMoney(Double actualMoney) {
        this.actualMoney = actualMoney;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrganId() {
        return organId;
    }

    public void setOrganId(int organId) {
        this.organId = organId;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getDpCode() {
        return dpCode;
    }

    public void setDpCode(String dpCode) {
        this.dpCode = dpCode;
    }

    public int getDpStatus() {
        return dpStatus;
    }

    public void setDpStatus(int dpStatus) {
        this.dpStatus = dpStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getResponseUpdateDate() {
        return responseUpdateDate;
    }

    public void setResponseUpdateDate(String responseUpdateDate) {
        this.responseUpdateDate = responseUpdateDate;
    }

    public String getResultUpdateDate() {
        return resultUpdateDate;
    }

    public void setResultUpdateDate(String resultUpdateDate) {
        this.resultUpdateDate = resultUpdateDate;
    }

    public int getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(int buyerUserId) {
        this.buyerUserId = buyerUserId;
    }
}
