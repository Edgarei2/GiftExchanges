package com.taiqiwen.base_framework.ui.selectionsheet

import android.content.Context
import com.taiqiwen.base_framework.R

object SheetHelper {

    @JvmStatic
    fun <T : ISheetData> showSheet(context: Context?,
                                   title: String,
                                   dataSource: List<T>?,
                                   searchNotFoundText: String = "未找到相关结果",
                                   onClickListener: (SheetSelectionItem, Int) -> Unit) {
        if (context == null || dataSource == null) {
            return
        }
        SheetSelection.Builder(context)
            .title(title)
            .items(
                source = dataSource,
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