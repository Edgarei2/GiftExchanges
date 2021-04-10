package com.taiqiwen.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.base_framework.DialogHelper
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.wheeldialog.widget.dialog.WheelDialogFragment
import com.taiqiwen.profile_api.ProfileServiceUtil
import com.test.account_api.AccountServiceUtil
import java.util.ArrayList

class SendGiftActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var receiver: TextView
    private lateinit var chooseFriendButton: Button
    private lateinit var giftName: TextView
    private lateinit var chooseGiftButton: Button
    private lateinit var sendGiftButton: Button
    private lateinit var viewModel: SendGiftViewModel
    private var friendsUidList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_gift)
        friendsUidList = intent.getStringArrayListExtra(KEY_FRIEND_UID)
        viewModel = ViewModelProvider(this).get(SendGiftViewModel::class.java)
        initView()
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateWords(s.toString())
            }
        })
        chooseFriendButton.setOnClickListener {
            val uidList = friendsUidList
            if (uidList == null) {
                ToastHelper.showToast("网络错误")
            } else {
                viewModel.fetchFriendsDetail(uidList) { friends ->
                    DialogHelper.showDefaultWheelDialog(supportFragmentManager,
                        friends.map { it.userName }, object : WheelDialogFragment.OnWheelDialogListener {
                        override fun onClickLeft(dialog: DialogFragment?, value: String?) {
                            dialog?.dismiss()
                        }

                        override fun onClickRight(dialog: DialogFragment?, value: String?) {
                            viewModel.updateReceiver(findFriend(friends, value))
                            dialog?.dismiss()
                        }

                        override fun onValueChanged(dialog: DialogFragment?, value: String?) { }
                    })
                }
            }
        }
        chooseGiftButton.setOnClickListener {
            viewModel.fetchGiftsDetail(AccountServiceUtil.getSerVice().getCurUserId()) { gifts ->
                if (gifts == null) {
                    ToastHelper.showToast("网络错误")
                } else {
                    DialogHelper.showDefaultWheelDialog(supportFragmentManager,
                        gifts.map { it.title }, object : WheelDialogFragment.OnWheelDialogListener {
                        override fun onClickLeft(dialog: DialogFragment?, value: String?) {
                            dialog?.dismiss()
                        }

                        override fun onClickRight(dialog: DialogFragment?, value: String?) {
                            viewModel.updateGift(findGift(gifts, value))
                            dialog?.dismiss()
                        }

                        override fun onValueChanged(dialog: DialogFragment?, value: String?) {}
                    })
                }
            }
        }
        sendGiftButton.setOnClickListener {
            if (viewModel.getSendStatus().value == true) {
                viewModel.sendGift(AccountServiceUtil.getSerVice().getCurUserId()) { result ->
                    if (result) {
                        ToastHelper.showToast("送礼成功")
                        finish()
                    } else {
                        ToastHelper.showToast("网络错误，请稍后再试")
                    }
                }
            } else {
                ToastHelper.showToast("请填写留言，收取人和礼物")
            }
        }
    }

    private fun initView() {
        editText = findViewById(R.id.edit_text_real)
        receiver = findViewById(R.id.receiver_name)
        chooseFriendButton = findViewById(R.id.btn_choose_friend)
        giftName = findViewById(R.id.gift_name)
        chooseGiftButton = findViewById(R.id.btn_choose_gift)
        sendGiftButton = findViewById(R.id.send)

        viewModel.getReceiver().observe(this, Observer {  user ->
            receiver.text = user?.userName
        })
        viewModel.getGift().observe(this, Observer {  gift ->
            giftName.text = gift?.title
        })
        viewModel.getSendStatus().observe(this, Observer {  ok ->
            if (ok) {
                sendGiftButton.setBackgroundResource(R.drawable.round_send_bg)
            } else {
                sendGiftButton.setBackgroundResource(R.drawable.round_send_fail_bg)
            }
        })
    }

    private fun findFriend(friends: List<GiftUser>, name: String?): GiftUser? {
        for (friend in friends) {
            if (friend.userName == name) {
                return friend
            }
        }
        return null
    }

    private fun findGift(gifts: List<GiftDetailDTO>, name: String?): GiftDetailDTO? {
        for (gift in gifts) {
            if (gift.title == name) {
                return gift
            }
        }
        return null
    }

    companion object {

        const val KEY_FRIEND_UID = "friends_uid"

        @JvmStatic
        fun start(context: Context, friendsUidList: List<String>?) {
            val intent = Intent(context, SendGiftActivity::class.java)
            intent.putStringArrayListExtra(KEY_FRIEND_UID, friendsUidList as ArrayList<String>?)
            context.startActivity(intent)
        }

    }

}