package com.taiqiwen.gift

import android.content.Context
import androidx.fragment.app.Fragment
import com.beyondsw.lib.IGiftService
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.gift.network.GiftApi

class GiftServiceImpl : IGiftService {

    override fun getGiftGalleryFragment(): Fragment? {
        return CardFragment()
    }

    override fun getCollectedGifts(userId: String?, cb: ((List<GiftDetailDTO>?) -> Unit)?) {
        GiftApi.getCollectedGifts(userId, cb)
    }

    override fun startGiftActivity(context: Context, params: GiftDetailDTO) {
        GiftActivity.start(context, params)
    }
}