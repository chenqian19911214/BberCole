package com.bber.company.android.view.adapter;

import android.content.Context;

import com.bber.company.android.R;
import com.bber.company.android.view.customcontrolview.DataClass;
import com.ledlau.widgets.HorizontalPageAdapter;

import java.util.List;

/**
 * Created by carlo.c on 2018/3/28.
 */

public class Myadapter extends HorizontalPageAdapter<DataClass> {
    List<DataClass> data;
    private Context mContext;

    public Myadapter(Context context, List<DataClass> data) {
        super(context, data, R.layout.gridviewitem_myviplayout);
        this.mContext = context;
        this.data = data;
    }

    @Override
    public void bindViews(com.ledlau.widgets.ViewHolder viewHolder, DataClass viewHolder2, int i) {

        viewHolder.setText(R.id.gridvie_text_id, viewHolder2.getText()).setImageResource(R.id.gridvie_image_id, viewHolder2.getImage());
    }
}
