package com.bber.company.android.view.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Order;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.List;

public class BillListAdapter extends RecyclerView.Adapter<BillListAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private List<Order> orders;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    private String[] colors = new String[]{"#f3267e", "#fe7979", "#04edbe", "#6cdeff", "#ffd708", "#ffb108"};
    private String orderstatus[] = {"", "订单发送", "", "订单开始", "等待评价", "订单结束", "订单取消", "订单取消", "", "争议订单"};


    public BillListAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void updateListView(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myorder, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Order order = orders.get(position);

        int index = (int) (Math.random() * colors.length);
//        viewHolder.user_icon.setBorderColor(Color.parseColor(colors[index]));

        RoundingParams roundingParams =
                viewHolder.user_icon.getHierarchy().getRoundingParams();
        roundingParams.setBorder(Color.parseColor(colors[index]), 4);
        roundingParams.setRoundAsCircle(true);
        viewHolder.user_icon.getHierarchy().setRoundingParams(roundingParams);

        String URL = order.getSellerHead();
        viewHolder.user_icon.setImageResource(R.mipmap.user_icon);
        if (!Tools.isEmpty(URL)) {
            viewHolder.user_icon.setTag(URL);
            if (viewHolder.user_icon.getTag() != null
                    && viewHolder.user_icon.getTag().equals(URL)) {
                Uri uri = Uri.parse(URL);
                viewHolder.user_icon.setImageURI(uri);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = new Long(order.getCreateTime());
        String dayTime = format.format(time);

        viewHolder.user_name.setText(order.getSellerNickName());
        viewHolder.order_date.setText("下单时间：" + dayTime);
        viewHolder.order_status.setText(orderstatus[order.getStatus()]);
        viewHolder.order_price.setText("¥" + order.getMoney());

        viewHolder.itemView.setTag(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Order) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemLongClick(view, (Order) view.getTag());
        }
        return false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Order data);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, Order data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon;
        public TextView user_name, order_date, order_status, order_price;

        public ViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            user_name = itemView.findViewById(R.id.user_name);
            order_status = itemView.findViewById(R.id.order_status);
            order_price = itemView.findViewById(R.id.order_price);
            order_date = itemView.findViewById(R.id.order_date);
        }
    }
}
