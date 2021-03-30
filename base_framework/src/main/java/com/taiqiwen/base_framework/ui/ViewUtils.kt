package com.taiqiwen.base_framework.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

object ViewUtils {

    fun getActivity(context: Context?): Activity? {
        var c = context
        while (c != null) {
            c = if (c is Activity) {
                return c
            } else if (c is ContextWrapper) {
                c.baseContext
            } else {
                return null
            }
        }
        return null
    }

}