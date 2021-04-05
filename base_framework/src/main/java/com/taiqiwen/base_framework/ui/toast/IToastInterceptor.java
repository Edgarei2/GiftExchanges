package com.taiqiwen.base_framework.ui.toast;

import android.widget.Toast;

public interface IToastInterceptor {

    /**
     * 根据显示的文本决定是否拦截该 Toast
     */
    boolean intercept(Toast toast, CharSequence text);
}