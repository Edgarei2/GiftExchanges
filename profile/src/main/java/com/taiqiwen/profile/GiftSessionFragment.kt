package com.taiqiwen.profile

import android.app.Activity
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondsw.lib.model.GiftSentStatusDTO
import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.ButtonEnum
import com.nightonke.boommenu.Piece.PiecePlaceEnum
import com.taiqiwen.base_framework.DialogHelper
import com.taiqiwen.base_framework.EventBusWrapper
import com.taiqiwen.base_framework.ShareViewModel
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.model.GiftChangedEvent
import com.taiqiwen.base_framework.ui.LoadingDialog
import com.taiqiwen.base_framework.ui.MyItemAnimator
import com.taiqiwen.base_framework.ui.boombutton.BuilderManager
import com.taiqiwen.base_framework.ui.boombutton.DefaultOnBoomListener
import com.taiqiwen.base_framework.ui.divider.HorizontalDividerItemDecoration
import com.taiqiwen.profile.giftsession.GiftSessionAdapter
import com.taiqiwen.profile_api.event.LoginSuccessEvent
import com.test.account_api.AccountServiceUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GiftSessionFragment : Fragment()/*, GiftCardView.OnCheckOut */ {

    //private lateinit var giftCardView: GiftCardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GiftSessionViewModel
    private var loadingDialog: LoadingDialog? = null
    private lateinit var button: BoomMenuButton
    private lateinit var shareViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProvider(this).get(GiftSessionViewModel::class.java)
        viewModel.refreshUserStatus()
        EventBusWrapper.register(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refreshPage(activity)
    }

    private fun refreshPage(activity: Activity?) {
        if (viewModel.getLoginStatus().value != true) {
            return
        }
        if (loadingDialog == null) {
            loadingDialog = activity?.let { LoadingDialog(it) }
        }
        loadingDialog?.show()
        viewModel.fetchGiftList(AccountServiceUtil.getSerVice().getCurUserId()) { result, number ->
            loadingDialog?.dismiss()
            if (!result) {
                ToastHelper.showToast("网络错误")
            } else {
                EventBusWrapper.post(GiftChangedEvent(number))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gift_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.giftList)
        button = view.findViewById(R.id.bmb_button)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)

        val paint = Paint()
        paint.strokeWidth = 5f
        paint.color = Color.BLUE
        paint.isAntiAlias = true
        paint.pathEffect = DashPathEffect(floatArrayOf(25.0f, 25.0f), 0F)
        recyclerView.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context)
                .paint(paint)
                .build()
        )
        viewModel.getGiftList().observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                view.findViewById<View>(R.id.giftList).visibility = View.GONE
                view.findViewById<View>(R.id.gift_status_hint).visibility = View.VISIBLE
            }
            viewModel.updateCheckedGiftsNumber(it.size)
            val dataSet = mutableListOf<GiftSentStatusDTO>().apply {
                addAll(it)
            }
            recyclerView.apply {
                adapter = GiftSessionAdapter(context, dataSet, viewModel)
                itemAnimator = MyItemAnimator().apply {
                    removeDuration = 200
                }
                layoutManager = LinearLayoutManager(context)
                //addItemDecoration(SpacesItemDecoration(100));
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        recyclerView.layoutManager as LinearLayoutManager
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            (recyclerView.adapter as GiftSessionAdapter).dismissAllBubbleViews()
                        }
                    }
                })
            }
        })

        viewModel.getCheckedGiftsNumber().observe(viewLifecycleOwner, Observer {  number ->
            EventBusWrapper.post(GiftChangedEvent(number))
        })

        viewModel.getLoginStatus().observe(viewLifecycleOwner, Observer {isLoggedIn ->
            if (isLoggedIn) {
                refreshPage(activity)
                view.findViewById<View>(R.id.account_status_hint).visibility = View.GONE
                view.findViewById<View>(R.id.giftList).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.VISIBLE
                view.findViewById<View>(R.id.giftList).visibility = View.GONE
            }
        })

        BuilderManager.setImageResources(intArrayOf(R.drawable.give, R.drawable.refresh, R.drawable.accept, R.drawable.decline))
        with(button){
            buttonEnum = ButtonEnum.Ham
            piecePlaceEnum = PiecePlaceEnum.HAM_4
            buttonPlaceEnum = ButtonPlaceEnum.HAM_4
            addBuilder(BuilderManager.getHamButtonBuilder(R.string.give, R.string.give_des))
            addBuilder(BuilderManager.getHamButtonBuilder(R.string.refresh_gift_list, R.string.refresh_gift_list_des))
            addBuilder(BuilderManager.getHamButtonBuilder(R.string.checkout_all, R.string.checkout_all_des))
            addBuilder(BuilderManager.getHamButtonBuilder(R.string.decline_all, R.string.decline_all_des))
        }
        button.onBoomListener = object : DefaultOnBoomListener() {

            override fun onClicked(index: Int, boomButton: BoomButton?) {
                when (index) {
                    0 -> context?.let { SendGiftActivity.start(it, shareViewModel.friendsUidList.value) }
                    1 -> refreshPage(activity)
                    2 -> {
                        val data = (recyclerView.adapter as GiftSessionAdapter).getUncheckedIds()
                        viewModel.checkoutAllGifts(data.first,
                            data.second,
                            AccountServiceUtil.getSerVice().getCurUserId()) { result ->
                            if (result) {
                                ToastHelper.showToast("签收成功")
                                refreshPage(activity)
                            } else {
                                ToastHelper.showToast("网络错误")
                            }
                        }
                    }
                    3 -> {
                        activity?.let {
                            DialogHelper.showDialog(it, "确认全部拒收吗",
                                "拒收的礼物不会再出现在您的列表",
                                negativeText = "我再想想",
                                positiveCb = {
                                    val data = (recyclerView.adapter as GiftSessionAdapter).getUncheckedIds()
                                    viewModel.declineAllGifts(data.first) { result ->
                                        if (result) {
                                            ToastHelper.showToast("拒收成功")
                                            refreshPage(activity)
                                        } else {
                                            ToastHelper.showToast("网络错误")
                                        }
                                    }
                                })
                        }
                    }
                }
            }

            override fun onBoomWillShow() {
                cleanBubbles()
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            cleanBubbles()
        } else {
            viewModel.setLoginStatus(AccountServiceUtil.getSerVice().isLogged())
        }
    }

    private fun cleanBubbles() {
        if (this::recyclerView.isInitialized) {
            (recyclerView.adapter as? GiftSessionAdapter)?.dismissAllBubbleViews()
        }
    }

    override fun onDestroy() {
        EventBusWrapper.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logInSuccessEvent(event: LoginSuccessEvent) {
        viewModel.setLoginStatus(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logCheckoutEvent(event: GiftChangedEvent) { }

    companion object {
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
                GiftSessionFragment().apply {
                    arguments = Bundle().apply {
                        //putString(ARG_PARAM1, param1)
                        //putString(ARG_PARAM2, param2)
                    }
                }
    }
}