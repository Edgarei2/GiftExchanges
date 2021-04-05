package com.taiqiwen.base_framework.ui.selectionsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.R
import com.taiqiwen.base_framework.ui.SheetHelper
import com.taiqiwen.base_framework.ui.SheetHelper.KEY_CHECKOUT_STATUS
import kotlinx.android.extensions.LayoutContainer

typealias OnItemSelectedListener = (item: SheetSelectionItem, position: Int) -> Unit

class SheetSelectionAdapter(
    private val source: List<SheetSelectionItem>,
    private val selectedPosition: Int,
    private val searchNotFoundText: String,
    private val onItemSelectedListener: OnItemSelectedListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<SheetSelectionItem> = source

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int =
        when (items[position].key) {
            KEY_SEARCH_NOT_FOUND -> R.layout.row_empty_item
            else -> R.layout.row_selection_item
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.row_selection_item -> ItemViewHolder(view)
            R.layout.row_empty_item -> EmptyViewHolder(view, searchNotFoundText)
            else -> throw IllegalAccessException("Item view type doesn't match.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.onBindView(
                item = items[position],
                position = position,
                selected = position == selectedPosition,
                onItemSelectedListener = onItemSelectedListener
            )
        }
    }

    fun search(keyword: String?) {
        if (keyword.isNullOrBlank()) {
            updateItems(source)
        } else {
            val searchResult = source.filter { it.value.contains(keyword, true) }
            if (searchResult.isEmpty()) {
                updateItems(
                    listOf(
                        SheetSelectionItem(KEY_SEARCH_NOT_FOUND, searchNotFoundText)
                    )
                )
            } else {
                updateItems(searchResult)
            }
        }
    }

    private fun updateItems(items: List<SheetSelectionItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun onBindView(
            item: SheetSelectionItem,
            position: Int,
            selected: Boolean,
            onItemSelectedListener: OnItemSelectedListener?
        ) {
/*            val selectedIcon = if (selected) R.drawable.ic_check else 0
            val textViewItem = containerView.findViewById<TextView>(R.id.textViewItem)
            textViewItem.setCompoundDrawablesWithIntrinsicBounds(item.icon ?: 0, 0, selectedIcon, 0)
            textViewItem.text = item.value*/
            val textView = containerView.findViewById<TextView>(R.id.gift_name)
            containerView.findViewById<SimpleDraweeView>(R.id.gift_icon).setImageURI(item.iconUrl)
            textView.text = item.value
            containerView.setOnClickListener {
                onItemSelectedListener?.invoke(item, position)
            }
            //处理业务具体逻辑
            val extraInfo = item.extraInfo
            extraInfo?.getString(KEY_CHECKOUT_STATUS)?.let { checkoutStatus ->
                containerView.context?.let {
                    if (checkoutStatus == "0") {
                        textView.setTextColor(it.resources.getColor(R.color.colorAccent))
                    } else {
                        textView.setTextColor(it.resources.getColor(R.color.checked_gift))
                    }
                }

            }
        }
    }

    class EmptyViewHolder(override val containerView: View, emptyText: String) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        init {
            containerView.findViewById<TextView>(R.id.textViewItem).text = emptyText
        }
    }

    companion object {
        const val KEY_SEARCH_NOT_FOUND = "SheetSelectionAdapter:search_not_found"
    }
}