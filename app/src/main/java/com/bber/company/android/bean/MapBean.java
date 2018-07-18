package com.bber.company.android.bean;


import java.io.Serializable;

/**
 * Author: Bruce
 * Date: 2016/5/20
 * Version:
 * Describe:
 */
public class MapBean implements Serializable {

    private double longitude;      //经度
    private double latitude;       //纬度
    private String mTitle;         //返回POI的名称。如：天安门
    private String mSnippet;       //返回POI的地址。如：某某路100号；
    private String cityName;       //返回POI的城市名称。如：北京市
    private String provinceName;   //返回POI的省/自治区/直辖市/特别行政区名称 。如北京市/广东省
    private String adName;         //返回POI的行政区划名称。如：白云区
    private String businessArea;   //返回POI的所在商圈。 如：北京路步行街

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getadName() {
        return adName;
    }

    public void setadName(String aName) {
        this.adName = aName;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmSnippet() {
        return mSnippet;
    }

    public void setmSnippet(String mSnippet) {
        this.mSnippet = mSnippet;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLonPoint() {
        return longitude;
    }

    public void setLonPoint(double longitude) {
        this.longitude = longitude;
    }

}
