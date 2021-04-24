package com.taiqiwen.im

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.model.Msg
import com.test.account_api.AccountServiceUtil


class MsgAdapter(private val msgList: List<Msg>, private val user: GiftUser) : RecyclerView.Adapter<MsgViewHolder>() {

    private val curUser = AccountServiceUtil.getSerVice().getCurUser()

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == Msg.TYPE_RECEIVED) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_left_item, parent, false)
        LeftViewHolder(view)
    } else {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_right_item, parent, false)
        RightViewHolder(view)
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> {
                holder.leftMsg.text = msg.content
                holder.icon.setImageURI(user.avatarUrl)
            }
            is RightViewHolder -> {
                holder.rightMsg.text = msg.content
                holder.icon.setImageURI(curUser?.avatarUrl)
            }
         }
    }

    override fun getItemCount() = msgList.size

}