package com.taiqiwen.im

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.itingchunyu.badgeview.BadgeTextView
import com.taiqiwen.im_api.event.NewMessageEvent
import com.taiqiwen.im_api.model.Group
import com.taiqiwen.im_api.model.Session
import com.test.account_api.AccountServiceUtil

class SessionAdapter(
    private val curContext: Context,
    private var sessionList: List<Session>,
    private var groupList: List<Group>,
    private var channelObjIdMap: Map<String, String>) : RecyclerView.Adapter<SessionAdapter.BaseViewHolder>() {

    fun updateSessionList(newSessionList: List<Session>?) {
        sessionList = newSessionList ?: emptyList()
    }

    fun updateGroupList(newGroupList: List<Group>?) {
        groupList = newGroupList ?: emptyList()
        notifyItemChanged(0)
    }

    fun updateChannelObjIdMap(map: Map<String, String>) {
        channelObjIdMap = map
    }

    fun checkSessionExist(uid: String?) : Boolean {
        for (session in sessionList) {
            if (session.user?.userId == uid) {
                return true
            }
        }
        return false
    }

    fun updateLatestMessage(event: NewMessageEvent, unRead: Int = 1) {
        if (event.newMsg.senderUid == AccountServiceUtil.getSerVice().getCurUserId()) return
        for (index in sessionList.indices) {
            if (sessionList[index].user?.userId == event.newMsg.senderUid) {
                sessionList[index].latestMsg = event.newMsg.msg
                sessionList[index].unRead += unRead
                notifyItemChanged(index + 1)
                break
            }
        }
    }

    fun updateReadStatus(uid: String): Int {
        var consumedMsg = 0
        for (index in sessionList.indices) {
            if (sessionList[index].user?.userId == uid) {
                consumedMsg = sessionList[index].unRead
                sessionList[index].unRead = 0
                notifyItemChanged(index + 1)
                break
            }
        }
        return consumedMsg
    }

    override fun getItemViewType(position: Int): Int =
        if (position == 0) {
            0
        } else {
            sessionList[position - 1].getType()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.session_top_group_entry, parent, false)
            GroupViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.session_item_layout, parent, false)
            SessionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is SessionViewHolder) {
            val session = sessionList[position - 1]
            if (session.getType() == Session.TYPE_INDIVIDUAL) {
                holder.avatar.setImageURI(session.user?.avatarUrl)
                holder.name.text = session.user?.userName
                holder.lastMsg.text = session.latestMsg
                holder.type.text = if (session.user?.userId == "0") "系统" else "好友"
                holder.area.setOnClickListener {
                    val channelId = SessionViewModel.getChannelId(AccountServiceUtil.getSerVice().getCurUserId(), session.user?.userId)
                    ChatActivity.start(curContext, session.user, channelId, channelObjIdMap[channelId])
                }
            } else {
                holder.type.text = "群组"
            }
            if (session.unRead > 0) {
                holder.unreadIcon.setBadgeCount(session.unRead)
                holder.unreadIcon.setBadgeShown(true)
            } else {
                holder.unreadIcon.setBadgeShown(false)
            }
        } else if (holder is GroupViewHolder) {
            val entryHolder = holder.entryHolder
            var startIndex = 0
            while (startIndex < groupList.size) {
                val lineEntry = GroupEntryLineLayout(curContext)
                if (startIndex == 0) {
                    lineEntry.visibility = View.VISIBLE
                } else {
                    lineEntry.visibility = View.GONE
                }
                startIndex = lineEntry.initView(groupList, startIndex)
                entryHolder.addView(lineEntry)
            }
            if (GroupEntryLineLayout.getEntryCount(curContext) < groupList.size) {
                holder.showMore.visibility = View.VISIBLE
            }
            holder.showMore.setOnClickListener {
                it.visibility = View.GONE
                holder.showLess.visibility = View.VISIBLE
                for (index in 0 until entryHolder.childCount) {
                    if (index == 0) continue
                    entryHolder.getChildAt(index).visibility = View.VISIBLE
                }
            }
            holder.showLess.setOnClickListener {
                it.visibility = View.GONE
                holder.showMore.visibility = View.VISIBLE
                for (index in 0 until entryHolder.childCount) {
                    if (index == 0) continue
                    entryHolder.getChildAt(index).visibility = View.GONE
                }
            }
        }

    }

    override fun getItemCount() = sessionList.size + 1

    open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class SessionViewHolder(view: View) : BaseViewHolder(view) {
        val area = view.findViewById<View>(R.id.list_item)
        val avatar = view.findViewById<SimpleDraweeView>(R.id.avatar)
        val name = view.findViewById<TextView>(R.id.name)
        val lastMsg = view.findViewById<TextView>(R.id.last_message)
        val unreadIcon = view.findViewById<BadgeTextView>(R.id.new_session_label)
        val type = view.findViewById<TextView>(R.id.type_info)
    }

    class GroupViewHolder(view: View) : BaseViewHolder(view) {
        val entryHolder = view.findViewById<ViewGroup>(R.id.line_entry_holder)
        val showMore = view.findViewById<View>(R.id.show_more)
        val showLess = view.findViewById<View>(R.id.show_less)
    }

}