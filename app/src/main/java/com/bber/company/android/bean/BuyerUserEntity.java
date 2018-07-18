package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/14 0014.
 */
public class BuyerUserEntity implements Serializable {


    private Integer anchorStatus;
    private Integer ubuyer;

    private Integer ubPoptype;

    private String ubPopularrize;


    private String ubName;

    private String ubPhone;

    private String ubEmail;

    private String ubPassword;

    private String ubHeadm;

    private String ubHeadbig;

    private Integer ubSex;
    private Integer fbSex;

    private Double ubMoney;

    private Integer ubState;

    private Boolean ubKade;

    private Integer ubKey;

    private String inviteCode;

    private String vipEndTime;
    private String vipStartTime;
    private String vipName;
    private String uBirthday;
    private Integer vipId;
    private Integer vipLevel;
    private Double gameFreeMoney;
    private Integer withdrawStatus;

    private String iSVerify;

    public Integer getAnchorStatus() {
        return anchorStatus;
    }

    public void setAnchorStatus(Integer anchorStatus) {
        this.anchorStatus = anchorStatus;
    }

    public String getiSVerify() {
        return iSVerify;
    }

    public void setiSVerify(String iSVerify) {
        this.iSVerify = iSVerify;
    }

    public Integer getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(Integer withdrawStatus) {

        this.withdrawStatus = withdrawStatus;
    }

    public Double getGameFreeMoney() {

        return gameFreeMoney;
    }

    public void setGameFreeMoney(Double gameFreeMoney) {
        this.gameFreeMoney = gameFreeMoney;
    }

    public String getuBirthday() {
        return uBirthday;
    }

    public void setuBirthday(String uBirthday) {
        this.uBirthday = uBirthday;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Integer getUbuyer() {
        return ubuyer;
    }

    public void setUbuyer(Integer ubuyer) {
        this.ubuyer = ubuyer;
    }

    public Integer getUbPoptype() {
        return ubPoptype;
    }

    public void setUbPoptype(Integer ubPoptype) {
        this.ubPoptype = ubPoptype;
    }

    public String getUbPopularrize() {
        return ubPopularrize;
    }

    public void setUbPopularrize(String ubPopularrize) {
        this.ubPopularrize = ubPopularrize;
    }

    public String getUbName() {
        return ubName;
    }

    public void setUbName(String ubName) {
        this.ubName = ubName;
    }

    public String getUbPhone() {
        return ubPhone;
    }

    public void setUbPhone(String ubPhone) {
        this.ubPhone = ubPhone;
    }

    public String getUbEmail() {
        return ubEmail;
    }

    public void setUbEmail(String ubEmail) {
        this.ubEmail = ubEmail;
    }

    public String getUbPassword() {
        return ubPassword;
    }

    public void setUbPassword(String ubPassword) {
        this.ubPassword = ubPassword;
    }

    public String getUbHeadm() {
        return ubHeadm;
    }

    public void setUbHeadm(String ubHeadm) {
        this.ubHeadm = ubHeadm;
    }

    public String getUbHeadbig() {
        return ubHeadbig;
    }

    public void setUbHeadbig(String ubHeadbig) {
        this.ubHeadbig = ubHeadbig;
    }

    public Integer getUbSex() {
        return ubSex;
    }

    public void setUbSex(Integer ubSex) {
        this.ubSex = ubSex;
    }

    public Integer getFbSex() {
        return fbSex;
    }

    public void setFbSex(Integer fbSex) {
        this.fbSex = fbSex;
    }

    public Double getUbMoney() {
        return ubMoney;
    }

    public void setUbMoney(Double ubMoney) {
        this.ubMoney = ubMoney;
    }

    public Integer getUbState() {
        return ubState;
    }

    public void setUbState(Integer ubState) {
        this.ubState = ubState;
    }

    public Boolean getUbKade() {
        return ubKade;
    }

    public void setUbKade(Boolean ubKade) {
        this.ubKade = ubKade;
    }

    public Integer getUbKey() {
        return ubKey;
    }

    public void setUbKey(Integer ubKey) {
        this.ubKey = ubKey;
    }

    public String getVipEndTime() {
        return vipEndTime;
    }

    public void setVipEndTime(String vipEndTime) {
        this.vipEndTime = vipEndTime;
    }

    public String getVipStartTime() {
        return vipStartTime;
    }

    public void setVipStartTime(String vipStartTime) {
        this.vipStartTime = vipStartTime;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public Integer getVipId() {
        return vipId;
    }

    public void setVipId(Integer vipId) {
        this.vipId = vipId;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }
}
