package com.taiqiwen.im

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView

sealed class MsgViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val icon: SimpleDraweeView = view.findViewById(R.id.icon)
}

class LeftViewHolder(view: View) : MsgViewHolder(view) {
    val leftMsg: TextView = view.findViewById(R.id.leftMsg)
}

class RightViewHolder(view: View) : MsgViewHolder(view) {
    val rightMsg: TextView = view.findViewById(R.id.rightMsg)
}