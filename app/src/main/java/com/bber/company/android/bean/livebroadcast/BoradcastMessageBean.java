package com.bber.company.android.bean.livebroadcast;

import java.io.Serializable;

/**
 * Created by carlo.c on 2018/5/4.
 */

public class BoradcastMessageBean implements Serializable {
    private String iconuri;
    private String message;
    private int background;
    private String beSelf;
    private String time;
    private String userid;


    public void setUserid(String userid) {
        this.userid = userid;
    }

    public BoradcastMessageBean(String iconuri, String message, String beSelf) {
        this.iconuri = iconuri;
        this.message = message;
        this.beSelf = beSelf;
    }

    public BoradcastMessageBean() {

    }

    public String geticonuri() {
        return iconuri;
    }

    public void seticonuri(String iconuri) {
        this.iconuri = iconuri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String isBeSelf() {
        return beSelf;
    }

    public void setBeSelf(String beSelf) {
        this.beSelf = beSelf;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

}
