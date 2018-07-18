package com.bber.company.android.util.country;

import com.bber.company.android.bean.livebroadcast.BroadcaseSearchSingleBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlo.c on 2018/4/17.
 */

public class ListCache<T> implements Serializable {
    // 序列化UID 当需要反序列化的时候,此UID必须要.
    private static final long serialVersionUID = -3276096981990292013L;
    // 对象列表(用于存储需要缓存下来的列表)
    private ArrayList<T> objList;

    public ArrayList<T> getObjList() {
        return objList;
    }

    public void setObjList(List<T> objList) {
        this.objList = (ArrayList<T>) objList;
    }
}
