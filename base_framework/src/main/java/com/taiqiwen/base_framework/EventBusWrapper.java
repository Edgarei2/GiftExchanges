package com.taiqiwen.base_framework;

import org.greenrobot.eventbus.EventBus;

public class EventBusWrapper {
    public static void post(Object obj) {
        EventBus.getDefault().post(obj);
    }

    public static void postSticky(Object obj) {
        EventBus.getDefault().postSticky(obj);
    }

    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    public static void unregister(Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    public static boolean isRegistered(Object subscriber) {
        return EventBus.getDefault().isRegistered(subscriber);
    }

    public static void cancelEventDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

}
