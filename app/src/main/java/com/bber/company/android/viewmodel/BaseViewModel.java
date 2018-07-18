package com.bber.company.android.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.graphics.drawable.Drawable;

import com.bber.company.android.app.MyApplication;
import com.bber.company.android.listener.IactionListener;


/**
 * Created by PanHuaChao on 2016/1/14.
 * Description:
 */
public class BaseViewModel extends BaseObservable {
    protected Context gContext;
    protected MyApplication appContext;
    protected Resources res;
    protected IactionListener iactionListener;

    public BaseViewModel(Context _context) {
        gContext = _context;
        appContext = MyApplication.getContext();
        res = gContext.getResources();
    }

    /**
     * 设置action listener
     *
     * @param _listener
     */
    public void setActionListener(IactionListener _listener) {
        iactionListener = _listener;
    }

    /**
     * 通过资源id获取字符串
     *
     * @param resouceId
     * @return
     */
    public String getString(int resouceId) {
        return res.getString(resouceId);
    }

    /**
     * 通过资源id获取drawable
     *
     * @param resourceId
     * @return
     */
    public Drawable getDrawble(int resourceId) {
        return res.getDrawable(resourceId);
    }

}
