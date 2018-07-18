package com.bber.company.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.bber.company.android.R;

public class NoticeMFE extends MarqueeFactory<TextView, String> {
    private LayoutInflater inflater;

    public NoticeMFE(Context mContext) {
        super(mContext);
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public TextView generateMarqueeItemView(String data) {
        TextView mView = (TextView) inflater.inflate(R.layout.notice_item, null);
        mView.setText(data);
        return mView;
    }
}