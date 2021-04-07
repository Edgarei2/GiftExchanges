package com.taiqiwen.base_framework.model

import android.os.Bundle
import com.google.gson.annotations.SerializedName
import com.taiqiwen.base_framework.ui.selectionsheet.ISheetData
import java.io.Serializable


data class GiftUser(
    @SerializedName ("objectId") var objectId: String? = null,
    @SerializedName("userId") var userId: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("password") var password: String = "",
    @SerializedName("phoneNumber") var phoneNumber: String = "",
    @SerializedName("curCredit") var curCredit: Int = 0,
    @SerializedName("avatarUrl") var avatarUrl: String = ""
): ISheetData, Serializable {
    override fun getKey(): String  = "key_$userId"

    override fun getValue(): String = userName

    override fun getIconUrl(): String? = avatarUrl

    override fun getExtraInfo(): Bundle? =
        Bundle().apply {
            putSerializable("gift_detail", this@GiftUser)
        }
}
