package com.bber.company.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2015-08-07.
 */
public class NotifyingScrollView extends ScrollView {

    private Context mContext;
    private OnScrollChangedListener mOnScrollChangedListener;

    public NotifyingScrollView(Context context) {
        super(context);
        this.mContext = context;
    }

    public NotifyingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public NotifyingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }

    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }


    /**阻尼**/
    /**
     * Finalize inflating a view from XML. This is called as the last phase of
     * inflation, after all child views have been added.
     */

}
