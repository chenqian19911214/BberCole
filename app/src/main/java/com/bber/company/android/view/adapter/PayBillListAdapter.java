package com.bber.company.android.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.DpPayBean;
import com.bber.company.android.tools.Tools;

import java.util.List;

public class PayBillListAdapter extends RecyclerView.Adapter<PayBillListAdapter.ViewHolder> implements View.OnClickListener {
    private List<DpPayBean> dpPayBeanList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public PayBillListAdapter(List<DpPayBean> dpPayBeanList) {
        this.dpPayBeanList = dpPayBeanList;
    }

    public void updateListView(List<DpPayBean> dpPayBeanList) {
        this.dpPayBeanList = dpPayBeanList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pay_bill, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        DpPayBean dpPayBean = dpPayBeanList.get(position);

        viewHolder.pay_money.setText("¥" + dpPayBean.getMoney());
        viewHolder.pay_status.setText(dpPayBean.getStatus() + "");

        int type = dpPayBean.getType();
        String typeName = "充值";
        if (type == 16) {
            typeName = "提现";
        }
        if (dpPayBean.getDpStatus() == 1) {
            if (dpPayBean.getStatus() == 1) {
                viewHolder.pay_status.setText(typeName + "成功");
            } else if (dpPayBean.getStatus() == 2) {
                viewHolder.pay_status.setText(typeName + "失败");
            } else if (dpPayBean.getStatus() == 3) {
                viewHolder.pay_status.setText(typeName + "异常");
            } else {
                viewHolder.pay_status.setText(typeName + "中");
            }
        } else {
            viewHolder.pay_status.setText(typeName + "失败");
        }
        String dateString = Tools.timeStamp2Date(dpPayBean.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
        viewHolder.dateTime.setText(dateString);

        viewHolder.username.setText(typeName);
        viewHolder.itemView.setTag(dpPayBean);
    }

    @Override
    public int getItemCount() {
        return dpPayBeanList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (DpPayBean) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, DpPayBean data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, dateTime, pay_status, pay_money;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            dateTime = itemView.findViewById(R.id.dateTime);
            pay_status = itemView.findViewById(R.id.pay_status);
            pay_money = itemView.findViewById(R.id.pay_money);
        }
    }
}
