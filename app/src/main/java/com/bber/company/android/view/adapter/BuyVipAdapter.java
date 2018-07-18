package com.bber.company.android.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.VipServiceBean;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class BuyVipAdapter extends RecyclerView.Adapter<BuyVipAdapter.ViewHolder> implements View.OnClickListener {
    private List<VipServiceBean> vipServices;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private List<ViewHolder> viewHolderList = new ArrayList<>();

    public BuyVipAdapter(List<VipServiceBean> vipServices) {
        this.vipServices = vipServices;
    }

    public void updateListView(List<VipServiceBean> vipServices) {
        this.vipServices = vipServices;
        resetAllView();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_buyvip, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        VipServiceBean VipService = vipServices.get(position);

        String URL = VipService.getVipImagePath();
        viewHolder.tv_item_icon.setImageResource(R.mipmap.user_icon);
        if (!Tools.isEmpty(URL)) {
            viewHolder.tv_item_icon.setTag(URL);
            if (viewHolder.tv_item_icon.getTag() != null
                    && viewHolder.tv_item_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(URL);
                viewHolder.tv_item_icon.setImageURI(uri);
            }
        }
        viewHolder.tv_item_name.setText(VipService.getVipDetailName());
        viewHolder.position = position;
        viewHolder.item_service_dec.setText(VipService.getVipDetailDesc());
        viewHolder.itemView.setTag(viewHolder);

        viewHolderList.add(viewHolder);
    }

    @Override
    public int getItemCount() {
        return vipServices.size();
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

    /**
     * 设置展开
     *
     * @param position
     */

    private void setView(int position) {

        for (int i = 0; i < viewHolderList.size(); i++) {
            if (viewHolderList.get(i).position == position) {
                viewHolderList.get(i).item_service_dec.setVisibility(View.VISIBLE);
                viewHolderList.get(i).item_service_arrow.setVisibility(View.VISIBLE);
            } else {
                viewHolderList.get(i).item_service_dec.setVisibility(View.GONE);
                viewHolderList.get(i).item_service_arrow.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 清除所有展开
     */
    private void resetAllView() {
        for (int i = 0; i < viewHolderList.size(); i++) {
            viewHolderList.get(i).item_service_dec.setVisibility(View.GONE);
            viewHolderList.get(i).item_service_arrow.setVisibility(View.GONE);
        }
    }


    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int postion);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public int position;
        public SimpleDraweeView tv_item_icon;
        public TextView tv_item_name, item_service_dec;
        public ImageView item_service_arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_icon = itemView.findViewById(R.id.tv_item_icon);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            item_service_dec = itemView.findViewById(R.id.item_service_dec);
            item_service_arrow = itemView.findViewById(R.id.item_service_arrow);

            item_service_dec.setVisibility(View.GONE);
            item_service_arrow.setVisibility(View.GONE);
        }
    }
}
