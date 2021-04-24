package com.test.account_api

import com.taiqiwen.base_framework.model.GiftUser

interface IAccountService {

    fun checkLogin(userName: String, password: String, cb: ((Boolean) -> Unit)?)

    fun logOut()

    fun getCurUser(): GiftUser?

    fun getCurUserId(): String?

    fun isLogged(): Boolean

    fun refresh(userId: String?, statusCb: (() -> Unit)?)

    fun setCurUser(giftUser: GiftUser?)

    fun updateLastOnLineTime()

}