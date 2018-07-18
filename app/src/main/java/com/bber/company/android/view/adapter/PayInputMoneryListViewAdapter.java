package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bber.company.android.R;
import com.bber.company.android.bean.PayQueryBean;
import com.bber.company.android.view.customcontrolview.ListItemLinePay;

import java.util.List;

/**
 * Created by carlo.c on 2018/3/27.
 */

public class PayInputMoneryListViewAdapter extends BaseAdapter {

    private Context context;
    private List<PayQueryBean.DataCollectionBean> listviewdata;
    private boolean[] mMenuItemsIsSelect = null;

    public PayInputMoneryListViewAdapter(Context context, List<PayQueryBean.DataCollectionBean> listviewdata) {

        this.context = context;
        this.listviewdata = listviewdata;

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
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ListItemLinePay item1;
        if (convertView == null) {
            item1 = (ListItemLinePay) LayoutInflater.from(context)
                    .inflate(R.layout.listview_payinput_itme_layout, viewGroup, false);
        } else {
            item1 = (ListItemLinePay) convertView;
        }

        String payname = listviewdata.get(position).getPayName();
        String imguri = listviewdata.get(position).getPayIcon();
        item1.init(imguri, payname, mMenuItemsIsSelect[position]);

        return item1;

    }

}
