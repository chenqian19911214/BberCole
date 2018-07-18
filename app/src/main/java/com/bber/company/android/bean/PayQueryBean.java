package com.bber.company.android.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlo.c on 2018/3/27.
 */

public class PayQueryBean implements Serializable {

    /**
     * currentPage : 0
     * dataCollection : [{"createTime":"2018-03-27 10:08:38","id":1,"payCode":"WEIXIN","payDescription":"微信支付","payFlag":"1","payIcon":"payment/weixin.icon","payMax":"1000","payName":"微信","payOrder":"1","updateTime":"2018-03-27 10:08:40"}]
     * resultCode : 1
     * resultMessage :
     * totalPage : 0
     */

    private int currentPage;
    private int resultCode;
    private String resultMessage;
    private int totalPage;
    private List<DataCollectionBean> dataCollection;

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

    public List<DataCollectionBean> getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(List<DataCollectionBean> dataCollection) {
        this.dataCollection = dataCollection;
    }

    public static class DataCollectionBean {
        /**
         * createTime : 2018-03-27 10:08:38
         * id : 1
         * payCode : WEIXIN
         * payDescription : 微信支付
         * payFlag : 1
         * payIcon : payment/weixin.icon
         * payMax : 1000
         * payName : 微信
         * payOrder : 1
         * updateTime : 2018-03-27 10:08:40
         */

        private String createTime;
        private int id;
        private String payCode;
        private String payDescription;
        private String payFlag;
        private String payIcon;
        private String payMax;
        private String payName;
        private String payOrder;
        private String updateTime;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPayCode() {
            return payCode;
        }

        public void setPayCode(String payCode) {
            this.payCode = payCode;
        }

        public String getPayDescription() {
            return payDescription;
        }

        public void setPayDescription(String payDescription) {
            this.payDescription = payDescription;
        }

        public String getPayFlag() {
            return payFlag;
        }

        public void setPayFlag(String payFlag) {
            this.payFlag = payFlag;
        }

        public String getPayIcon() {
            return payIcon;
        }

        public void setPayIcon(String payIcon) {
            this.payIcon = payIcon;
        }

        public String getPayMax() {
            return payMax;
        }

        public void setPayMax(String payMax) {
            this.payMax = payMax;
        }

        public String getPayName() {
            return payName;
        }

        public void setPayName(String payName) {
            this.payName = payName;
        }

        public String getPayOrder() {
            return payOrder;
        }

        public void setPayOrder(String payOrder) {
            this.payOrder = payOrder;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
