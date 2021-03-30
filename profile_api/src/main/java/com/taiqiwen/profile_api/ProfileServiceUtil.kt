package com.taiqiwen.profile_api

import com.taiqiwen.base_framework.ServiceManager

object ProfileServiceUtil {

    @JvmStatic
    fun getSerVice(): IProfileService {
        return ServiceManager.get().getService(IProfileService::class.java)
                ?: ProfileServiceDefault()
    }

}