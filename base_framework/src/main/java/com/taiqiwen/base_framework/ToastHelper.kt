package com.taiqiwen.base_framework

import android.view.Gravity
import com.taiqiwen.base_framework.ui.toast.ToastUtils

object ToastHelper {

    @JvmStatic
    fun showToast(toastText: String) {
        ToastUtils.setView(R.layout.toast_custom_view)
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
        ToastUtils.show(toastText)
    }

}