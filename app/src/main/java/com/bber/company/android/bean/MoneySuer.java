package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * Created by carlo.c on 2018/3/14.
 */

public class MoneySuer implements Serializable {


    /**
     * currentPage : 0   分页
     * dataCollection : {"QQ":"5000","YINLIAN_SM_FLAG":"1","YINLIAN":"5000","WEIXIN":"3000","YINLIAN_FALG":"1","ZHIFUBAO_FALG":"-1","ZHIFUBAO":"1900","WEIXIN_FALG":"-1","QQ_FALG":"1","WEIXIN_H5_FLAG":"-1","QQ_H5_FLAG":"3"}
     * resultCode : 1   返回 1 正常
     * resultMessage :
     * totalPage : 0     分页
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
         * QQ : 5000
         * YINLIAN_SM_FLAG : 1   银联无卡
         * YINLIAN : 5000
         * WEIXIN : 3000
         * YINLIAN_FALG : 1      银联扫码
         * ZHIFUBAO_FALG : -1     支付宝扫码
         * ZHIFUBAO : 1900
         * WEIXIN_FALG : -1     微信扫码
         * QQ_FALG : 1          QQ扫码
         * WEIXIN_H5_FLAG : -1  微信跳转
         * QQ_H5_FLAG : 3       QQ跳转
         */

        private String QQ;
        /**
         * 银联无卡
         */
        private String YINLIAN_SM_FLAG;
        private String YINLIAN;
        private String WEIXIN;
        /**
         * 银联扫码
         */
        private String YINLIAN_FALG;
        /**
         * 支付宝扫码
         */
        private String ZHIFUBAO_FALG;
        private String ZHIFUBAO;
        /**
         * 微信扫码
         */
        private String WEIXIN_FALG;
        /**
         * QQ扫码
         */
        private String QQ_FALG;
        /**
         * 微信跳转
         */
        private String WEIXIN_H5_FLAG;
        /**
         * QQ跳转
         */
        private String QQ_H5_FLAG;

        public String getQQ() {
            return QQ;
        }

        public void setQQ(String QQ) {
            this.QQ = QQ;
        }

        public String getYINLIAN_SM_FLAG() {
            return YINLIAN_SM_FLAG;
        }

        public void setYINLIAN_SM_FLAG(String YINLIAN_SM_FLAG) {
            this.YINLIAN_SM_FLAG = YINLIAN_SM_FLAG;
        }

        public String getYINLIAN() {
            return YINLIAN;
        }

        public void setYINLIAN(String YINLIAN) {
            this.YINLIAN = YINLIAN;
        }

        public String getWEIXIN() {
            return WEIXIN;
        }

        public void setWEIXIN(String WEIXIN) {
            this.WEIXIN = WEIXIN;
        }

        public String getYINLIAN_FALG() {
            return YINLIAN_FALG;
        }

        public void setYINLIAN_FALG(String YINLIAN_FALG) {
            this.YINLIAN_FALG = YINLIAN_FALG;
        }

        public String getZHIFUBAO_FALG() {
            return ZHIFUBAO_FALG;
        }

        public void setZHIFUBAO_FALG(String ZHIFUBAO_FALG) {
            this.ZHIFUBAO_FALG = ZHIFUBAO_FALG;
        }

        public String getZHIFUBAO() {
            return ZHIFUBAO;
        }

        public void setZHIFUBAO(String ZHIFUBAO) {
            this.ZHIFUBAO = ZHIFUBAO;
        }

        public String getWEIXIN_FALG() {
            return WEIXIN_FALG;
        }

        public void setWEIXIN_FALG(String WEIXIN_FALG) {
            this.WEIXIN_FALG = WEIXIN_FALG;
        }

        public String getQQ_FALG() {
            return QQ_FALG;
        }

        public void setQQ_FALG(String QQ_FALG) {
            this.QQ_FALG = QQ_FALG;
        }

        public String getWEIXIN_H5_FLAG() {
            return WEIXIN_H5_FLAG;
        }

        public void setWEIXIN_H5_FLAG(String WEIXIN_H5_FLAG) {
            this.WEIXIN_H5_FLAG = WEIXIN_H5_FLAG;
        }

        public String getQQ_H5_FLAG() {
            return QQ_H5_FLAG;
        }

        public void setQQ_H5_FLAG(String QQ_H5_FLAG) {
            this.QQ_H5_FLAG = QQ_H5_FLAG;
        }
    }
}
