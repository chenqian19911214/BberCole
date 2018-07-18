package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Administrator on 2015-05-22.
 */
public class HistoryModelListAdapter extends RecyclerView.Adapter<HistoryModelListAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private Activity activity;
    private LayoutInflater mInflater;
    private String[] colors = new String[]{"#f3267e", "#fe7979", "#04edbe", "#6cdeff", "#ffd708", "#ffb108"};
    private List<SellerUserVo> sellerUserList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public HistoryModelListAdapter(Activity activity, List<SellerUserVo> sellerUserVo) {
        this.activity = activity;
        this.sellerUserList = sellerUserVo;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateListView(List<SellerUserVo> list) {
        this.sellerUserList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history_model, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        SellerUserVo seller = sellerUserList.get(position);
        viewHolder.show_time.setText(seller.getNowTime());
        viewHolder.seller_name.setText(seller.getUsName());
        RoundingParams roundingParams =
                viewHolder.user_icon.getHierarchy().getRoundingParams();
        int index = (int) (Math.random() * colors.length);
        roundingParams.setBorder(Color.parseColor(colors[index]), 4);
        roundingParams.setRoundAsCircle(true);
        viewHolder.user_icon.getHierarchy().setRoundingParams(roundingParams);
        String URL = seller.getUsHeadm();

        viewHolder.user_icon.setImageResource(R.mipmap.user_icon);
        if (!Tools.isEmpty(URL)) {
            viewHolder.user_icon.setTag(URL);
            if (viewHolder.user_icon.getTag() != null
                    && viewHolder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(URL);
                viewHolder.user_icon.setImageURI(uri);
            }
        }
        viewHolder.itemView.setTag(seller);
    }

    @Override
    public int getItemCount() {
        return sellerUserList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (SellerUserVo) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemLongClick(view, (SellerUserVo) view.getTag());
        }
        return false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, SellerUserVo data);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, SellerUserVo data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView user_icon;
        public TextView seller_name, show_time;

        public ViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            seller_name = itemView.findViewById(R.id.seller_name);
            show_time = itemView.findViewById(R.id.show_time);
        }
    }
}
