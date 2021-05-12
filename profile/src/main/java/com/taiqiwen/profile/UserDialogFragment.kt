package com.taiqiwen.profile

import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.taiqiwen.base_framework.ui.wheeldialog.base.BaseDialogFragment

class UserDialogFragment : BaseDialogFragment() {

    private var name: TextView? = null
    private var button: Button? = null

    override fun getLayoutID(): Int = R.layout.fragment_user_layout

    override fun onStart() {
        super.onStart()
        //设置对话框显示在底部
        dialog?.window?.setGravity(Gravity.CENTER)
        //设置对话框弹出动画，从底部滑入，从底部滑出
        dialog?.window?.attributes?.windowAnimations = R.style.Dialog_Animation
        //设置让对话框宽度充满屏幕
        dialog?.window?.attributes?.height?.let {
            dialog?.window?.setLayout(
                getScreenWidth(activity) * 9 / 10,
                it
            )
        }
    }

    override fun initView(view: View?) {
        button = view?.findViewById(R.id.button)

    }

    override fun setSubView() {
        name?.text = "ldsfdsf"
    }

    override fun initEvent() {
        button?.setOnClickListener {
            Toast.makeText(context, "但是粉丝地方", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCancel() {
    }

}