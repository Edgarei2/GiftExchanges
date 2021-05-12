package com.taiqiwen.base_framework.ui.countdown;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class SysUtils {

    public static int convertSpToPixel(Context context, float sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, metrics));
    }

}
