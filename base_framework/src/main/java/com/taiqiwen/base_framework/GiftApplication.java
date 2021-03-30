package com.taiqiwen.base_framework;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.taiqiwen.base_framework.model.GiftUser;

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
