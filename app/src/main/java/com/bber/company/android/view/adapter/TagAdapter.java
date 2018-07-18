package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Tag;

import java.util.List;

/**
 * Created by Administrator on 2015-06-17.
 */
public class TagAdapter extends BaseAdapter {

    private Activity activity;
    private List<Tag> lists;
    private LayoutInflater mInflater;
    private int mark;//0:表示profile      1：表示评价


    public TagAdapter(Activity mactivity, List<Tag> infos, int mark) {
        activity = mactivity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lists = infos;
        this.mark = mark;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder = new ViewHolder();
        if ((position == lists.size() - 1) && lists.get(position) == null) {
            convertView = mInflater.inflate(R.layout.tag_grid_item_add, null);
        } else {
            convertView = mInflater.inflate(R.layout.tag_grid_item, null);
            holder.tag_text = convertView.findViewById(R.id.tag_text);
            holder.count_text = convertView.findViewById(R.id.count_text);
            if (mark == 0) {
                convertView.setEnabled(false);
                convertView.setBackgroundResource(R.drawable.btn_tag_nor);
                holder.tag_text.setTextColor(activity.getResources().getColor(R.color.item_bg));
                holder.count_text.setTextColor(activity.getResources().getColor(R.color.item_bg));
            } else {
                convertView.setEnabled(true);
                convertView.setBackgroundResource(R.drawable.btn_tag_bg2);
                holder.tag_text.setTextColor(activity.getResources().getColorStateList(R.color.tag_text));
                holder.count_text.setTextColor(activity.getResources().getColorStateList(R.color.tag_text));
            }


            Tag tag = lists.get(position);
            holder.tag_text.setText(tag.getContent());
            holder.count_text.setText(" (" + tag.getCount() + ")");
        }


        return convertView;
    }

    private static class ViewHolder {
        private TextView tag_text, count_text;

    }

}
