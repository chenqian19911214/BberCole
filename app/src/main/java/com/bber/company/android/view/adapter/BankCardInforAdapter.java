package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.BindBankBean;
import com.bber.company.android.entity.EnumBankIconType;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.RectangleView;
import com.bber.customview.utils.LogUtils;

import java.util.List;

public class BankCardInforAdapter extends RecyclerView.Adapter<BankCardInforAdapter.ViewHolder> implements View.OnClickListener,
        View.OnLongClickListener {

    private Activity mContext;
    private LayoutInflater mInflater;
    private List<BindBankBean> bankCardInfors;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    //银行卡的ID
    private int code;

    public BankCardInforAdapter(Activity context, List<BindBankBean> infos, int code) {
        this.mContext = context;
        this.code = code;
        bankCardInfors = infos;
        LogUtils.e(infos.size() + "22222222");
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateListView(List<BindBankBean> infos) {
        LogUtils.e(infos.size());
        bankCardInfors = infos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = mInflater.inflate(R.layout.item_bank_card, parent, false);
//        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BindBankBean bankCardInfor = bankCardInfors.get(position);
        if (!Tools.isEmpty(bankCardInfor.getBankCard())) {

            String banksname = mContext.getResources().getStringArray(R.array.card)[code];
            holder.bank_name.setText(banksname);
            holder.card_name.setText(bankCardInfor.getCardName());
            holder.icon_bank.setImageDrawable(EnumBankIconType.getbankType(code));
            String str = null;
            str = bankCardInfor.getBankCard().substring(bankCardInfor.getBankCard().length() - 4, bankCardInfor.getBankCard().length());
            holder.card_num.setText("**** **** **** " + str);
        }
        holder.itemView.setTag(bankCardInfor);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return bankCardInfors.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (BindBankBean) view.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(view, (BindBankBean) view.getTag());
        }
        return false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, BindBankBean data);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, BindBankBean data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RectangleView card_info;
        private TextView bank_name, card_num, card_name;
        private ImageView icon_bank;

        public ViewHolder(View itemView) {
            super(itemView);
            bank_name = itemView.findViewById(R.id.bank_name);
            card_num = itemView.findViewById(R.id.card_num);
            card_name = itemView.findViewById(R.id.card_name);
            icon_bank = itemView.findViewById(R.id.icon_bank);
        }
    }

}
