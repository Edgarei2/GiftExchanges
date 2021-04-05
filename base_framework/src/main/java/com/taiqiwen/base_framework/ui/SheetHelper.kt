package com.taiqiwen.base_framework.ui

import android.content.Context
import android.os.Bundle
import com.taiqiwen.base_framework.R
import com.taiqiwen.base_framework.ui.selectionsheet.ISheetData
import com.taiqiwen.base_framework.ui.selectionsheet.SheetSelection
import com.taiqiwen.base_framework.ui.selectionsheet.SheetSelectionItem

object SheetHelper {

    private const val MIN_SHEET_ITEM = 6
    const val KEY_RECEIVER = "key_receiver"
    const val KEY_CHECKOUT_STATUS = "key_receiver"


    @JvmStatic
    fun showSheet(context: Context?,
                  title: String,
                  dataSource: List<ISheetData>?,
                  searchNotFoundText: String = "未找到相关结果",
                  noItemHintText: String = "这里为空",
                  onClickListener: (SheetSelectionItem, Int) -> Unit) {
        if (context == null || dataSource == null) {
            return
        }
        val realDataSource = mutableListOf<ISheetData>().apply {
            addAll(dataSource)
        }
        if (realDataSource.size < MIN_SHEET_ITEM) {
            for (index in realDataSource.size until MIN_SHEET_ITEM) {
                if (index == 0) {
                    realDataSource.add(EmptySheetData(true, noItemHintText))
                } else {
                    realDataSource.add(EmptySheetData())
                }
            }
        }
        SheetSelection.Builder(context)
            .title(title)
            .items(
                source = realDataSource,
                mapper = {
                    SheetSelectionItem(
                        key = it.getKey(),
                        value = it.getValue(),
                        iconUrl = it.getIconUrl(),
                        extraInfo = it.getExtraInfo()
                    )
                }
            )
            .showDraggedIndicator(true)
            .searchEnabled(true)
            .searchNotFoundText(searchNotFoundText)
            .theme(R.style.Theme_Custom_SheetSelection)
            .onItemClickListener(onClickListener)
            .show()
    }

}

class EmptySheetData(val firstItem: Boolean = false, val hintText: String = ""): ISheetData {
    override fun getKey(): String = "key_empty"

    override fun getValue(): String = if (firstItem) hintText else ""

    override fun getIconUrl(): String? = null

    override fun getExtraInfo(): Bundle? = null
}