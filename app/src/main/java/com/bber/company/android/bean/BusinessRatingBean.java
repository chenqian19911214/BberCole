package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * Author: Bruce
 * Date: 2016/12/6
 * Version:
 * Describe:
 */
public class BusinessRatingBean implements Serializable {
    private int id;
    private int shopId;
    private String shopSerialNumber;
    private String shopName;
    private String shopPhone;
    private int buyerId;
    private String buyerName;
    private String buyerHeadm;
    private float commentScore1;
    private float commentScore2;
    private float commentScore3;
    private float commentTotalScore;
    private double commentMoney;
    private int commentGood;
    private String commentMessage;
    private String createDate;
    private String shopSortNumber;
    private String commentPhoto;
    private String commentPhoto2;
    private String commentPhoto3;

    public String getCommentPhoto() {
        return commentPhoto;
    }

    public void setCommentPhoto(String commentPhoto) {
        this.commentPhoto = commentPhoto;
    }

    public String getCommentPhoto2() {
        return commentPhoto2;
    }

    public void setCommentPhoto2(String commentPhoto2) {
        this.commentPhoto2 = commentPhoto2;
    }

    public String getCommentPhoto3() {
        return commentPhoto3;
    }

    public void setCommentPhoto3(String commentPhoto3) {
        this.commentPhoto3 = commentPhoto3;
    }

    public String getBuyerHeadm() {
        return buyerHeadm;
    }

    public void setBuyerHeadm(String buyerHeadm) {
        this.buyerHeadm = buyerHeadm;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopSerialNumber() {
        return shopSerialNumber;
    }

    public void setShopSerialNumber(String shopSerialNumber) {
        this.shopSerialNumber = shopSerialNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public float getCommentScore1() {
        return commentScore1;
    }

    public void setCommentScore1(float commentScore1) {
        this.commentScore1 = commentScore1;
    }

    public float getCommentScore2() {
        return commentScore2;
    }

    public void setCommentScore2(float commentScore2) {
        this.commentScore2 = commentScore2;
    }

    public float getCommentScore3() {
        return commentScore3;
    }

    public void setCommentScore3(float commentScore3) {
        this.commentScore3 = commentScore3;
    }

    public float getCommentTotalScore() {
        return commentTotalScore;
    }

    public void setCommentTotalScore(float commentTotalScore) {
        this.commentTotalScore = commentTotalScore;
    }

    public double getCommentMoney() {
        return commentMoney;
    }

    public void setCommentMoney(double commentMoney) {
        this.commentMoney = commentMoney;
    }

    public int getCommentGood() {
        return commentGood;
    }

    public void setCommentGood(int commentGood) {
        this.commentGood = commentGood;
    }

    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getShopSortNumber() {
        return shopSortNumber;
    }

    public void setShopSortNumber(String shopSortNumber) {
        this.shopSortNumber = shopSortNumber;
    }
}
