package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * 抵用券
 * Created by Administrator on 2015/11/24 0024.
 */
public class VoucherBean implements Serializable {

    private String cashCardId;
    private String cashCardName;
    private String cashCardState;
    private String createDate;
    private String endDate;
    private String cashCardMoney;

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

    public String getCashCardName() {
        return cashCardName;
    }

    public void setCashCardName(String cashCardName) {
        this.cashCardName = cashCardName;
    }

    public String getCashCardState() {
        return cashCardState;
    }

    public void setCashCardState(String cashCardState) {
        this.cashCardState = cashCardState;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
