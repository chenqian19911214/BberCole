package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.RecommendBroadcastBean;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 直播 推荐 GridView Adapter
 * Created by carlo.c on 2018/4/11.
 */

public class LiveBoadcastRecommendGridViewAdapter extends BaseAdapter {
    private List<RecommendBroadcastBean.DataCollectionBean> items;
    private Context context;

    public LiveBoadcastRecommendGridViewAdapter(Context context, List<RecommendBroadcastBean.DataCollectionBean> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.liveboadcast_gridview_item_layout, null);

        /**
         * 图片地址
         * */
        StringBuffer imageurlhead = new StringBuffer();
        String iamgeurihead = (String) SharedPreferencesUtils.get(context, "IMAGE_FILE", "");
        imageurlhead.append(iamgeurihead);
        imageurlhead.append(items.get(position).getHeadm());


        String liveboadcastname = items.get(position).getName();
        String liveboadcastcity = items.get(position).getCity();
        /**主播状态*/
        int liveboadcaststatus = items.get(position).getStatus();
        /**主播等级*/
        int liveboadcastlevel = items.get(position).getLevelId();

        TextView name = view.findViewById(R.id.liveboadcast_gridview_item_name);
        TextView city = view.findViewById(R.id.liveboadcast_gridview_item_city);
        ImageView imageView = view.findViewById(R.id.liveboadcast_gridview_item_image);
        ImageView imageViewstatus = view.findViewById(R.id.liveboadcast_gridview_item_stuts);
        ImageView level = view.findViewById(R.id.liveboadcast_gridview_item_level);

        city.setText(liveboadcastcity);
        switch (liveboadcaststatus) {

            case 1:

                imageViewstatus.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                imageViewstatus.setImageResource(R.mipmap.livebroadcast_status_bebusy);

                break;
            case 3:
                imageViewstatus.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }

        switch (liveboadcastlevel) {

            case 1:
                level.setImageResource(R.mipmap.liveboadcastlevel_primar);
                break;
            case 2:
                level.setImageResource(R.mipmap.liveboadcastlevel_intermediate);

                break;
            case 3:
                level.setImageResource(R.mipmap.liveboadcastlevel_senior);

                break;

            default:
                level.setImageResource(R.mipmap.liveboadcastlevel_senior);

        }

        Glide.with(context).load(imageurlhead.toString())/*.override(500, 500)*/.fitCenter().placeholder(R.mipmap.default_shop_boadcast).into(imageView);
        name.setText(liveboadcastname);
        return view;
    }
}
