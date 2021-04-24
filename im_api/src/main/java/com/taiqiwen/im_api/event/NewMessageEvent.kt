package com.taiqiwen.im_api.event

data class NewMessage(
    val msg: String,
    val senderUid: String
)

data class NewMessageEvent(
    val channelId: String,
    val newMsg: NewMessage
)