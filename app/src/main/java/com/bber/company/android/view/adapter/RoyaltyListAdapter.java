package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Balance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2015-05-22.
 */
public class RoyaltyListAdapter extends RecyclerView.Adapter<RoyaltyListAdapter.ViewHolder> {
    private static Activity activity;
    private List<Balance> balances;

    public RoyaltyListAdapter(Activity activity, List<Balance> infos) {
        RoyaltyListAdapter.activity = activity;
        balances = infos;
    }

    public void update(List<Balance> infos) {
        balances = infos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.royalty_item, viewGroup, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        //将创建的View注册点击事件
//        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Balance balance = balances.get(position);
        // 绑定数据到ViewHolder上

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(balance.getCreateTime()));
        sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date(balance.getCreateTime()));

        viewHolder.date.setText(date);
        viewHolder.time.setText(time);

        viewHolder.type.setText(getStatusString(balance.getType()));
        if (balance.getType() == 1) {//提成
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 2) {//充值
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 3) {//开通会员
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 4) {//升级会员
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 5) {//续费会员
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 6) {//免费赠送会员
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 7) {//实际提现金额
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 8) {//提现手续费
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 9) {//免费赠送游戏金额
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 10) {// 消费美模
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 11) {//推荐人赠送现金
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 12) {//受邀人赠送点数
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 100) {//购买游戏号码
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else if (balance.getType() == 101) {// 游戏中奖金额
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 102) {// 游戏中奖池金额
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.text_yellow));
            viewHolder.money.setText("+ ￥" + balance.getMoney());
        } else if (balance.getType() == 110) {//使用免费赠送游戏金额购买游戏号码
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        } else {//返佣
            viewHolder.money.setTextColor(activity.getResources().getColor(R.color.btn_unable));
            viewHolder.money.setText("- ￥" + balance.getMoney());
        }

        //将数据保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(balance);
    }

    /**
     * 获取支付状态
     */
    private String getStatusString(int status) {
        String returnValue = "其他";
        switch (status) {
            case 2:
                returnValue = "充值";
                break;
            case 4:
                returnValue = "升级vip";
                break;
            case 5:
                returnValue = "续费vip";
                break;
            case 6:
                returnValue = "免费vip";
                break;
            case 3:
                returnValue = "开通vip";
                break;
            case 7:
                returnValue = "实际提现金额";
                break;
            case 8:
                returnValue = "实际提现手续费";
                break;
            case 9:
                returnValue = "免费赠送游戏金额";
                break;
            case 11:
                returnValue = "推荐人赠送现金";
                break;
            case 12:
                returnValue = "受邀人赠送点数";
                break;
            case 100:
                returnValue = "游戏买入";
                break;
            case 101:
                returnValue = "游戏中奖";
                break;
            case 102:
                returnValue = "奖池中奖";
                break;
            case 110:
                returnValue = "游戏买入，使用赠送金额";
                break;
        }
        return returnValue;
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView date, time, money, type;

        public ViewHolder(final View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            money = itemView.findViewById(R.id.money);
            type = itemView.findViewById(R.id.type);
        }
    }

}
