package com.taiqiwen.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.taiqiwen.profile_api.ProfileServiceUtil
import com.taiqiwen.profile_api.model.FriendListDTO
import com.test.account_api.AccountServiceUtil

class ExtraFunctionActivity : AppCompatActivity() {

    private lateinit var titleBar: CommonTitleBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var friendsList: FriendListDTO
    private lateinit var adapter: ExtraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_function)
        supportActionBar?.hide()
        (intent?.getSerializableExtra(KEY_FRIEND_DATA) as? FriendListDTO)?.apply {
            friendsList = this
        }
        initView()
    }

    private fun initView() {
        titleBar = findViewById(R.id.title_bar)
        titleBar.setBackgroundResource(R.drawable.shape_gradient)
        adapter = ExtraAdapter(friendsList, supportFragmentManager)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    companion object {

        private const val KEY_FRIEND_DATA = "key_friend_data"

        @JvmStatic
        fun start(context: Context?, friendsList: FriendListDTO) {
            if (context == null) return
            else if (!AccountServiceUtil.getSerVice().isLogged()) {
                ProfileServiceUtil.getSerVice().startLoginActivity(context)
            } else {
                val intent = Intent(context, ExtraFunctionActivity::class.java)
                intent.putExtra(KEY_FRIEND_DATA, friendsList)
                context.startActivity(intent)
            }
        }
    }

}