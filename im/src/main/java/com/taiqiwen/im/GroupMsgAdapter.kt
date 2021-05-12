package com.taiqiwen.im

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taiqiwen.im_api.model.GroupMsg
import com.taiqiwen.im_api.model.Msg

class GroupMsgAdapter(private val msgList: ArrayList<GroupMsg>) : RecyclerView.Adapter<GroupMsgViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position].msg
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == Msg.TYPE_RECEIVED) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_group_left_item, parent, false)
        GroupLeftViewHolder(view)
    } else {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_group_right_item, parent, false)
        GroupRightViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMsgViewHolder, position: Int) {
        val msg = msgList[position].msg
        val user = msgList[position].user
        holder.icon.setImageURI(user?.avatarUrl)
        holder.senderName.text = user?.userName
        when (holder) {
            is GroupLeftViewHolder -> {
                holder.leftMsg.text = msg.content
            }
            is GroupRightViewHolder -> {
                holder.rightMsg.text = msg.content
            }
        }
    }

    override fun getItemCount() = msgList.size

}