package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * 银行卡号码
 * Created by Administrator on 2015/11/24 0024.
 */
public class BankCardInfor implements Serializable {
    private String cardName;
    private String cardNum;
    private String bankName;
    private int cardType;
    private int code;

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
