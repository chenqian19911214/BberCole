package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.VoucherBean;
import com.bber.company.android.tools.Tools;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> implements View.OnClickListener {

    private Activity mContext;
    private List<VoucherBean> voucherBeanList;
    private String[] status = {"未使用", "已使用", "被冻结", "使用中"};
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public VoucherAdapter(Activity context, List<VoucherBean> infos) {
        this.mContext = context;
        voucherBeanList = infos;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (VoucherBean) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void updateListView(List<VoucherBean> infos) {
        voucherBeanList = infos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        VoucherBean voucherBean = voucherBeanList.get(position);
        holder.tv_vouvherMoney.setText(voucherBean.getCashCardMoney());
        holder.tv_vouvherName.setText(voucherBean.getCashCardName());

        String startTime = voucherBean.getCreateDate();
        String endTime = voucherBean.getEndDate();

        if (!Tools.isEmpty(startTime)) {
            startTime = startTime.replaceAll("-", ".");
        }
        if (!Tools.isEmpty(endTime)) {
            endTime = endTime.replaceAll("-", ".");
        }
        String useDate = "";
        if (startTime != null && startTime.length() > 10 && endTime != null && endTime.length() > 10) {
            useDate = "有效期:" + startTime.substring(0, 10) + " - " +
                    endTime.substring(0, 10);
        }
        holder.tv_vouvherTime.setText(useDate);
        if (Integer.parseInt(voucherBean.getCashCardState()) < status.length) {
            holder.tv_use_status.setText(status[Integer.parseInt(voucherBean.getCashCardState())]);
        } else {
            holder.tv_use_status.setText("");
        }

        holder.itemView.setTag(voucherBean);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return voucherBeanList.size();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, VoucherBean data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_vouvherMoney, tv_vouvherName, tv_vouvherTime, tv_use_status;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_vouvherMoney = itemView.findViewById(R.id.tv_vouvherMoney);
            tv_vouvherName = itemView.findViewById(R.id.tv_vouvherName);
            tv_vouvherTime = itemView.findViewById(R.id.tv_vouvherTime);
            tv_use_status = itemView.findViewById(R.id.tv_use_status);
        }
    }
}
