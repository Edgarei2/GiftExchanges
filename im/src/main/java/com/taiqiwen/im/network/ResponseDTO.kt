package com.taiqiwen.im.network

import com.google.gson.annotations.SerializedName
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.model.Group

class SessionObjIdResponseDTO(
    @SerializedName("session_objId_map") var sessionObjMap: Map<String, String>?
)

class SendMessageResponseDTO(
    @SerializedName("send_result") var result: String?
)

class UnreadMessageResponseDTO(
    @SerializedName("unread_message") var unreadMessage: Map<String, List<String>>?
)

class GroupInfoResponseDTO(
    @SerializedName("group_detail") var groupDetail: List<Group>?
)

class FriendsDetailResponseDTO(
    @SerializedName("friends_detail") var friendsDetail: Map<String, GiftUser>?
)

class SendGroupMessageResponseDTO(
    @SerializedName("send_result") var result: String?
)

class GroupMemberResponseDTO(
    @SerializedName("group_member_info") var memberInfo: Map<String, GiftUser>?
)

class FriendsResponseDTO(
    @SerializedName("cur_friends") var friends: List<String>?
)

class UpdateScoreResponseDTO(
    @SerializedName("update_result") var result: String?
)

class GameResponseDTO(
    @SerializedName("winner_id") var winnerUserId: String?
)