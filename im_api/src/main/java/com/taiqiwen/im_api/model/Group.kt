package com.taiqiwen.im_api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Group(
    @SerializedName("objectId") val objectId: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar_url") val icon: String,
    @SerializedName("desc") val desc: String,
    val hasUnRead: Boolean = false
): Serializable