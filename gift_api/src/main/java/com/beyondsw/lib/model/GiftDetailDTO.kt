package com.beyondsw.lib.model

import android.os.Bundle
import com.google.gson.annotations.SerializedName
import com.taiqiwen.base_framework.ui.selectionsheet.ISheetData
import java.io.Serializable

data class GiftDetailDTO(
    @SerializedName ("id") var id: String? = null,
    @SerializedName ("title")val title: String? = null,
    @SerializedName ("credit")val credit: Int? = null,
    @SerializedName ("imageUrls")val imageUrls: List<String>? = null,
    @SerializedName ("detail")val detail: String? = null
): ISheetData, Serializable {
    override fun getKey(): String = "key_$id"

    override fun getValue(): String = title?:""

    override fun getIconUrl(): String? =
        if (imageUrls.isNullOrEmpty()) {
            null
        } else {
            imageUrls[0]
        }

    override fun getExtraInfo(): Bundle? =

        Bundle().apply {
            putSerializable("gift_detail", this@GiftDetailDTO)
        }
}
