package com.taiqiwen.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.hitomi.tilibrary.style.index.NumberIndexIndicator
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator
import com.hitomi.tilibrary.transfer.TransferConfig
import com.hitomi.tilibrary.transfer.Transferee
import com.taiqiwen.base_framework.DialogHelper
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.ui.divider.HorizontalDividerItemDecoration
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
import com.taiqiwen.profile.GiftsGalleryViewModel.Companion.KEY_GIFT_CHANGED_STATUS
import com.taiqiwen.profile.GiftsGalleryViewModel.Companion.KEY_GIFT_CREDIT
import com.taiqiwen.profile.GiftsGalleryViewModel.Companion.KEY_GIFT_DETAIL
import com.taiqiwen.profile.GiftsGalleryViewModel.Companion.KEY_GIFT_OBJ_ID
import com.taiqiwen.profile.GiftsGalleryViewModel.Companion.KEY_SENDER_ID
import com.test.account_api.AccountServiceUtil
import com.vansz.glideimageloader.GlideImageLoader
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

class GiftsGalleryActivity : AppCompatActivity() {

    private lateinit var transferee: Transferee
    private lateinit var rvImages: RecyclerView
    private lateinit var viewModel: GiftsGalleryViewModel
    private lateinit var titleBar: CommonTitleBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifts_gallery)
        supportActionBar?.hide()

        transferee = Transferee.getDefault(this)
        rvImages = findViewById(R.id.rv_images)
        rvImages.layoutManager = LinearLayoutManager(this)
        val paint = Paint()
        paint.strokeWidth = 5f
        paint.color = Color.BLUE
        paint.isAntiAlias = true
        paint.pathEffect = DashPathEffect(floatArrayOf(25.0f, 25.0f), 0F)
        rvImages.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(this)
                //.drawable(R.drawable.sample)
                .paint(paint)
                //.size(15)
                .build()
        )
        viewModel = ViewModelProvider(this).get(GiftsGalleryViewModel::class.java)
        viewModel.refreshGiftsCircleList(AccountServiceUtil.getSerVice().getCurUserId())
        viewModel.getGiftsCircleListInfo().observe(this, Observer { info ->
            viewModel.getExtraInfoList().value ?.let {
                rvImages.adapter = GiftsCircleAdapter(this, it , info)
            }
        })

        titleBar = findViewById(R.id.title_bar)
        titleBar.setBackgroundResource(R.drawable.shape_gradient)
        titleBar.setCenterText("拥有的礼物列表")

        findViewById<View>(R.id.close).setOnClickListener {
            finish()
        }
    }

    private inner class GiftsCircleAdapter(val context: Context, val extraInfo: List<Map<String, String?>>, data: List<Pair<String, List<String>>>?)
        : CommonAdapter<Pair<String, List<String>>>(context, R.layout.item_gifts_circle, data) {

        private val divider: DividerGridItemDecoration = DividerGridItemDecoration(
            Color.TRANSPARENT,
            ConvertUtils.dp2px(8f),
            ConvertUtils.dp2px(8f)
        )

        override fun convert(viewHolder: ViewHolder?, item: Pair<String, List<String>>?, position: Int) {
            viewHolder?.setText(R.id.tv_content, item?.first)
            viewHolder?.getView<TextView>(R.id.tv_detail)?.text = extraInfo[position][KEY_GIFT_DETAIL]
            val sendUid = extraInfo[position][KEY_SENDER_ID]
            if (sendUid == null) {
                viewHolder?.getView<TextView>(R.id.tv_name)?.text = "积分兑换"
            } else {
                viewModel.fetchCertainUserDetail(sendUid) { user ->
                    viewHolder?.getView<SimpleDraweeView>(R.id.iv_header)?.setImageURI(user?.avatarUrl)
                    viewHolder?.getView<TextView>(R.id.tv_name)?.text = "${user?.userName} 赠送给您"
                }
            }
            val takenOut = viewHolder?.getView<TextView>(R.id.changed_to_real)
            val changeToCredit = viewHolder?.getView<TextView>(R.id.change_to_credit)
            val takeOut = viewHolder?.getView<TextView>(R.id.change_to_real)
            val credit = extraInfo[position][KEY_GIFT_CREDIT]
            val curUserCredit = AccountServiceUtil.getSerVice().getCurUser()?.curCredit.toString()
            val curUserObjectId = AccountServiceUtil.getSerVice().getCurUser()?.objectId.toString()
            val giftObjectId = extraInfo[position][KEY_GIFT_OBJ_ID]

            if (extraInfo[position][KEY_GIFT_CHANGED_STATUS] == "1") {
                takenOut?.visibility = View.VISIBLE
            } else {
                changeToCredit?.visibility = View.VISIBLE
                takeOut?.visibility = View.VISIBLE
            }

            changeToCredit?.setOnClickListener {
                DialogHelper.showDialog(
                    this@GiftsGalleryActivity,
                    title = "您确定要将该礼物兑换为积分吗",
                    detail = "兑换后该礼物的积分会加入您的积分余额中",
                    positiveCb = {
                        viewModel.restoreGift2Credit(giftObjectId,
                            curUserObjectId,
                            credit,
                            curUserCredit) { result ->
                            if (result) {
                                ToastHelper.showToast("兑换积分成功")
                                rvImages.postDelayed({
                                    viewModel.refreshGiftsCircleList(AccountServiceUtil.getSerVice().getCurUserId())
                                }, 2000)
                            }
                            else{
                                ToastHelper.showToast("网络错误")
                            }
                        }
                    }
                )
            }

            takeOut?.setOnClickListener {
                DialogHelper.showDialog(
                    this@GiftsGalleryActivity,
                    title = "您确定要提现该礼物吗",
                    detail = "提现后该礼物将会被寄送到您提供的地址",
                    positiveCb = {
                        viewModel.takeGift(giftObjectId) { result ->
                            if (result) {
                                ToastHelper.showToast("提现成功")
                                takenOut?.visibility = View.VISIBLE
                                takeOut?.visibility = View.GONE
                                changeToCredit?.visibility = View.GONE
                            }
                            else{
                                ToastHelper.showToast("网络错误")
                            }
                        }
                    }
                )
            }

            val rvPhotos = viewHolder?.getView<RecyclerView>(R.id.rv_photos)
            // 重置 divider
            rvPhotos?.removeItemDecoration(divider)
            rvPhotos?.addItemDecoration(divider)
            if (rvPhotos?.layoutManager == null) {
                rvPhotos?.layoutManager = GridLayoutManager(context, 3)
            }
            val photosAdapter = PhotosAdapter(
                context,
                R.layout.item_image2,
                item?.second
            )
            photosAdapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, pos: Int) {
                    transferee.apply(getBuilder(position)
                        .setNowThumbnailIndex(pos)
                        .setSourceUrlList(item?.second)
                        .bindRecyclerView(view.parent as RecyclerView, R.id.iv_thum)
                    ).show()
                }

                override fun onItemLongClick(view: View, viewHolder: RecyclerView.ViewHolder, i: Int): Boolean {
                    return false
                }
            })
            rvPhotos?.adapter = photosAdapter
        }

    }

    private inner class PhotosAdapter(context: Context?, layoutId: Int, data: List<String?>?) : CommonAdapter<String?>(context, layoutId, data) {
        override fun convert(holder: ViewHolder, url: String?, position: Int) {
            val imageView = holder.getView<ImageView>(R.id.iv_thum)
            Glide.with(imageView)
                .load(url)
                .placeholder(R.mipmap.ic_empty_photo)
                .into(imageView)
        }
    }

    private fun getBuilder(pos: Int): TransferConfig.Builder {
        val builder = TransferConfig.build()
            .setProgressIndicator(ProgressBarIndicator())
            .setIndexIndicator(NumberIndexIndicator())
            .setImageLoader(GlideImageLoader.with(applicationContext))
        when (pos) {
            4 -> {
                builder.enableHideThumb(false)
            }
            5 -> {
                builder.enableJustLoadHitPage(true)
            }
            6 -> {
                builder.enableDragPause(true)
            }
        }
        return builder
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, GiftsGalleryActivity::class.java)
            context.startActivity(intent)
        }

    }

}
