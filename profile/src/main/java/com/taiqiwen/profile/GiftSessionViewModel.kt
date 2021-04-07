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

    fun getLoginStatus(): LiveData<Boolean> {
        return isLoggedIn
    }

    fun getGiftList(): LiveData<List<GiftSentStatusDTO>> {
        return giftDataList
    }

    fun refreshUserStatus() {
        isLoggedIn.value = AccountServiceUtil.getSerVice().getCurUser() != null
    }

    fun fetchGiftList(userId: String?, cb: (Boolean) -> Unit) {
        ProfileApi.fetchUnCheckedGifts(userId) { uncheckedGiftList ->
            if (uncheckedGiftList != null) {
                giftDataList.value = uncheckedGiftList
                cb.invoke(true)
            } else {
                cb.invoke(false)
            }
        }
    }
}