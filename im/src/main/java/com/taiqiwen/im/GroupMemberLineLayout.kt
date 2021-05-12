package com.taiqiwen.im

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.ScreenUtils
import kotlin.math.min

class GroupMemberLineLayout
@JvmOverloads constructor(private val curContext: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0)
    : LinearLayout(curContext, attrs, defStyleAttr) {

    private var holder: ViewGroup
    private var addNewMemberButton: View
    private val memberList = mutableListOf<GiftUser>()

    init {
        LayoutInflater.from(curContext).inflate(R.layout.group_member_line_layout, this, true)
        holder = findViewById(R.id.member_holder)
        addNewMemberButton = findViewById(R.id.add_new_member)
    }

    fun setAddMemberListener(listener: (() -> Unit)) {
        addNewMemberButton.setOnClickListener {
            listener.invoke()
        }
    }

    fun setMemberList(list: List<GiftUser>, listener: ((GiftUser) -> Unit)? = null) {
        memberList.addAll(list)
        bindView(list)
        if (listener != null) {
            setAvatarClickListener(listener)
        }
    }

    private fun bindView(list: List<GiftUser>) {
        val count = min(getMemberCount(curContext), list.size)
        repeat(count) { index ->
            val avatarView = LayoutInflater.from(curContext)
                .inflate(R.layout.item_group_member, holder, false)
                .findViewById<SimpleDraweeView>(R.id.icon_member)
            holder.addView(avatarView.apply {
                setImageURI(list[index].avatarUrl)
            })
        }
    }

    private fun setAvatarClickListener(listener: ((GiftUser) -> Unit)) {
        for (i in 0 until memberList.size) {
            if (i >= holder.childCount) continue
            holder.getChildAt(i).setOnClickListener {
                listener.invoke(memberList[i])
            }
        }
    }

    private fun getMemberCount(curContext: Context): Int {
        val widthPixels = ScreenUtils.getScreenWidth(curContext)
        val entryWidth = ScreenUtils.dp2PxInt(curContext, AVATAR_DP_WIDTH)
        val padding = ScreenUtils.dp2PxInt(curContext, 20f)
        val marginEnd = ScreenUtils.dp2PxInt(curContext, AVATAR_DP_MARGIN_END)
        return (widthPixels - padding) / (entryWidth + marginEnd)
    }

    companion object {
        private const val AVATAR_DP_WIDTH = 45f
        private const val AVATAR_DP_MARGIN_END = 15f

    }
}