package com.bber.company.android.util;

import android.util.Log;

/**
 * Created by carlo.c on 2018/3/13.
 */

public class ChenQianLog {

    private static boolean isOpen = true;

    public static void i(String massge) {

        if (isOpen) {

            Log.i("chenqian", "" + massge);
        }

    }
}
