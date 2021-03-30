package com.taiqiwen.gift

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.gift.model.CommentInfoVO
import com.taiqiwen.gift.network.GiftApi
import com.test.account_api.AccountServiceUtil

class GiftViewModel : ViewModel() {

    private val title: MutableLiveData<String> = MutableLiveData()

    private val credit: MutableLiveData<Int> = MutableLiveData()

    private val urlList: MutableLiveData<List<String>> = MutableLiveData()

    private val comment: MutableLiveData<CommentInfoVO?> = MutableLiveData()

    private val wantedFriends: MutableLiveData<Int> = MutableLiveData()

    private val collected: MutableLiveData<Int> = MutableLiveData()

    init {
        //initTest()
    }

    fun initTest() {
        title.value = "这是一件测试礼物"
        urlList.value = listOf(
            "http://static.fdc.com.cn/avatar/sns/1486263782969.png",
            "http://static.fdc.com.cn/avatar/sns/1485055822651.png",
            "http://static.fdc.com.cn/avatar/sns/1486194909983.png",
            "http://static.fdc.com.cn/avatar/sns/1486194996586.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486173526402.png",
            "http://static.fdc.com.cn/avatar/sns/1486173639603.png",
            "http://static.fdc.com.cn/avatar/sns/1486172566083.png")
        credit.value = 20
        comment.value = CommentInfoVO("https://b-ssl.duitang.com/uploads/item/201711/24/20171124221428_WNAu5.thumb.700_0.jpeg",
            "测试用户", "很喜欢这件礼物，要是有谁能送我就好了！")
        wantedFriends.value = 5
    }

    fun getCurTitle(): LiveData<String> {
        return title
    }

    fun getCurCredit(): LiveData<Int> {
        return credit
    }

    fun getCurUrlList(): LiveData<List<String>> {
        return urlList
    }

    fun getCommentInfo(): LiveData<CommentInfoVO?> {
        return comment
    }

    fun getFriendsWanted(): LiveData<Int> {
        return wantedFriends
    }

    fun getCollected(): LiveData<Int> {
        return collected
    }

    fun refreshGiftInfo(giftDetailDTO: GiftDetailDTO) {
        title.value = giftDetailDTO.title
        credit.value = giftDetailDTO.credit
        urlList.value = giftDetailDTO.imageUrls
        GiftApi.fetchComment(giftDetailDTO.id) { success, giftCommentResponseDTO ->
            if (success) {
                comment.value = CommentInfoVO(
                    senderIconUrl = giftCommentResponseDTO?.avatarUrl,
                    senderName = giftCommentResponseDTO?.userName,
                    content = giftCommentResponseDTO?.content
                )
            }
        }

        val giftId = giftDetailDTO.id
        val curUserId = AccountServiceUtil.getSerVice().getCurUser()?.userId
        if (giftId != null && curUserId != null) {
            GiftApi.getWantedFriends(giftId, curUserId) { success, curUserCollected, wantedFriendsList ->
                if (success) {
                    wantedFriends.value = wantedFriendsList?.size ?: 0
                    collected.value = if (curUserCollected) 1 else 0
                } else {
                    wantedFriends.value = 0
                    collected.value = 0
                }
            }
        }
    }

    fun collectGift(giftId: String?, cb: ((Int) -> Unit)?) {
        GiftApi.collectGift(giftId, AccountServiceUtil.getSerVice().getCurUser()?.userId) { success ->
            val oldState = collected.value
            if (success && oldState != null) {
                cb?.invoke(1 - oldState)
                collected.value = (1 - oldState)
            }
        }
    }

}