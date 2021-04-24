package com.taiqiwen.im_api

import com.taiqiwen.base_framework.ServiceManager

object ChatServiceUtil {

    @JvmStatic
    fun getSerVice(): IChatService {
        return ServiceManager.get().getService(IChatService::class.java)
            ?: ChatServiceDefault()
    }

}