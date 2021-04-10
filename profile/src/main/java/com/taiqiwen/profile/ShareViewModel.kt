package com.taiqiwen.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.base_framework.model.GiftUser

class ShareViewModel : ViewModel() {

    val friendsUidList: MutableLiveData<List<String>?> = MutableLiveData()

    val words: MutableLiveData<String?> = MutableLiveData()

    val receiver: MutableLiveData<GiftUser?> = MutableLiveData()

    val gift: MutableLiveData<GiftDetailDTO?> = MutableLiveData()



}