package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bber.company.android.R;

import java.util.List;

/**
 * Created by carlo.c on 2018/4/16.
 */

public class BroadcastSearchGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    public BroadcastSearchGridViewAdapter(Context context, List<String> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ResultViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_broadcase_search_gridview_layout, viewGroup, false);
            holder = new ResultViewHolder();
            holder.name = view.findViewById(R.id.boradcase_search_text);
            view.setTag(holder);
        } else {
            holder = (ResultViewHolder) view.getTag();
        }
        holder.name.setText(list.get(position));
        return view;
    }

    public static class ResultViewHolder {
        TextView name;
    }
    //return view;
}
