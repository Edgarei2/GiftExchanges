package com.taiqiwen.im

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.ui.SheetHelper
import com.taiqiwen.im_api.ChatServiceUtil
import com.taiqiwen.im_api.model.Group
import com.test.account_api.AccountServiceUtil

abstract class GroupDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(group: Group, viewModel: GroupChatViewModel)
}

class HeadViewHolder(val context: Context, view: View) : GroupDetailViewHolder(view) {
    val groupName: TextView = view.findViewById(R.id.name)
    val groupIcon: SimpleDraweeView = view.findViewById(R.id.icon)
    override fun bind(group: Group, viewModel: GroupChatViewModel) {
        groupName.text = group.name
        groupIcon.setImageURI(group.icon)
    }
}

class MemberViewHolder(val context: Context, view: View) : GroupDetailViewHolder(view) {
    val groupMemberNum: TextView = view.findViewById(R.id.number)
    val showMore: View = view.findViewById(R.id.show_more)
    val memberAvatarLayout: GroupMemberLineLayout = view.findViewById(R.id.member_line_layout)
    override fun bind(group: Group, viewModel: GroupChatViewModel) {
        viewModel.getGroupMember(group.objectId) { list ->
            groupMemberNum.text = (list?.size?:0).toString()
            memberAvatarLayout.setMemberList(list?: emptyList())
            showMore.setOnClickListener {
                SheetHelper.showSheet(context, "群成员", list, noItemHintText = "您还没有朋友") { item, position ->
                    ToastHelper.showToast(item.value)
                }
            }
        }
    }
}

class DetailViewHolder(val context: Context, view: View) : GroupDetailViewHolder(view) {
    val groupName: TextView = view.findViewById(R.id.group_name)
    val groupDesc: TextView = view.findViewById(R.id.group_description)
    override fun bind(group: Group, viewModel: GroupChatViewModel) {
        groupName.text = group.name
        groupDesc.text = group.desc
    }
}

class EmptyViewHolder(val context: Context, view: View) : GroupDetailViewHolder(view) {
    override fun bind(group: Group, viewModel: GroupChatViewModel) { }
}