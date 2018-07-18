package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Administrator on 2015-05-25.
 */
public class HorListAdapter extends RecyclerView.Adapter<HorListAdapter.ViewHolder> implements View.OnClickListener {
    private Activity activity;
    private LayoutInflater mInflater;
    private String[] projectImages;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public HorListAdapter(Activity mactivity, String[] infos) {
        activity = mactivity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        projectImages = infos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hor_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);

        int width = getImageWidth();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        params.leftMargin = 2;
        holder.imageView.setLayoutParams(params);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        String imageSmall = projectImages[position];
//        //加载项目图片
        if (!Tools.isEmpty(imageSmall)) {
            viewHolder.imageView.setTag(imageSmall);
            //预设图片
            //通过tag来防止图片错位
            if (viewHolder.imageView.getTag() != null
                    && viewHolder.imageView.getTag().equals(imageSmall)) {
                Uri uri = Uri.parse(imageSmall);
                viewHolder.imageView.setImageURI(uri);
            }
        }

        viewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return projectImages.length;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //计算每个image的宽度
    private int getImageWidth() {
        int width = ((MyApplication.screenWidth) / 4) - 2;//除以列数 再减去每张图的间距
        return width;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
