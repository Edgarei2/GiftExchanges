package com.taiqiwen.base_framework.ui

import android.content.Context
import android.widget.FrameLayout

class LoadingLayout(context: Context): FrameLayout(context) {

    init {
        addView(LoadingWidget(context))
    }

}