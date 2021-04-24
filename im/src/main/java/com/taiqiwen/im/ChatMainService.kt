package com.taiqiwen.im

import androidx.fragment.app.Fragment
import com.taiqiwen.im_api.IChatService

class ChatMainService : IChatService {

    override fun getSessionFragment(): Fragment = SessionFragment.newInstance()

}