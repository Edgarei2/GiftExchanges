package com.test.account_api

import com.taiqiwen.base_framework.model.GiftUser

class AccountServiceDefault: IAccountService {

    override fun checkLogin(userName: String, password: String, cb: ((Boolean) -> Unit)?) { }

    override fun logOut() { }

    override fun getCurUser(): GiftUser? {
        return null
    }

    override fun isLogged(): Boolean = false

    override fun refresh(userId: String?, statusCb: (() -> Unit)?) { }

    override fun setCurUser(giftUser: GiftUser?) { }

    override fun getCurUserId(): String? = null

    override fun updateLastOnLineTime() { }
}