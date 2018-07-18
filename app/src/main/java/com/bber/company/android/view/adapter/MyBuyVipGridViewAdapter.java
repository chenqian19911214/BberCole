package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by carlo.c on 2018/3/26.
 */

public class MyBuyVipGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Map> listviewdata;


    public MyBuyVipGridViewAdapter(Context context, ArrayList<Map> listviewdata) {

        this.context = context;
        this.listviewdata = listviewdata;
    }


    @Override
    public int getCount() {

        return listviewdata.size();
    }

    @Override
    public Object getItem(int i) {
        return listviewdata.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder hodele = null;
        if (convertView == null) {
            hodele = new ViewHolder();
            View view = LayoutInflater.from(context).inflate(R.layout.gridviewitem_myviplayout, viewGroup, false);
            hodele.imageViewicon = view.findViewById(R.id.gridvie_image_id);
            hodele.textViewname = view.findViewById(R.id.gridvie_text_id);

            hodele.textViewname.setText((String) listviewdata.get(i).get("text"));//img
            hodele.imageViewicon.setBackgroundResource((Integer) listviewdata.get(i).get("img"));


            view.setTag(hodele);
            return view;
        } else {

            hodele = (ViewHolder) convertView.getTag();
        }

        hodele.textViewname.setText((String) listviewdata.get(i).get("text"));
        hodele.imageViewicon.setBackgroundResource((Integer) listviewdata.get(i).get("img"));

        return convertView;
    }

    class ViewHolder {
        ImageView imageViewicon;
        TextView textViewname;
    }

}
