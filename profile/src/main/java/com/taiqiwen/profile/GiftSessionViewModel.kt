package com.taiqiwen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taiqiwen.base_framework.GiftApplication
import com.taiqiwen.profile_api.model.GiftItem
import com.test.account_api.AccountServiceUtil

class GiftSessionViewModel : ViewModel() {

    private val giftDataList: MutableLiveData<MutableList<GiftItem>> = MutableLiveData()

    private val isLoggedIn: MutableLiveData<Boolean> = MutableLiveData( false)

    init {
        fetchGiftList()
    }

    fun getLoginStatus(): LiveData<Boolean> {
        return isLoggedIn
    }
    fun getGiftList(): LiveData<MutableList<GiftItem>> {
        return giftDataList
    }

    fun refreshUserStatus() {
        isLoggedIn.value = AccountServiceUtil.getSerVice().getCurUser() != null
    }

    private fun fetchGiftList() {
        giftDataList.value = mutableListOf(GiftItem("小明", "这里有一些礼物赠语", "礼物A", "https", 12),
                GiftItem("小王", "这里有一些礼物赠语这里有一些礼物赠语", "礼物B", "https", 167),
                GiftItem("小李", "这里有一些礼物赠语这里有一些礼物赠语这里有一些礼物赠语", "礼物C", "https", 67),
                GiftItem("小戴", "这里有一些礼物赠语这里有一些礼物赠语这里有一些礼物赠语", "礼物D", "https", 43),
                GiftItem("小刘", "这里有一些礼物赠语", "礼物E", "https", 27),
                GiftItem("小朱", "这里有一些礼物赠语这里有一些礼物赠语", "礼物F", "https", 87)
        )
    }
}