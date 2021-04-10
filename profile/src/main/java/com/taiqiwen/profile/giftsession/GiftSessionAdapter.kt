package com.taiqiwen.profile.giftsession

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beyondsw.lib.GiftServiceUtil
import com.beyondsw.lib.model.GiftSentStatusDTO
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.DialogHelper
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.ui.BubblePopupWindow
import com.taiqiwen.base_framework.ui.UIUtils
import com.taiqiwen.base_framework.ui.ViewUtils
import com.taiqiwen.profile.GiftSessionViewModel
import com.taiqiwen.profile.ProfileApi
import com.taiqiwen.profile.R
import com.test.account_api.AccountServiceUtil

class GiftSessionAdapter(private val context: Context,
                         private val dataSet: MutableList<GiftSentStatusDTO>,
                         private val viewModel: GiftSessionViewModel) :
    RecyclerView.Adapter<GiftSessionAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val giftCardView: com.taiqiwen.profile.ui.GiftCardView = itemView.findViewById(R.id.giftCard)
        val giftLayout: View = itemView.findViewById(R.id.gift_description_layout)
        val avatarLayout: ViewGroup = itemView.findViewById(R.id.avatarLayout)
        val avatar: SimpleDraweeView = itemView.findViewById(R.id.avatar)
        val giftInfoLayout: ViewGroup = itemView.findViewById(R.id.giftMessage)
        val giftIcon: SimpleDraweeView = itemView.findViewById(R.id.gift_icon)
        val giftTitle: TextView = itemView.findViewById(R.id.gift_title)
        val senderName: TextView = itemView.findViewById(R.id.gift_sender)
        val bgTextLayout: View = itemView.findViewById(R.id.layoutForMeasure)
        val refuseView: View = itemView.findViewById(R.id.img_refuse)
    }

    private val bubbles = mutableMapOf<String?, BubblePopupWindow>()
    private val giftObjId = mutableMapOf<Int, String?>()
    private val checkedGifts = mutableSetOf<String?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_gift_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gift = dataSet[position]
        val words = "TA给您的寄语:\n${gift.words}"
        holder.let {
            it.giftCardView.giftCardEvents = object : IGiftCard {
                override fun onSeeMoreButtonClicked() {
                    it.giftInfoLayout.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        postDelayed({
                            ObjectAnimator.ofFloat(it.giftInfoLayout,
                                "alpha",
                                0f, 1f).apply {
                                duration = 2000
                                start()
                            }
                        }, 1000)
                    }
                    showBubble(holder, position, words)
                }
            }
            ProfileApi.fetchGiftDetail(gift.giftId) { gift ->
                it.giftCardView.apply {
                    setMPrice(gift?.credit?.toFloat()?:0f)
                    requestLayout()
                }
                it.giftTitle.text = gift?.title
                it.giftIcon.setImageURI(gift?.getIconUrl())
                giftObjId[position] = gift?.objectId
                if (gift != null) {
                    it.giftLayout.setOnClickListener {
                        GiftServiceUtil.getSerVice().startGiftActivity(context, gift)
                    }
                }
            }
            ProfileApi.fetchUserDetail(gift.sender) { user ->
                it.senderName.text = user?.userName
                it.avatar.setImageURI(user?.avatarUrl)
            }
            it.bgTextLayout.findViewById<TextView>(R.id.textViewForMeasure).text = words
        }
        holder.avatarLayout.setOnClickListener {
            showBubble(holder, position, words)
        }
        holder.giftCardView.setOnCheckOut {
            if (checkedGifts.contains(dataSet[position].objId)) {
                ToastHelper.showToast("您已签收")
            } else {
                ProfileApi.checkoutGift(dataSet[position].objId,
                    giftObjId[position],
                    AccountServiceUtil.getSerVice().getCurUserId()) { result ->
                    if (result == "1") {
                        holder.giftCardView.apply {
                            setButtonCheckText("已签收")
                            invalidate()
                        }
                        ToastHelper.showToast("签收成功")
                        checkedGifts.add(dataSet[position].objId)
                        ObjectAnimator.ofFloat(holder.refuseView,
                            "alpha",
                            1f, 0f).apply {
                            duration = 400
                            start()
                        }
                        viewModel.reduceGiftsNumberBy1()
                    } else {
                        ToastHelper.showToast("网络错误")
                    }
                }
            }
        }
        holder.refuseView.setOnClickListener {
            if (checkedGifts.contains(dataSet[position].objId)) {
                ToastHelper.showToast("您已签收")
                return@setOnClickListener
            }
            DialogHelper.showDialog(context, "确定要拒收该礼物吗", "拒收后该礼物将不在您的列表出现", positiveCb = {
                ProfileApi.declineAllGifts(dataSet[position].objId) { result ->
                    if (result == "1") {
                        checkedGifts.add(dataSet[position].objId)
                        ToastHelper.showToast("拒收成功")
                        dismissBubbleView(dataSet[position].objId)
                        viewModel.reduceGiftsNumberBy1()
                        dataSet.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, dataSet.size)
                    } else {
                        ToastHelper.showToast("网络错误")
                    }
                }
            })

        }
    }

    private fun showBubble(holder: ViewHolder, position: Int, words: String) {
        val bgTextView = holder.bgTextLayout
        bgTextView.post {
            if (!bubbles.containsKey(dataSet[position].objId)) {
                val contentView = LayoutInflater.from(ViewUtils.getActivity(context))
                    .inflate(R.layout.layout_dialog, holder.avatarLayout, false)
                contentView.findViewById<TextView>(R.id.popUpWords).text = words
                val bubbleWidth = bgTextView.width
                val bubbleHeight = bgTextView.height
                val wordsBubble: BubblePopupWindow = BubblePopupWindow(ViewUtils.getActivity(context)).apply {
                    setBubbleView(contentView)
                    setParam(bubbleWidth, bubbleHeight)
                    inAnimTime = VALUE_300
                    setOutAnimTime(50)
                    setAutoDismissDelayMillis(0)
                    isOutsideTouchable = false
                    isFocusable = false
                }
                val avaterHeight = UIUtils.dip2Px(context, AVATAR_HEIGHT).toInt()
                val offSet = (bubbleHeight - avaterHeight) / 2
                if (!wordsBubble.isShowing) {
                    wordsBubble.show(holder.avatarLayout, -offSet, offSet)
                    bubbles[dataSet[position].objId] = wordsBubble
                }
            }
        }
    }

    fun getUncheckedIds(): Pair<List<String?>, List<String?>> {
        val exchangeObjIdList = mutableListOf<String?>()
        val giftObjIdList = mutableListOf<String?>()
        for (i in dataSet.indices) {
            if (!checkedGifts.contains(dataSet[i].objId)) {
                exchangeObjIdList.add(dataSet[i].objId)
                giftObjIdList.add(giftObjId[i])
            }
        }
        return Pair(exchangeObjIdList, giftObjIdList)
    }

    fun dismissBubbleView(objId: String?) {
        val bubble = bubbles[objId]
        if (bubble?.isShowing == true) {
            bubble.dismiss()
        }
    }

    fun dismissAllBubbleViews() {
        for ((k, v) in bubbles) {
            if (v.isShowing) {
                v.dismiss()
            }
        }
        bubbles.clear()
    }

    companion object {

        const val VALUE_300 = 300L
        const val AVATAR_HEIGHT = 60f

    }
}