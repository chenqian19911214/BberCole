package com.bber.company.android.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.DiscussBean;
import com.bber.company.android.tools.Tools;
import com.bber.customview.recyclerview.BaseRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class DiscussPicAdapter extends BaseRecyclerAdapter<DiscussPicAdapter.ViewHolder> implements View.OnClickListener {
    private List<DiscussBean> discussList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private Context mContext;

    public DiscussPicAdapter(Context mContext, List<DiscussBean> discussList) {
        this.mContext = mContext;
        this.discussList = discussList;
    }

    public void updateListView(List<DiscussBean> discussList) {
        this.discussList = discussList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discuss, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public ViewHolder getViewHolder(View view, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, boolean isItem) {
        DiscussBean discussBean = discussList.get(position);

        String coverURL = discussBean.getCoverPicture();
        viewHolder.discuss_cover.setImageResource(R.mipmap.default_shop);
        viewHolder.discuss_cover.setVisibility(View.VISIBLE);
        if (!Tools.isEmpty(coverURL)) {
            viewHolder.discuss_cover.setTag(coverURL);
            if (viewHolder.discuss_cover.getTag() != null
                    && viewHolder.discuss_cover.getTag().equals(coverURL)) {
                Uri uri = Uri.parse(coverURL);
                Glide.with(mContext)
                        .load(uri)
                        .error(R.mipmap.default_shop)
                        .placeholder(R.mipmap.default_shop)
                        .into(viewHolder.discuss_cover);
//                viewHolder.discuss_cover.setImageURI(uri);
            }
        }
        //  String userURL = discussBean.getCoverPicture();
        //  viewHolder.user_icon.setImageResource(R.mipmap.user_icon);
        //  viewHolder.user_icon.setVisibility(View.VISIBLE);
        //   if (!Tools.isEmpty(userURL)) {
        //       viewHolder.user_icon.setTag(userURL);
        //       if (viewHolder.user_icon.getTag() != null
        //               && viewHolder.user_icon.getTag().equals(userURL)) {
        //          Uri uri = Uri.parse(userURL);
        //           viewHolder.user_icon.setImageURI(uri);
        //       }
        //   }
        String daytime = Tools.timeStamp2Date(discussBean.getStartTime(), "yyyy-MM-dd");
        viewHolder.discuss_date.setText(daytime);
        viewHolder.tv_tittle.setText(discussBean.getTitle());
        if (discussList != null) {
            viewHolder.tv_picsum.setText(discussBean.getImg().size() + "图");
        }

        viewHolder.itemView.setTag(discussBean);
    }

    @Override
    public int getAdapterItemCount() {
        return discussList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (DiscussBean) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, DiscussBean data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //  public SimpleDraweeView user_icon;
        public SimpleDraweeView discuss_cover;
        public TextView discuss_date, tv_picsum, tv_tittle;

        public ViewHolder(View itemView) {
            super(itemView);
            //    user_icon = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            //    user_name= (TextView) itemView.findViewById(R.id.user_name);
            discuss_cover = itemView.findViewById(R.id.sd_cover);
            discuss_date = itemView.findViewById(R.id.tv_date);
            tv_tittle = itemView.findViewById(R.id.tv_tittle);
            tv_picsum = itemView.findViewById(R.id.tv_picsum);

        }
    }
}
