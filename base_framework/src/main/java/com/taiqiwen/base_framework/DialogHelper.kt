package com.taiqiwen.base_framework

import android.content.Context
import android.graphics.Color
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt



object DialogHelper {

    fun showDialog(
        context: Context,
        title: String,
        detail: String,
        positiveText: String = "确认",
        negativeText: String = "取消",
        positiveCb: (() -> Unit)? = null,
        negativeCb: (() -> Unit)? = null) {

        MaterialStyledDialog.Builder(context)
            .setHeaderDrawable(R.drawable.header)
            .setIcon(
                IconicsDrawable(context, MaterialDesignIconic.Icon.gmi_card_giftcard).apply {
                    colorInt = Color.WHITE
                }
            )
            .withDialogAnimation(true)
            .setTitle(title)
            .setDescription(detail)
            .setPositiveText(positiveText)
            .onPositive {
                positiveCb?.invoke()
            }
            .setNegativeText(negativeText)
            .onNegative {
                negativeCb?.invoke()
            }
            .show()
    }

}