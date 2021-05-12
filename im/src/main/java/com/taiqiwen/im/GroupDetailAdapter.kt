package com.taiqiwen.im

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taiqiwen.im_api.model.Group

class GroupDetailAdapter(private val group: Group,
                         private val viewModel: GroupChatViewModel) : RecyclerView.Adapter<GroupDetailViewHolder>() {

    override fun getItemCount(): Int = VIEW_TYPE_ARRAY.size

    override fun getItemViewType(position: Int): Int = VIEW_TYPE_ARRAY[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupDetailViewHolder {
        val layoutId = when (viewType) {
            TYPE_HEAD -> R.layout.group_detail_header_layout
            TYPE_MEMBER -> R.layout.group_detail_member_layout
            TYPE_DETAIL -> R.layout.group_detail_detail_layout
            else -> 0
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return when (viewType) {
            TYPE_HEAD -> HeadViewHolder(parent.context, view)
            TYPE_MEMBER -> MemberViewHolder(parent.context, view)
            TYPE_DETAIL -> DetailViewHolder(parent.context, view)
            else -> EmptyViewHolder(parent.context, view)
        }
    }

    override fun onBindViewHolder(holder: GroupDetailViewHolder, position: Int) {
        holder.bind(group, viewModel)
    }

    companion object {

        private const val TYPE_HEAD = 0
        private const val TYPE_MEMBER = 1
        private const val TYPE_DETAIL = 2
        private const val TYPE_EXTRA = 3
        private val VIEW_TYPE_ARRAY = listOf(TYPE_HEAD, TYPE_MEMBER, TYPE_DETAIL)
    }
}