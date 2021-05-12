package com.taiqiwen.profile

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.SheetHelper
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.taiqiwen.base_framework.ui.wheeldialog.base.BaseDialogFragment
import com.taiqiwen.im_api.ChatServiceUtil
import com.taiqiwen.im_api.model.GamerDTO
import com.taiqiwen.profile_api.model.FriendListDTO
import com.test.account_api.AccountServiceUtil

class GameStartHintFragment : BaseDialogFragment()  {

    private var choiceLayout: View? = null
    private var roomInfoLayout: View? = null
    private var buttonInviteFriend: Button? = null
    private var buttonFillRoomId: Button? = null
    private var editText: EditText? = null
    private var buttonRoomIdOk: Button? = null

    private var rightButton: View? = null
    private var friendList: List<GiftUser> = emptyList()
    private var friendsSelected: MutableList<Boolean> = mutableListOf()
    private var num = 0
    private val gamerList = mutableListOf(AccountServiceUtil.getSerVice().getCurUser()!!)

    override fun getLayoutID(): Int = R.layout.click_game_hint_layout

    override fun initView(view: View?) {
        choiceLayout = view?.findViewById(R.id.choice_layout)
        roomInfoLayout = view?.findViewById(R.id.room_layout)
        buttonInviteFriend = view?.findViewById(R.id.button_invite_friend)
        buttonFillRoomId = view?.findViewById(R.id.button_fill_room_id)
        editText = view?.findViewById(R.id.edit_box)
        buttonRoomIdOk = view?.findViewById(R.id.edit_ok)
        rightButton = view?.findViewById(R.id.right_button)
    }

    override fun setSubView() {

    }

    override fun initEvent() {
        rightButton?.setOnClickListener {
            dismiss()
        }
        buttonInviteFriend?.setOnClickListener {
            SheetHelper.showSheet(context, "邀请1位好友", friendList, showChecked = true) { item, position ->
                if (position >= friendList.size) return@showSheet
                if (friendsSelected[position]) {
                    friendsSelected[position] = false
                    num--
                } else {
                    friendsSelected[position] = true
                    if (++num == 1) {
                        var uids = ""
                        for (index in friendsSelected.indices) {
                            if (friendsSelected[index]) {
                                gamerList.add(friendList[index])
                                uids += if (uids.isNotEmpty()) {
                                    ",${friendList[index].userId}"
                                } else {
                                    friendList[index].userId
                                }
                            }
                        }
                        val curUser = AccountServiceUtil.getSerVice().getCurUser()
                        ProfileApi.inviteGame(uids, curUser?.userName, curUser?.userId) { channelId ->
                            dismiss()
                            ChatServiceUtil.getSerVice().startGameActivity(context, channelId, "0")
                        }
                    }
                }
            }
        }
        buttonFillRoomId?.setOnClickListener {
            choiceLayout?.visibility = View.GONE
            roomInfoLayout?.visibility = View.VISIBLE
        }
        buttonRoomIdOk?.setOnClickListener {
            val userId = AccountServiceUtil.getSerVice().getCurUserId()
            val roomId = editText?.text.toString()
            ProfileApi.enterGameCheck(userId, roomId) { result, beginNow ->
                if (result == "1") {
                    dismiss()
                    ChatServiceUtil.getSerVice().startGameActivity(context, roomId, beginNow)
                } else {
                    ToastHelper.showToast("房间号输入错误")
                }
            }
        }
    }

    override fun onCancel() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            friendList = (it.getSerializable(KEY_FRIEND_DETAIL) as FriendListDTO).friendList
        }
        repeat(friendList.size) {
            friendsSelected.add(false)
        }
    }

    override fun onStart() {
        super.onStart()
        //设置对话框显示在底部
        dialog?.window?.setGravity(Gravity.CENTER)
        //设置对话框弹出动画，从底部滑入，从底部滑出
        dialog?.window?.attributes?.windowAnimations = R.style.Dialog_Animation
        //设置让对话框宽度充满屏幕
        dialog?.window?.attributes?.height?.let {
            dialog?.window?.setLayout(
                getScreenWidth(activity) * 9 / 10,
                it
            )
        }
    }

    companion object {

        const val KEY_FRIEND_DETAIL = "friend_detail"

    }
}