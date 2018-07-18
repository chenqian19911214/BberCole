package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.cityBean;

import java.util.List;


/**
 * author zaaach on 2016/1/26.
 */
public class DistrictGridviewAdapter extends BaseAdapter {
    private Context mContext;
    private List<cityBean> mCities;

    public DistrictGridviewAdapter(Context mContext, List<cityBean> mCities) {
        this.mCities = mCities;
        this.mContext = mContext;
    }

    public void changeData(List<cityBean> list) {
        if (mCities == null) {
            mCities = list;
        } else {
            mCities.clear();
            mCities.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public cityBean getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ResultViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_city_gridview, parent, false);
            holder = new ResultViewHolder();
            holder.name = view.findViewById(R.id.tv_hot_city_name);
            view.setTag(holder);
        } else {
            holder = (ResultViewHolder) view.getTag();
        }
        holder.name.setText(mCities.get(position).getName());
        return view;
    }

    public static class ResultViewHolder {
        TextView name;
    }
}
