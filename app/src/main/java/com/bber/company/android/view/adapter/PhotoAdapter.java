package com.bber.company.android.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.bean.PhotoBean;
import com.bber.company.android.tools.imageload.ImageLoader;
import com.bber.company.android.util.OtherUtils;
import com.bber.company.android.viewmodel.ChatPicViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：图片选择界面GridView适配器
 * <p>
 * Created by Mjj on 2016/12/2.
 */
public class PhotoAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;

    private List<PhotoBean> mDatas;
    //存放已选中的Photo数据
    private List<String> mSelectedPhotos;
    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //照片选择模式，默认单选
    private int mSelectMode = ChatPicViewModel.MODE_SINGLE;
    //图片选择数量
    private int mMaxNum = ChatPicViewModel.DEFAULT_NUM;

    private View.OnClickListener mOnPhotoClick;
    private PhotoClickCallBack mCallBack;

    private int curentNum; // 当前已选择的图片数量

    public PhotoAdapter(Context context, List<PhotoBean> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        int screenWidth = OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4)) / 3;
    }

    public int getCurentNum() {
        return curentNum;
    }

    public void setCurentNum(int curentNum) {
        this.curentNum = curentNum;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) != null && getItem(position).isCamera()) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public PhotoBean getItem(int position) {
        if (mDatas == null || mDatas.size() == 0) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDatas.get(position).getId();
    }

    public void setDatas(List<PhotoBean> mDatas) {
        this.mDatas = mDatas;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
        if (mIsShowCamera) {
            PhotoBean camera = new PhotoBean(null);
            camera.setIsCamera(true);
            mDatas.add(0, camera);
        }
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }

    /**
     * 获取已选中相片
     *
     * @return
     */
    public List<String> getmSelectedPhotos() {
        return mSelectedPhotos;
    }

    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
        if (mSelectMode == ChatPicViewModel.MODE_MULTI) {
            initMultiMode();
        }
    }

    /**
     * 初始化多选模式所需要的参数
     */
    private void initMultiMode() {
        mSelectedPhotos = new ArrayList<>();
        mOnPhotoClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = v.findViewById(R.id.imageview_photo).getTag().toString();
                if (mSelectedPhotos.contains(path)) {
                    v.findViewById(R.id.mask).setVisibility(View.GONE);
                    v.findViewById(R.id.checkmark).setSelected(false);
                    mSelectedPhotos.remove(path);
                } else {
                    if (mSelectedPhotos.size() >= mMaxNum) {
                        Toast.makeText(mContext, R.string.msg_maxi_capacity,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 判断显示总共的数量
                    mSelectedPhotos.add(path);
                    int num = mSelectedPhotos.size() + getCurentNum();
                    Log.e("PhotoAdapter999", "selectPhoto: " + mSelectedPhotos.size() + "  *********  " + getCurentNum());
                    if (num > 9) {
                        mSelectedPhotos.remove(mSelectedPhotos.size() - 1);
                        Toast.makeText(mContext, "图片数量已达上限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    v.findViewById(R.id.mask).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.checkmark).setSelected(true);
                }
                if (mCallBack != null) {
                    mCallBack.onPhotoClick();
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_camera_layout, null);
            convertView.setTag(null);
            //设置高度等于宽度
            GridView.LayoutParams lp = new GridView.LayoutParams(mWidth, mWidth);
            convertView.setLayoutParams(lp);
        } else {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_photo_layout, null);
                holder.photoImageView = convertView.findViewById(R.id.imageview_photo);
                holder.selectView = convertView.findViewById(R.id.checkmark);
                holder.maskView = convertView.findViewById(R.id.mask);
                holder.wrapLayout = convertView.findViewById(R.id.wrap_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.photoImageView.setImageResource(R.mipmap.ic_photo_loading);
            PhotoBean photo = getItem(position);
            if (mSelectMode == ChatPicViewModel.MODE_MULTI) {
                holder.wrapLayout.setOnClickListener(mOnPhotoClick);
                holder.photoImageView.setTag(photo.getPath());
                holder.selectView.setVisibility(View.VISIBLE);
                if (mSelectedPhotos != null && mSelectedPhotos.contains(photo.getPath())) {
                    holder.selectView.setSelected(true);
                    holder.maskView.setVisibility(View.VISIBLE);
                } else {
                    holder.selectView.setSelected(false);
                    holder.maskView.setVisibility(View.GONE);
                }
            } else {
                holder.selectView.setVisibility(View.GONE);
            }
            ImageLoader.getInstance().display(photo.getPath(), holder.photoImageView,
                    mWidth, mWidth);
        }
        return convertView;
    }

    /**
     * 多选时，点击相片的回调接口
     */
    public interface PhotoClickCallBack {
        void onPhotoClick();
    }

    private class ViewHolder {
        private ImageView photoImageView;
        private ImageView selectView;
        private View maskView;
        private FrameLayout wrapLayout;
    }
}
