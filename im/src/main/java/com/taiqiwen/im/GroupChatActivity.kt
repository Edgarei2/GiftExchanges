package com.taiqiwen.im

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.taiqiwen.base_framework.ShareViewModel
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.taiqiwen.im_api.model.Group
import com.taiqiwen.profile_api.ProfileServiceUtil
import com.test.account_api.AccountServiceUtil

class GroupChatActivity : AppCompatActivity() {

    private lateinit var titleBar: CommonTitleBar
    private lateinit var leftButton: View
    private lateinit var rightButton: View
    private lateinit var fragmentHolder: ViewGroup
    private lateinit var group: Group
    private lateinit var chatFragment: GroupChatFragment
    private lateinit var detailFragment: GroupDetailFragment
    private lateinit var viewModel: GroupChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        (intent?.getSerializableExtra(KEY_GROUP_DETAIL) as Group).apply {
            group = this
        }
        initView()
        viewModel = ViewModelProvider(this).get(GroupChatViewModel::class.java)
        viewModel.fragmentStatus.observe(this, Observer {  status ->
            if (status == 0) {
                titleBar.setCenterText(group.name)
                leftButton.setOnClickListener {
                    finish()
                }
                rightButton.visibility = View.VISIBLE
                rightButton.setOnClickListener {
                    gotoDetailFragment()
                }
            } else if (status == 1) {
                titleBar.setCenterText("群详情")
                leftButton.setOnClickListener {
                    gotoChatFragment()
                }
                rightButton.visibility = View.GONE
            }
        })
        viewModel.fragmentStatus.value = 0

        gotoChatFragment()
        supportActionBar?.hide()
        titleBar.setBackgroundResource(R.drawable.shape_gradient)
    }

    private fun initView() {
        titleBar = findViewById(R.id.title_bar)
        leftButton = titleBar.leftImageButton
        rightButton = titleBar.rightImageButton
        fragmentHolder = findViewById(R.id.content)
        chatFragment = GroupChatFragment.newInstance(group)
        detailFragment = GroupDetailFragment.newInstance(group)
        val fragmentManager = supportFragmentManager
        with(fragmentManager.beginTransaction()) {
            add(R.id.content, chatFragment)
            add(R.id.content, detailFragment)
            commit()
        }
    }

    private fun gotoDetailFragment() {
        viewModel.fragmentStatus.value = 1
        val fragmentManager = supportFragmentManager
        with(fragmentManager.beginTransaction()) {
            hide(chatFragment)
            show(detailFragment)
            commit()
        }
    }

    private fun gotoChatFragment() {
        viewModel.fragmentStatus.value = 0
        val fragmentManager = supportFragmentManager
        with(fragmentManager.beginTransaction()) {
            show(chatFragment)
            hide(detailFragment)
            commit()
        }
    }

    companion object {

        private const val KEY_GROUP_DETAIL = "key_group_info"

        fun start(context: Context?, group: Group) {
            if (context == null) {
                return
            } else if (!AccountServiceUtil.getSerVice().isLogged()) {
                ProfileServiceUtil.getSerVice().startLoginActivity(context)
            } else {
                val intent = Intent(context, GroupChatActivity::class.java)
                intent.putExtras(Bundle().apply {
                    putSerializable(KEY_GROUP_DETAIL, group)
                })
                context.startActivity(intent)
            }
        }
    }
}