package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bber.company.android.bean.GameGirlsBean;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class UnlimmitedViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private int mChildCount = 0;
    private LayoutInflater inflater = null;
    private List<GameGirlsBean> imagePathList;
    private List<ImageView> imageViewList = new ArrayList<>();

    public UnlimmitedViewPagerAdapter(Context context, List<GameGirlsBean> imagePathList) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        String path = (String) SharedPreferencesUtils.get(mContext, "IMAGE_FILE", "");
        for (int i = 0; i < imagePathList.size(); i++) {
            ImageView iv = new ImageView(mContext);
            Glide.with(mContext).load(path + imagePathList.get(i).imgPath).dontAnimate().into(iv);
            imageViewList.add(iv);
        }
        this.imagePathList = imagePathList;
    }

    @Override
    public int getCount() {
        return imagePathList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViewList.get(position));
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

        ((ViewPager) container).addView(imageViewList.get(position));
        return imageViewList.get(position);
    }

}
