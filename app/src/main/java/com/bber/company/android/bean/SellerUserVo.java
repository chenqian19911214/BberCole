package com.bber.company.android.bean;


import java.io.Serializable;

/**
 * Created by Administrator on 2015/11/4 0004.
 */
public class SellerUserVo implements Serializable {
    private Integer uSeller;
    private String usName;
    private Integer usHeight;//身高
    private String usBrassiere;//胸罩杯
    private String usDescribe;//描述
    private String[] images;//相册
    private String usPhone;
    private String usPassword;
    private String usHeadm;
    private String usHeadbig;
    private Integer usSex;//性别
    private Integer ssSex;//服务性别
    private String usImage;
    private String usVideo;
    private String usVideonumber;
    private Integer usChecks;
    private Integer usState;
    private String usKade;
    private Double usMoney;
    private String usKey;
    private String usAge;
    private String usPopularrize;
    private String usPoptype;
    private Integer organiId;
    private Float usGrade;        //评价平均分
    private int level;            //卖家等级
    private Integer isAcceptOrder;//是否上线（是否接单）
    private Integer sellerVipLevel;//模特vip等级,实际上是用户的等级要和模特的设定的等级匹配才行
    private Integer sellerVipId;//模特vip的id
    private double recdmoney;
    private String recdmoneyEndTime;

    private String radius;        //距离
    private String nowTime;        //用户操作的时间
    private Integer isMatchAll;    //是否已经对附近的人员完全匹配了	，当值是1的时候，就表示已经全部匹配了,没有匹配到
    private int isEnoughKeys;        //钥匙数量是否足够，如果其值是1，表示钥匙已经不够了，默认值0就表示钥匙数量足够

    public String getUsAge() {
        return usAge;
    }

    public void setUsAge(String usAge) {
        this.usAge = usAge;
    }

    public double getRecdmoney() {
        return recdmoney;
    }

    public void setRecdmoney(double recdmoney) {
        this.recdmoney = recdmoney;
    }

    public String getRecdmoneyEndTime() {
        return recdmoneyEndTime;
    }

    public void setRecdmoneyEndTime(String recdmoneyEndTime) {
        this.recdmoneyEndTime = recdmoneyEndTime;
    }

    public String getUsDescribe() {
        return usDescribe;
    }

    public void setUsDescribe(String usDescribe) {
        this.usDescribe = usDescribe;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public Integer getIsMatchAll() {
        return isMatchAll;
    }

    public void setIsMatchAll(Integer isMatchAll) {
        this.isMatchAll = isMatchAll;
    }

    public int getIsEnoughKeys() {
        return isEnoughKeys;
    }

    public void setIsEnoughKeys(int isEnoughKeys) {
        this.isEnoughKeys = isEnoughKeys;
    }

    public Integer getuSeller() {
        return uSeller;
    }

    public void setuSeller(Integer uSeller) {
        this.uSeller = uSeller;
    }

    public String getUsName() {
        return usName;
    }

    public void setUsName(String usName) {
        this.usName = usName;
    }

    public Integer getUsHeight() {
        return usHeight;
    }

    public void setUsHeight(Integer usHeight) {
        this.usHeight = usHeight;
    }

    public String getUsBrassiere() {
        return usBrassiere;
    }

    public void setUsBrassiere(String usBrassiere) {
        this.usBrassiere = usBrassiere;
    }

    public String getUsPhone() {
        return usPhone;
    }

    public void setUsPhone(String usPhone) {
        this.usPhone = usPhone;
    }

    public String getUsPassword() {
        return usPassword;
    }

    public void setUsPassword(String usPassword) {
        this.usPassword = usPassword;
    }

    public String getUsHeadm() {
        return usHeadm;
    }

    public void setUsHeadm(String usHeadm) {
        this.usHeadm = usHeadm;
    }

    public String getUsHeadbig() {
        return usHeadbig;
    }

    public void setUsHeadbig(String usHeadbig) {
        this.usHeadbig = usHeadbig;
    }

    public Integer getUsSex() {
        return usSex;
    }

    public void setUsSex(Integer usSex) {
        this.usSex = usSex;
    }

    public Integer getSsSex() {
        return ssSex;
    }

    public void setSsSex(Integer ssSex) {
        this.ssSex = ssSex;
    }

    public String getUsImage() {
        return usImage;
    }

    public void setUsImage(String usImage) {
        this.usImage = usImage;
    }

    public String getUsVideo() {
        return usVideo;
    }

    public void setUsVideo(String usVideo) {
        this.usVideo = usVideo;
    }

    public String getUsVideonumber() {
        return usVideonumber;
    }

    public void setUsVideonumber(String usVideonumber) {
        this.usVideonumber = usVideonumber;
    }

    public Integer getUsChecks() {
        return usChecks;
    }

    public void setUsChecks(Integer usChecks) {
        this.usChecks = usChecks;
    }

    public Integer getUsState() {
        return usState;
    }

    public void setUsState(Integer usState) {
        this.usState = usState;
    }

    public String getUsKade() {
        return usKade;
    }

    public void setUsKade(String usKade) {
        this.usKade = usKade;
    }

    public Double getUsMoney() {
        return usMoney;
    }

    public void setUsMoney(Double usMoney) {
        this.usMoney = usMoney;
    }

    public String getUsKey() {
        return usKey;
    }

    public void setUsKey(String usKey) {
        this.usKey = usKey;
    }


    public String getUsPopularrize() {
        return usPopularrize;
    }

    public void setUsPopularrize(String usPopularrize) {
        this.usPopularrize = usPopularrize;
    }

    public String getUsPoptype() {
        return usPoptype;
    }

    public void setUsPoptype(String usPoptype) {
        this.usPoptype = usPoptype;
    }

    public Integer getOrganiId() {
        return organiId;
    }

    public void setOrganiId(Integer organiId) {
        this.organiId = organiId;
    }

    public Float getUsGrade() {
        return usGrade;
    }

    public void setUsGrade(Float usGrade) {
        this.usGrade = usGrade;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Integer getIsAcceptOrder() {
        return isAcceptOrder;
    }

    public void setIsAcceptOrder(Integer isAcceptOrder) {
        this.isAcceptOrder = isAcceptOrder;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public Integer getSellerVipLevel() {
        return sellerVipLevel;
    }

    public void setSellerVipLevel(Integer sellerVipLevel) {
        this.sellerVipLevel = sellerVipLevel;
    }

    public Integer getSellerVipId() {
        return sellerVipId;
    }

    public void setSellerVipId(Integer sellerVipId) {
        this.sellerVipId = sellerVipId;
    }
}
