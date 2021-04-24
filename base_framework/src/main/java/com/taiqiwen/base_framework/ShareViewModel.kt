package com.taiqiwen.base_framework

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taiqiwen.base_framework.model.GiftUser

class ShareViewModel : ViewModel() {

    val friendsUidList: MutableLiveData<List<String>?> = MutableLiveData()

    val sessionExtra: MutableLiveData<GiftUser> = MutableLiveData()

    val channelObjIdMap:  MutableLiveData<Map<String, String>> = MutableLiveData()

    val friendsDetailList: MutableLiveData<Map<String, GiftUser>> = MutableLiveData()

    val msgUnreadTotal: MutableLiveData<Int> = MutableLiveData()

    fun addUnRead(unread: Int) {
        val oldState = msgUnreadTotal.value ?: 0
        val newState = oldState + unread
        msgUnreadTotal.value = newState
    }
}