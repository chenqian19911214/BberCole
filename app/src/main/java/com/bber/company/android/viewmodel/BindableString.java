package com.bber.company.android.viewmodel;

import android.databinding.BaseObservable;

/**
 * Created by PanHuaChao on 2016/1/14.
 * Description:
 */
public class BindableString extends BaseObservable {
    private String value = "";

    public String get() {
        return value;
    }

    public void set(String _value) {

        this.value = _value;
        notifyChange();
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
