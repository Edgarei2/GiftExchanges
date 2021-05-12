package com.taiqiwen.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.taiqiwen.base_framework.DialogHelper
import com.taiqiwen.base_framework.ui.wheeldialog.base.BaseDialogFragment
import com.taiqiwen.profile.GameStartHintFragment.Companion.KEY_FRIEND_DETAIL
import com.taiqiwen.profile_api.model.FriendListDTO
import com.test.account_api.AccountServiceUtil

class ExtraAdapter(private val friendList: FriendListDTO,
                   private val fragmentManager: FragmentManager) : RecyclerView.Adapter<ExtraAdapter.BaseViewHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.extra_funtion_view_holder_layout, parent, false)
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int = 2


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (position) {
            MINI_GAME_VIEW_TYPE -> {
                holder.typeName.text = "小游戏"
                holder.firstIcon.setImageURI("https://img.zcool.cn/community/011bb05783600e0000018c1bb7bb1d.jpg@1280w_1l_2o_100sh.jpg")
                holder.firstEntry.text = "2048"
                holder.containerView.setOnClickListener {
                    if (!AccountServiceUtil.getSerVice().isLogged()) return@setOnClickListener
                    BaseDialogFragment.newInstance<GameStartHintFragment>(
                        GameStartHintFragment::class.java,
                        Bundle().apply {
                            putBoolean(BaseDialogFragment.DIALOG_BG_TRANSPARENT, false)
                            putSerializable(KEY_FRIEND_DETAIL, friendList)
                        }
                    ).show(fragmentManager, null)
                }
            }
            ACCOUNT_SERVICE -> {
                holder.typeName.text = "账号"
                holder.firstIcon.setImageURI("http://pic.51yuansu.com/pic2/cover/00/49/77/58162f2349e25_610.jpg")
                holder.firstEntry.text = "退出登录"
                holder.containerView.setOnClickListener {
                    DialogHelper.showDialog(context, "确认要退出吗", "退出当前账号", positiveCb = {
                        AccountServiceUtil.getSerVice().setCurUser(null)
                    })
                }
            }
        }
    }

    open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val containerView: View = view.findViewById(R.id.layout)
        val typeName: TextView = view.findViewById(R.id.type_info)
        val firstIcon: SimpleDraweeView = view.findViewById(R.id.group_icon)
        val firstEntry: TextView = view.findViewById(R.id.group_name)
    }


    companion object {
        const val MINI_GAME_VIEW_TYPE = 0
        const val ACCOUNT_SERVICE = 1
    }
}