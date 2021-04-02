package com.taiqiwen.base_framework.ui.selectionsheet

import android.os.Bundle

interface ISheetData {

    fun getKey(): String

    fun getValue(): String

    fun getIconUrl(): String?

    fun getExtraInfo(): Bundle?

}