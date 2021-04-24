package com.taiqiwen.im.network

import com.google.gson.annotations.SerializedName

class SessionObjIdResponseDTO(
    @SerializedName("session_objId_map") var sessionObjMap: Map<String, String>?
)

class SendMessageResponseDTO(
    @SerializedName("send_result") var result: String?
)