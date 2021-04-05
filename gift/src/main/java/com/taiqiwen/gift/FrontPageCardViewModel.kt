package com.taiqiwen.gift

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.gift.network.GiftApi

class FrontPageCardViewModel : ViewModel()  {

    private val fetchStatus: MutableLiveData<Int> = MutableLiveData()

    private val giftIdList: MutableLiveData<List<String?>> = MutableLiveData()

    private val images: MutableLiveData<List<String?>> = MutableLiveData()

    private val labels: MutableLiveData<List<String?>> = MutableLiveData()

    fun getFetchStatus():  LiveData<Int> {
        return fetchStatus
    }

    fun getGiftIdList(): LiveData<List<String?>> {
        return giftIdList
    }

    fun getImages(): LiveData<List<String?>> {
        return images
    }

    fun getLabels(): LiveData<List<String?>> {
        return labels
    }

    fun refreshFrontCards(adapter: CardAdapter) {
        GiftApi.fetchFrontPageCards { success, list ->
            if (success) {
                giftIdList.value = list.map { it.id }
                images.value = list.map { it.imageUrls?.get(0) }
                labels.value = list.map { it.title }
                adapter.setGiftDetails(list)
                fetchStatus.value = 1
            } else {
                fetchStatus.value = 2
            }
        }
    }

    fun refresh2FrontCards(cb: ((List<GiftDetailDTO>) -> Unit)? = null) {
        GiftApi.fetchFrontPageCards { success, list ->
            if (success) {
                giftIdList.value = list.map { it.id }
                images.value = list.map { it.imageUrls?.get(0) }
                labels.value = list.map { it.title }
                fetchStatus.value = 1
                cb?.invoke(list)
            } else {
                fetchStatus.value = 2
            }
        }
    }

}

data class FrontCardDTO(
    val giftId: Int,
    val imageUrl: String,
    val label: String
)