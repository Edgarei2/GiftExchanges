package com.taiqiwen.base_framework.ui;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {

    private static int screenWidth = -1;
    private static int screenHeight = -1;

    private static void initScreenSize(Context context) {
        if (context == null) {
            return;
        }
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                if (display == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealSize(size);
                } else {
                    display.getSize(size);
                }
                screenHeight = size.y;
                screenWidth = size.x;
            } else {
                final DisplayMetrics dm = context.getResources().getDisplayMetrics();
                screenHeight = dm.heightPixels;
                screenWidth = dm.widthPixels;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (screenWidth > 0) {
            return screenWidth;
        }
        if (context == null) {
            return 0;
        }
        initScreenSize(context);
        return Math.max(screenWidth, 0);
    }

    /**
     * @param context 这个context应该传递Activity类型的context，如果是其他类型的context可能会导致获取的结果不对
     */
    public static int getScreenHeight(Context context) {
        if (screenHeight > 0) {
            return screenHeight;
        }
        if (context == null) {
            return 0;
        }
        initScreenSize(context);
        return Math.max(screenHeight, 0);
    }

}
