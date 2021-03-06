package com.taiqiwen.profile.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.taiqiwen.profile.ImageUtil;
import com.taiqiwen.profile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy on 2017/1/4.
 * desc:
 */

public class AvatarLayout extends RelativeLayout {
    private static final String TAG = "AvatarLayout";
    AnimateImageView circleImageView;
    Context mContext;
    PullToRefeshLayout mPullLayout;
    ViewDragHelper mDragHelper;

    List<AnimateImageView> mIvList;

    ViewTrackController mViewTrackController;

    String url;

    public AvatarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //this.url = ImageUtil.avatarUrl;
    }

    public AvatarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new MyViewDragHelper());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
        mViewTrackController = ViewTrackController.create();
        //this.url = ImageUtil.avatarUrl;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
/*        LayoutParams params = new LayoutParams(ScreenUtil.dip2px(mContext, 80), ScreenUtil.dip2px(mContext, 80));
        params.topMargin = ScreenUtil.dip2px(mContext, 180 - 80 / 2);
        params.leftMargin = ScreenUtil.getScreenWidth(mContext) / 2 - ScreenUtil.dip2px(mContext, 80) / 2;
        mIvList = new ArrayList<>();
        Drawable drawable = null;
        if (url != null) {
            drawable = new BitmapDrawable(getResources(), ImageUtil.getBitmapFromFresco(url));
        }
        for (int i = 0; i < 5; i++) {
            AnimateImageView circleImageView = new AnimateImageView(mContext);
            if (drawable != null) {
                circleImageView.setBackgroundDrawable(drawable);
            } else {
                circleImageView.setImageResource(R.drawable.avatar);
            }
            circleImageView.setBorderWidth(ScreenUtil.dip2px(mContext, 1));
            circleImageView.setBorderColor(Color.WHITE);
            circleImageView.setLayoutParams(params);
            addView(circleImageView);
            if (i == 4) {
                this.circleImageView = circleImageView;
            } else {
                circleImageView.setAlpha(0.3f);
            }
            mIvList.add(circleImageView);
        }

        mViewTrackController.init(mIvList);

        mPullLayout = (PullToRefeshLayout) getChildAt(0);
        mPullLayout.setmListener(new PullToRefeshLayout.OnPullRefreshListener() {
            @Override
            public void onMoveY(float distance) {
                if (circleImageView != null) {
                    circleImageView.setTranslationY(distance);
                    mViewTrackController.setOtherVisiable(false);
                }
            }

            @Override
            public void onMoveEnd() {
                if (circleImageView != null) {
                    circleImageView.animate().translationY(0);
                }
            }
        });*/

    }

    public void setImage(String url) {
        LayoutParams params = new LayoutParams(ScreenUtil.dip2px(mContext, 80), ScreenUtil.dip2px(mContext, 80));
        params.topMargin = ScreenUtil.dip2px(mContext, 180 - 80 / 2);
        params.leftMargin = ScreenUtil.getScreenWidth(mContext) / 2 - ScreenUtil.dip2px(mContext, 80) / 2;
        mIvList = new ArrayList<>();
        Drawable drawable = null;
        if (url != null) {
            drawable = new BitmapDrawable(getResources(), ImageUtil.getBitmapFromFresco(url));
        }
        for (int i = 0; i < 5; i++) {
            AnimateImageView circleImageView = new AnimateImageView(mContext);
            if (drawable != null) {
                circleImageView.setImageDrawable(drawable);
            } else {
                circleImageView.setImageResource(R.drawable.avatar);
            }
            circleImageView.setBorderWidth(ScreenUtil.dip2px(mContext, 1));
            circleImageView.setBorderColor(Color.WHITE);
            circleImageView.setLayoutParams(params);
            addView(circleImageView);
            if (i == 4) {
                this.circleImageView = circleImageView;
            } else {
                circleImageView.setAlpha(0.3f);
            }
            mIvList.add(circleImageView);
        }

        mViewTrackController.init(mIvList);

        mPullLayout = (PullToRefeshLayout) getChildAt(0);
        mPullLayout.setmListener(new PullToRefeshLayout.OnPullRefreshListener() {
            @Override
            public void onMoveY(float distance) {
                if (circleImageView != null) {
                    circleImageView.setTranslationY(distance);
                    mViewTrackController.setOtherVisiable(false);
                }
            }

            @Override
            public void onMoveEnd() {
                if (circleImageView != null) {
                    circleImageView.animate().translationY(0);
                }
            }
        });
    }

    private class MyViewDragHelper extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child == circleImageView) {
                circleImageView.stopAnimation();
                return true;
            }
            return false;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mViewTrackController.onRelease();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mViewTrackController.setOtherVisiable(true);
            mViewTrackController.onTopViewPosChanged(left, top);
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && clickInAvatarView(event)) {
            return true;
        }
        Log.d(TAG, "onInterceptTouchEvent: " + mDragHelper.shouldInterceptTouchEvent(event));
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewTrackController.setOriginPos(circleImageView.getLeft(), circleImageView.getTop());
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private boolean clickInAvatarView(MotionEvent event) {
        boolean isInCircle = true;
        int clickX = (int) event.getRawX();
        int clickY = (int) event.getRawY();

        //??????????????????????????????
        int[] location = new int[2];
        circleImageView.getLocationOnScreen(location);

        //????????????????????????x???y??????
        int x = location[0];
        int y = location[1];

        //????????? ??????????????????????????????getLeft
        int r = (circleImageView.getRight() - circleImageView.getLeft()) / 2;

        //????????????
        int vCenterX = x + r;
        int vCenterY = y + r;

        //????????????x??????????????????x???????????????
        int distanceX = Math.abs(vCenterX - clickX);
        //????????????y??????????????????y???????????????
        int distanceY = Math.abs(vCenterY - clickY);
        //????????????????????????????????????
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        if (distanceZ > r) {
            return false;
        }
        return isInCircle;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
