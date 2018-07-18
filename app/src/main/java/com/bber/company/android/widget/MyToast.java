package com.bber.company.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.tools.Tools;

import java.lang.reflect.Field;

/**
 * 自定义动画的Toast
 *
 * @author ziyao
 */
public class MyToast extends Toast {

    public MyToast(Context context) {
        super(context);
    }

    /**
     * 调用有动画的Toast
     *
     * @return
     */
    public static Toast makeTextAnim(Context context, int id,
                                     int duration, int styleId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //将布局文件转换成相应的View对象
        View view = inflater.inflate(R.layout.toast_layout_custom, null);
        view.getBackground().setAlpha(200);
        TextView textView = view.findViewById(R.id.text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (MyApplication.screenWidth, Tools.dip2px(context, 45));
        textView.setLayoutParams(params);
        textView.setText(context.getResources().getText(id));
        //实例化一个Toast对象
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(view);

        try {
            Object mTN = null;
            mTN = getField(toast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params2 = (WindowManager.LayoutParams) mParams;
                    params2.windowAnimations = styleId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toast;
    }

    public static Toast makeTextAnim(Context context, String text,
                                     int duration, int styleId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //将布局文件转换成相应的View对象
        View view = inflater.inflate(R.layout.toast_layout_custom, null);
        view.getBackground().setAlpha(200);
        TextView textView = view.findViewById(R.id.text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (MyApplication.screenWidth, Tools.dip2px(context, 45));
        textView.setLayoutParams(params);
        textView.setText(text);
        //实例化一个Toast对象
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(view);

        try {
            Object mTN = null;
            mTN = getField(toast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params2 = (WindowManager.LayoutParams) mParams;
                    params2.windowAnimations = styleId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toast;
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }

}
