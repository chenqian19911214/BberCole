package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.CircleImageView;

/**
 * Created by Administrator on 2015-05-25.
 */
public class IconGridAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater mInflater;
//    private List<ProjectImage> projectImages;
//    private AsyncImageLoader asyncImageLoader;

    public IconGridAdapter(Activity mactivity) {
        activity = mactivity;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        asyncImageLoader = new AsyncImageLoader(activity);
//        projectImages = infos;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 9;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    //根据位置得到视图
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.circle_item, null);
            holder.imageView = convertView.findViewById(R.id.img_list_item);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            int width = getImageWidth();
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width));

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


//        ProjectImage projectImage = projectImages.get(position);
//        String imageSmall = projectImage.getImageSmall();
//        //加载项目图片
//        if (!Tools.isEmpty(imageSmall)) {
//            //预设图片
//            imageView.setImageResource(R.color.dialog_item);
//            //通过tag来防止图片错位
//            asyncImageLoader.loadDrawable(
//                    imageSmall, null, new AsyncImageLoader.ImageCallback() {
//
//                        public void imageLoaded(Bitmap bitmap,
//                                                String imageUrl) {
//                            imageView.setImageBitmap(bitmap);
//                        }
//                    });
//
//        }

        return convertView;
    }

    //计算每个image的宽度
    private int getImageWidth() {
        int dex = Tools.dip2px(activity, 40 * 2 + 10 * 2); //布局左右间距+每张图片间距
        int width = (MyApplication.screenWidth - dex) / 3;//除以列数，得到每张图的宽度

        return width;
    }

    private static class ViewHolder {
        private CircleImageView imageView;
    }
}
