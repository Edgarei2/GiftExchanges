package com.taiqiwen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taiqiwen.base_framework.model.GiftUser

class GiftsGalleryViewModel : ViewModel()  {

    private val giftsCircleListInfo: MutableLiveData<List<Pair<String, List<String>>>?> = MutableLiveData()

    private val extraInfoList: MutableLiveData<List<Map<String, String?>>> = MutableLiveData()

    fun getGiftsCircleListInfo(): LiveData<List<Pair<String, List<String>>>?> {
        return giftsCircleListInfo
    }

    fun getExtraInfoList(): LiveData<List<Map<String, String?>>> {
        return extraInfoList
    }

    fun refreshGiftsCircleList(userId: String?) {
/*        val list = mutableListOf<Pair<String, List<String>>>()

        val pair1 = Pair("giftA", listOf("http://p3.pstatp.com/temai/c2293c4907cfe74e712c77e19e62bdbbwww800-800~tplv-obj.webp",
            "http://static.fdc.com.cn/avatar/sns/1486263782969.png",
            "http://static.fdc.com.cn/avatar/sns/1485055822651.png",
            "http://static.fdc.com.cn/avatar/sns/1486194909983.png",
            "http://static.fdc.com.cn/avatar/sns/1486194996586.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486173526402.png",
            "http://static.fdc.com.cn/avatar/sns/1486173639603.png",
            "http://static.fdc.com.cn/avatar/sns/1486172566083.png"))

        val pair2 = Pair("giftB", listOf("http://p3.pstatp.com/temai/c2293c4907cfe74e712c77e19e62bdbbwww800-800~tplv-obj.webp",
            "http://static.fdc.com.cn/avatar/sns/1486263782969.png",
            "http://static.fdc.com.cn/avatar/sns/1485055822651.png",
            "http://static.fdc.com.cn/avatar/sns/1486194909983.png",
            "http://static.fdc.com.cn/avatar/sns/1486194996586.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486173526402.png",
            "http://static.fdc.com.cn/avatar/sns/1486173639603.png",
            "http://static.fdc.com.cn/avatar/sns/1486172566083.png"))

        list.add(pair1)
        list.add(pair2)
        return list*/

        ProfileApi.fetchOwnedGiftsDetail(userId) {
            extraInfoList.value = it.map {
                mapOf(
                    KEY_SENDER_ID to it.second,
                    KEY_GIFT_DETAIL to it.first.detail,
                    KEY_GIFT_CREDIT to it.first.credit.toString(),
                    KEY_GIFT_CHANGED_STATUS to it.first.takenOut,
                    KEY_GIFT_OBJ_ID to it.first.objectId
                )
            }
            giftsCircleListInfo.value = it.map {  pair ->
                Pair(pair.first.title.toString(), pair.first.imageUrls?: emptyList())
            }
        }

    }

    fun fetchCertainUserDetail(userId: String?, cb: ((GiftUser?) -> Unit)?) {
        ProfileApi.fetchCurUserFriendsDetail(userId) { friendsDetailResponseDTO ->
            cb?.invoke(friendsDetailResponseDTO?.friendsDetail?.get(userId))
        }
    }

    fun restoreGift2Credit(giftObjId: String?,
                           userObjId: String?,
                           credit: String?,
                           curCredit: String?,
                           cb: (Boolean) -> Unit) {
        ProfileApi.restoreGift2Credit(giftObjId,
            userObjId,
            credit,
            curCredit) { result ->
            if (result == "1") cb.invoke(true)
            else cb.invoke(false)
        }
    }

    fun takeGift(giftObjId: String?, cb: (Boolean) -> Unit) {
        ProfileApi.takeGift(giftObjId) { result ->
            if (result == "1") cb.invoke(true)
            else cb.invoke(false)
        }
    }

    companion object {

        const val KEY_SENDER_ID = "key_sender_id"
        const val KEY_GIFT_DETAIL = "key_gift_detail"
        const val KEY_GIFT_CHANGED_STATUS = "key_changed_to_real"
        const val KEY_GIFT_OBJ_ID = "key_gift_obj_id"
        const val KEY_GIFT_CREDIT = "key_gift_credit"

    }

}