package com.bber.company.android.bean;

import java.io.Serializable;

public class VipServiceBean implements Serializable {

    public Integer vipDetailId;
    public String vipDetailName;
    public String vipDetailDesc;
    public String vipImagePath;
    public Integer vipManageLevel;

    public Integer getVipDetailId() {
        return vipDetailId;
    }

    public void setVipDetailId(Integer vipDetailId) {
        this.vipDetailId = vipDetailId;
    }

    public String getVipDetailName() {
        return vipDetailName;
    }

    public void setVipDetailName(String vipDetailName) {
        this.vipDetailName = vipDetailName;
    }

    public String getVipDetailDesc() {
        return vipDetailDesc;
    }

    public void setVipDetailDesc(String vipDetailDesc) {
        this.vipDetailDesc = vipDetailDesc;
    }

    public String getVipImagePath() {
        return vipImagePath;
    }

    public void setVipImagePath(String vipImagePath) {
        this.vipImagePath = vipImagePath;
    }

    public Integer getVipManageLevel() {
        return vipManageLevel;
    }

    public void setVipManageLevel(Integer vipManageLevel) {
        this.vipManageLevel = vipManageLevel;
    }
}
