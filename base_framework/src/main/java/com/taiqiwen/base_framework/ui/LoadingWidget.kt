package com.taiqiwen.base_framework.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import com.taiqiwen.base_framework.R

class LoadingWidget(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), ILoadingDialogAnimate {

    init {
        setBackgroundResource(R.drawable.ic_loading)
    }

    override fun startAnimation() {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.loading_dialog_animation))
    }

    override fun stopAnimation() {
        clearAnimation()
    }

}

interface ILoadingDialogAnimate {

    fun startAnimation()

    fun stopAnimation()

}