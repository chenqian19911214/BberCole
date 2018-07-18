package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.VipInforBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-05-25.
 */
public class VipItemListAdapter extends RecyclerView.Adapter<VipItemListAdapter.ViewHolder> implements View.OnClickListener {
    private List<VipInforBean> viplist;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private Context mContext;
    private List<ViewHolder> viewHolderList = new ArrayList<>();

    public VipItemListAdapter(Context context, List<VipInforBean> viplist) {
        mContext = context;
        this.viplist = viplist;
    }

    public void updataList(List<VipInforBean> viplist) {
        this.viplist = viplist;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_vip, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        VipInforBean vipInforBean = viplist.get(position);

        viewHolder.im_vip_icon.setImageResource(vipInforBean.getVipResId());
        viewHolder.tv_vip_name.setText(vipInforBean.getVipName());
//        viewHolder.tv_vip_money.setText("¥" + vipInforBean.getVipMoney());

//        if (vipInforBean.getVipDisMoney() < vipInforBean.getVipMoney()) {
//            viewHolder.tv_vip_money.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
//        }
        viewHolder.position = position;

        viewHolder.itemView.setTag(viewHolder);
        viewHolderList.add(viewHolder);
    }

    @Override
    public int getItemCount() {
        return viplist.size();
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, viewHolder.position);
        }
        setView(viewHolder.position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setView(int position) {
        for (int i = 0; i < viewHolderList.size(); i++) {
            if (viewHolderList.get(i).position == position) {
//                viewHolderList.get(i).tv_vip_money.setSelected(true);
                viewHolderList.get(i).ll_item.setSelected(true);
            } else {
//                viewHolderList.get(i).tv_vip_money.setSelected(false);
                viewHolderList.get(i).ll_item.setSelected(false);
            }
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public int position;
        private ImageView im_vip_icon;
        private TextView tv_vip_name;
        //        private TextView tv_vip_money;
        private LinearLayout ll_item;

        public ViewHolder(View itemView) {
            super(itemView);
            im_vip_icon = itemView.findViewById(R.id.im_vip_icon);
            ll_item = itemView.findViewById(R.id.ll_item);
            tv_vip_name = itemView.findViewById(R.id.tv_vip_name);
//            tv_vip_money = (TextView) itemView.findViewById(R.id.tv_vip_money);
        }
    }

}
