package com.bber.company.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.bber.company.android.R;

public class NoticeMF_ extends MarqueeFactory<TextView, String> {
    private LayoutInflater inflater;

    public NoticeMF_(Context mContext) {
        super(mContext);
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public TextView generateMarqueeItemView(String data) {
        TextView mView = (TextView) inflater.inflate(R.layout.notice_2item, null);
        mView.setText(data);
        return mView;
    }
}