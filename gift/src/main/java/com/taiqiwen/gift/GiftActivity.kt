package com.taiqiwen.gift

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.hitomi.tilibrary.style.index.NumberIndexIndicator
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator
import com.hitomi.tilibrary.transfer.TransferConfig
import com.hitomi.tilibrary.transfer.Transferee
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.beyondsw.lib.model.GiftDetailDTO
import com.taiqiwen.profile_api.ProfileServiceUtil
import com.test.account_api.AccountServiceUtil
import com.vansz.picassoimageloader.PicassoImageLoader
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter

class GiftActivity : AppCompatActivity() {

    private lateinit var initData: GiftDetailDTO
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GiftViewModel
    private lateinit var transferee: Transferee
    private lateinit var titleBar: CommonTitleBar
    private lateinit var title: TextView
    private lateinit var creditView: TextView
    private lateinit var noContentView: TextView
    private lateinit var hasContentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift)
        supportActionBar?.hide()

        (intent?.getSerializableExtra(KEY_STORE_PARAM) as? GiftDetailDTO)?.apply {
            initData = this
        }

        viewModel = ViewModelProvider(this).get(GiftViewModel::class.java)
        transferee = Transferee.getDefault(this)

        initView()
        bindView()
        bindHeaderPictures()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.rv_transferee)
        titleBar = findViewById(R.id.title_bar)
        titleBar.setBackgroundResource(R.drawable.shape_gradient)
        titleBar.setCenterText("礼物详情")
        title = findViewById(R.id.gift_title)
        creditView = findViewById(R.id.gift_credit)
        noContentView = findViewById(R.id.no_content_hint)
        hasContentView = findViewById(R.id.comment_area)
    }

    private fun bindView() {
        findViewById<View>(R.id.close).setOnClickListener {
            finish()
        }
        viewModel.getCurTitle().observe(this, Observer { curTitle ->
            title.text = curTitle
        })
        viewModel.getCurCredit().observe(this, Observer { credit ->
            creditView.text = credit.toString()
        })
        viewModel.getCommentInfo().observe(this, Observer { commentInfo ->
            if (commentInfo == null) {
                noContentView.visibility = View.VISIBLE
                hasContentView.visibility = View.GONE
            } else {
                noContentView.visibility = View.GONE
                hasContentView.visibility = View.VISIBLE
                val icon = hasContentView.findViewById<SimpleDraweeView>(R.id.avatar)
                icon.setImageURI(commentInfo.senderIconUrl)
                findViewById<TextView>(R.id.sender_name).text = commentInfo.senderName
                findViewById<TextView>(R.id.comment_content).text = commentInfo.content
            }
        })
        viewModel.getFriendsWanted().observe(this, Observer { number ->
            if (number > 0) {
                findViewById<TextView>(R.id.friend_want).text = number.toString()
                findViewById<View>(R.id.noFriendsArea).visibility = View.GONE
            } else {
                findViewById<View>(R.id.hasFriendsArea).visibility = View.GONE
            }
        })

        viewModel.getCollected().observe(this, Observer { status ->
            if (status == 1) {
                findViewById<View>(R.id.collect_icon_checked).visibility = View.VISIBLE
                findViewById<View>(R.id.collect_icon_unchecked).visibility = View.GONE
            } else {
                findViewById<View>(R.id.collect_icon_checked).visibility = View.GONE
                findViewById<View>(R.id.collect_icon_unchecked).visibility = View.VISIBLE
            }
        })

        viewModel.refreshGiftInfo(initData)

        findViewById<View>(R.id.collect_area).setOnClickListener {
            viewModel.collectGift(initData.id) { newState ->
                if (newState == 1) {
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "取消收藏成功", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun bindHeaderPictures() {
        val recyclerTransConfig = TransferConfig.build()
            .setSourceUrlList(viewModel.getCurUrlList().value)
            .setProgressIndicator(ProgressBarIndicator())
            .setIndexIndicator(NumberIndexIndicator())
            .setImageLoader(PicassoImageLoader.with(applicationContext))
            .enableHideThumb(false)
            .bindRecyclerView(recyclerView, R.id.iv_thum)
        val recyclerAdapter = RecyclerAdapter(this, R.layout.item_image, viewModel.getCurUrlList().value!!)
        recyclerAdapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {

            override fun onItemLongClick(p0: View?, p1: RecyclerView.ViewHolder?, p2: Int): Boolean = false

            override fun onItemClick(view: View?, viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                recyclerTransConfig.nowThumbnailIndex = pos
                transferee.apply(recyclerTransConfig).show()
            }
        })
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = recyclerAdapter
    }


    companion object {

        const val KEY_STORE_PARAM = "gift_param"

        @JvmStatic
        fun start(context: Context, giftParams: GiftDetailDTO) {
            if (!AccountServiceUtil.getSerVice().isLogged()) {
                ProfileServiceUtil.getSerVice().startLoginActivity(context)
            } else {
                val intent = Intent(context, GiftActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable(KEY_STORE_PARAM, giftParams)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        }
    }
}