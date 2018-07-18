package com.bber.company.android.tools;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;


/**
 * 提示框
 */
public class ToastUtils {

    private static MyApplication myApplication;

    static {
        myApplication = MyApplication.getContext();
    }

    /**
     * showToast(显示提示内容) TODO(根据id与时间显示提示框)
     *
     * @param id   ID
     * @param type 时间类型 0:shor  1:long
     */
    public static void showToast(int id, int type) {
        int time = 0;
        if (type == 0) {
            time = Toast.LENGTH_SHORT;
        } else {
            time = Toast.LENGTH_LONG;
        }
        LayoutInflater inflater = (LayoutInflater) myApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //将布局文件转换成相应的View对象
        View view = inflater.inflate(R.layout.toast_layout, null);
        view.getBackground().setAlpha(160);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(myApplication.getResources().getText(id));
        //实例化一个Toast对象
        Toast toast = new Toast(myApplication);
        toast.setDuration(time);
        toast.setView(view);
        toast.show();
    }

    /**
     * showToast(显示提示内容) TODO(根据id与时间显示提示框)
     *
     * @param str  str
     * @param type 时间类型 0:shor  1:long
     */
    public static void showToastString(String str, int type) {
        int time = 0;
        if (type == 0) {
            time = Toast.LENGTH_SHORT;
        } else {
            time = Toast.LENGTH_LONG;
        }
        LayoutInflater inflater = (LayoutInflater) myApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //将布局文件转换成相应的View对象
        View view = inflater.inflate(R.layout.toast_layout, null);
        view.getBackground().setAlpha(160);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(str);
        //实例化一个Toast对象
        Toast toast = new Toast(myApplication);
        toast.setDuration(time);
        toast.setView(view);
        toast.show();
    }


    /**
     * showToast(显示提示内容) TODO(根据id与时间显示提示框)
     *
     * @param id   ID
     * @param type 时间类型 0:shor  1:long
     */
    public static void showToastCustom(int id, int type) {
        int time = 0;
        if (type == 0) {
            time = Toast.LENGTH_SHORT;
        } else {
            time = Toast.LENGTH_LONG;
        }
        LayoutInflater inflater = (LayoutInflater) myApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //将布局文件转换成相应的View对象
        View view = inflater.inflate(R.layout.toast_layout_custom, null);
        view.getBackground().setAlpha(200);
        TextView textView = view.findViewById(R.id.text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (MyApplication.screenWidth, Tools.dip2px(myApplication, 45));
        textView.setLayoutParams(params);
        textView.setText(myApplication.getResources().getText(id));
        //实例化一个Toast对象
        Toast toast = new Toast(myApplication);
        toast.setDuration(time);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(view);
        toast.show();
    }

    public static void showToast2(int id, int type, boolean mark) {
        int time = 0;
        if (type == 0) {
            time = Toast.LENGTH_SHORT;
        } else {
            time = Toast.LENGTH_LONG;
        }
        String str = null;
        if (mark) {
            str = myApplication.getResources().getString(id);
        } else {
            str = "“" + myApplication.getResources().getString(id) + "”" + "不能为空";
        }

        LayoutInflater inflater = (LayoutInflater) myApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //将布局文件转换成相应的View对象
        View view = inflater.inflate(R.layout.toast_layout, null);
        view.getBackground().setAlpha(200);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(str);
        //实例化一个Toast对象
        Toast toast = new Toast(myApplication);
        toast.setDuration(time);
//        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.setView(view);
        toast.show();
    }

}
