package com.bber.company.android.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.PhotoFolderBean;
import com.bber.company.android.tools.imageload.ImageLoader;
import com.bber.company.android.util.OtherUtils;

import java.util.List;

/**
 * Description: 相册目录列表适配器
 * <p>
 * Created by Mjj on 2016/12/2.
 */
public class FolderAdapter extends BaseAdapter {

    List<PhotoFolderBean> mDatas;
    Context mContext;
    int mWidth;

    public FolderAdapter(Context context, List<PhotoFolderBean> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        mWidth = OtherUtils.dip2px(context, 90);
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public PhotoFolderBean getItem(int position) {
        if (mDatas == null || mDatas.size() == 0) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_folder_layout, null);
            holder.photoIV = convertView.findViewById(R.id.imageview_folder_img);
            holder.folderNameTV = convertView.findViewById(R.id.textview_folder_name);
            holder.photoNumTV = convertView.findViewById(R.id.textview_photo_num);
            holder.selectIV = convertView.findViewById(R.id.imageview_folder_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PhotoFolderBean folder = getItem(position);
        if (folder == null) {
            return convertView;
        }
        if (folder.getPhotoList() == null || folder.getPhotoList().size() == 0) {
            return convertView;
        }
        holder.selectIV.setVisibility(View.GONE);
        holder.photoIV.setImageResource(R.mipmap.ic_photo_loading);
        if (folder.isSelected()) {
            holder.selectIV.setVisibility(View.VISIBLE);
        }
        holder.folderNameTV.setText(folder.getName());
        holder.photoNumTV.setText(folder.getPhotoList().size() + "张");
        ImageLoader.getInstance().display(folder.getPhotoList().get(0).getPath(), holder.photoIV,
                mWidth, mWidth);
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoIV;
        private TextView folderNameTV;
        private TextView photoNumTV;
        private ImageView selectIV;
    }

}
