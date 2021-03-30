package com.taiqiwen.gift;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.beyondsw.lib.model.GiftDetailDTO;

/**
 * Created by wensefu on 17-3-4.
 */
public class ImageCardItem extends BaseCardItem {

    private static final String TAG = "ImageCardItem";

    private String url;
    private String label;

    public ImageCardItem(Context context, String url, String label) {
        super(context);
        this.url = url;
        this.label = label;
    }

    public  static class ViewHolder{
        ImageView left;
        ImageView right;
        ImageView up;
        ImageView down;
    }

    @Override
    public View getView(View convertView, ViewGroup parent, final GiftDetailDTO giftDetailDTO) {
        convertView = View.inflate(mContext,R.layout.item_imagecard,null);
        SimpleDraweeView imageView = Utils.findViewById(convertView,R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GiftActivity.start(mContext, giftDetailDTO);
            }
        });
        TextView labelview = Utils.findViewById(convertView,R.id.label);
        ImageView left = Utils.findViewById(convertView,R.id.left);
        ImageView right = Utils.findViewById(convertView,R.id.right);
        ImageView up = Utils.findViewById(convertView,R.id.up);
        ImageView down = Utils.findViewById(convertView,R.id.down);
        ViewHolder vh = new ViewHolder();
        vh.left = left;
        vh.right = right;
        vh.up = up;
        vh.down = down;
        convertView.setTag(vh);
/*        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.img_dft)
                .centerCrop()
                .crossFade()
                .into(imageView);*/

        imageView.setImageURI(url);
        labelview.setText(label);
        return convertView;
    }
}
