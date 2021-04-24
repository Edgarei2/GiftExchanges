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
    private val groupList: MutableLiveData<List<Group>> = MutableLiveData()
    private val sessionChangeType: MutableLiveData<Int> = MutableLiveData()
    private val channelObjIdMap: MutableLiveData<Map<String, String>> = MutableLiveData()

    fun getLoginStatus(): LiveData<Boolean> = isLoggedIn

    fun setLoginStatus(loggedIn: Boolean) {
        isLoggedIn.value = loggedIn
    }

    fun setChangeType(type: Int) {
        sessionChangeType.value = type
    }

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
/*        val user2 = GiftUser("331e34862d", "2", "user2", "abc", "", 43, "https://c-ssl.duitang.com/uploads/blog/202012/01/20201201143923_6333b.thumb.1000_0.png")
        val user3 = GiftUser("FMoTGGGL", "3", "user3", "abc", "", 30, "https://pic.qiushibaike.com/system/avtnew/1112/11125684/medium/20170416133732.JPEG")
        val user4 = GiftUser("T8NdFFFM", "4", "user4", "abc", "", 25, "http://p5.itc.cn/images03/20200517/a128b02d7fd348ddbebf2bd6b25a4837.jpeg")

        val list = listOf(
            Session(user4, "你妈死了！", "individual", 2)
        )*/
        sessionList.value = context ?.let {
            IMLocalStorageUtil.loadSessionState(it)
        } ?: emptyList()

    }

    fun getGroupList(): LiveData<List<Group>>  = groupList

    fun initGroupList(){
        val url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fku.90sjimg.com%2Felement_origin_min_pic%2F01%2F30%2F84%2F90573b26c0a7e16.jpg&refer=http%3A%2F%2Fku.90sjimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621679496&t=6155552f8611719368fb308cf284356d"
        groupList.value = listOf(
            Group("nmsl1", url, false),
            Group("nmsl2", url, false),
            Group("nmsl3", url, false),
            Group("nmsl4", url, false),
            Group("nmsl5", url, false),
            Group("nmsl6", url, false),
            Group("nmsl7", url, false)
        )
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

    companion object {

        const val TYPE_ADD_LAST = 1

        fun getChannelId(curUserId: String?, uid: String?) : String {
            return if (curUserId == null || uid == null) ""
            else if (curUserId < uid) "${curUserId}_${uid}" else "${uid}_${curUserId}"
        }

    }
}