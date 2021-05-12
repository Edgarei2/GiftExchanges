package com.taiqiwen.im_api

import android.content.Context
import androidx.fragment.app.Fragment
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.model.GamerDTO

interface IChatService {

    fun getSessionFragment(): Fragment?

    fun startChatActivity(context: Context?, user: GiftUser?, channelId: String?, channelObjId: String?)

    fun startGameActivity(context: Context?, gameRoomId: String?, beginNow: String?)
}