package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;

import java.util.List;


/**
 * Created by Administrator on 2015-05-22.
 */
public class ReplyListAdapter extends RecyclerView.Adapter<ReplyListAdapter.ViewHolder> implements View.OnClickListener {
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<String> replyList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public ReplyListAdapter(Activity context, List<String> infos) {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        replyList = infos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_reply, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String reply = replyList.get(position);
        holder.itemView.setTag(reply);
        holder.texttip.setText(reply);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String reply);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView texttip;

        public ViewHolder(View itemView) {
            super(itemView);
            texttip = itemView.findViewById(R.id.text_tip);
        }
    }

}
