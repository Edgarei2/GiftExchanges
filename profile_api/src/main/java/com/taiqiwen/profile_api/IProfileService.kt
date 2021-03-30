package com.taiqiwen.profile_api

import android.content.Context
import androidx.fragment.app.Fragment

interface IProfileService {

    fun getProfileFragment(): Fragment?

    fun getGiftSessionFragment(): Fragment?

    fun getDebugInfo(): String

    fun startLoginActivity(context: Context)

}