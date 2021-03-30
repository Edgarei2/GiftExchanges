package com.taiqiwen.base_framework.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taiqiwen.base_framework.R;

import static android.view.View.MeasureSpec.AT_MOST;

public class BubblePopupWindow extends PopupWindow {

    public static int DEFAULT_MARGIN;
    private BubbleLayout bubbleView;
    private Activity activity;
    private TextView mTextView;
    private int mWidth;
    private int mHeight;
    // 是否显示虚拟键盘
    private boolean isHideVirtualKey;
    //默认箭头中间，可以自己设定偏移量
    private int mXOffset;
    //默认箭头中间，可以自己设定偏移量
    private int mYOffset;
    private boolean mIsAlreadyDismiss;
    private int mGravity;
    //箭头可以在居中模式下，也可以定义偏移量
    private int mBubbleOffset = 0;
    private int mBgColor;
    private boolean mNeedPath = true;
    private boolean mNeedPressFade;
    private boolean mNeedOverShoot;

    public long mAutoDismissDelayMillis = 7000;

    private AnimatorSet set;

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {

            animatorEasyInOut(false, mGravity);
        }
    };

    private long inAnimTime = 800;
    private long outAnimTime = 200;

    public void setOutAnimTime(long outAnimTime) {
        this.outAnimTime = outAnimTime;
    }

    public long getInAnimTime() {
        return inAnimTime;
    }

    public void setInAnimTime(long inAnimTime) {
        this.inAnimTime = inAnimTime;
    }

    public boolean isNeedOverShoot() {
        return mNeedOverShoot;
    }

    public void setNeedOverShoot(boolean needOverShoot) {
        this.mNeedOverShoot = needOverShoot;
    }

    public BubblePopupWindow(Activity activity, boolean defaultView) {
        super(activity);
        this.activity = activity;
        DEFAULT_MARGIN = (int) dip2Px(this.activity, 3);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(false);
        setClippingEnabled(false);

        ColorDrawable dw = new ColorDrawable(0);
        setBackgroundDrawable(dw);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (defaultView) {
            defaultView();
        }
    }

    public BubblePopupWindow(Activity activity) {
        this(activity, true);
    }

    /**
     * 默认的布局，就一个textview，添加文字
     */
    private void defaultView() {
        mTextView = new TextView(activity);
        mTextView.setTextColor(activity.getResources().getColor(R.color.ConstTextInverse));
        mTextView.setTextSize(13);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT));
        mTextView.setMaxLines(2);
        mTextView.setGravity(Gravity.CENTER);
        setBubbleView(mTextView); // 设置气泡内容
        isHideVirtualKey = true;
        getContentView().measure(AT_MOST, AT_MOST);
    }

    /**
     * 设置显示的view
     *
     * @param view
     */
    public void setBubbleView(View view) {
        bubbleView = new BubbleLayout(activity);
        bubbleView.setBackgroundColor(Color.TRANSPARENT);
        bubbleView.addView(view);
        bubbleView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        bubbleView.setLayoutParams(layoutParams);
        bubbleView.setVisibility(View.GONE);
        if (mBgColor != 0) {
            bubbleView.setBgColor(mBgColor);
        }
        bubbleView.setNeedPath(mNeedPath);
        bubbleView.setNeedPressFade(mNeedPressFade);
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickBubble();
                }
                //dismiss();
            }
        });
        setContentView(bubbleView);
    }

    /**
     * 隐藏虚拟键盘
     */
    private void hideStatusBar() {
        if (!isHideVirtualKey) {
            return;
        }
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            getContentView().setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            getContentView().setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 是否显示虚拟键盘
     *
     * @param b
     */
    public void setHideVirtualKey(boolean b) {
        isHideVirtualKey = b;
    }

    public void setSupportAutoRtl(boolean supportAutoRtl) {
        // Douyin do nothing
    }

    /**
     * 设置布局的大小
     *
     * @param width
     * @param height
     */
    public void setParam(int width, int height) {
        mWidth = width;
        mHeight = height;
        setWidth(width);
        setHeight(height);
        BubbleLayout.DEFAULT_WIDTH = width;
        BubbleLayout.DEFAULT_HEIGHT = height;
    }

    public void setParam(int width, int height, int delta) {
        mWidth = width;
        mHeight = height + delta;
        setWidth(width);
        setHeight(height + delta);
        BubbleLayout.DEFAULT_WIDTH = width;
        BubbleLayout.DEFAULT_HEIGHT = height;
    }

    public void setParamHeight(int height) {
        mHeight = height;
        setHeight(height);
        BubbleLayout.DEFAULT_HEIGHT = height;
    }

    public void setParamWidth(int width) {
        mWidth = width;
        setWidth(width);
        BubbleLayout.DEFAULT_WIDTH = width;
    }

    public void setBubbleText(String showText) {
        mTextView.setText(showText);
    }

    public void setBubbleText(int showTextResId) {
        mTextView.setText(showTextResId);
    }

    public void setBubbleTextSize(int unit, int bubbleTextSizee) {
        mTextView.setTextSize(unit, bubbleTextSizee);
    }

    public void setTextTypeface(Typeface tf) {
        mTextView.setTypeface(tf);
    }

    public void show(View parent) {
        show(parent, Gravity.BOTTOM, true, 0);
    }

    public void show(View parent, int gravity) {
        show(parent, gravity, true, 0);
    }

    public void show(View parent, int bubbleOffset, int yOffset) {
        setYOffset(yOffset);
        mBubbleOffset = bubbleOffset;
        show(parent, Gravity.RIGHT, true, 0);
    }

    public void setBubbleOffset(int bubbleOffset) {
        mBubbleOffset = bubbleOffset;
    }

    public void setXOffset(int xOffset) {
        mXOffset = xOffset;
    }

    public void setYOffset(int yOffset) {
        mYOffset = yOffset;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
        if (bubbleView != null) {
            bubbleView.setBubbleOrientation(getOrientation(mGravity));
        }
    }

    /**
     * 显示弹窗
     *
     * @param parent
     * @param gravity
     */
    public void show(View parent, int gravity, boolean isMiddle, float bubbleOffset) {
        if (activity.isFinishing() || parent == null || parent.getWindowToken() == null) {
            return;
        }
        getContentView().removeCallbacks(mDismissRunnable);
        mGravity = gravity;
        if (!this.isShowing()) {
            int orientation = getOrientation(gravity);
            if (mWidth != 0 && mHeight != 0) {
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY);
                getContentView().measure(widthMeasureSpec, heightMeasureSpec);
            } else {
                getContentView().measure(AT_MOST, AT_MOST);
            }

            //气泡尖角位置偏移量。默认位于中间
            if (isMiddle) {
                if (gravity == Gravity.BOTTOM || gravity == Gravity.TOP) {
                    bubbleOffset = getMeasuredWidth() / 2;
                } else {
                    bubbleOffset = getMeasureHeight() / 2;
                }
            }
            bubbleView.setBubbleParams(orientation, bubbleOffset + mBubbleOffset); // 设置气泡布局方向及尖角偏移
            int[] location = new int[2];
            int[] windowLoc = new int[2];
            if (mLocationSupplier != null) {
                Point point = mLocationSupplier.get();
                location[0] = point.x;
                location[1] = point.y;
            } else {
                parent.getLocationInWindow(location);
            }
            hideStatusBar();
            int middleSize = 0;
            switch (gravity) {
                case Gravity.BOTTOM:
                    //设置进入退出动画
                    // 使箭头指向parent的中部
                    if (isMiddle) {
                        middleSize = (parent.getMeasuredWidth() - getMeasuredWidth()) / 2;
                    }
                    showAsDropDown(parent, mXOffset + middleSize, DEFAULT_MARGIN + mYOffset);
                    animatorEasyInOut(true, gravity);
                    break;
                case Gravity.TOP:
                    if (isMiddle) {
                        middleSize = (parent.getMeasuredWidth() - getMeasuredWidth()) / 2;
                    }
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + mXOffset + middleSize, location[1] -
                            getMeasureHeight() + mYOffset - DEFAULT_MARGIN);
                    animatorEasyInOut(true, gravity);
                    break;
                case Gravity.RIGHT:
                    if (isMiddle) {
                        middleSize = (parent.getMeasuredHeight() - getMeasureHeight()) / 2;
                    }
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + mXOffset + parent.getWidth() + DEFAULT_MARGIN,
                            location[1] + mYOffset + middleSize);
                    animatorEasyInOut(true, gravity);
                    break;
                case Gravity.LEFT:
                    if (isMiddle) {
                        middleSize = (parent.getMeasuredHeight() - getMeasureHeight()) / 2;
                    }
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + mXOffset - getMeasuredWidth() - DEFAULT_MARGIN,
                            location[1] + mYOffset + middleSize);
                    animatorEasyInOut(true, gravity);
                    break;
                default:
                    break;
            }
            mIsAlreadyDismiss = false;
            if (mAutoDismissDelayMillis > 0) {
                getContentView().postDelayed(mDismissRunnable, mAutoDismissDelayMillis);
            }
        } else {
            super.dismiss();
        }
    }

    /**
     * 位置调整，安全位置位于上64dp，左右16dp位置处
     */
    public void showStickerPop(View parent) {
        if (activity.isFinishing() || parent == null || parent.getWindowToken() == null) {
            return;
        }

        float padding = UIUtils.dip2Px(activity, 36);
        float limitHeight = UIUtils.dip2Px(activity, 64f);
        float limitWidth = UIUtils.dip2Px(activity, 16f);

        getContentView().removeCallbacks(mDismissRunnable);
        if (!this.isShowing()) {
            int[] location = new int[2];
            if (mLocationSupplier != null) {
                Point point = mLocationSupplier.get();
                location[0] = point.x;
                location[1] = point.y;
            } else {
                parent.getLocationOnScreen(location);
            }
            mGravity = Gravity.TOP;
            mBubbleOffset = -(int) UIUtils.dip2Px(parent.getContext(), 3.5f);
            // 如果点击位置+window高度+padding到顶部的距离小于64dp，那么弹窗展示在下面
            if (location[1] - getMeasureHeight() - padding < limitHeight) {
                mGravity = Gravity.BOTTOM;
            }
            // 如果点击位置-window宽度/2小于左边的临界值，那么需要向右边偏移
            if (location[0] - getMeasuredWidth() / 2f < limitWidth) {
                mXOffset = (int) (limitWidth - (location[0] - getMeasuredWidth() / 2f));
            }
            // 如果点击位置+window宽度/2小于右边边的临界值，那么需要向左边偏移
            if (ScreenUtils.getScreenWidth(activity) - (location[0] + getMeasuredWidth() / 2f) < limitWidth) {
                mXOffset = (int) (ScreenUtils.getScreenWidth(activity) - (location[0] + getMeasuredWidth() / 2f) - limitWidth);
            }

            int orientation = getOrientation(mGravity);
            float bubbleOffset = getMeasuredWidth() / 2f;
            // 三角位置优先和手指点击位置上下方
            bubbleView.setBubbleParams(orientation, bubbleOffset + mBubbleOffset - mXOffset); // 设置气泡布局方向及尖角偏移

            hideStatusBar();
            switch (mGravity) {
                case Gravity.BOTTOM:
                    showAtLocation(parent, Gravity.NO_GRAVITY,
                            (int) (location[0] + mXOffset - bubbleOffset), (int) (location[1] + mYOffset + padding));
                    animatorEasyInOut(true, mGravity);
                    break;
                case Gravity.TOP:
                    showAtLocation(parent, Gravity.NO_GRAVITY, (int) (location[0] + mXOffset - bubbleOffset),
                            (int) (location[1] + mYOffset - padding - getMeasureHeight()));
                    animatorEasyInOut(true, mGravity);
                    break;
                default:
                    break;
            }
            mIsAlreadyDismiss = false;
            if (mAutoDismissDelayMillis > 0) {
                getContentView().postDelayed(mDismissRunnable, mAutoDismissDelayMillis);
            }
        } else {
            super.dismiss();
        }
    }

    protected int getOrientation(int gravity) {
        int orientation = BubbleLayout.LEFT;
        switch (gravity) {
            case Gravity.BOTTOM:
                orientation = BubbleLayout.TOP;
                break;
            case Gravity.TOP:
                orientation = BubbleLayout.BOTTOM;
                break;
            case Gravity.RIGHT:
                orientation = BubbleLayout.LEFT;
                break;
            case Gravity.LEFT:
                orientation = BubbleLayout.RIGHT;
                break;
            default:
                break;
        }
        return orientation;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        try {
            super.showAtLocation(parent, gravity, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animatorEasyInOut(final boolean isIn, final int gravity) {
        final View view = bubbleView;
        if (!isIn) {
            mIsAlreadyDismiss = true;
        }
        if (set == null) {
            set = new AnimatorSet();
        } else {
            set.removeAllListeners();
            set.cancel();
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                if (set == null) {
                    return;
                }
                int pivotX = 0;
                int pivotY = 0;
                switch (gravity) {
                    case Gravity.BOTTOM:
                        pivotX = (int) (view.getX() + bubbleView.getBubbleOffset());
                        pivotY = (int) (view.getY());
                        break;
                    case Gravity.TOP:
                        pivotX = (int) (view.getX() + bubbleView.getBubbleOffset());
                        pivotY = (int) (view.getY() + view.getMeasuredHeight());
                        break;
                    case Gravity.RIGHT:
                        pivotX = (int) view.getX();
                        pivotY = (int) (view.getY() + bubbleView.getBubbleOffset());
                        break;
                    case Gravity.LEFT:
                        pivotX = (int) (view.getX() + view.getMeasuredWidth());
                        pivotY = (int) (view.getY() + bubbleView.getBubbleOffset());
                        break;
                    default:
                        break;
                }
                view.setPivotY(pivotY);
                view.setPivotX(pivotX);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", isIn ? 0 : 1f, isIn ? 1.0f : 0);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", isIn ? 0 : 1f, isIn ? 1.0f : 0);

                set.play(scaleX).with(scaleY);
                set.setDuration(isIn ? inAnimTime : outAnimTime);
                if (mNeedOverShoot) {
                    set.setInterpolator(new OvershootInterpolator(1f));
                }
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isIn) {
                            view.setVisibility(View.GONE);
                            onDestroy();
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (isIn) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }
                });
                set.start();
            }
        });
    }


    @Override
    public void dismiss() {
        if (!mIsAlreadyDismiss) {
            animatorEasyInOut(false, mGravity);
            getContentView().removeCallbacks(mDismissRunnable);
            mXOffset = 0;
            mYOffset = 0;
        }
    }

    public void dismissDirectly() {
        if (!mIsAlreadyDismiss) {
            bubbleView.setVisibility(View.GONE);
            onDestroy();
            getContentView().removeCallbacks(mDismissRunnable);
            mXOffset = 0;
            mYOffset = 0;
        }
    }


    /**
     * 测量高度
     *
     * @return
     */
    public int getMeasureHeight() {
        return getContentView().getMeasuredHeight();
    }

    /**
     * 测量宽度
     *
     * @return
     */
    public int getMeasuredWidth() {
        return getContentView().getMeasuredWidth();
    }

    /**
     * 在结束时，需要调用此方法，防止内崔泄漏
     */
    public void onDestroy() {
        if (set != null) {
            set.removeAllListeners();
            set.cancel();
            set = null;
        }
        if (activity.isFinishing() || !isShowing()) {
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 历史逻辑无从追溯 相比于onDestroy，这里取消了activity.isFinishing()的判定
     */
    public void dismissDirectly2() {
        if (!mIsAlreadyDismiss) {
            bubbleView.setVisibility(View.GONE);
            if (set != null) {
                set.removeAllListeners();
                set.cancel();
                set = null;
            }
            try {
                super.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            getContentView().removeCallbacks(mDismissRunnable);
            mXOffset = 0;
            mYOffset = 0;
        }
    }

    public static float dip2Px(Context activity, float dipValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    public void setAutoDismissDelayMillis(long autoDismissDelayMillis) {
        mAutoDismissDelayMillis = autoDismissDelayMillis;
    }

    private SupplierC<Point> mLocationSupplier;

    public void setLocationSupplier(SupplierC<Point> locationSupplier) {
        mLocationSupplier = locationSupplier;
    }

    public void measure() {
        if (mWidth != 0 && mHeight != 0) {
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY);
            getContentView().measure(widthMeasureSpec, heightMeasureSpec);
        } else {
            getContentView().measure(AT_MOST, AT_MOST);
        }
    }

    public void measureAtMost() {
        if (mWidth != 0 && mHeight != 0) {
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY);
            getContentView().measure(widthMeasureSpec, heightMeasureSpec);
        } else {
            getContentView()
                    .measure(View.MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth(getContentView().getContext()), AT_MOST),
                            View.MeasureSpec
                                    .makeMeasureSpec(ScreenUtils.getScreenHeight(getContentView().getContext()), AT_MOST));
        }
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        if (bubbleView != null) {
            bubbleView.setBgColor(mBgColor);
        }
    }

    public void setNeedPath(boolean needPath) {
        mNeedPath = needPath;
        if (bubbleView != null) {
            bubbleView.setNeedPath(needPath);
        }
    }

    public void setBorderColor(int borderColor) {
        if (bubbleView != null) {
            bubbleView.setBorderColor(borderColor);
        }
    }

    public void showVolumePop(View parent) {
        if (activity.isFinishing() || parent == null || parent.getWindowToken() == null) {
            return;
        }

        // CHECKSTYLE:OFF
        float limitWidth = UIUtils.dip2Px(activity, 16f);
        float marginEnd = UIUtils.dip2Px(activity, 16f);
        float parentSize = UIUtils.dip2Px(activity, 36f);
        // CHECKSTYLE:ON

        getContentView().removeCallbacks(mDismissRunnable);
        if (!this.isShowing()) {
            if (mWidth != 0 && mHeight != 0) {
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY);
                getContentView().measure(widthMeasureSpec, heightMeasureSpec);
            } else {
                getContentView().measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
            }
            int[] location = new int[2];
            if (mLocationSupplier != null) {
                Point point = mLocationSupplier.get();
                location[0] = point.x;
                location[1] = point.y;
            } else {
                parent.getLocationOnScreen(location);
            }
            mGravity = Gravity.BOTTOM;
            // 如果点击位置-window宽度/2小于左边的临界值，那么需要向右边偏移
            mXOffset = (int) (ScreenUtils.getScreenWidth(activity) - getMeasuredWidth());
            mYOffset = 0;

            int orientation = getOrientation(mGravity);
            int parentMeasureSize = parent.getMeasuredHeight() > 0 ? parent.getMeasuredHeight() : (int) parentSize;
            float bubbleOffset = getMeasuredWidth() - parentMeasureSize;
            // 三角位置优先和手指点击位置上下方
            bubbleView.setBubbleParams(orientation, bubbleOffset); // 设置气泡布局方向及尖角偏移

            hideStatusBar();
            showAtLocation(parent, Gravity.NO_GRAVITY, mXOffset,
                    location[1] + mYOffset + parentMeasureSize);
            animatorEasyInOut(true, mGravity);
            mIsAlreadyDismiss = false;
            if (mAutoDismissDelayMillis > 0) {
                getContentView().postDelayed(mDismissRunnable, mAutoDismissDelayMillis);
            }
        } else {
            super.dismiss();
        }
    }

    /**
     * 种草页收藏气泡
     */
    public void showPreviewCollectPop(View parent) {
        if (activity.isFinishing() || parent == null || parent.getWindowToken() == null) {
            return;
        }
        getContentView().removeCallbacks(mDismissRunnable);
        mGravity = Gravity.TOP;
        if (!this.isShowing()) {
            int orientation = getOrientation(mGravity);
            if (mWidth != 0 && mHeight != 0) {
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY);
                getContentView().measure(widthMeasureSpec, heightMeasureSpec);
            } else {
                getContentView().measure(AT_MOST, AT_MOST);
            }

            int[] location = new int[2];
            if (mLocationSupplier != null) {
                Point point = mLocationSupplier.get();
                location[0] = point.x;
                location[1] = point.y;
            } else {
                parent.getLocationOnScreen(location);
            }
            hideStatusBar();
            int bubbleOffset = getMeasuredWidth() / 2;                                          // 气泡尖角位置偏移量, 默认位于中间
            int toParentDistance = (parent.getMeasuredWidth() - getMeasuredWidth()) / 2;        // 相对于AnchorView偏移的距离
            int bubbleViewX = location[0] + mXOffset + toParentDistance;

            int limitLeftMargin = (int) UIUtils.dip2Px(activity, 16f);                          // 离页面左边距最小距离
            if (bubbleViewX < limitLeftMargin) {
                bubbleOffset -= limitLeftMargin - bubbleViewX;                                  // 气泡箭头为了保持在原位置, 相对向左移动
                bubbleViewX = limitLeftMargin;                                                  // 气泡整体向右移动
            }

            bubbleView.setBubbleParams(orientation, bubbleOffset);                              // 设置气泡布局方向及尖角偏移
            showAtLocation(parent, Gravity.NO_GRAVITY, bubbleViewX, location[1] - getMeasureHeight() + mYOffset - DEFAULT_MARGIN);
            animatorEasyInOut(true, mGravity);
            mIsAlreadyDismiss = false;
            if (mAutoDismissDelayMillis > 0) {
                getContentView().postDelayed(mDismissRunnable, mAutoDismissDelayMillis);
            }
        } else {
            super.dismiss();
        }
    }

    public void setNeedPressFade(boolean needPressFade) {
        this.mNeedPressFade = needPressFade;
    }

    private OnClickBubbleListener listener;

    public void setListener(OnClickBubbleListener listener) {
        this.listener = listener;
    }

    public interface OnClickBubbleListener {
        void clickBubble();
    }
}