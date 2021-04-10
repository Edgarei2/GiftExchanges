package com.taiqiwen.base_framework.ui;

import android.view.View;

import androidx.core.view.ViewPropertyAnimatorListener;

interface VpaListenerAdapter extends ViewPropertyAnimatorListener {
    void onAnimationStart(View view);
    void onAnimationCancel(View view);
    void onAnimationEnd(View view);
}
