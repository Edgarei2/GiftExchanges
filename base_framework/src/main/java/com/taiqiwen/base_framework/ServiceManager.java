package com.taiqiwen.base_framework;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;

final public class ServiceManager {

    @Nullable
    public <T> T getService(@NotNull Class<T> clazz) {

        for (T t : ServiceLoader.load(clazz)) {
            return t;
        }
        Log.d("ttest", "empty");
        return null;
    }

    public static ServiceManager get() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ServiceManager INSTANCE = new ServiceManager();
    }

}
