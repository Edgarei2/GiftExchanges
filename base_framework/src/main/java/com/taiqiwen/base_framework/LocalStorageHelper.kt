package com.taiqiwen.base_framework

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.taiqiwen.base_framework.model.GiftUser


object LocalStorageHelper {

    private val gson = Gson()
    private const val TAG_USER_STATUS = "shared_preferences_user"
    private const val KEY_CUR_LOGGED_USER = "cur_user"

    @JvmStatic
    fun saveUserInfo(context: Context, giftUser: GiftUser?) {
        if (giftUser == null) {
            return
        }
        val userInfo: SharedPreferences = context.getSharedPreferences(TAG_USER_STATUS, MODE_PRIVATE)
        val editor = userInfo.edit()
        editor.putString(KEY_CUR_LOGGED_USER, gson.toJson(giftUser))
        editor.apply()
    }

    @JvmStatic
    fun loadUserInfo(context: Context): GiftUser? {
        val userInfo: SharedPreferences = context.getSharedPreferences(TAG_USER_STATUS, MODE_PRIVATE)
        val info = userInfo.getString(KEY_CUR_LOGGED_USER, "")
        return try {
            gson.fromJson<GiftUser>(info, GiftUser::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

}