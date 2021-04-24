package com.taiqiwen.im.network

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IIMNetWork {

    @FormUrlEncoded
    @POST("getChannelObjIdMap")
    fun getChannelObjIdMap(@Field("channel_ids") channelIds: String?): Observable<SessionObjIdResponseDTO?>

    @FormUrlEncoded
    @POST("sendMessage")
    fun sendMessage(@Field("channel_obj_id") channelId: String,
                    @Field("new_message") msg: String,
                    @Field("sender_uid") sender: String): Observable<SendMessageResponseDTO?>

}