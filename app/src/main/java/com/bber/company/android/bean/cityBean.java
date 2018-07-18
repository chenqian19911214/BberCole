package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * Author: Bruce
 * Date: 2016/12/6
 * Version:
 * Describe:
 */
public class cityBean implements Serializable {
    private int id;
    private int adcode;
    private String name;
    private double lat;
    private double lng;
    private int parentAdcode;
    private String citycode;
    private String pinyin;

    public cityBean() {
    }

    public cityBean(int id, int adcode, String name,
                    double lat, double lng, int parentAdcode,
                    String citycode, String pinyin) {
        this.id = id;
        this.adcode = adcode;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.parentAdcode = parentAdcode;
        this.citycode = citycode;
        this.pinyin = pinyin;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdcode() {
        return adcode;
    }

    public void setAdcode(int adcode) {
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getParentAdcode() {
        return parentAdcode;
    }

    public void setParentAdcode(int parentAdcode) {
        this.parentAdcode = parentAdcode;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
