package com.bber.company.android.bean;

/**
 * Created by carlo.c on 2018/3/29.
 */

public class PrivilegesReyclerVewData {
    private int image;
    private String text;

    public  PrivilegesReyclerVewData(int image, String text) {

        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
