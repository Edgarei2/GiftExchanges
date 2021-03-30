package com.taiqiwen.base_framework.ui.selectionsheet

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SheetSelectionItem(
    val key: String,
    val value: String,
    val iconUrl: String? = null,
    val extraInfo: Bundle? = null
) : Parcelable