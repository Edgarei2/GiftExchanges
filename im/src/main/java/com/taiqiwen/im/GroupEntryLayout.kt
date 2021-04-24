package com.taiqiwen.im

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView

class GroupEntryLayout@JvmOverloads constructor(val curContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(curContext, attrs, defStyleAttr) {

    private var icon: SimpleDraweeView
    private var name: TextView
    private var redDot: View

    init {
        LayoutInflater.from(curContext).inflate(R.layout.group_chat_entry_layout, this, true)
        icon = findViewById(R.id.group_icon)
        name = findViewById(R.id.group_name)
        redDot = findViewById(R.id.unread_label)
    }

    fun bind(data: Map<String, String>) {
        if (data.containsKey(KEY_ICON_URI)) {
            icon.setImageURI(data[KEY_ICON_URI])
        }
        if (data.containsKey(KEY_GROUP_NAME)) {
            name.text = data[KEY_GROUP_NAME]
        }
        if (data.containsKey(KEY_HAS_UNREAD)) {
            redDot.visibility = if (data[KEY_HAS_UNREAD] == "true") {
                View.VISIBLE
            } else View.GONE
        }
    }

    companion object {

        const val DP_WIDTH = 60f
        const val DP_HEIGHT = 75f
        const val KEY_ICON_URI = "icon_url"
        const val KEY_GROUP_NAME = "group_name"
        const val KEY_HAS_UNREAD = "has_unread"

    }


}