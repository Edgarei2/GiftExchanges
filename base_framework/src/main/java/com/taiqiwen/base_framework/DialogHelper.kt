package com.taiqiwen.base_framework

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.colorInt
import com.taiqiwen.base_framework.ui.wheeldialog.widget.dialog.WheelDialogFragment
import java.util.ArrayList


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

    fun showDefaultWheelDialog(fragmentManager: FragmentManager,
                               itemList: List<String?>,
                               listener: WheelDialogFragment.OnWheelDialogListener) {
        val realList = itemList.map { it.toString() }.toTypedArray()
        val bundle = Bundle().apply {
            putBoolean(WheelDialogFragment.DIALOG_BACK, false)
            putBoolean(WheelDialogFragment.DIALOG_CANCELABLE, false)
            putBoolean(WheelDialogFragment.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, false)
            putString(WheelDialogFragment.DIALOG_LEFT, "取消")
            putString(WheelDialogFragment.DIALOG_RIGHT, "确定")
            putStringArray(WheelDialogFragment.DIALOG_WHEEL, realList)
        }
        WheelDialogFragment.newInstance<WheelDialogFragment>(WheelDialogFragment::class.java, bundle).apply {
            setWheelDialogListener(listener)
            show(fragmentManager, "")
        }
    }

}