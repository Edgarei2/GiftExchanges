package com.taiqiwen.im

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.taiqiwen.base_framework.LocalStorageHelper
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.event.NewMessage
import com.taiqiwen.im_api.event.NewMessageEvent
import com.taiqiwen.im_api.model.Session
import com.test.account_api.AccountServiceUtil

object IMLocalStorageUtil {

    private val gson = Gson()
    private const val TAG_SESSION_STATUS = "shared_preferences_session"
    private const val KEY_PREVIOUS_SESSION = "previous_session"
    private const val TAG_MESSAGE_RECEIVED = "message_received"
    private const val KEY_MESSAGE_USER_PREFIX = "message_user"

    @JvmStatic
    fun saveSessionState(context: Context, sessionList: List<Session>) {
        val userInfo: SharedPreferences = context.getSharedPreferences(TAG_SESSION_STATUS, Context.MODE_PRIVATE)
        val editor = userInfo.edit()
        editor.putString(KEY_PREVIOUS_SESSION, gson.toJson(sessionList))
        editor.apply()
    }

    @JvmStatic
    fun loadSessionState(context: Context) : List<Session>{
        val userInfo: SharedPreferences = context.getSharedPreferences(TAG_SESSION_STATUS, Context.MODE_PRIVATE)
        val info = userInfo.getString(KEY_PREVIOUS_SESSION, "")
        return try {
            val typeOf = object : TypeToken<List<Session>>() {}.type
            gson.fromJson<List<Session>>(info, typeOf)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @JvmStatic
    fun saveMessage(context: Context?, newMsg: NewMessageEvent) {
        if (context == null) return
        val userInfo: SharedPreferences = context.getSharedPreferences(TAG_MESSAGE_RECEIVED, Context.MODE_PRIVATE)
        val channelId = newMsg.channelId
        val previousMsg = userInfo.getString("${KEY_MESSAGE_USER_PREFIX}_${channelId}", "")
        val editor = userInfo.edit()
        editor.putString("${KEY_MESSAGE_USER_PREFIX}_${channelId}", "${previousMsg}${gson.toJson(newMsg.newMsg)}&^")
        editor.apply()
    }

    @JvmStatic
    fun loadMessage(context: Context?, channelId: String) : List<NewMessage> {
        if (context == null) return emptyList()
        val userInfo: SharedPreferences = context.getSharedPreferences(TAG_MESSAGE_RECEIVED, Context.MODE_PRIVATE)
        val info = userInfo.getString("${KEY_MESSAGE_USER_PREFIX}_${channelId}", "")
        val list = info.split("&^")
        val ans = mutableListOf<NewMessage>()
        for (msgStr in list) {
            val newMessage = try {
                gson.fromJson<NewMessage>(msgStr, NewMessage::class.java)
            } catch (e: Exception) {
                null
            }
            if (newMessage != null) {
                ans.add(newMessage)
            }
        }
        return ans
    }
}