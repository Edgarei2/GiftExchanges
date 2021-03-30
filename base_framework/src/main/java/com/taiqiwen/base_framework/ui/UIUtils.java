package com.taiqiwen.base_framework.ui;


import android.content.Context;
import android.util.TypedValue;

public class UIUtils {

    private static final float FLOAT_BIAS = 0.5f;

    public static float sp2px(Context context, float sp) {
        if (context != null) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        }
        return 0;
    }


    public static float dip2Px(Context context, float dipValue) {
        if (context != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return dipValue * scale + FLOAT_BIAS;
        }
        return 0;
    }

    public static int px2dip(Context context, float pxValue) {
        if (context != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + FLOAT_BIAS);
        }
        return 0;
    }
}
