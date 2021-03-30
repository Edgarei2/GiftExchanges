package com.taiqiwen.profile

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
import com.taiqiwen.profile.giftsession.GiftSessionAdapter
import com.taiqiwen.profile.giftsession.animator.FadeInLeftAnimator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GiftSessionFragment : Fragment()/*, GiftCardView.OnCheckOut */ {

    //private lateinit var giftCardView: GiftCardView
    private val animator = FadeInLeftAnimator()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GiftSessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
        viewModel = ViewModelProvider(this).get(GiftSessionViewModel::class.java)
        viewModel.getGiftList().observe(this, Observer {
            refreshGiftList()
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUserStatus()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gift_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
/*        giftCardView = view.findViewById(R.id.gc_shop)
        giftCardView.setCardTip("您的礼物将会被送给:")
        giftCardView.setOnCheckOut(Buyer("", "xxx群组",
                "xxxx", "未接受自动退换:3天"),
                this)*/
        recyclerView = view.findViewById(R.id.giftList)
        recyclerView.apply {
            itemAnimator = animator.apply {
                addDuration = 500
                removeDuration = 500

            }
            adapter = GiftSessionAdapter(context, viewModel.getGiftList().value!!)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpacesItemDecoration(100));
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    Log.d("ttest", "  $firstVisibleItemPosition $lastVisiblePosition")
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //暂停
/*                        for (i in firstVisibleItemPosition until lastVisiblePosition) {
                            val holder = recyclerView.findViewHolderForAdapterPosition(i) as GiftSessionAdapter.ViewHolder
                            holder.avatarLayout.postDelayed({
                                val bubbleWindow = holder.wordsBubble
                                if (bubbleWindow?.isShowing == false) {
                                    bubbleWindow.show(holder.avatarLayout, -holder.offSet, holder.offSet)
                                }
                            }, 500)
                        }*/
                    }
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        //滑动
                        (recyclerView.adapter as GiftSessionAdapter).dismissAllBubbleViews()
                    }
                }
            })
        }

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

/*    override fun ok(vid: Int) {
        Toast.makeText(context, "thank you", Toast.LENGTH_LONG).show()
        giftCardView.restore();
    }*/

    private fun refreshGiftList() {

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