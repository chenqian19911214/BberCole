package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;

    private int mChildCount = 0;
    private LayoutInflater inflater = null;
    private List<View> views;

    public ViewPagerAdapter(Context context, List<View> _views) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.views = _views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(views.get(position));
//			 container.setOnLongClickListener(new View.OnLongClickListener() {
//				 @Override
//				 public boolean onLongClick(View v) {
//					 MyToast.makeTextAnim(mContext, "可是这里怎么保存图片呀", 0, R.style.PopToast).show();
//					 return false;
//				 }
//			 });

        return views.get(position);
    }
}
