package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlo.c on 2018/4/16.
 */

public class BroadcaseSearchSingleBean implements Serializable {

    /**
     * currentPage : 1
     * dataCollection : [{"age":797875200000,"buyer_id":779459,"city":"Manila","create_time":1523680435000,"describe":"紧急集合","headm":"/anchor/portrait/30/1523680448916pH54cp.jpg","id":30,"image":"/anchor/photo/30/1523774853341d0MlXy.jpg,/anchor/photo/30/152377485347113QPGA.jpg,/anchor/photo/30/1523774853525YuUdgP.jpg,/anchor/photo/30/1523774853571pPEiT7.jpg,","images":["/anchor/photo/30/1523774853341d0MlXy.jpg","/anchor/photo/30/152377485347113QPGA.jpg","/anchor/photo/30/1523774853525YuUdgP.jpg","/anchor/photo/30/1523774853571pPEiT7.jpg"],"level":"初级主播","levelId":1,"money":10,"name":"Qianapii","onlineTime":585,"priority":-1,"score":0,"status":1,"time":1,"type":"波霸美胸","update_time":1523864438000,"video":"/anchor/video/30/1523680434592YWAMUN.mp4","videonumber":"2680"}]
     * resultCode : 1
     * resultMessage :
     * totalPage : 1
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

    public static class DataCollectionBean implements Serializable{
        /**
         * age : 797875200000
         * buyer_id : 779459
         * city : Manila
         * create_time : 1523680435000
         * describe : 紧急集合
         * headm : /anchor/portrait/30/1523680448916pH54cp.jpg
         * id : 30
         * image : /anchor/photo/30/1523774853341d0MlXy.jpg,/anchor/photo/30/152377485347113QPGA.jpg,/anchor/photo/30/1523774853525YuUdgP.jpg,/anchor/photo/30/1523774853571pPEiT7.jpg,
         * images : ["/anchor/photo/30/1523774853341d0MlXy.jpg","/anchor/photo/30/152377485347113QPGA.jpg","/anchor/photo/30/1523774853525YuUdgP.jpg","/anchor/photo/30/1523774853571pPEiT7.jpg"]
         * level : 初级主播
         * levelId : 1
         * money : 10
         * name : Qianapii
         * onlineTime : 585
         * priority : -1
         * score : 0
         * status : 1
         * time : 1
         * type : 波霸美胸
         * update_time : 1523864438000
         * video : /anchor/video/30/1523680434592YWAMUN.mp4
         * videonumber : 2680
         */

        private long age;
        private int buyer_id;
        private String city;
        private long create_time;
        private String describe;
        private String headm;
        private int id;
        private String image;
        private String level;
        private int levelId;
        private int money;
        private String name;
        private int onlineTime;
        private int priority;
        private int score;
        private int status;
        private int time;
        private String type;
        private long update_time;
        private String video;
        private String videonumber;
        private List<String> images;

        public long getAge() {
            return age;
        }

        public void setAge(long age) {
            this.age = age;
        }

        public int getBuyer_id() {
            return buyer_id;
        }

        public void setBuyer_id(int buyer_id) {
            this.buyer_id = buyer_id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public long getCreate_time() {
            return create_time;
        }

        public void setCreate_time(long create_time) {
            this.create_time = create_time;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getHeadm() {
            return headm;
        }

        public void setHeadm(String headm) {
            this.headm = headm;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public int getLevelId() {
            return levelId;
        }

        public void setLevelId(int levelId) {
            this.levelId = levelId;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOnlineTime() {
            return onlineTime;
        }

        public void setOnlineTime(int onlineTime) {
            this.onlineTime = onlineTime;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(long update_time) {
            this.update_time = update_time;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getVideonumber() {
            return videonumber;
        }

        public void setVideonumber(String videonumber) {
            this.videonumber = videonumber;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }
}
