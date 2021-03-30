package com.beyondsw.lib

import android.content.Context
import androidx.fragment.app.Fragment
import com.beyondsw.lib.model.GiftDetailDTO

interface IGiftService {

    fun getGiftGalleryFragment(): Fragment?

    fun getCollectedGifts(userId: String?, cb: ((List<GiftDetailDTO>?) -> Unit)?)

    fun startGiftActivity(context: Context, params: GiftDetailDTO)

}