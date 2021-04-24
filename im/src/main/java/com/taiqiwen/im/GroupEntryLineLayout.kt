package com.taiqiwen.im

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.taiqiwen.base_framework.ui.ScreenUtils
import com.taiqiwen.im.GroupEntryLayout.Companion.KEY_GROUP_NAME
import com.taiqiwen.im.GroupEntryLayout.Companion.KEY_HAS_UNREAD
import com.taiqiwen.im.GroupEntryLayout.Companion.KEY_ICON_URI
import com.taiqiwen.im_api.model.Group
import kotlin.math.min

class GroupEntryLineLayout
@JvmOverloads constructor(val curContext: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0)
    : LinearLayout(curContext, attrs, defStyleAttr) {

    private var holder: ViewGroup

    init {
        LayoutInflater.from(curContext).inflate(R.layout.group_entry_line_layout, this, true)
        holder = findViewById(R.id.entry_holder)
    }

    fun initView(groupInfo: List<Group>, startIndex: Int): Int {
        if (startIndex >= groupInfo.size) {
            return groupInfo.size
        }
        val num = getEntryCount(curContext)
        val endIndex = min(startIndex + num, groupInfo.size)
        for (i in startIndex until endIndex) {
            val data = mapOf(
                KEY_ICON_URI to groupInfo[i].icon,
                KEY_GROUP_NAME to groupInfo[i].name,
                KEY_HAS_UNREAD to (if (groupInfo[i].hasUnRead) "true" else "false")
            )
            val entry = GroupEntryLayout(curContext)
            entry.bind(data)
            holder.addView(entry)
        }
        return endIndex
    }

    companion object {

        fun getEntryCount(curContext: Context): Int {
            val widthPixels = ScreenUtils.getScreenWidth(curContext)
            val entryWidth = ScreenUtils.dp2PxInt(curContext, GroupEntryLayout.DP_WIDTH)
            val padding = ScreenUtils.dp2PxInt(curContext, 20f)
            return (widthPixels - padding) / entryWidth

        }

    }

}