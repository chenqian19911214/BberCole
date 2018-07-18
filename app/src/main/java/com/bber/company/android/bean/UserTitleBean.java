package com.bber.company.android.bean;

import com.bber.company.android.R;

import java.util.List;

/**
 * 银行卡号码
 * Created by Vencent on 2016/02/22 0024.
 * 侧拉的的头部集合
 */
public class UserTitleBean {
    private List<Integer> imageList; // 图片集合
    private List<String> titleList; //头像集合
    private String currentMoney;//当前的金钱

    public String getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(String currentMoney) {
        this.currentMoney = currentMoney;
    }

    public List<String> getTitleList() {
        titleList.add("我的钱包");
        titleList.add("现金券");
        titleList.add("商户");
        titleList.add("我的收藏");
        titleList.add("预约订单");
        titleList.add("分享得现金");
        titleList.add("设置");
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public List<Integer> getImageList() {
        imageList.add(R.mipmap.icon_wallect);
        imageList.add(R.mipmap.icon_cash);
        imageList.add(R.mipmap.icon_shop);
        imageList.add(R.mipmap.icon_love);
        imageList.add(R.mipmap.icon_appoint);
        imageList.add(R.mipmap.icon_share);
        imageList.add(R.mipmap.icon_setting);
        return imageList;
    }

    public void setImageList(List<Integer> imageList) {
        this.imageList = imageList;
    }
}
