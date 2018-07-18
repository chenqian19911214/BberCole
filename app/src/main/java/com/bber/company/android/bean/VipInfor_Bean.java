package com.bber.company.android.bean;


import java.io.Serializable;

public class VipInfor_Bean implements Serializable {

    public String vipDetailStr;
    private Integer vipId;
    private Integer inviteeReward;
    private Integer referralsReward;
    private String vipName;
    private String vipValidity;
    private Double vipMoney;
    private Integer vipLevel;
    private Double vipDisMoney;
    private String vipDisStartTime;
    private String vipDisEndTime;
    private int vipResId;

    public Integer getInviteeReward() {
        return inviteeReward;
    }

    public void setInviteeReward(Integer inviteeReward) {
        this.inviteeReward = inviteeReward;
    }

    public Integer getReferralsReward() {
        return referralsReward;
    }

    public void setReferralsReward(Integer referralsReward) {
        this.referralsReward = referralsReward;
    }

    public Integer getVipId() {
        return vipId;
    }

    public void setVipId(Integer vipId) {
        this.vipId = vipId;
    }

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }

    public String getVipValidity() {
        return vipValidity;
    }

    public void setVipValidity(String vipValidity) {
        this.vipValidity = vipValidity;
    }

    public Double getVipMoney() {
        return vipMoney;
    }

    public void setVipMoney(Double vipMoney) {
        this.vipMoney = vipMoney;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Double getVipDisMoney() {
        return vipDisMoney;
    }

    public void setVipDisMoney(Double vipDisMoney) {
        this.vipDisMoney = vipDisMoney;
    }

    public String getVipDisStartTime() {
        return vipDisStartTime;
    }

    public void setVipDisStartTime(String vipDisStartTime) {
        this.vipDisStartTime = vipDisStartTime;
    }

    public String getVipDisEndTime() {
        return vipDisEndTime;
    }

    public void setVipDisEndTime(String vipDisEndTime) {
        this.vipDisEndTime = vipDisEndTime;
    }

    public int getVipResId() {
        return vipResId;
    }

    public void setVipResId(int vipResId) {
        this.vipResId = vipResId;
    }
}
