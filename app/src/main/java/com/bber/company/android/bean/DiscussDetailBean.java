package com.bber.company.android.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import java.io.Serializable;

public class DiscussDetailBean extends BaseObservable implements Serializable {
    private int id;
    private String detail;
    private String source;
    private String talkPictureId;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
        notifyPropertyChanged(BR.detail);
    }

    @Bindable
    public String getSource() {

        return source;
    }

    public void setSource(String source) {
        this.source = source;
        notifyPropertyChanged(BR.source);
    }

    @Bindable
    public String getTalkPictureId() {
        return talkPictureId;
    }

    public void setTalkPictureId(String talkPictureId) {
        this.talkPictureId = talkPictureId;
        notifyPropertyChanged(BR.talkPictureId);
    }

    @Override
    public String toString() {
        return "DiscussDetailBean{" +
                "id=" + id +
                ", detail='" + detail + '\'' +
                ", source='" + source + '\'' +
                ", talkPictureId='" + talkPictureId + '\'' +
                '}';
    }
}



