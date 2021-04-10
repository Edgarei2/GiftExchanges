package com.taiqiwen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.model.GiftSentStatusDTO
import com.beyondsw.lib.model.GiftSentStatusDetailDTO
import com.taiqiwen.base_framework.GiftApplication
import com.taiqiwen.profile_api.model.GiftItem
import com.test.account_api.AccountServiceUtil

class GiftSessionViewModel : ViewModel() {

    private val giftDataList: MutableLiveData<List<GiftSentStatusDTO>> = MutableLiveData()

    private val isLoggedIn: MutableLiveData<Boolean> = MutableLiveData( false)

    private val unCheckedGiftsNumber: MutableLiveData<Int> = MutableLiveData( )

    fun getLoginStatus(): LiveData<Boolean> {
        return isLoggedIn
    }

    fun setLoginStatus(loggedIn: Boolean) {
        isLoggedIn.value = loggedIn
    }

    fun getGiftList(): LiveData<List<GiftSentStatusDTO>> {
        return giftDataList
    }

    fun getCheckedGiftsNumber(): LiveData<Int> {
        return unCheckedGiftsNumber
    }

    fun updateCheckedGiftsNumber(number: Int) {
        unCheckedGiftsNumber.value = number
    }

    fun reduceGiftsNumberBy1() {
        val oldState = unCheckedGiftsNumber.value
        if (oldState != null) {
            unCheckedGiftsNumber.value = (oldState - 1)
        }
    }

    fun refreshUserStatus() {
        isLoggedIn.value = AccountServiceUtil.getSerVice().getCurUser() != null
    }

    fun fetchGiftList(userId: String?, cb: (Boolean, Int) -> Unit) {
        ProfileApi.fetchUnCheckedGifts(userId) { uncheckedGiftList ->
            if (uncheckedGiftList != null) {
                giftDataList.value = uncheckedGiftList
                cb.invoke(true, uncheckedGiftList.size)
            } else {
                cb.invoke(false, 0)
            }
        }
    }

    fun checkoutAllGifts(exchangeObjIdList: List<String?>,
                         giftObjIdList: List<String?>,
                         owner: String?,
                         cb: (Boolean) -> Unit) {
        val exchangeObjIds = exchangeObjIdList.joinToString(",")
        val giftObjIds = giftObjIdList.joinToString(",")
        ProfileApi.checkoutAllGifts(exchangeObjIds, giftObjIds, owner) { result ->
            if (result == "1") {
                cb.invoke(true)
            } else {
                cb.invoke(false)
            }
        }
    }

    fun declineAllGifts(exchangeObjIdList: List<String?>, cb: (Boolean) -> Unit) {
        val exchangeObjIds = exchangeObjIdList.joinToString(",")
        ProfileApi.declineAllGifts(exchangeObjIds) { result ->
            if (result == "1") {
                cb.invoke(true)
            } else {
                cb.invoke(false)
            }
        }
    }
}