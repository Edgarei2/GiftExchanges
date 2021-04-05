package com.beyondsw.lib.model

import android.os.Bundle
import com.taiqiwen.base_framework.ui.SheetHelper.KEY_CHECKOUT_STATUS
import com.taiqiwen.base_framework.ui.SheetHelper.KEY_RECEIVER
import com.taiqiwen.base_framework.ui.selectionsheet.ISheetData
import java.io.Serializable

data class GiftSentStatusDetailDTO(
    val receiver: String?,
    val checkoutStatus: String?,
    val giftDetail: GiftDetailDTO?
): ISheetData, Serializable {
    override fun getKey(): String  = "key_$receiver"

    override fun getValue(): String = giftDetail?.getValue()?:""

    override fun getIconUrl(): String? = giftDetail?.getIconUrl()?:""

    override fun getExtraInfo(): Bundle? = Bundle().apply {
        putString(KEY_RECEIVER, receiver)
        putString(KEY_CHECKOUT_STATUS, checkoutStatus)
    }
}