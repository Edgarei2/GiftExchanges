package com.beyondsw.lib.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GiftSentStatusDTO (
    @SerializedName("sender_uid") var sender: String? = null,
    @SerializedName("receiver_uid") var receiver: String? = null,
    @SerializedName("gift_id") var giftId: String? = null,
    @SerializedName("checkout_status") var status: String? = null
): Serializable