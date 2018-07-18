package com.bber.company.android.view.customcontrolview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by carlo.c on 2018/3/28.
 */

public class ListViewPay extends ListView {
    public ListViewPay(Context context) {
        super(context);
    }

    public ListViewPay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewPay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

