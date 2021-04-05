package com.taiqiwen.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.hitomi.tilibrary.style.index.NumberIndexIndicator
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator
import com.hitomi.tilibrary.transfer.TransferConfig
import com.hitomi.tilibrary.transfer.Transferee
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar
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
        viewModel = ViewModelProvider(this).get(GiftsGalleryViewModel::class.java)
        rvImages.adapter = GiftsCircleAdapter(this, viewModel.getFriendsCircleList())

        titleBar = findViewById(R.id.title_bar)
        titleBar.setBackgroundResource(R.drawable.shape_gradient)
        titleBar.setCenterText("拥有的礼物列表")

        findViewById<View>(R.id.close).setOnClickListener {
            finish()
        }
    }

    private inner class GiftsCircleAdapter(val context: Context, data: List<Pair<String, List<String>>>?)
        : CommonAdapter<Pair<String, List<String>>>(context, R.layout.item_gifts_circle, data) {

        private val divider: DividerGridItemDecoration = DividerGridItemDecoration(
            Color.TRANSPARENT,
            ConvertUtils.dp2px(8f),
            ConvertUtils.dp2px(8f)
        )

        override fun convert(viewHolder: ViewHolder?, item: Pair<String, List<String>>?, position: Int) {
            viewHolder?.setText(R.id.tv_content, item?.first)
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
