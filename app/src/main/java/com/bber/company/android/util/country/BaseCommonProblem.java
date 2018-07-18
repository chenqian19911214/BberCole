package com.bber.company.android.util.country;

import java.util.List;

/**
 * Created by carlo.c on 2018/3/26.
 */

public class BaseCommonProblem {


    /**
     * currentPage : 0
     * dataCollection : [{"answer":"345654434444463422222222222","createTime":"2018-03-23 14:00:12","id":5,"question":"65yu7454354565432565432546433","updateTime":"2018-03-23 14:00:16"},{"answer":"而儿童润体乳吞云吐雾二个人太阳热武器二胎","createTime":"2018-03-23 13:59:54","id":4,"question":"yhuy6wte4ryhger沃尔防守打法","updateTime":"2018-03-23 13:59:58"},{"answer":"水电费萨达刚问过的二个如果热锅","createTime":"2018-03-23 13:59:38","id":3,"question":"的大是大非胜多负少当时发生发送","updateTime":"2018-03-23 13:59:41"},{"answer":"登登录系统打开直播登录系统打开直播登录系统打开直播录系统打开直播","createTime":"2018-03-23 13:58:46","id":2,"question":"要要怎么开直播要怎么开直播怎么开直播","updateTime":"2018-03-23 13:58:49"},{"answer":"点击充值","createTime":"2018-03-22 14:37:03","id":1,"question":"如何充值","updateTime":"2018-03-22 14:37:06"}]
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
         * answer : 345654434444463422222222222
         * createTime : 2018-03-23 14:00:12
         * id : 5
         * question : 65yu7454354565432565432546433
         * updateTime : 2018-03-23 14:00:16
         */

        private String answer;
        private String createTime;
        private int id;
        private String question;
        private String updateTime;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

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

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
