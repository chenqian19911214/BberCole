package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.cityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class HotCityGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<cityBean> mCities;

    public HotCityGridAdapter(Context context) {
        this.mContext = context;
        mCities = new ArrayList<>();
        cityBean beijing = new cityBean(480, 110100, "北京市", 39.904989, 116.405285, 110000, "010", "beijingshishixiaqu");
        cityBean guangzhou = new cityBean(681, 440100, "广州市", 23.125178, 113.280637, 440000, "020", "guangzhoushi");
        cityBean shanghai = new cityBean(553, 310100, "上海市", 31.231706, 121.472644, 310000, "021", "shanghaishishixiaqu");
        cityBean shenzhen = new cityBean(683, 440300, "深圳市", 22.547, 114.085947, 440000, "0755", "shenzhenshi");
        cityBean chengdu = new cityBean(737, 510100, "成都市", 30.659462, 104.065735, 510000, "028", "chengdushi");
        cityBean wuhan = new cityBean(650, 420100, "武汉市", 30.584355, 114.298572, 420000, "027", "wuhanshi");
        cityBean nanjing = new cityBean(555, 320100, "南京市", 32.041544, 118.767413, 320000, "025", "nanjingshi");
        cityBean chognqing = new cityBean(735, 500100, "重庆市", 29.533155, 106.504962, 500000, "023", "chognqingshi");

        mCities.add(beijing);
        mCities.add(guangzhou);
        mCities.add(shanghai);
        mCities.add(shenzhen);
        mCities.add(chengdu);
        mCities.add(wuhan);
        mCities.add(nanjing);
        mCities.add(chognqing);
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
        HotCityViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_city_gridview, parent, false);
            holder = new HotCityViewHolder();
            holder.name = view.findViewById(R.id.tv_hot_city_name);
            view.setTag(holder);
        } else {
            holder = (HotCityViewHolder) view.getTag();
        }
        holder.name.setText(mCities.get(position).getName());
        return view;
    }

    public static class HotCityViewHolder {
        TextView name;
    }
}
