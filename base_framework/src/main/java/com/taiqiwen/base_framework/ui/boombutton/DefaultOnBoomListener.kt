package com.taiqiwen.base_framework.ui.boombutton

import com.nightonke.boommenu.OnBoomListener

abstract class DefaultOnBoomListener: OnBoomListener {

    override fun onBoomDidShow() { }

    override fun onBackgroundClick() { }

    override fun onBoomDidHide() { }

    override fun onBoomWillHide() { }

    override fun onBoomWillShow() { }
}