package com.test.account

import com.taiqiwen.base_framework.GiftApplication
import com.taiqiwen.base_framework.model.GiftUser
import com.test.account_api.IAccountService


class AccountServiceImpl: IAccountService {

    override fun checkLogin(userName: String, password: String, cb: ((Boolean) -> Unit)?) {
        AccountApi.checkLogin(userName, password, cb) { user ->
            if (user != null) {
                GiftApplication.getInstance().giftUser = user
            }
        }
    }

    override fun logOut() {
        GiftApplication.getInstance().logOut()
    }

    override fun getCurUser(): GiftUser? {
        return GiftApplication.getInstance().giftUser
    }

    override fun isLogged(): Boolean {
        return GiftApplication.getInstance().giftUser != null
    }

    override fun refresh(userId: String?, statusCb: (() -> Unit)?) {
        AccountApi.refreshCurUser(userId) { giftUser ->
            if (giftUser != null) {
                GiftApplication.getInstance().giftUser = giftUser
            }
            statusCb?.invoke()
        }
    }
}