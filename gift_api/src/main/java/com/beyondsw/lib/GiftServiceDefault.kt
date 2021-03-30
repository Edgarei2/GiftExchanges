package com.beyondsw.lib

import android.content.Context
import androidx.fragment.app.Fragment
import com.beyondsw.lib.model.GiftDetailDTO

class GiftServiceDefault : IGiftService {
    override fun getGiftGalleryFragment(): Fragment? = null

    override fun getCollectedGifts(userId: String?, cb: ((List<GiftDetailDTO>?) -> Unit)?) { }

    override fun startGiftActivity(context: Context, params: GiftDetailDTO) { }
}