package com.bber.company.android.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.PrivilegesReyclerVewData;
import com.bber.company.android.bean.livebroadcast.BroadcaseSearchSingleBean;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 *  搜索 浏览历史记录  ReyclerView 适配器
 * Created by carlo.c on 2018/4/16.
 */

public class BroadcastSearchReyclerViewAdapter extends RecyclerView.Adapter<BroadcastSearchReyclerViewAdapter.ViewHole> implements View.OnClickListener {

    private Context context;
    private List<BroadcaseSearchSingleBean.DataCollectionBean> listdata;
    private OnItemClickListener mOnItemClickListener = null;

    public BroadcastSearchReyclerViewAdapter(Context context, List listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @Override
    public ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_broadcast_search_grvyclerview_layout, parent, false);
        ViewHole viewhole = new ViewHole(view);

        view.setOnClickListener(this);
        return viewhole;
    }

    @Override
    public void onBindViewHolder(final ViewHole holder, final int position) {
        /**
         * 图片地址
         * */
        StringBuffer imageurlhead = new StringBuffer();
        String iamgeurihead = (String) SharedPreferencesUtils.get(context, "IMAGE_FILE", "");
        imageurlhead.append(iamgeurihead);
        imageurlhead.append(listdata.get(position).getHeadm());
        holder.linearicon.setImageURI(imageurlhead.toString());
        int liveboadcaststatus = listdata.get(position).getStatus();
        switch (liveboadcaststatus) {
            case 1:
                holder.imageViewStyte.setImageResource(R.mipmap.livebroadcast_status_offline);
                holder.textViewname.setTextColor(Color.parseColor("#666666"));

                break;
            case 2:
                holder.imageViewStyte.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                holder.textViewname.setTextColor(Color.parseColor("#D30E02"));


                break;
            case 3:
                holder.imageViewStyte.setImageResource(R.mipmap.livebroadcast_status_online);
                holder.textViewname.setTextColor(Color.parseColor("#3CB034"));

                break;
        }

        holder.textViewname.setText(listdata.get(position).getName());
        holder.linearicon.setTag(position);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public void onClick(View view) {

        ViewHole viewHole = new ViewHole(view);

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) viewHole.linearicon.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ViewHole extends RecyclerView.ViewHolder {
        private ImageView imageViewStyte;
        private TextView textViewname;
        private SimpleDraweeView linearicon;

        @SuppressLint("WrongViewCast")
        public ViewHole(View view) {
            super(view);
            imageViewStyte = view.findViewById(R.id.search_user_styte);
            textViewname = view.findViewById(R.id.search_user_name);
            linearicon = view.findViewById(R.id.search_user_icon);
        }
    }
}
