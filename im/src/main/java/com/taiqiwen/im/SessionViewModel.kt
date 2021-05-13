package com.taiqiwen.im

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im.network.ImApi
import com.taiqiwen.im_api.model.Group
import com.taiqiwen.im_api.model.Session
import com.test.account_api.AccountServiceUtil

class SessionViewModel : ViewModel() {

    private val isLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    private val sessionList: MutableLiveData<List<Session>> = MutableLiveData(emptyList())
    private val groupList: MutableLiveData<List<Group>> = MutableLiveData(emptyList())
    private val sessionChangeType: MutableLiveData<Int> = MutableLiveData()
    private val channelObjIdMap: MutableLiveData<Map<String, String>> = MutableLiveData()
    // 接受离线消息
    private val conditionSatisfied: MutableLiveData<Int> = MutableLiveData()
    val unReadMsgMap: MutableLiveData<Map<String, List<String>>> = MutableLiveData()

    fun getLoginStatus(): LiveData<Boolean> = isLoggedIn

    fun setLoginStatus(loggedIn: Boolean) {
        isLoggedIn.value = loggedIn
    }

    fun setChangeType(type: Int) {
        sessionChangeType.value = type
    }

    fun addCondition() {
        val oldState = conditionSatisfied.value ?: 0
        val newState = oldState + 1
        conditionSatisfied.value = newState
    }

    fun getConditionSatisfied(): LiveData<Int> = conditionSatisfied

    fun updateChannelObjIdList(uidList: List<String>) {
        val curUserId = AccountServiceUtil.getSerVice().getCurUserId() ?: return
        val channelIdList = mutableListOf<String>()
        for (uid in uidList) {
            channelIdList.add(getChannelId(curUserId, uid))
        }
        val channelIds = channelIdList.joinToString(",")
        ImApi.getChannelObjIdList(channelIds) {  map ->
            channelObjIdMap.value = map
        }
    }

    fun getChannelObjIdMap() : LiveData<Map<String, String>> = channelObjIdMap

    fun getChangeType(): Int? = sessionChangeType.value

    fun getSessionList(): LiveData<List<Session>>  = sessionList

    fun initSessionList(context: Context?) {
        sessionList.value = context ?.let {
            IMLocalStorageUtil.loadSessionState(it)
        } ?: emptyList()

    }

    fun getGroupList(): LiveData<List<Group>>  = groupList

    fun initGroupList(){
        ImApi.getGroupInfo(AccountServiceUtil.getSerVice().getCurUserId()) { list ->
            groupList.value = list
        }
    }

    fun addNewSession(user: GiftUser?) {
        if (user == null) return
        val oldSessionList = sessionList.value
        for (session in oldSessionList!!) {
            if (user.objId == session.user?.objId) {
                return
            }
        }
        val newSessionList = oldSessionList.toMutableList()
        newSessionList.add(Session(user, "", Session.TYPE_INDIVIDUAL_STR, 0))
        sessionChangeType.value = TYPE_ADD_LAST
        sessionList.value = newSessionList
    }

    fun getUnReadMessage() {
        val curUserId = AccountServiceUtil.getSerVice().getCurUserId()
        ImApi.getUnReadMessage(curUserId) { map ->
            unReadMsgMap.value = map
            addCondition()
        }
    }

    companion object {

        const val TYPE_ADD_LAST = 1

        fun getChannelId(curUserId: String?, uid: String?) : String {
            return if (curUserId == null || uid == null) ""
            else if (curUserId < uid) "${curUserId}_${uid}" else "${uid}_${curUserId}"
        }

    }
}