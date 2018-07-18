package com.bber.company.android.bean;

import java.io.Serializable;

/**
 * 卖家标签
 * Created by Administrator on 2015/11/24 0024.
 */
public class Tag implements Serializable {
    private String content;
    private int count;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
