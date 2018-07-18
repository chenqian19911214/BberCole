package com.bber.company.android.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * author zaaach on 2016/1/26.
 */
public class MeasureRecycleView extends RecyclerView {

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    float xDistance = 0;
    float yDistance = 0;
    float xStart = 0;
    float yStart = 0;
    float xEnd = 0;
    float yEnd = 0;
    private Context mContext;

    public MeasureRecycleView(Context context) {

        super(context);
        mContext = context;
    }

    public MeasureRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
