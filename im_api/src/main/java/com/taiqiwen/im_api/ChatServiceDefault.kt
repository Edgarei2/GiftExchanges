package com.taiqiwen.im_api

import android.content.Context
import androidx.fragment.app.Fragment
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.model.GamerDTO

class ChatServiceDefault : IChatService {

    override fun getSessionFragment(): Fragment? = null

    override fun startChatActivity(context: Context?, user: GiftUser?, channelId: String?, channelObjId: String?) { }

    override fun startGameActivity(context: Context?, gameRoomId: String?, beginNow: String?) {}
}