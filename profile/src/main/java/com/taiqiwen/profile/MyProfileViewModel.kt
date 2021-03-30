package com.taiqiwen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.GiftServiceUtil
import com.beyondsw.lib.model.GiftDetailDTO
import com.test.account_api.AccountServiceUtil

class MyProfileViewModel : ViewModel()  {

    private val isLoggedIn: MutableLiveData<Boolean> = MutableLiveData( false)
    private val userName: MutableLiveData<String?> = MutableLiveData()
    private val userAvatarUrl: MutableLiveData<String?> = MutableLiveData()
    private val friends: MutableLiveData<List<String>?> = MutableLiveData()
    private val credit: MutableLiveData<Int?> = MutableLiveData()
    private val collectedGifts: MutableLiveData<List<GiftDetailDTO>?> = MutableLiveData()

    fun getLoginStatus(): LiveData<Boolean> {
        return isLoggedIn
    }

    fun getUserName(): LiveData<String?> {
        return userName
    }

    fun getUserAvatarUrl(): LiveData<String?> {
        return userAvatarUrl
    }

    fun getFriends(): LiveData<List<String>?> {
        return friends
    }

    fun getCredit(): LiveData<Int?> {
        return credit
    }

    fun getCollection(): LiveData<List<GiftDetailDTO>?> {
        return collectedGifts
    }

    fun refreshUserStatus(fetchErrCb: (() -> Unit)?) {
        isLoggedIn.value = AccountServiceUtil.getSerVice().getCurUser() != null
        AccountServiceUtil.getSerVice().getCurUser()?.let {
            userName.value = it.userName
            userAvatarUrl.value = it.avatarUrl
            credit.value = it.curCredit
            ProfileApi.fetchCurUserFriends(it.userId){ success, friendsResponseDTO ->
                if (success) {
                    friends.value = friendsResponseDTO?.friends
                } else {
                    fetchErrCb?.invoke()
                }
            }
            GiftServiceUtil.getSerVice().getCollectedGifts(it.userId) { collection ->
                collectedGifts.value = collection
            }
        }

    }

}