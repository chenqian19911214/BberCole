package com.bber.company.android.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.BroadcasFragmentClassificationBean;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.activity.BroadcasrClassificationActivity;
import com.bber.company.android.view.activity.LiveBroadcastDetailsActivity;
import com.bber.company.android.view.customcontrolview.MyImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

/**
 * Created by carlo.c on 2018/4/17.
 */

public class LiveBroadcastClassificationListViewAdapter extends BaseAdapter{

    private List<BroadcasFragmentClassificationBean.DataCollectionBean> list;
    /**
     * 每个item的数据
     */
    private List<BroadcasFragmentClassificationBean.DataCollectionBean.AnchorBean> itemdata;


    private Context context;


    public LiveBroadcastClassificationListViewAdapter(Context context, List<BroadcasFragmentClassificationBean.DataCollectionBean> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {



        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_broadcast_fragemt_classification_listview_layout, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.classification_type_name.setText(list.get(position).getAcType());
        setBackage(position, holder);

        holder.classification_type_backage_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BroadcasrClassificationActivity.class);

                ChenQianLog.i("listView position:"+position);
                intent.putExtra("type", list.get(position).getAcType());
                context.startActivity(intent);
            }
        });

        ChenQianLog.i("position:"+position);

        itemdata = list.get(position).getAnchor();

        switch (itemdata.size()) {

            case 0:
                holder.classification_brablecase_data_id.setVisibility(View.GONE);
                break;
            case 1:
                setDataSezione(holder,position);

                break;
            case 2:

                setDataTwo(holder,position);

                break;
            case 3:
                setDtataThere(holder,position);
                break;
        }


        return view;
    }

    private void setDtataThere(ViewHolder holder,final int position) {
        holder.classification_brablecase_data_id.setVisibility(View.VISIBLE);
        holder.layout_1.setVisibility(View.VISIBLE);
        holder.layout_2.setVisibility(View.VISIBLE);
        holder.layout_3.setVisibility(View.VISIBLE);


        Glide.with(context).load(mergeImageUri(itemdata.get(0).getHeadm())).into(holder.classfifctation_brablecastion_icon);
        holder.classfifctation_brablecastion_name.setText(itemdata.get(0).getName());
        switch (itemdata.get(0).getStatus()) {
            case 1:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                break;
            case 3:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }
        Glide.with(context).load(mergeImageUri(itemdata.get(1).getHeadm())).into(holder.classfifctation_brablecastion_icon2);
        holder.classfifctation_brablecastion_name2.setText(itemdata.get(1).getName());
        switch (itemdata.get(1).getStatus()) {
            case 1:
                holder.classfifctation_brablecastion_type2.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                holder.classfifctation_brablecastion_type2.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                break;
            case 3:
                holder.classfifctation_brablecastion_type2.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }
        Glide.with(context).load(mergeImageUri(itemdata.get(2).getHeadm())).into(holder.classfifctation_brablecastion_icon3);
        holder.classfifctation_brablecastion_name3.setText(itemdata.get(2).getName());
        switch (itemdata.get(2).getStatus()) {
            case 1:
                holder.classfifctation_brablecastion_type3.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                holder.classfifctation_brablecastion_type3.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                break;
            case 3:
                holder.classfifctation_brablecastion_type3.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }

        holder.layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdata = list.get(position).getAnchor();

                Intent intent = new Intent(context, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", itemdata.get(0));
                intent.putExtra("fragmentname", "fragementclassification");
                context.startActivity(intent);
            }
        });
        holder.layout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdata = list.get(position).getAnchor();

                Intent intent = new Intent(context, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", itemdata.get(1));
                intent.putExtra("fragmentname", "fragementclassification");
                context.startActivity(intent);
            }
        });
        holder.layout_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdata = list.get(position).getAnchor();

                Intent intent = new Intent(context, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", itemdata.get(2));
                intent.putExtra("fragmentname", "fragementclassification");
                context.startActivity(intent);
            }
        });
    }

    private void setDataTwo(ViewHolder holder,final int position) {
        holder.classification_brablecase_data_id.setVisibility(View.VISIBLE);
        holder.layout_1.setVisibility(View.VISIBLE);
        holder.layout_2.setVisibility(View.VISIBLE);
        holder.layout_3.setVisibility(View.INVISIBLE);

        Glide.with(context).load(mergeImageUri(itemdata.get(0).getHeadm())).into(holder.classfifctation_brablecastion_icon);
        holder.classfifctation_brablecastion_name.setText(itemdata.get(0).getName());
        switch (itemdata.get(0).getStatus()) {
            case 1:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                break;
            case 3:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }

        Glide.with(context).load(mergeImageUri(itemdata.get(1).getHeadm())).into(holder.classfifctation_brablecastion_icon2);
        holder.classfifctation_brablecastion_name2.setText(itemdata.get(1).getName());
        switch (itemdata.get(0).getStatus()) {
            case 1:
                holder.classfifctation_brablecastion_type2.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                holder.classfifctation_brablecastion_type2.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                break;
            case 3:
                holder.classfifctation_brablecastion_type2.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }

        holder.layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdata = list.get(position).getAnchor();

                Intent intent = new Intent(context, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", itemdata.get(0));
                intent.putExtra("fragmentname", "fragementclassification");
                context.startActivity(intent);
            }
        });
        holder.layout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdata = list.get(position).getAnchor();

                Intent intent = new Intent(context, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", itemdata.get(1));
                intent.putExtra("fragmentname", "fragementclassification");
                context.startActivity(intent);
            }
        });
    }

    private void setDataSezione(ViewHolder holder, final int position) {
        holder.classification_brablecase_data_id.setVisibility(View.VISIBLE);
        holder.layout_1.setVisibility(View.VISIBLE);
        holder.layout_2.setVisibility(View.INVISIBLE);
        holder.layout_3.setVisibility(View.INVISIBLE);
        Glide.with(context).load(mergeImageUri(itemdata.get(0).getHeadm())).into(holder.classfifctation_brablecastion_icon);
        holder.classfifctation_brablecastion_name.setText(itemdata.get(0).getName());
        switch (itemdata.get(0).getStatus()) {
            case 1:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_offline);
                break;
            case 2:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_bebusy);
                break;
            case 3:
                holder.classfifctation_brablecastion_type.setImageResource(R.mipmap.livebroadcast_status_online);

                break;
        }

        holder.layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemdata = list.get(position).getAnchor();

                Intent intent = new Intent(context, LiveBroadcastDetailsActivity.class);
                intent.putExtra("data", itemdata.get(0));
                intent.putExtra("fragmentname", "fragementclassification");
                context.startActivity(intent);
            }
        });
    }

    /**
     * 设置背景图片
     */
    private void setBackage(int position, final ViewHolder holder) {
        Glide.with(context)
                .load(mergeImageUri(list.get(position).getAcPath()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(180, 180) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.classification_type_backage_id.setBackground(drawable);
                        }
                    }
                });
    }

    public static class ViewHolder {
        public View rootView;
        public TextView classification_type_name;
        public RelativeLayout classification_type_backage_id;
        public MyImageView classfifctation_brablecastion_icon;
        public ImageView classfifctation_brablecastion_type;
        public TextView classfifctation_brablecastion_name;
        public RelativeLayout layout_1;
        public MyImageView classfifctation_brablecastion_icon2;
        public ImageView classfifctation_brablecastion_type2;
        public TextView classfifctation_brablecastion_name2;
        public RelativeLayout layout_2;
        public MyImageView classfifctation_brablecastion_icon3;
        public ImageView classfifctation_brablecastion_type3;
        public TextView classfifctation_brablecastion_name3;
        public RelativeLayout layout_3;
        public LinearLayout classification_brablecase_data_id;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.classification_type_name = (TextView) rootView.findViewById(R.id.classification_type_name);
            this.classification_type_backage_id = (RelativeLayout) rootView.findViewById(R.id.classification_type_backage_id);
            this.classfifctation_brablecastion_icon =  rootView.findViewById(R.id.classfifctation_brablecastion_icon);
            this.classfifctation_brablecastion_type = (ImageView) rootView.findViewById(R.id.classfifctation_brablecastion_type);
            this.classfifctation_brablecastion_name = (TextView) rootView.findViewById(R.id.classfifctation_brablecastion_name);
            this.layout_1 = (RelativeLayout) rootView.findViewById(R.id.layout_1);
            this.classfifctation_brablecastion_icon2 =  rootView.findViewById(R.id.classfifctation_brablecastion_icon2);
            this.classfifctation_brablecastion_type2 = (ImageView) rootView.findViewById(R.id.classfifctation_brablecastion_type2);
            this.classfifctation_brablecastion_name2 = (TextView) rootView.findViewById(R.id.classfifctation_brablecastion_name2);
            this.layout_2 = (RelativeLayout) rootView.findViewById(R.id.layout_2);
            this.classfifctation_brablecastion_icon3 =  rootView.findViewById(R.id.classfifctation_brablecastion_icon3);
            this.classfifctation_brablecastion_type3 = (ImageView) rootView.findViewById(R.id.classfifctation_brablecastion_type3);
            this.classfifctation_brablecastion_name3 = (TextView) rootView.findViewById(R.id.classfifctation_brablecastion_name3);
            this.layout_3 = (RelativeLayout) rootView.findViewById(R.id.layout_3);
            this.classification_brablecase_data_id = (LinearLayout) rootView.findViewById(R.id.classification_brablecase_data_id);
        }

    }

    /**
     * 合并图片地址
     */
    private String mergeImageUri(String uri) {
        StringBuffer imageurlhead = new StringBuffer();
        String iamgeurihead = (String) SharedPreferencesUtils.get(context, "IMAGE_FILE", "");
        imageurlhead.append(iamgeurihead);
        imageurlhead.append(uri);
        return imageurlhead.toString();

    }


}
