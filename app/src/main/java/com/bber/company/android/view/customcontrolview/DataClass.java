package com.bber.company.android.view.customcontrolview;

/**
 * Created by carlo.c on 2018/3/28.
 */

public class DataClass {

    private String text;
    private int image;


    public DataClass(int image, String text) {
        this.text = text;
        this.image = image;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
