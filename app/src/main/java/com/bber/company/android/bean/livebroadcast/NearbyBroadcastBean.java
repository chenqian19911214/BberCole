package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlo.c on 2018/4/12.
 */

public class NearbyBroadcastBean implements Serializable {

    /**
     * currentPage : 1
     * dataCollection : [{"age":792259200000,"buyer_id":785516,"city":"citynull","create_time":1518170321000,"describe":"我是美女哦哦哦","headm":"/anchor/portrait/1/1518170321078XiaIJl.jpg","id":1,"image":"/anchor/photo/1/1518589292351OzfAWf.jpg,/anchor/photo/1/151858929235117DI7C.jpg,/anchor/photo/1/1518589292352ZfIlAQ.jpg,","images":["/anchor/photo/1/1518589292351OzfAWf.jpg","/anchor/photo/1/151858929235117DI7C.jpg","/anchor/photo/1/1518589292352ZfIlAQ.jpg"],"level":"高级主播","levelId":3,"money":20,"name":"kkken123","onlineTime":0,"priority":-1,"score":0,"status":3,"time":200,"type":"萌系萝莉","update_time":1521798057000,"video":"/anchor/video/1/1518170509842oQtlUR.mp4","videonumber":"3 5 7 6"},{"age":508694400000,"buyer_id":785518,"city":"马卡蒂","create_time":1518588405000,"describe":"美人","headm":"/anchor/portrait/2/1518588405189D2gqPt.jpg","id":2,"image":"/anchor/photo/2/1518589574403Q40qIF.jpg,/anchor/photo/2/1518589574403CjI9bU.jpg,/anchor/photo/2/15185895744049MwEi4.jpg,","images":["/anchor/photo/2/1518589574403Q40qIF.jpg","/anchor/photo/2/1518589574403CjI9bU.jpg","/anchor/photo/2/15185895744049MwEi4.jpg"],"level":"菜鸟主播","levelId":1,"money":10,"name":"abc9871","onlineTime":15243,"priority":1,"score":0,"status":3,"time":8336,"type":"萌系萝莉 ,长腿美模","update_time":1519302154000,"video":"/anchor/video/2/15185885130376nQvT2.mp4","videonumber":"1 6 1 9"},{"age":null,"buyer_id":785530,"city":"马卡蒂","create_time":1521797417000,"describe":"","headm":"/anchor/portrait/9/1521797416704WwihmU.jpg","id":9,"image":"","images":[],"level":"菜鸟主播","levelId":1,"money":10,"name":"kkken123","onlineTime":2250,"priority":-1,"score":0,"status":3,"time":132,"type":"","update_time":1523437513000,"video":"/anchor/video/9/1521797454346JejNSG.mp4","videonumber":"7 9 8 6"},{"age":446572800000,"buyer_id":785525,"city":"马卡蒂","create_time":1519624538000,"describe":"GG","headm":"/anchor/portrait/4/1519624538481XOuyt6.jpg","id":4,"image":"/anchor/photo/4/1519624922699YVHZg1.jpg,/anchor/photo/4/1519624922699N9O2Di.jpg,/anchor/photo/4/1519624922700llxDfB.jpg,","images":["/anchor/photo/4/1519624922699YVHZg1.jpg","/anchor/photo/4/1519624922699N9O2Di.jpg","/anchor/photo/4/1519624922700llxDfB.jpg"],"level":"菜鸟主播","levelId":1,"money":10,"name":"Try3691","onlineTime":0,"priority":-1,"score":0,"status":1,"time":1,"type":"波霸美胸,混血天使,萌系萝莉 ","update_time":1519624923000,"video":"/anchor/video/4/1519624767603w2lIif.mp4","videonumber":"8 7 7 6"},{"age":null,"buyer_id":785534,"city":"马卡蒂","create_time":1523355796000,"describe":"","headm":"/anchor/portrait/12/1523355796300sPQRLA.jpg","id":12,"image":"","images":[],"level":"菜鸟主播","levelId":1,"money":10,"name":"kkkken123","onlineTime":1,"priority":0,"score":0,"status":1,"time":1,"type":"","update_time":1523359595000,"video":"/anchor/video/12/1523355854947ZBLItf.mp4","videonumber":"1 8 4 9"}]
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

    public static class DataCollectionBean implements Serializable {
        /**
         * age : 792259200000
         * buyer_id : 785516
         * city : citynull
         * create_time : 1518170321000
         * describe : 我是美女哦哦哦
         * headm : /anchor/portrait/1/1518170321078XiaIJl.jpg
         * id : 1
         * image : /anchor/photo/1/1518589292351OzfAWf.jpg,/anchor/photo/1/151858929235117DI7C.jpg,/anchor/photo/1/1518589292352ZfIlAQ.jpg,
         * images : ["/anchor/photo/1/1518589292351OzfAWf.jpg","/anchor/photo/1/151858929235117DI7C.jpg","/anchor/photo/1/1518589292352ZfIlAQ.jpg"]
         * level : 高级主播
         * levelId : 3
         * money : 20
         * name : kkken123
         * onlineTime : 0
         * priority : -1
         * score : 0
         * status : 3
         * time : 200
         * type : 萌系萝莉
         * update_time : 1521798057000
         * video : /anchor/video/1/1518170509842oQtlUR.mp4
         * videonumber : 3 5 7 6
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
