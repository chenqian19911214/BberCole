package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.bber.company.android.R;

import java.util.List;

public class MapPlaceListAdapter extends RecyclerView.Adapter<MapPlaceListAdapter.ViewHolder>
        implements View.OnClickListener {
    private Activity activity;
    private LayoutInflater mInflater;
    private int position;
    private List<PoiItem> listPalce;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public MapPlaceListAdapter(Activity mactivity, List<PoiItem> listPalce, int position) {
        activity = mactivity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listPalce = listPalce;
        this.position = position;
    }

    public void updateListView(List<PoiItem> list, int position) {
        this.listPalce = list;
        this.position = position;
        notifyDataSetChanged();
    }

    public void updatePosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_map_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        PoiItem palce = listPalce.get(position);
        viewHolder.palceName.setText(palce.getTitle());
        String address = palce.getCityName()
                + "," + palce.getAdName() + "," + palce.getBusinessArea() + "," + palce.getSnippet();
        viewHolder.address.setText(palce.getCityName() + address);

        if (position == this.position) {
            viewHolder.view_this.setVisibility(View.VISIBLE);
        } else {
            viewHolder.view_this.setVisibility(View.GONE);
        }
        viewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return listPalce.size();
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

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView palceName;
        private TextView address;
        private ImageView view_this;

        public ViewHolder(View itemView) {
            super(itemView);
            palceName = itemView.findViewById(R.id.palceName);
            address = itemView.findViewById(R.id.address);
            view_this = itemView.findViewById(R.id.view_this);
            view_this.setVisibility(View.GONE);
        }
    }

}
