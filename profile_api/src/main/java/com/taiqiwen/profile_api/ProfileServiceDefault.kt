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

    override fun startLoginActivity(context: Context) {
    }
}