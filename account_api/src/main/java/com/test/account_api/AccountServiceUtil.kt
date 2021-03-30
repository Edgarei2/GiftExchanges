package com.test.account_api

import com.taiqiwen.base_framework.ServiceManager

object AccountServiceUtil {

    @JvmStatic
    fun getSerVice(): IAccountService {
        return ServiceManager.get().getService(IAccountService::class.java)
            ?: AccountServiceDefault()
    }

}