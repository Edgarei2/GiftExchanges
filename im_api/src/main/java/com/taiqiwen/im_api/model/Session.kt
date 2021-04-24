package com.taiqiwen.im_api.model

import com.taiqiwen.base_framework.model.GiftUser

data class Session(val user: GiftUser?,
                   var latestMsg: String,
                   val type: String,
                   var unRead: Int) {

    fun getType() = when(type) {
        TYPE_INDIVIDUAL_STR -> 1
        "group" -> 2
        else -> 3
    }

    companion object {

        const val TYPE_INDIVIDUAL_STR = "individual"
        const val TYPE_INDIVIDUAL = 1
        const val TYPE_GROUP = 2

    }
}