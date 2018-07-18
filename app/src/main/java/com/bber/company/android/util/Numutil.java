package com.bber.company.android.util;

/**
 * Created by bn on 2017/9/14.
 */

public class Numutil {
    public static String NumberFormat(float f, int m) {
        return String.format("%." + m + "f", f);
    }

    //去掉小数点
    public static String NumberFormata(float f, int m) {
        String s = Float.toString(f);
        s = s.substring(0, s.indexOf('.'));
        return s;
    }

    public static float NumberFormatFloat(float f, int m) {

        String strfloat = NumberFormata(f, m);
        return Float.parseFloat(strfloat);
    }
}
