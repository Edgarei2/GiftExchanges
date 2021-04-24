package com.taiqiwen.im

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taiqiwen.base_framework.EventBusWrapper
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.taiqiwen.im.network.ImApi
import com.taiqiwen.im_api.event.NewMessage
import com.taiqiwen.im_api.event.NewMessageEvent
import com.taiqiwen.im_api.event.ReadMessageEvent
import com.taiqiwen.im_api.model.Msg
import com.taiqiwen.profile_api.ProfileServiceUtil
import com.test.account_api.AccountServiceUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    private val msgList = ArrayList<Msg>()

    private lateinit var adapter: MsgAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputText: EditText
    private lateinit var sendButton: Button
    private lateinit var user: GiftUser
    private lateinit var channelId: String
    private lateinit var channelObjId: String
    private lateinit var titleBar: CommonTitleBar
    private val curUserId = AccountServiceUtil.getSerVice().getCurUserId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //AndroidBug5497Workaround.assistActivity(this)
        findViewById<View>(R.id.back).setOnClickListener {
            finish()
        }
        EventBusWrapper.register(this)
        (intent?.getSerializableExtra(KEY_USER) as GiftUser).apply {
            user = this
        }
        (intent?.getSerializableExtra(KEY_CHANNEL_ID) as String).apply {
            channelId = this
        }
        (intent?.getSerializableExtra(KEY_CHANNEL_OBJ_ID) as String).apply {
            channelObjId = this
        }
        supportActionBar?.hide()
        initView()
        initMsg()
        titleBar.setCenterText(user.userName)
        titleBar.setBackgroundResource(R.drawable.shape_gradient);
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(msgList, user)
        }
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(msgList.size - 1)  // 将 RecyclerView定位到最后一行
        sendButton.setOnClickListener(this)
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        inputText = findViewById(R.id.input_text)
        sendButton = findViewById(R.id.send)
        titleBar = findViewById(R.id.title_bar)
    }

    override fun onClick(v: View?) {
        when (v) {
            sendButton -> {
                val content = inputText.text.toString()
                if (content.isNotEmpty()) {
                    IMLocalStorageUtil.saveMessage(this, NewMessageEvent(channelId, NewMessage(content, curUserId!!)))
                    ImApi.sendMessage(channelObjId, content, curUserId) { result ->
                        if (result == "0") {
                            ToastHelper.showToast("网络错误")
                        }
                    }
                    val msg = Msg(content, Msg.TYPE_SENT)
                    msgList.add(msg)
                    adapter.notifyItemInserted(msgList.size - 1) // 当有新消息时，刷新RecyclerView中的显示
                    recyclerView.scrollToPosition(msgList.size - 1)  // 将 RecyclerView定位到最后一行
                    inputText.setText("") // 清空输入框中的内容
                }
            }
        }
    }

    private fun initMsg() {
/*        val msg1 = Msg("Hello guy.", Msg.TYPE_RECEIVED)
        msgList.add(msg1)
        val msg2 = Msg("Hello. Who is that?", Msg.TYPE_SENT)
        msgList.add(msg2)
        val msg3 = Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED)
        msgList.add(msg3)*/
        val curUserId = AccountServiceUtil.getSerVice().getCurUserId()
        val previousMsgList = IMLocalStorageUtil.loadMessage(this, channelId)
        msgList.addAll(
            previousMsgList.map {
                Msg(it.msg, if (it.senderUid == curUserId) Msg.TYPE_SENT else Msg.TYPE_RECEIVED)
            }
        )
    }

    override fun onStop() {
        EventBusWrapper.post(ReadMessageEvent(user.userId))
        super.onStop()
    }

    override fun onDestroy() {
        EventBusWrapper.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logReadMessageEvent(event: ReadMessageEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logNewMessageEvent(event: NewMessageEvent) {
        if (event.newMsg.senderUid != curUserId) {
            msgList.add(Msg(event.newMsg.msg, Msg.TYPE_RECEIVED))
            adapter.notifyItemInserted(msgList.size - 1)
            recyclerView.scrollToPosition(msgList.size - 1)
        }
    }

    companion object {

        private const val KEY_USER = "key_user"
        private const val KEY_CHANNEL_ID = "key_channel_id"
        private const val KEY_CHANNEL_OBJ_ID = "key_channel_obj_id"

        fun start(context: Context, user: GiftUser?, channelId: String?, channelObjId: String?) {
            if (user == null || channelObjId == null) {
                return
            } else if (!AccountServiceUtil.getSerVice().isLogged()) {
                ProfileServiceUtil.getSerVice().startLoginActivity(context)
            } else {
                val intent = Intent(context, ChatActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable(KEY_USER, user)
                bundle.putSerializable(KEY_CHANNEL_ID, channelId)
                bundle.putSerializable(KEY_CHANNEL_OBJ_ID, channelObjId)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        }

    }
}