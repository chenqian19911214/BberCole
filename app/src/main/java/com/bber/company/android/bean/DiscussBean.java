package com.bber.company.android.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.TimeUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class DiscussBean extends BaseObservable implements Serializable {
    private int id;
    private int mucFlag;
    private String coverPicture;
    private String detail;
    private String endTime;
    private String readTimes;
    private String startTime;
    private String mucId;
    private String title;
    private String url;
    private String type;
    private String fontColor;
    private String[] keywords;
    private List<DiscussDetailBean> img;


    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public int getMucFlag() {
        return mucFlag;
    }

    public void setMucFlag(int mucFlag) {
        this.mucFlag = mucFlag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMucId() {
        return mucId;
    }

    public void setMucId(String mucId) {
        this.mucId = mucId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Bindable
    public String getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(String readTimes) {
        this.readTimes = readTimes;
        notifyPropertyChanged(BR.readTimes);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDataStr() {
        int betweendays = 0;
        String str = "";
        TimeUtil timeUtil = new TimeUtil();
        try {
            betweendays = timeUtil.daysBetween(new Date(Long.valueOf(startTime)), new Date(System.currentTimeMillis()));
            if (Math.abs(betweendays) < 1) {
                str = "今天" + Tools.timeStamp2Date(startTime, " HH:mm");
                return str;
            } else if (Math.abs(betweendays) < 2) {
                str = "昨天" + Tools.timeStamp2Date(startTime, " HH:mm");
                return str;
            } else if (Math.abs(betweendays) <= 7) {
                str = Math.abs(betweendays) + "天前" + Tools.timeStamp2Date(startTime, " HH:mm");
                return str;
            } else {
                return Tools.timeStamp2Date(startTime, "MM/dd HH:mm");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DiscussDetailBean> getImg() {
        return img;
    }

    public void setImg(List<DiscussDetailBean> img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "DiscussBean{" +
                "id=" + id +
                ", coverPicture='" + coverPicture + '\'' +
                ", detail='" + detail + '\'' +
                ", endTime='" + endTime + '\'' +
                ", readTimes='" + readTimes + '\'' +
                ", startTime='" + startTime + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", img=" + img +
                '}';
    }
}



