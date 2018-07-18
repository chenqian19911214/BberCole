package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;

/**
 * Created by carlo.c on 2018/4/26.
 */

public class BroadcaseLowMonet implements Serializable {


    /**
     * currentPage : 0
     * dataCollection : {"papaWebSite":"http：www","lowMoney":"11","talkMoney":"5","promptTime":"30"}
     * resultCode : 1
     * resultMessage : 主播视频计费接口
     * totalPage : 0
     */

    private int currentPage;
    private DataCollectionBean dataCollection;
    private int resultCode;
    private String resultMessage;
    private int totalPage;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public DataCollectionBean getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(DataCollectionBean dataCollection) {
        this.dataCollection = dataCollection;
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

    public static class DataCollectionBean {
        /**
         * papaWebSite : http：www
         * lowMoney : 11
         * talkMoney : 5
         * promptTime : 30
         */

        /**
         * 水印
         */
        private String papaWebSite;

        /**
         * 最低限额
         */
        private String lowMoney;
        /**
         * 第一次 与主播 文字聊天扣费
         */
        private String talkMoney;

        /**
         * 低于秒数提示用户
         */
        private String promptTime;

        public String getPapaWebSite() {
            return papaWebSite;
        }

        public void setPapaWebSite(String papaWebSite) {
            this.papaWebSite = papaWebSite;
        }

        public String getLowMoney() {
            return lowMoney;
        }

        public void setLowMoney(String lowMoney) {
            this.lowMoney = lowMoney;
        }

        public String getTalkMoney() {
            return talkMoney;
        }

        public void setTalkMoney(String talkMoney) {
            this.talkMoney = talkMoney;
        }

        public String getPromptTime() {
            return promptTime;
        }

        public void setPromptTime(String promptTime) {
            this.promptTime = promptTime;
        }
    }
}
