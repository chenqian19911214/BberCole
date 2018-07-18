package com.bber.company.android.bean;

/**
 * 银行卡号码
 * Created by Vencent on 2016/02/22 0024.
 * C端获取详细银行卡返回的bean类型
 */
public class BindBankBean {
    private String bankId;    //银行code
    private String bankCard;    //银行卡号
    private String cardName;    //持卡人姓名

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @Override
    public String toString() {
        return "BindBankBean{" +
                "bankId='" + bankId + '\'' +
                ", bankCard='" + bankCard + '\'' +
                ", cardName='" + cardName + '\'' +
                '}';
    }
}
