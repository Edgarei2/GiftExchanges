package com.taiqiwen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.GiftServiceUtil
import com.beyondsw.lib.model.GiftDetailDTO
import com.beyondsw.lib.model.GiftSentStatusDetailDTO
import com.taiqiwen.base_framework.model.GiftUser
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

    fun refreshUserStatus(fetchCb: ((Boolean) -> Unit)?) {
        isLoggedIn.value = AccountServiceUtil.getSerVice().getCurUser() != null
        AccountServiceUtil.getSerVice().getCurUser()?.let {
            userName.value = it.userName
            userAvatarUrl.value = it.avatarUrl
            credit.value = it.curCredit
            ProfileApi.fetchCurUserFriends(it.userId){ success, friendsResponseDTO ->
                if (success) {
                    friends.value = friendsResponseDTO?.friends
                    fetchCb?.invoke(true)
                } else {
                    fetchCb?.invoke(false)
                }
            }
            GiftServiceUtil.getSerVice().getCollectedGifts(it.userId) { collection ->
                collectedGifts.value = collection
            }
        }
    }

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

    fun fetchSentGiftsInfo(userId: String?, cb: ((List<GiftSentStatusDetailDTO>?) -> Unit)?) {
        ProfileApi.fetchGiftSentInfo(userId, cb)
    }

    fun fetchCertainFriendName(userId: String?, cb: ((String) -> Unit)?){
        ProfileApi.fetchCurUserFriendsDetail(userId) { friendsDetailResponseDTO ->
            val detailMap = friendsDetailResponseDTO?.friendsDetail
            val list = mutableListOf<GiftUser>()
            if (detailMap != null) {
                for (detail in detailMap.values) {
                    list.add(detail)
                }
            }
            if (list.isNotEmpty()) {
                cb?.invoke(list[0].userName)
            } else {
                cb?.invoke("对方")
            }
        }
    }

}