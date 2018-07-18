package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlo.c on 2018/4/16.
 */

public class BroadcaseLabelBean implements Serializable {


    /**
     * currentPage : 0
     * dataCollection : ["二次元9","1","玩玩"]
     * resultCode : 1
     * resultMessage : 主播关键字分类获取成功
     * totalPage : 0
     */

    private int currentPage;
    private int resultCode;
    private String resultMessage;
    private int totalPage;
    private List<String> dataCollection;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<String> getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(List<String> dataCollection) {
        this.dataCollection = dataCollection;
    }
}
