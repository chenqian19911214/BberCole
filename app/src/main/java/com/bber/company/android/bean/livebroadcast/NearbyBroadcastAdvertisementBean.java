package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlo.c on 2018/4/13.
 * 附近主播 广告json类
 */

public class NearbyBroadcastAdvertisementBean implements Serializable {


    /**
     * currentPage : 0
     * dataCollection : [{"adCity":"","adFrom":"","adIndex":1,"adPicture":"http://10.60.2.15:1888/staticResource/ads/ANCHOR_NEAR/15172224001981ZP9Vc.jpg","adPlace":"ANCHOR_NEAR","adTarget":"www.baidu.com","adTimeEnd":null,"adTimeStart":null,"adTitle":"test","id":194}]
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
         * adCity :
         * adFrom :
         * adIndex : 1
         * adPicture : http://10.60.2.15:1888/staticResource/ads/ANCHOR_NEAR/15172224001981ZP9Vc.jpg
         * adPlace : ANCHOR_NEAR
         * adTarget : www.baidu.com
         * adTimeEnd : null
         * adTimeStart : null
         * adTitle : test
         * id : 194
         */

        private String adCity;
        private String adFrom;
        private int adIndex;
        private String adPicture;
        private String adPlace;
        private String adTarget;
        private Object adTimeEnd;
        private Object adTimeStart;
        private String adTitle;
        private int id;

        public String getAdCity() {
            return adCity;
        }

        public void setAdCity(String adCity) {
            this.adCity = adCity;
        }

        public String getAdFrom() {
            return adFrom;
        }

        public void setAdFrom(String adFrom) {
            this.adFrom = adFrom;
        }

        public int getAdIndex() {
            return adIndex;
        }

        public void setAdIndex(int adIndex) {
            this.adIndex = adIndex;
        }

        public String getAdPicture() {
            return adPicture;
        }

        public void setAdPicture(String adPicture) {
            this.adPicture = adPicture;
        }

        public String getAdPlace() {
            return adPlace;
        }

        public void setAdPlace(String adPlace) {
            this.adPlace = adPlace;
        }

        public String getAdTarget() {
            return adTarget;
        }

        public void setAdTarget(String adTarget) {
            this.adTarget = adTarget;
        }

        public Object getAdTimeEnd() {
            return adTimeEnd;
        }

        public void setAdTimeEnd(Object adTimeEnd) {
            this.adTimeEnd = adTimeEnd;
        }

        public Object getAdTimeStart() {
            return adTimeStart;
        }

        public void setAdTimeStart(Object adTimeStart) {
            this.adTimeStart = adTimeStart;
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
