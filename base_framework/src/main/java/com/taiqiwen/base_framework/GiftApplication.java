package com.taiqiwen.base_framework;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.taiqiwen.base_framework.model.GiftUser;
import com.taiqiwen.base_framework.ui.toast.ToastInterceptor;
import com.taiqiwen.base_framework.ui.toast.ToastUtils;
import com.taiqiwen.base_framework.ui.toast.style.ToastBlackStyle;

import androidx.annotation.Nullable;


public class GiftApplication extends Application {

    private static GiftApplication appInstance;

    public static GiftApplication getInstance(){
        return appInstance;
    }

    private GiftUser giftUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        Fresco.initialize(this);

        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor() {
            @Override
            public boolean intercept(Toast toast, CharSequence text) {
                boolean intercept = super.intercept(toast, text);
                if (intercept) {
                    Log.e("Toast", "空 Toast");
                } else {
                    Log.d("Toast", text.toString());
                }
                return intercept;
            }
        });
        // 初始化吐司工具类
        ToastUtils.init(this, new ToastBlackStyle(this));

    }

    @Nullable
    public GiftUser getGiftUser() {
        return giftUser;
    }

    public void setGiftUser(GiftUser user) {
        giftUser = user;
    }

    public void logOut() {
        giftUser = null;
    }

}
