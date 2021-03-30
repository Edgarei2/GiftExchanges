package com.beyondsw.lib

import com.taiqiwen.base_framework.ServiceManager

object GiftServiceUtil {

    @JvmStatic
    fun getSerVice(): IGiftService {
        return ServiceManager.get().getService(IGiftService::class.java)
            ?: GiftServiceDefault()
    }
}