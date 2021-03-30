package com.taiqiwen.testlibrary

import android.util.Log
import com.taiqiwen.profile_api.ProfileServiceUtil

object TestUtil {

    fun getDebug() {
        Log.d("ttest_testModel", ProfileServiceUtil.getSerVice().getDebugInfo())
    }

}