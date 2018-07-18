package com.bber.company.android.tools;

/**
 * Created by bn on 2017/5/16.
 */

public class NoDoubleUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();

        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            lastClickTime = time;
            return false;
        }
        return true;
    }
}
