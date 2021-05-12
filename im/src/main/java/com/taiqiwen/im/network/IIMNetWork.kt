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
                    @Field("sender_uid") sender: String,
                    @Field("receiver_uid") receiver: String): Observable<SendMessageResponseDTO?>


    @FormUrlEncoded
    @POST("getUnReadMessage")
    fun getUnReadMessage(@Field("user_id") userId: String?): Observable<UnreadMessageResponseDTO?>

    @FormUrlEncoded
    @POST("getGroupChatInfo")
    fun getGroupChatInfo(@Field("user_id") userId: String?): Observable<GroupInfoResponseDTO?>

    @FormUrlEncoded
    @POST("getFriendsDetail")
    fun getFriendsDetail(@Field("user_ids") userIds: String?): Observable<FriendsDetailResponseDTO?>

    @FormUrlEncoded
    @POST("sendGroupMessage")
    fun sendGroupMessage(@Field("group_obj_id") groupObjId: String,
                         @Field("new_message") msg: String,
                         @Field("sender_uid") sender: String?): Observable<SendGroupMessageResponseDTO?>

    @FormUrlEncoded
    @POST("getGroupMember")
    fun getGroupMember(@Field("group_obj_id") groupObjId: String): Observable<GroupMemberResponseDTO?>

    @FormUrlEncoded
    @POST("getCurUserFriends")
    fun getCurUserFriends(@Field("user_id") userId: String?): Observable<FriendsResponseDTO?>

    @FormUrlEncoded
    @POST("updateCurUserScore")
    fun updateCurUserScore(@Field("room_id") roomId: String?,
                           @Field("user_id") userId: String?,
                           @Field("score") score: String?): Observable<UpdateScoreResponseDTO?>

    @FormUrlEncoded
    @POST("endGame")
    fun endGame(@Field("room_id") roomId: String?): Observable<GameResponseDTO?>
}