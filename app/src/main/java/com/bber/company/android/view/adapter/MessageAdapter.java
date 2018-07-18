package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.livebroadcast.BoradcastMessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by carlo.c on 2018/5/4.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<BoradcastMessageBean> messageBeanList;
    protected final LayoutInflater inflater;

    public MessageAdapter(Context context, List<BoradcastMessageBean> messageBeanList) {
        inflater = ((Activity) context).getLayoutInflater();
        this.messageBeanList = messageBeanList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.msg_item_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        setupView(holder, position);

    }

    @Override
    public int getItemCount() {

        return messageBeanList.size();
    }


    private void setupView(MyViewHolder holder, int position) {

        BoradcastMessageBean bean = messageBeanList.get(position);

        String isBeslf = bean.isBeSelf();
        if (bean != null && isBeslf != null) {
            if (isBeslf.equals("true")) {

                Uri uri = Uri.parse(bean.geticonuri());
                holder.textViewSelfName.setImageURI(uri);
                holder.textViewSelfMsg.setText(bean.getMessage());

            } else {
                Uri uri2 = Uri.parse(bean.geticonuri());
                holder.textViewOtherName.setImageURI(uri2);
                holder.textViewOtherMsg.setText(bean.getMessage());
                // if (bean.getBackground() != 0) {
                //     holder.textViewOtherName.setBackgroundResource(bean.getBackground());
                // }
            }

            holder.layoutRight.setVisibility(isBeslf.equals("true") ? View.VISIBLE : View.GONE);
            holder.layoutLeft.setVisibility(isBeslf.equals("true") ? View.GONE : View.VISIBLE);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView textViewOtherName;
        private TextView textViewOtherMsg;
        private SimpleDraweeView textViewSelfName;
        private TextView textViewSelfMsg;
        private RelativeLayout layoutLeft;
        private RelativeLayout layoutRight;  //SimpleDraweeView

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewOtherName = itemView.findViewById(R.id.item_name_l);
            textViewSelfName = itemView.findViewById(R.id.item_name_r);
            textViewOtherMsg = itemView.findViewById(R.id.item_msg_l);
            textViewSelfMsg = itemView.findViewById(R.id.item_msg_r);
            layoutLeft = itemView.findViewById(R.id.item_layout_l);
            layoutRight = itemView.findViewById(R.id.item_layout_r);
        }
    }
}