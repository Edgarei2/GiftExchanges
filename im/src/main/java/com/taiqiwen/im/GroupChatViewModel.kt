package com.taiqiwen.im

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im.network.ImApi
import com.test.account_api.AccountServiceUtil

class GroupChatViewModel : ViewModel()  {

    val fragmentStatus : MutableLiveData<Int> = MutableLiveData(0)

    private val userInfoBuffer : MutableLiveData<HashMap<String, GiftUser>> = MutableLiveData()

    val friendsUidSet : MutableLiveData<MutableSet<String>> = MutableLiveData()

    init {
        ImApi.getFriendsUid(AccountServiceUtil.getSerVice().getCurUserId()) { friendsUidList ->
            if (friendsUidList != null) {
                for (uid in friendsUidList) {
                    friendsUidSet.value?.add(uid)
                }
            }
        }
    }

    fun getUserInfo(userId: String, cb: (GiftUser?) -> Unit) {
        userInfoBuffer.value?.let { buffer ->
            if (buffer.containsKey(userId)) {
                cb.invoke(buffer[userId])
            } else {
                ImApi.getFriendsDetail(userId) { map ->
                    map?.get(userId)?.let { user ->
                        buffer[userId] = user
                        cb.invoke(user)
                    }
                }
            }
        }
    }

    fun getGroupMember(groupObjId: String, cb: (List<GiftUser>?) -> Unit) {
        ImApi.getGroupMember(groupObjId) { map ->
            val list = mutableListOf<GiftUser>()
            if (map != null) {
                for ((k, v) in map) {
                    userInfoBuffer.value?.put(k, v)
                    list.add(v)
                }
            }
            cb.invoke(list)
        }
    }

}