package com.taiqiwen.profile

import android.content.Context
import androidx.fragment.app.Fragment
import com.taiqiwen.profile_api.IProfileService

class ProfileMainService: IProfileService {

    override fun getProfileFragment(): Fragment? {
        return MyProfileFragment()
    }

    override fun getGiftSessionFragment(): Fragment? {
        return GiftSessionFragment.newInstance()
    }

    override fun startLoginActivity(context: Context) {
        LoginActivity.start(context)
    }
}