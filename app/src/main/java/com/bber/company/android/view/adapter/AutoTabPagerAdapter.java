package com.bber.company.android.view.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhuang on 2016/5/17.
 */
public class AutoTabPagerAdapter extends PagerAdapter {
    private List<View> mViewList;
    private List<String> mTitleList;//页卡标题集合

    public AutoTabPagerAdapter(List<View> mViewList) {
        this.mViewList = mViewList;
//        this.mTitleList = tabList;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));//添加页卡
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));//删除页卡
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return mTitleList.get(position);//页卡标题
//    }
}
