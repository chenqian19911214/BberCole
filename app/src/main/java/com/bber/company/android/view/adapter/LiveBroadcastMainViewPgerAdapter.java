package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by carlo.c on 2018/3/30.
 */

public class LiveBroadcastMainViewPgerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> list;

    public LiveBroadcastMainViewPgerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
