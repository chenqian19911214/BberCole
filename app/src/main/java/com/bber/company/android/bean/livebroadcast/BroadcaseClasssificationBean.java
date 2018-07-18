package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlo.c on 2018/4/17.
 */

public class BroadcaseClasssificationBean implements Serializable {


    /**
     * currentPage : 1
     * dataCollection : [{"age":955814400000,"buyer_id":779463,"city":"Manila","create_time":1523847388000,"describe":"外婆和有个约会","headm":"/anchor/portrait/33/1523847414261KuKpFS.jpg","id":33,"image":"/anchor/photo/33/1523868101294umHMhl.jpg,/anchor/photo/33/1523868101343JR5tik.jpg,/anchor/photo/33/1523868101359OIquCD.jpg,/anchor/photo/33/1523868101375n1sCps.jpg,/anchor/photo/33/1523868101389Djwupf.jpg,/anchor/photo/33/1523868101402jHsepw.jpg,/anchor/photo/33/1523868101455uJzW4j.jpg,","images":["/anchor/photo/33/1523868101294umHMhl.jpg","/anchor/photo/33/1523868101343JR5tik.jpg","/anchor/photo/33/1523868101359OIquCD.jpg","/anchor/photo/33/1523868101375n1sCps.jpg","/anchor/photo/33/1523868101389Djwupf.jpg","/anchor/photo/33/1523868101402jHsepw.jpg","/anchor/photo/33/1523868101455uJzW4j.jpg"],"level":"初级主播","levelId":1,"money":10,"name":"王丽丽","onlineTime":0,"priority":-1,"score":0,"status":3,"time":1,"type":"长腿美模,波霸美胸,丝袜美足","update_time":1523868102000,"video":"/anchor/video/33/1523847388292XHeCeC.mp4","videonumber":"2139"},{"age":999273600000,"buyer_id":17299,"city":"马卡蒂","create_time":1516698004000,"describe":"fwage","headm":"/anchor/portrait/15/1516698066606rN9T3r.jpg","id":15,"image":"/anchor/photo/15/1518508989805Wmp8QD.jpg,/anchor/photo/15/1518508989929qYrptJ.jpg,/anchor/photo/15/15185089899797fueyL.jpg,/anchor/photo/15/1518508990028kUuIds.jpg,","images":["/anchor/photo/15/1518508989805Wmp8QD.jpg","/anchor/photo/15/1518508989929qYrptJ.jpg","/anchor/photo/15/15185089899797fueyL.jpg","/anchor/photo/15/1518508990028kUuIds.jpg"],"level":"初级主播","levelId":1,"money":10,"name":"abcde","onlineTime":31,"priority":1,"score":0,"status":1,"time":1,"type":"丝袜美足","update_time":1523868726000,"video":"/anchor/video/15/1516698025354WkEi8f.mp4","videonumber":"4 3 0 5"}]
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
         * age : 955814400000
         * buyer_id : 779463
         * city : Manila
         * create_time : 1523847388000
         * describe : 外婆和有个约会
         * headm : /anchor/portrait/33/1523847414261KuKpFS.jpg
         * id : 33
         * image : /anchor/photo/33/1523868101294umHMhl.jpg,/anchor/photo/33/1523868101343JR5tik.jpg,/anchor/photo/33/1523868101359OIquCD.jpg,/anchor/photo/33/1523868101375n1sCps.jpg,/anchor/photo/33/1523868101389Djwupf.jpg,/anchor/photo/33/1523868101402jHsepw.jpg,/anchor/photo/33/1523868101455uJzW4j.jpg,
         * images : ["/anchor/photo/33/1523868101294umHMhl.jpg","/anchor/photo/33/1523868101343JR5tik.jpg","/anchor/photo/33/1523868101359OIquCD.jpg","/anchor/photo/33/1523868101375n1sCps.jpg","/anchor/photo/33/1523868101389Djwupf.jpg","/anchor/photo/33/1523868101402jHsepw.jpg","/anchor/photo/33/1523868101455uJzW4j.jpg"]
         * level : 初级主播
         * levelId : 1
         * money : 10
         * name : 王丽丽
         * onlineTime : 0
         * priority : -1
         * score : 0
         * status : 3
         * time : 1
         * type : 长腿美模,波霸美胸,丝袜美足
         * update_time : 1523868102000
         * video : /anchor/video/33/1523847388292XHeCeC.mp4
         * videonumber : 2139
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
