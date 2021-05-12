package com.taiqiwen.im

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobRealTimeData
import cn.bmob.v3.listener.ValueEventListener
import com.taiqiwen.base_framework.EventBusWrapper
import com.taiqiwen.base_framework.ShareViewModel
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.event.NewMessage
import com.taiqiwen.im_api.event.NewMessageEvent
import com.taiqiwen.im_api.event.ReadMessageEvent
import com.test.account_api.AccountServiceUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class SessionFragment : Fragment() {

    private lateinit var viewModel: SessionViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var recyclerView: RecyclerView
    private var longLinkEstablished = false
    private var adapter: SessionAdapter? = null
    private val bmobRealTimeData = BmobRealTimeData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        EventBusWrapper.register(this)
        viewModel = ViewModelProvider(this).get(SessionViewModel::class.java)
        viewModel.setLoginStatus(AccountServiceUtil.getSerVice().isLogged())
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.session)
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.initSessionList(context)
        viewModel.initGroupList()
        shareViewModel.friendsDetailList.observe(viewLifecycleOwner, Observer {
            viewModel.addCondition()
        })
        val sessionList = viewModel.getSessionList().value ?: emptyList()
        val groupList = viewModel.getGroupList().value ?: emptyList()
        adapter = context?.let { SessionAdapter(it, sessionList, groupList, emptyMap()) }
        recyclerView.adapter = adapter
        viewModel.getLoginStatus().observe(viewLifecycleOwner, Observer {  isLogged ->
            if (isLogged) {
                recyclerView.visibility = View.VISIBLE
                view.findViewById<View>(R.id.account_status_hint).visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                view.findViewById<View>(R.id.account_status_hint).visibility = View.VISIBLE
            }
        })
        viewModel.getConditionSatisfied().observe(viewLifecycleOwner, Observer {  conditions ->
            if (conditions == 2) {
                logNewMessages(viewModel.unReadMsgMap.value, shareViewModel.friendsDetailList.value)
            }
        })
        shareViewModel.sessionExtra.observe(viewLifecycleOwner, Observer {  user ->
            viewModel.addNewSession(user)
        })
        shareViewModel.friendsUidList.observe(viewLifecycleOwner, Observer { userIdList ->
            if (userIdList == null) return@Observer
            viewModel.updateChannelObjIdList(userIdList)
        })
        viewModel.getUnReadMessage()

        viewModel.getSessionList().observe(viewLifecycleOwner, Observer { list ->
            var unread = 0
            for (session in list) {
                unread += session.unRead
            }
            shareViewModel.addUnRead(unread)
            adapter?.updateSessionList(list)
            adapter?.notifyItemInserted(list.size)
        })
        viewModel.getGroupList().observe(viewLifecycleOwner, Observer {  list ->
            adapter?.updateGroupList(list)
        })
        // 建立长链接
        viewModel.getChannelObjIdMap().observe(viewLifecycleOwner, Observer { map ->
            if (longLinkEstablished) return@Observer
            longLinkEstablished = true
            shareViewModel.channelObjIdMap.value = map
            adapter?.updateChannelObjIdMap(map)
            establishLongLink(map)
        })

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (this::viewModel.isInitialized){
                viewModel.setLoginStatus(AccountServiceUtil.getSerVice().isLogged())
            }
        }
    }

    private fun establishLongLink(map: Map<String, String>) {
        bmobRealTimeData.start(object : ValueEventListener {
            override fun onDataChange(jsonObject: JSONObject?) {
                val action = jsonObject?.optString("action")
                if (action == BmobRealTimeData.ACTION_UPDATEROW) {
                    val data = jsonObject.optJSONObject("data")
                    val channelId = data.optString("channel_id")
                    val message = data.optString("last_message")
                    val sendUid = data.optString("sender_uid")
                    EventBusWrapper.post(NewMessageEvent(channelId, NewMessage(message, sendUid)))
                }
            }

            override fun onConnectCompleted(p0: Exception?) {
                if (p0 == null && bmobRealTimeData.isConnected) {
                    for ((channelId, objectId) in map) {
                        bmobRealTimeData.subRowUpdate("Session", objectId)
                    }
                }
            }
        })
    }

    override fun onStop() {
        context?.let {
            IMLocalStorageUtil.saveSessionState(it, viewModel.getSessionList().value?: emptyList())
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (bmobRealTimeData.isConnected) {
            val map = viewModel.getChannelObjIdMap().value ?: return
            for ((channelId, objectId) in map) {
                bmobRealTimeData.unsubRowUpdate("Session", objectId)
            }
        }
        EventBusWrapper.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logNewMessageEvent(event: NewMessageEvent) {
        if (this::shareViewModel.isInitialized && adapter?.checkSessionExist(event.newMsg.senderUid) == false) {
            shareViewModel.sessionExtra.value = shareViewModel.friendsDetailList.value?.get(event.newMsg.senderUid)
        }
        IMLocalStorageUtil.saveMessage(context, event)
        adapter?.updateLatestMessage(event)
        if (event.newMsg.senderUid != AccountServiceUtil.getSerVice().getCurUserId()) {
            shareViewModel.addUnRead(1)
        }
    }

    private fun logNewMessages(newMessage: Map<String, List<String>>?, friendsDetailList: Map<String, GiftUser>?) {
        if (newMessage == null) return
        val curUserId = AccountServiceUtil.getSerVice().getCurUserId()
        var unRead = 0
        for ((sendUid, msgList) in newMessage) {
            if(adapter?.checkSessionExist(sendUid) == false) {
                shareViewModel.sessionExtra.value = friendsDetailList?.get(sendUid)
            }
            val channelId = SessionViewModel.getChannelId(curUserId, sendUid)
            val lastMsg = msgList[msgList.size - 1]
            adapter?.updateLatestMessage(
                NewMessageEvent(
                    channelId,
                    NewMessage(lastMsg, sendUid)
                ),
                unRead = msgList.size
            )
            IMLocalStorageUtil.saveMessage(context, channelId, msgList.map { NewMessage(it, sendUid) })
            unRead += msgList.size
        }
        shareViewModel.addUnRead(unRead)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logReadMessageEvent(event: ReadMessageEvent) {
        val consumedMsg = adapter?.updateReadStatus(event.uid)?:0
        shareViewModel.addUnRead(-consumedMsg)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SessionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}