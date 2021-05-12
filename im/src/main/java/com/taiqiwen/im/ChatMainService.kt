package com.taiqiwen.im

import android.content.Context
import androidx.fragment.app.Fragment
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im.game.Game2048Activity
import com.taiqiwen.im_api.IChatService
import com.taiqiwen.im_api.model.GamerDTO

class ChatMainService : IChatService {

    override fun getSessionFragment(): Fragment = SessionFragment.newInstance()

    override fun startChatActivity(context: Context?, user: GiftUser?, channelId: String?, channelObjId: String?) {
        ChatActivity.start(context, user, channelId, channelObjId)
    }

    override fun startGameActivity(context: Context?, gameRoomId: String?, beginNow: String?) {
        Game2048Activity.start(context, gameRoomId, beginNow)
    }
}