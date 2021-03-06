package com.taiqiwen.gift;

import android.view.View;
import android.view.ViewGroup;

import com.beyondsw.lib.widget.StackCardsView;
import com.beyondsw.lib.model.GiftDetailDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wensefu on 17-3-4.
 */
public class CardAdapter extends StackCardsView.Adapter {

    private List<BaseCardItem> mItems;
    private List<GiftDetailDTO> giftDetails;

    public void appendItems(List<BaseCardItem> items){
        int size = items == null ? 0 : items.size();
        if (size == 0) {
            return;
        }
        if (mItems == null) {
            mItems = new ArrayList<>(size);
        }
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(int position){
        mItems.remove(position);
        giftDetails.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView.setTag(giftDetails.get(position));
        return mItems.get(position).getView(convertView,parent,giftDetails.get(position));
    }

    @Override
    public int getSwipeDirection(int position) {
        BaseCardItem item = mItems.get(position);
        return item.swipeDir;
    }

    @Override
    public int getDismissDirection(int position) {
        BaseCardItem item = mItems.get(position);
        return item.dismissDir;
    }

    @Override
    public boolean isFastDismissAllowed(int position) {
        BaseCardItem item = mItems.get(position);
        return item.fastDismissAllowed;
    }

    @Override
    public int getMaxRotation(int position) {
        BaseCardItem item = mItems.get(position);
        return item.maxRotation;
    }

    public void setGiftDetails(List<GiftDetailDTO> giftDetails) {
        this.giftDetails = giftDetails;
    }
}
