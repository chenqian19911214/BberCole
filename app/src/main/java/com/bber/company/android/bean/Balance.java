package com.bber.company.android.bean;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class Balance {

    private long createTime;
    private double money;
    private int type;//账单类型  1：提成  2: 充值   3：返佣  2;    //充值
//    3;    //开通会员
//    4; //升级会员
//    5; //续费会员
//    6;    //免费赠送会员
//    7;        //实际提现金额
//    8; //实际提现手续费
//    9; //免费赠送游戏金额
//    100; //游戏买入
//    101; //游戏中奖
//    102; //奖池中奖
//    110; //游戏买入，使用赠送金额

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
