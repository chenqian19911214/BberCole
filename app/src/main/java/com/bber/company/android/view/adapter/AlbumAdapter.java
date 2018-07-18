package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.customcontrolview.SquareLayout;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;
import java.util.logging.Handler;

/**
 * 编辑主播相册
 * Created by carlo.c on 2018/4/14.
 */

public class AlbumAdapter extends BaseAdapter {

    private Activity activity;
    private List<File> lists;
    private LayoutInflater mInflater;
    private OnClickListenerImge onClickListenerImge;

    public AlbumAdapter(Activity mactivity, List<File> infos) {
        activity = mactivity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lists = infos;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.album_grid_item, null);
            holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.selected_icon = (ImageView) convertView.findViewById(R.id.selected_icon);
            holder.view_image = (SquareLayout) convertView.findViewById(R.id.view_image);
            holder.view_add = (SquareLayout) convertView.findViewById(R.id.view_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File path = lists.get(position);
        holder.selected_icon.setVisibility(View.VISIBLE);

        //为删除小按钮 设置监听
        holder.selected_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onClickListenerImge != null) {
                    onClickListenerImge.onClick(view, position,lists.get(position));
                }
            }
        });
        if (position == lists.size() - 1) {
            if (position == 8) {
                if (path == null) {
                    holder.view_image.setVisibility(View.GONE);
                    holder.view_add.setVisibility(View.VISIBLE);
                } else {
                    holder.view_add.setVisibility(View.GONE);
                    holder.view_image.setVisibility(View.VISIBLE);
                }
            } else {
                holder.view_image.setVisibility(View.GONE);//11111111
                holder.view_add.setVisibility(View.VISIBLE);
                // holder.selected_icon.setVisibility(View.VISIBLE);
            }
        } else {
            holder.view_add.setVisibility(View.GONE);//222222
            holder.view_image.setVisibility(View.VISIBLE);
//            holder.image.setImageResource(R.drawable.test);
        }


        if (path != null) {
            Log.e("eeeeeeeeeeeeeee", "path:" + path + "--position:" + position);
            holder.image.setTag(path);
            // 通过 tag 来防止图片错位
            if (holder.image.getTag() != null && holder.image.getTag().equals(path)) {

                Bitmap bitmap = BitmapFactory.decodeFile(path.getPath(), getBitmapOption(2));
                holder.image.setImageBitmap(bitmap);
              /*  try {
                  *//*  Uri uri = Uri.parse((String) path);//IMAGE_FILE
                    String imageurltou = (String) SharedPreferencesUtils.get(activity,"IMAGE_FILE","");
                    String uitt= imageurltou+uri;
                    holder.image.setImageURI(uitt);*//*
                } catch (Exception e) {

                  //  Bitmap bitmap =  path;
                  //  holder.image.setImageBitmap(bitmap);
                }*/

            }
        }

        return convertView;
    }

    public static class ViewHolder {
        private SimpleDraweeView image;
        public ImageView selected_icon;
        public SquareLayout view_image, view_add;

    }

    public void setOnClickListenerImge(OnClickListenerImge onClickListenerImge) {

        this.onClickListenerImge = onClickListenerImge;
    }

    public interface OnClickListenerImge {

        void onClick(View view, int position,File file);
    }
}
