package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by carlo.c on 2018/3/30.
 */

public class VipPrivilegeViewPgerAdapter extends PagerAdapter {

    public List<View> mListViews;
    private Context context;

    public VipPrivilegeViewPgerAdapter(List<View> mListViews, Context context) {
        this.mListViews = mListViews;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = mListViews.get(position);
        //添加到容器
        container.addView(view);
        //返回显示的view
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
