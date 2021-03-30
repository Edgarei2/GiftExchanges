package com.taiqiwen.base_framework.model

import android.os.Parcelable
import android.text.TextUtils
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Keep
@Parcelize
open class ECUrlModel : Serializable, Parcelable {
    @SerializedName("uri")
    var uri: String? = null

    @SerializedName("url_list")
    var urlList: List<String>? = null

    @SerializedName("width")
    var width = 0

    @SerializedName("height")
    var height = 0

    @SerializedName("url_key")
    var urlKey: String? = null

    @SerializedName("data_size")
    var size : Long = 0         //单位B  视频下载大小

    // 文件md5值，用于校验完整性
    @SerializedName("file_hash")
    var fileHash: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ECUrlModel) return false
        if (if (uri != null) uri != other.uri else other.uri != null) return false
        if (if (urlKey != null) urlKey != other.urlKey else other.urlKey != null) return false
        return if (urlList != null) urlList == other.urlList else other.urlList == null
    }

    override fun hashCode(): Int {
        val key = if (TextUtils.isEmpty(urlKey)) uri else urlKey
        var result = key?.hashCode() ?: 0
        result = 31 * result + if (urlList != null) urlList.hashCode() else 0
        return result
    }
}