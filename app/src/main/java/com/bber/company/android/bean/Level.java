package com.bber.company.android.bean;

/**
 * Created by Administrator on 2015/11/13 0013.
 */
public class Level {

    private int id;
    private int maxMoney;//最高价
    private int minMoney;//最低价
    private int level;//设置成4的level
    private int expendKey;//消耗的钥匙数量 4
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(int maxMoney) {
        this.maxMoney = maxMoney;
    }

    public int getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(int minMoney) {
        this.minMoney = minMoney;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExpendKey() {
        return expendKey;
    }

    public void setExpendKey(int expendKey) {
        this.expendKey = expendKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
