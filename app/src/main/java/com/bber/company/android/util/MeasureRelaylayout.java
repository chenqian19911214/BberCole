package com.bber.company.android.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * author zaaach on 2016/1/26.
 */
public class MeasureRelaylayout extends LinearLayout {

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

    public MeasureRelaylayout(Context context) {

        super(context);
        mContext = context;
    }

    public MeasureRelaylayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureRelaylayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {

            case MotionEvent.ACTION_DOWN:

                xDistance = yDistance = 0f;

                xStart = e.getX();

                yStart = e.getY();

                break;

            case MotionEvent.ACTION_MOVE:

                xEnd = e.getX();

                yEnd = e.getY();

                break;

            default:

                break;

        }

        xDistance = Math.abs(xEnd - xStart);

        yDistance = Math.abs(yEnd - yStart);

        if (xDistance > yDistance)

            return true;  //拦截事件向下分发
        return super.onInterceptTouchEvent(e);
    }

}
