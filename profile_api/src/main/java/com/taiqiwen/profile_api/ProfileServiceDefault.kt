package com.taiqiwen.profile_api

import android.content.Context
import androidx.fragment.app.Fragment

class ProfileServiceDefault: IProfileService {

    override fun getProfileFragment(): Fragment? {
        return null
    }

    override fun getGiftSessionFragment(): Fragment? {
        return null
    }

    override fun getDebugInfo(): String {
        return "default collected"
    }

    override fun startLoginActivity(context: Context) {
    }
}