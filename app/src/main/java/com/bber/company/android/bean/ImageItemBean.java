package com.bber.company.android.bean;

import android.graphics.Bitmap;

import com.bber.company.android.util.Bimp;

import java.io.IOException;
import java.io.Serializable;

/**
 * Description：单个图片实体类
 * <p>
 * Created by Mjj on 2016/12/2.
 */

public class ImageItemBean implements Serializable {

    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public boolean isSelected = false;
    private Bitmap bitmap;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            try {
                bitmap = Bimp.revitionImageSize(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
