package com.taiqiwen.base_framework.ui

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.taiqiwen.base_framework.R

class LoadingDialog(context: Activity)
    : Dialog(context, R.style.commerce_promotion_loading_dialog) {

    private lateinit var rootView: FrameLayout

    private val animationView: ILoadingDialogAnimate = LoadingWidget(context)

    init {
        setOwnerActivity(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        setContentView(R.layout.ec_commerce_loading_layout)
        rootView = findViewById(R.id.loading_layout)
        val loadingView = animationView as? View
        if (loadingView != null) {
            val sideLength = UIUtils.dip2Px(context, VALUE_LAYOUT_SIDE_LENGTH).toInt()
            rootView.addView(loadingView, FrameLayout.LayoutParams(sideLength,
                sideLength, Gravity.CENTER))
        }
    }

    override fun show() {
        if (isShowing || ownerActivity?.isFinishing != false) {
            return
        }
        super.show()
        animationView.startAnimation()
    }

    override fun dismiss() {
        if (ownerActivity?.isFinishing != false) {
            return
        }
        try {
            super.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        animationView.stopAnimation()
    }

    companion object {
        const val VALUE_LAYOUT_SIDE_LENGTH = 36f
    }
}