package com.bber.company.android.view.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.BusinessBean;
import com.bber.company.android.tools.Tools;
import com.bber.customview.recyclerview.BaseRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Administrator on 2015-05-22.
 */
public class BusinessModelAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    public static final int TYPE_CONTENT = 0;
    public static final int TYPE_ADS = 1;
    private List<BusinessBean> mBusinessList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public BusinessModelAdapter(List<BusinessBean> mBusinessList) {
        this.mBusinessList = mBusinessList;
    }

    public void updateListView(List<BusinessBean> mBusinessList) {
        this.mBusinessList = mBusinessList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view, int viewType) {
        if (TYPE_ADS == viewType) {
            return new adsViewHolder(view, TYPE_ADS);
        } else {
            return new busViewHolder(view, TYPE_CONTENT);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view;
        busViewHolder holder;
        adsViewHolder adsHolder;
        if (viewType == TYPE_CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
            holder = new busViewHolder(view, TYPE_CONTENT);
            view.setOnClickListener(this);
            return holder;
        } else if (TYPE_ADS == viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ads, parent, false);
            adsHolder = new adsViewHolder(view, TYPE_ADS);
            view.setOnClickListener(this);
            return adsHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isItem) {
        if (holder.getItemViewType() == TYPE_CONTENT) {
            BusinessBean businessBean = mBusinessList.get(position);
            String URL = businessBean.getShopHeadPhoto();
            ((busViewHolder) holder).tv_item_covers.setImageResource(R.mipmap.default_icon);
            if (!Tools.isEmpty(URL)) {
                ((busViewHolder) holder).tv_item_covers.setTag(URL);
                if (((busViewHolder) holder).tv_item_covers.getTag() != null
                        && ((busViewHolder) holder).tv_item_covers.getTag().equals(URL)) {
                    Uri uri = Uri.parse(URL);
                    ((busViewHolder) holder).tv_item_covers.setImageURI(uri);
                }
            }
            ((busViewHolder) holder).tv_shop_name.setText(businessBean.getShopName());
            if (businessBean.getShopMinMoney() > 0) {
                ((busViewHolder) holder).tv_shop_money.setText("¥" + businessBean.getShopMinMoney() + "起");
            }
            ((busViewHolder) holder).tv_district.setText(businessBean.getShopDistrictName());
            ((busViewHolder) holder).tv_style.setText(businessBean.getShopTypeName());
            ((busViewHolder) holder).rb_rating.setRating(businessBean.getShopScore());
            holder.itemView.setTag(businessBean);
        } else if (holder.getItemViewType() == TYPE_ADS) {
            BusinessBean businessBean = mBusinessList.get(position);
            String URL = businessBean.getShopHeadPhoto();
            if (!Tools.isEmpty(URL)) {
                ((adsViewHolder) holder).ads_icon.setTag(URL);
                if (((adsViewHolder) holder).ads_icon.getTag() != null
                        && ((adsViewHolder) holder).ads_icon.getTag().equals(URL)) {
                    Uri uri = Uri.parse(URL);
                    ((adsViewHolder) holder).ads_icon.setImageURI(uri);
                }
            }
            holder.itemView.setTag(businessBean);
        }
    }

    @Override
    public int getAdapterItemCount() {
        return mBusinessList.size();
    }

    @Override
    public int getAdapterItemViewType(int position) {
        return mBusinessList.get(position).getViewType();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (BusinessBean) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, BusinessBean data);
    }

    /**
     * bussiness
     */
    public static class busViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView tv_item_covers;
        public TextView tv_shop_name, tv_shop_money;
        public TextView tv_district, tv_distance, tv_style;
        public RatingBar rb_rating;
        public ImageView iv_new_shop;
        public int m_type;

        public busViewHolder(View itemView, int type) {
            super(itemView);
            m_type = type;
            tv_item_covers = itemView.findViewById(R.id.tv_item_covers);
            iv_new_shop = itemView.findViewById(R.id.btn_favorite);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            tv_shop_money = itemView.findViewById(R.id.tv_shop_money);
            tv_district = itemView.findViewById(R.id.tv_district);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_style = itemView.findViewById(R.id.tv_style);
            rb_rating = itemView.findViewById(R.id.rb_rating);
        }
    }

    public static class adsViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ads_icon;
        public int m_type;

        public adsViewHolder(View itemView, int type) {
            super(itemView);
            m_type = type;
            ads_icon = itemView.findViewById(R.id.ads_icon);
        }
    }

}
