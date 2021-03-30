package com.taiqiwen.gift

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

class RecyclerAdapter(context: Context, layoutId: Int, thumbUrlList: List<String>) :
    CommonAdapter<String>(context, layoutId, thumbUrlList) {

    override fun convert(viewHolder: ViewHolder, item: String?, position: Int) {
        val imageView = viewHolder.getView<ImageView>(R.id.iv_thum)
        Picasso.get()
            .load(item)
            .placeholder(R.mipmap.ic_empty_photo)
            .into(imageView)
    }

}