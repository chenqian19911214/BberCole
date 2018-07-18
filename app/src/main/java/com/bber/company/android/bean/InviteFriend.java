package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * 邀请朋友
 * Created by Administrator on 2015/11/24 0024.
 */
public class InviteFriend implements Serializable {
    private String ubuyer;
    private String ubName;
    private String ubHeadm;
    private String ubCtime;
    private String iSVerify;

    public String getUbuyer() {
        return ubuyer;
    }

    public void setUbuyer(String ubuyer) {
        this.ubuyer = ubuyer;
    }

    public String getUbName() {
        return ubName;
    }

    public void setUbName(String ubName) {
        this.ubName = ubName;
    }

    public String getUbHeadm() {
        return ubHeadm;
    }

    public void setUbHeadm(String ubHeadm) {
        this.ubHeadm = ubHeadm;
    }

    public String getUbCtime() {
        return ubCtime;
    }

    public void setUbCtime(String ubCtime) {
        this.ubCtime = ubCtime;
    }

    public String getiSVerify() {
        return iSVerify;
    }

    public void setiSVerify(String iSVerify) {
        this.iSVerify = iSVerify;
    }
}
