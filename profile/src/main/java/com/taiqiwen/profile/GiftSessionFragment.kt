package com.taiqiwen.profile

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.ui.LoadingDialog
import com.taiqiwen.base_framework.ui.divider.HorizontalDividerItemDecoration
import com.taiqiwen.profile.giftsession.GiftSessionAdapter
import com.taiqiwen.profile.giftsession.animator.FadeInLeftAnimator
import com.test.account_api.AccountServiceUtil


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GiftSessionFragment : Fragment()/*, GiftCardView.OnCheckOut */ {

    //private lateinit var giftCardView: GiftCardView
    private val animator = FadeInLeftAnimator()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GiftSessionViewModel
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewModel = ViewModelProvider(this).get(GiftSessionViewModel::class.java)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (loadingDialog == null) {
            loadingDialog = activity?.let { LoadingDialog(it) }
        }
        loadingDialog?.show()
        viewModel.refreshUserStatus()
        viewModel.fetchGiftList(AccountServiceUtil.getSerVice().getCurUserId()) { result ->
            loadingDialog?.dismiss()
            if (!result) {
                ToastHelper.showToast("网络错误")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gift_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.giftList)
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
            recyclerView.apply {
                itemAnimator = animator.apply {
                    addDuration = 500
                    removeDuration = 500

                }
                adapter = GiftSessionAdapter(context, it)
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


        viewModel.getLoginStatus().observe(viewLifecycleOwner, Observer {isLoggedIn ->
            if (isLoggedIn) {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.GONE
                view.findViewById<View>(R.id.giftList).visibility = View.VISIBLE
            } else {
                view.findViewById<View>(R.id.account_status_hint).visibility = View.VISIBLE
                view.findViewById<View>(R.id.giftList).visibility = View.GONE
            }
        })

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser && this::recyclerView.isInitialized) {
            (recyclerView.adapter as GiftSessionAdapter).dismissAllBubbleViews()
        }
    }

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