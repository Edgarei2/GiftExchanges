package com.taiqiwen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.base_framework.isNotNullOrEmpty
import com.taiqiwen.base_framework.model.GiftUser

class SendGiftViewModel : ViewModel() {

    private val words: MutableLiveData<String?> = MutableLiveData()
    private val receiver: MutableLiveData<GiftUser?> = MutableLiveData()
    private val gift: MutableLiveData<GiftDetailDTO?> = MutableLiveData()
    private val ok: MutableLiveData<Boolean> = MutableLiveData()

    init {
        ok.value = false
    }

    fun updateWords(word: String?) {
        words.value = word
        check()
    }

    fun updateReceiver(user: GiftUser?) {
        receiver.value = user
        check()
    }

    fun updateGift(g: GiftDetailDTO?) {
        gift.value = g
        check()
    }

    fun getReceiver(): LiveData<GiftUser?> = receiver

    fun getGift(): LiveData<GiftDetailDTO?> = gift

    fun getSendStatus(): LiveData<Boolean> = ok

    fun fetchFriendsDetail(userIdList: List<String>, cb: ((List<GiftUser>) -> Unit)?) {
        val userIds = userIdList.joinToString(",")
        ProfileApi.fetchCurUserFriendsDetail(userIds) { friendsDetailResponseDTO ->
            val detailMap = friendsDetailResponseDTO?.friendsDetail
            val list = mutableListOf<GiftUser>()
            if (detailMap != null) {
                for (detail in detailMap.values) {
                    list.add(detail)
                }
            }
            cb?.invoke(list)
        }
    }

    fun fetchGiftsDetail(userId: String?, cb: (List<GiftDetailDTO>?) -> Unit) {
        ProfileApi.fetchOwnedGiftsWithoutSenderInfo(userId, cb)
    }

    fun sendGift(userId: String?, cb: (Boolean) -> Unit) {
        ProfileApi.sendGift(userId, receiver.value?.userId, gift.value?.id, words.value, cb)
    }

    private fun check() {
        ok.value = (words.value.isNotNullOrEmpty() && receiver.value != null && gift.value != null)
    }

}