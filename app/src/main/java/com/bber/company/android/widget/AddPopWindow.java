package com.bber.company.android.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bber.company.android.R;

/**
 * Created by bn on 2017/9/27.
 */

public class AddPopWindow extends PopupWindow {
    private View conentView;
    private Context mContext;

    public AddPopWindow(final Activity context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.add_popup_dialog, null);
        int h = conentView.getMeasuredHeight();
        int w = conentView.getMeasuredWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(w / 2 + 50);
        // 设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimationPreview);
        LinearLayout addTaskLayout = conentView
                .findViewById(R.id.pop_layout);
//        LinearLayout teamMemberLayout = (LinearLayout) conentView
//                .findViewById(R.id.team_member_layout);
        addTaskLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AddPopWindow.this.dismiss();
            }
        });
    }

//    /**
//     * 显示popupWindow
//     *
//     * @param parent
//     */
//    public void showPopupWindow(View parent) {
//        if (!this.isShowing()) {
//            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent);
//        } else {
//            this.dismiss();
//        }
//    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }
        super.showAsDropDown(anchor);
    }
}
