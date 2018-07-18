package com.bber.company.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * author zaaach on 2016/1/26.
 */
public class StringUtils {
    /**
     * 提取出城市或者县
     *
     * @param city
     * @param district
     * @return
     */
    public static String extractLocation(final String city, final String district) {
        return district.contains("县") ? district.substring(0, district.length() - 1) : city.substring(0, city.length() - 1);
    }

    public static String doubleChangeIne(Double dou) {
        // DecimalFormat df = new DecimalFormat("######0");
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(dou);
    }

    public static List<String> changeString(String limit) {
        List<String> data = new ArrayList<>();
        for (int i = limit.length(); i > 0; i--) {
            data.add(limit.substring(limit.length(), limit.length() - 1));
        }

        return data;
    }


    public static String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "1.0";
        }
    }

    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public static String NumberFormat(float f, int m) {
        return String.format("%." + m + "f", f);
    }

    public static float NumberFormatFloat(float f, int m) {
        String strfloat = NumberFormat(f, m);
        return Float.parseFloat(strfloat);
    }

    private static Float[] splitnum(float num, int count) {
        Random random = new Random();
        float numtemp = num;
        float sum = 0;
        LinkedList<Float> nums = new LinkedList<Float>();
        nums.add(0f);
        while (true) {
            float nextFloat = NumberFormatFloat(
                    (random.nextFloat() * num * 2f) / (float) count,
                    2);
            System.out.println("next:" + nextFloat);
            if (numtemp - nextFloat >= 0) {
                sum = NumberFormatFloat(sum + nextFloat, 2);
                nums.add(sum);
                numtemp -= nextFloat;
            } else {
                nums.add(num);
                return nums.toArray(new Float[0]);
            }
        }
    }

    public String Emoji2code(String str) {


        StringBuilder buf = null;
        long w1;
        long w2;
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char codePoint = str.charAt(i);
            char codePoint2 = str.charAt(i);
            if (i < len - 1) {
                codePoint2 = str.charAt(i + 1);
            }

            if (buf == null) {
                buf = new StringBuilder(str.length());
            }

            buf.append(Integer.toHexString(codePoint));
        }

        if (buf == null) {
            return "";
        } else {
            return buf.toString();
        }


    }


}
