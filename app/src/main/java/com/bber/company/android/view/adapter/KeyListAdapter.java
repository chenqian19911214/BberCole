package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.InviteFriend;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class KeyListAdapter extends RecyclerView.Adapter<KeyListAdapter.ViewHolder> {

    private List<InviteFriend> inviteFriends;
    private String[] mVerify = {"未认证", "已认证", "", ""};

    public KeyListAdapter(Context mContext, List<InviteFriend> inviteFriends) {
        this.inviteFriends = inviteFriends;
    }

    public void updateListView(List<InviteFriend> inviteFriends) {
        this.inviteFriends = inviteFriends;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.key_item, viewGroup, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        InviteFriend inviteFriend = inviteFriends.get(position);
        viewHolder.userIcon.setImageURI(inviteFriend.getUbHeadm());
        viewHolder.name.setText(inviteFriend.getUbName());
        viewHolder.verify_status.setText(mVerify[Tools.StringToInt(inviteFriend.getiSVerify())]);
        viewHolder.time.setText(inviteFriend.getUbCtime().substring(0, 10));
        viewHolder.itemView.setTag(inviteFriend);
    }

    @Override
    public int getItemCount() {
        return inviteFriends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView userIcon;
        public TextView name;
        public TextView time;
        public TextView verify_status;

        public ViewHolder(View itemView) {
            super(itemView);
            userIcon = itemView.findViewById(R.id.user_icon);
            name = itemView.findViewById(R.id.user_name);
            time = itemView.findViewById(R.id.user_time);
            verify_status = itemView.findViewById(R.id.verify_status);
        }
    }
}
