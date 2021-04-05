package com.taiqiwen.profile

import androidx.lifecycle.ViewModel

class GiftsGalleryViewModel : ViewModel()  {

    fun getFriendsCircleList(): List<Pair<String, List<String>>> {
        val list = mutableListOf<Pair<String, List<String>>>()

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
        return list
    }

}