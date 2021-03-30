package com.beyondsw.lib.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GiftDetailDTO(
    @SerializedName ("id") var id: String? = null,
    @SerializedName ("title")val title: String? = null,
    @SerializedName ("credit")val credit: Int? = null,
    @SerializedName ("imageUrls")val imageUrls: List<String>? = null,
    @SerializedName ("detail")val detail: String? = null
): Serializable
