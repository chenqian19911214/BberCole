package com.bber.company.android.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.PrivilegesReyclerVewData;

import java.util.List;

/**
 * ???? ReyclerView ???
 * Created by carlo.c on 2018/3/29.
 */

public class VipPrivilegesReyclerViewAdapter extends RecyclerView.Adapter<VipPrivilegesReyclerViewAdapter.ViewHole> implements View.OnClickListener {

    private Context context;
    private List<PrivilegesReyclerVewData> listdata;
    private OnItemClickListener mOnItemClickListener = null;
    private boolean[] mMenuItemsIsSelect = null;

    public VipPrivilegesReyclerViewAdapter(Context context, List listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @Override
    public ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.vipprivileres_recyclerview_item_layout, parent, false);
        ViewHole viewhole = new ViewHole(view);

        view.setOnClickListener(this);
        return viewhole;
    }


    public void setMenuItemsIsSelect(boolean[] menuItemsIsSelect) {
        mMenuItemsIsSelect = menuItemsIsSelect;
    }


    public void changeMenuItemSelectState(int position) {

        if (mMenuItemsIsSelect != null) {

            for (int i = 0; i < mMenuItemsIsSelect.length; i++) {

                mMenuItemsIsSelect[i] = false;
            }

            mMenuItemsIsSelect[position] = true;
        }

    }

    @Override
    public void onBindViewHolder(final ViewHole holder, final int position) {

        holder.imageView.setBackgroundResource(listdata.get(position).getImage());
        holder.textView.setText(listdata.get(position).getText());
        holder.textView.setTag(position);
        if (mMenuItemsIsSelect[position]) {
            holder.linearLayout.setBackgroundResource(R.mipmap.viprivileges_select);
        } else {
            holder.linearLayout.setBackgroundResource(0);
        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public void onClick(View view) {

        ViewHole viewHole = new ViewHole(view);

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) viewHole.textView.getTag());
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
        private ImageView imageView;
        private TextView textView;
        private LinearLayout linearLayout;

        @SuppressLint("WrongViewCast")
        public ViewHole(View view) {
            super(view);
            imageView = view.findViewById(R.id.vip_priviler_reyclerview_item_image_id);
            textView = view.findViewById(R.id.vip_priviler_reyclerview_item_text_id);
            linearLayout = view.findViewById(R.id.linearlayouttopid);
        }
    }
}
