package com.bitlove.fetlife.legacy.util;

import android.content.Context;
import android.os.Build;

public class ColorUtil {

    public static int retrieverColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            return context.getResources().getColor(color);
        }
    }
}
