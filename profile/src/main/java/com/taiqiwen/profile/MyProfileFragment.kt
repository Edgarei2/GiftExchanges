package com.taiqiwen.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beyondsw.lib.GiftServiceUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.ShareViewModel
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.SheetHelper
import com.taiqiwen.base_framework.ui.SheetHelper.KEY_CHECKOUT_STATUS
import com.taiqiwen.im_api.ChatServiceUtil
import com.taiqiwen.profile.ui.AvatarLayout
import com.taiqiwen.profile_api.model.FriendListDTO
import com.test.account_api.AccountServiceUtil
import kotlin.concurrent.thread


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var ivBackGround: SimpleDraweeView
    private lateinit var avatarLayout: AvatarLayout
    private lateinit var viewModel: MyProfileViewModel
    private lateinit var shareViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            dynamicRefresh()
        }
    }

    override fun onResume() {
        super.onResume()
        dynamicRefresh()
    }

    private fun dynamicRefresh() {
        val curUserId = AccountServiceUtil.getSerVice().getCurUser()?.userId
        AccountServiceUtil.getSerVice().refresh(curUserId) {
            viewModel.refreshUserStatus { result ->
                if (result) {
                    thread {
                        Thread.sleep(2500)
                        shareViewModel.friendsUidList.postValue(viewModel.getFriends().value)
                    }
                    //shareViewModel.friendsUidList.value = viewModel.getFriends().value
                } else {
                    ToastHelper.showToast("网络错误")
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)

        ivBackGround = view.findViewById(R.id.iv_bg)
        avatarLayout = view.findViewById(R.id.avatar)
        viewModel.getUserAvatarUrl().observe(viewLifecycleOwner, Observer {
            ivBackGround.setImageURI(it)
            avatarLayout.setImage(it)
        })

        viewModel.getLoginStatus().observe(viewLifecycleOwner, Observer {isLoggedIn ->
            if (isLoggedIn) {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.GONE
                view.findViewById<View>(R.id.user_profile).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.VISIBLE
                view.findViewById<View>(R.id.user_profile).visibility = View.GONE
            }
        })

        viewModel.getUserName().observe(viewLifecycleOwner, Observer { userName ->
            view.findViewById<TextView>(R.id.name).text = userName
        })

        viewModel.getCredit().observe(viewLifecycleOwner, Observer { credit ->
            view.findViewById<TextView>(R.id.credit).text = credit.toString()
        })

        viewModel.getFriends().observe(viewLifecycleOwner, Observer { friends ->
            view.findViewById<TextView>(R.id.friends).text = friends?.size.toString()
        })

        viewModel.getCollection().observe(viewLifecycleOwner, Observer { collections ->
            view.findViewById<TextView>(R.id.collected).text = collections?.size.toString()
            view.findViewById<View>(R.id.collected_area).setOnClickListener {
                SheetHelper.showSheet(context, "收藏的礼物", collections, noItemHintText = "您还没有收藏礼物") { item, position ->
                    if (collections != null && position < collections.size) {
                        GiftServiceUtil.getSerVice().startGiftActivity(context!!, collections[position])
                    }
                }
            }
        })
        view.findViewById<View>(R.id.credit_area).setOnClickListener {
            ToastHelper.showToast("您的积分为${viewModel.getCredit().value?:0}")
        }
        shareViewModel.friendsUidList.observe(viewLifecycleOwner, Observer {  list ->
            if (list == null) return@Observer
            viewModel.fetchFriendsDetail(list) { users ->
                val map = mutableMapOf<String, GiftUser>()
                for (user in users) {
                    map[user.userId] = user
                }
                shareViewModel.friendsDetailList.value = map
            }
        })
        view.findViewById<View>(R.id.friends_area).setOnClickListener {
            val users = shareViewModel.friendsDetailList.value?.values?.toList() ?: return@setOnClickListener
            SheetHelper.showSheet(context, "我的朋友", users, noItemHintText = "您还没有朋友") { item, position ->
                if (position >= users.size) return@showSheet
                shareViewModel.sessionExtra.value = users[position]
                val channelId = getChannelId(AccountServiceUtil.getSerVice().getCurUserId(), users[position].userId)
                ChatServiceUtil.getSerVice().startChatActivity(context,
                    users[position],
                    channelId,
                    shareViewModel.channelObjIdMap.value?.get(channelId)
                )
            }
        }
        view.findViewById<View>(R.id.item2).setOnClickListener {
            val userId = AccountServiceUtil.getSerVice().getCurUserId()
            viewModel.fetchSentGiftsInfo(userId) { giftSentStatusDetailDTOList ->
                SheetHelper.showSheet(context, "送出的礼物", giftSentStatusDetailDTOList, noItemHintText = "您还没有送出礼物") { item, position ->
                    if (position < giftSentStatusDetailDTOList?.size?:0) {
                        val receiver = giftSentStatusDetailDTOList?.get(position)?.receiver
                        val checkoutStatus = item.extraInfo?.getString(KEY_CHECKOUT_STATUS)
                        viewModel.fetchCertainFriendName(receiver) { receiverName ->
                            when (checkoutStatus) {
                                "0" -> ToastHelper.showToast("${receiverName}还未签收您的礼物")
                                "1" -> ToastHelper.showToast("${receiverName}已经签收您的礼物")
                                "2" -> ToastHelper.showToast("${receiverName}拒收了您的礼物")
                            }
                        }
                    }
                }
            }
        }
        view.findViewById<View>(R.id.item1).setOnClickListener {
            context?.let { it1 -> GiftsGalleryActivity.start(it1) }
        }
        view.findViewById<View>(R.id.item3).setOnClickListener {
            ExtraFunctionActivity.start(
                context, FriendListDTO(shareViewModel.friendsDetailList.value?.values?.toList() ?: emptyList())
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window: Window? = activity?.window
        window?.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    private fun getChannelId(curUserId: String?, uid: String?) : String {
        return if (curUserId == null || uid == null) ""
        else if (curUserId < uid) "${curUserId}_${uid}" else "${uid}_${curUserId}"
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}