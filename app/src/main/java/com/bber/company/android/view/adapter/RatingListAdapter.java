package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.BusinessRatingBean;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.activity.BrowseImageActivity;
import com.bber.customview.recyclerview.BaseRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-05-25.
 */
public class RatingListAdapter extends BaseRecyclerAdapter<RatingListAdapter.ViewHolder> {

    private List<BusinessRatingBean> ratingBeanList;
    private Context mContex;

    public RatingListAdapter(Context mContex, List<BusinessRatingBean> ratingBeanList) {
        this.mContex = mContex;
        this.ratingBeanList = ratingBeanList;
    }

    public void update(List<BusinessRatingBean> ratingBeanList) {
        this.ratingBeanList = ratingBeanList;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder getViewHolder(View view, int viewType) {
        return new RatingListAdapter.ViewHolder(view, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        ViewHolder holder = new ViewHolder(view, true);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, boolean isItem) {
        BusinessRatingBean ratingBean = ratingBeanList.get(position);
        holder.tv_tips.setText(ratingBean.getCommentMessage());
        holder.name.setText(ratingBean.getBuyerName());
        holder.date.setText(ratingBean.getCreateDate());
        holder.rb_rating.setRating(ratingBean.getCommentTotalScore());

        holder.user_icon.setImageResource(R.mipmap.user_icon);
        if (!Tools.isEmpty(ratingBean.getBuyerHeadm())) {
            Uri uri = Uri.parse(ratingBean.getBuyerHeadm());
            holder.user_icon.setImageURI(uri);
        }
        String[] images = getPhoto(ratingBean.getCommentPhoto(), ratingBean.getCommentPhoto2(), ratingBean.getCommentPhoto3());
        initPhotos(holder, images);
        holder.itemView.setTag(position);
    }


    @Override
    public int getAdapterItemCount() {
        return (ratingBeanList != null ? ratingBeanList.size() : 0);
    }

    /**
     * 初始化相片列表
     *
     * @return
     */
    private void initPhotos(final ViewHolder viewHolder, final String[] images) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContex);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewHolder.recyclerView.setLayoutManager(linearLayoutManager);
        int height = (MyApplication.screenWidth) / 4;
        viewHolder.recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

        //加载相册
        if (images != null && images.length > 0) {
            viewHolder.recyclerView.setVisibility(View.VISIBLE);
            HorListAdapter horListAdapter = new HorListAdapter((Activity) mContex, images);
            viewHolder.recyclerView.setAdapter(horListAdapter);
            horListAdapter.setOnItemClickListener(new HorListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(mContex, BrowseImageActivity.class);
                    intent.putExtra("images", images);
                    intent.putExtra("currentIndex", position);
                    mContex.startActivity(intent);
                }
            });
        } else {
            viewHolder.recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取相册
     *
     * @param s1
     * @param s2
     * @param s3
     * @return
     */

    private String[] getPhoto(String s1, String s2, String s3) {
        String[] images = {s1, s2, s3};
        List<String> list = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {
            if (!Tools.isEmpty(images[i])) {
                list.add(images[i]);
            }
        }
        int size = list.size();
        return list.toArray(new String[size]);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView user_icon;
        private TextView name, date, tv_tips;
        private RatingBar rb_rating;
        private RecyclerView recyclerView;

        public ViewHolder(View itemView, boolean isItem) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            tv_tips = itemView.findViewById(R.id.tv_tips);
            rb_rating = itemView.findViewById(R.id.rb_rating);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
