package com.bber.company.android.bean.livebroadcast;

import java.util.List;

/**
 * Created by carlo.c on 2018/5/4.
 */

public class MessageListBean {
    private String accountOther;
    private List<BoradcastMessageBean> messageBeanList;

    public MessageListBean(String account, List<BoradcastMessageBean> messageBeanList) {
        this.accountOther = account;
        this.messageBeanList = messageBeanList;
    }

    public String getAccountOther() {
        return accountOther;
    }

    public void setAccountOther(String accountOther) {
        this.accountOther = accountOther;
    }

    public List<BoradcastMessageBean> getMessageBeanList() {
        return messageBeanList;
    }

    public void setMessageBeanList(List<BoradcastMessageBean> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }
}
