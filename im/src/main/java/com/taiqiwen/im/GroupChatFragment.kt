package com.taiqiwen.im

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobRealTimeData
import cn.bmob.v3.listener.ValueEventListener
import com.taiqiwen.base_framework.EventBusWrapper
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.taiqiwen.im.network.ImApi
import com.taiqiwen.im_api.event.NewMessage
import com.taiqiwen.im_api.event.NewMessageEvent
import com.taiqiwen.im_api.model.Group
import com.taiqiwen.im_api.model.GroupMsg
import com.taiqiwen.im_api.model.Msg
import com.test.account_api.AccountServiceUtil
import org.json.JSONObject

class GroupChatFragment : Fragment(), View.OnClickListener  {

    private var adapter: GroupMsgAdapter? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var inputText: EditText
    private lateinit var sendButton: Button
    private lateinit var group: Group
    private lateinit var viewModel: GroupChatViewModel
    private val curUserId = AccountServiceUtil.getSerVice().getCurUserId()
    private val msgList = ArrayList<GroupMsg>()
    private val bmobRealTimeData = BmobRealTimeData()
    private var longLinkEstablished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            group = it.getSerializable(KEY_GROUP_INFO) as Group
        }
        viewModel = ViewModelProvider(requireActivity()).get(GroupChatViewModel::class.java)
        // 建立长链接
        if (!longLinkEstablished) {
            longLinkEstablished = true
            establishLongLink()
        }
    }

    private fun establishLongLink() {
        bmobRealTimeData.start(object : ValueEventListener {
            override fun onDataChange(jsonObject: JSONObject?) {
                val action = jsonObject?.optString("action")
                if (action == BmobRealTimeData.ACTION_UPDATEROW) {
                    val data = jsonObject.optJSONObject("data")
                    val message = data.optString("last_message")
                    val sendUid = data.optString("sender_uid")
                    if (sendUid == curUserId) return
                    viewModel.getUserInfo(sendUid) { sendUser ->
                        showNewMessage(GroupMsg(
                            Msg(message, Msg.TYPE_RECEIVED),
                            sendUser
                        ))
                    }
                }
            }

            override fun onConnectCompleted(p0: Exception?) {
                if (p0 == null && bmobRealTimeData.isConnected) {
                    bmobRealTimeData.subRowUpdate("Group", group.objectId)
                }
            }
        })
    }

    private fun showNewMessage(groupMsg: GroupMsg) {
        msgList.add(groupMsg)
        adapter?.notifyItemInserted(msgList.size - 1) // 当有新消息时，刷新RecyclerView中的显示
        recyclerView?.scrollToPosition(msgList.size - 1)  // 将 RecyclerView定位到最后一行
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        adapter = GroupMsgAdapter(msgList)
        recyclerView?.adapter = adapter
        sendButton.setOnClickListener(this)
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        inputText = view.findViewById(R.id.input_text)
        sendButton = view.findViewById(R.id.send)
    }

    override fun onClick(v: View?) {
        when (v) {
            sendButton -> {
                val content = inputText.text.toString()
                if (content.isNotEmpty()) {
                    ImApi.sendGroupMessage(group.objectId, content, curUserId) { result ->
                        if (result == "0") {
                            ToastHelper.showToast("网络错误")
                        }
                    }
                    val groupMsg = GroupMsg(
                        Msg(content, Msg.TYPE_SENT),
                        AccountServiceUtil.getSerVice().getCurUser()
                    )
                    showNewMessage(groupMsg)
                    inputText.setText("") // 清空输入框中的内容
                }
            }
        }
    }

    override fun onDestroy() {
        if (bmobRealTimeData.isConnected) {
            bmobRealTimeData.unsubRowUpdate("Group", group.objectId)
        }
        super.onDestroy()
    }

    companion object {

        private const val KEY_GROUP_INFO = "key_group_info"

        @JvmStatic
        fun newInstance(group: Group) =
            GroupChatFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_GROUP_INFO, group)
                }
            }
    }
}