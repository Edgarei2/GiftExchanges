package com.taiqiwen.profile.giftsession

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taiqiwen.base_framework.ui.BubblePopupWindow
import com.taiqiwen.base_framework.ui.UIUtils
import com.taiqiwen.base_framework.ui.ViewUtils
import com.taiqiwen.profile.R
import com.taiqiwen.profile_api.model.GiftItem

class GiftSessionAdapter(private val context: Context, private val dataSet: MutableList<GiftItem>) :
    RecyclerView.Adapter<GiftSessionAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val giftCardView: com.taiqiwen.profile.ui.GiftCardView = itemView.findViewById(R.id.giftCard)
        val avatarLayout: ViewGroup = itemView.findViewById(R.id.avatarLayout)
        val avatar: ImageView = avatarLayout.findViewById(R.id.avatar)
        val giftInfoLayout: ViewGroup = itemView.findViewById(R.id.giftMessage)
        val giftTitle: TextView = giftInfoLayout.findViewById(R.id.giftTitle)
        val giftName: TextView = giftInfoLayout.findViewById(R.id.giftName)
        val giftDetailButton: Button = giftInfoLayout.findViewById(R.id.giftDetail)
        val bgTextLayout: View = itemView.findViewById(R.id.layoutForMeasure)
    }

    private val bubbles = mutableMapOf<Int, BubblePopupWindow>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_gift_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gift = dataSet[position]
        holder.let {
            it.giftCardView.giftCardEvents = object : IGiftCard {
                override fun onSeeMoreButtonClicked() {
                    it.giftInfoLayout.visibility = View.VISIBLE
                }
            }
            it.giftTitle.text = gift.sender + " 赠送给您一件礼物"
            it.giftName.text = gift.giftName
            it.giftDetailButton.setOnClickListener {
                // jump by schema
            }
            it.bgTextLayout.findViewById<TextView>(R.id.textViewForMeasure).text = gift.words
        }
        holder.avatarLayout.setOnClickListener {
            val bgTextView = holder.bgTextLayout
            bgTextView.post {
                if (!bubbles.containsKey(position)) {
                    val contentView = LayoutInflater.from(ViewUtils.getActivity(context))
                            .inflate(R.layout.layout_dialog, holder.avatarLayout, false)
                    contentView.findViewById<TextView>(R.id.popUpWords).text = gift.words
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
                        bubbles[position] = wordsBubble
                    }
                }
            }
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