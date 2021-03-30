package com.taiqiwen.base_framework.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.taiqiwen.base_framework.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 气泡布局
 */
public class BubbleLayout extends LinearLayout {
    //先定义 常量
    public static final int TOP = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    public static final int NONE = 4;

    /**
     * 气泡尖角方向
     */
    @IntDef({TOP, LEFT, RIGHT, BOTTOM, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BubbleOrientation {
    }

    public static float dip2Px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }
    // CHECKSTYLE:OFF
    public static int PADDING;
    public static int DEFAULT_PADDING;
    public static int LEG_HALF_BASE;
    public static float STROKE_WIDTH;
    public static float CORNER_RADIUS;
    public static float MIN_ARROW_DISTANCE;
    public static int DEFAULT_WIDTH;
    public static int DEFAULT_HEIGHT;
    // CHECKSTYLE:ON
    private Paint mFillPaint = null;
    private final Path mPath = new Path();
    private final Path mBubbleArrowPath = new Path();


    private RectF mRoundRect;
    private float mWidth;
    private float mHeight;

    private Path mBorderBubbleArrowPath = new Path();
    private RectF mBorderRoundRect;
    private int mBorderWidth;
    private Matrix mBorderMatrix;

    private Bitmap mBitmap;

    private Canvas mCanvas;

    private int mBgColor;

    private int mBorderColor;

    private float mBubbleArrowOffset = 0.75f;
    @BubbleOrientation
    private int mBubbleOrientation = LEFT;

    private boolean mNeedPath = true;
    private boolean mNeedPressFade;

    private PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    public BubbleLayout(Context context) {
        this(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        mBorderWidth = (int) dip2Px(context, 0.5f);
        if (mBorderWidth < 2) {
            mBorderWidth = 2;
        }

        PADDING = (int) dip2Px(context, 7);
        DEFAULT_PADDING = (int) dip2Px(context, 10);
        LEG_HALF_BASE = (int) dip2Px(context, 6);
        STROKE_WIDTH = 2.0f;
        CORNER_RADIUS = dip2Px(context, 6);
        MIN_ARROW_DISTANCE = PADDING + LEG_HALF_BASE;
        DEFAULT_WIDTH = (int) dip2Px(context, 50);
        DEFAULT_HEIGHT = (int) dip2Px(context, 46);
        //setGravity(Gravity.CENTER);

        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setStrokeCap(Paint.Cap.BUTT);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStrokeWidth(STROKE_WIDTH);
        mFillPaint.setStrokeJoin(Paint.Join.MITER);
//        mFillPaint.setPathEffect(new CornerPathEffect(CORNER_RADIUS));
        mBgColor = context.getResources().getColor(R.color.s6_80);
        mBorderColor = Color.parseColor("#1DFFFFFF");
        mFillPaint.setColor(mBgColor);

        setLayerType(LAYER_TYPE_SOFTWARE, mFillPaint);
        renderBubbleLegPrototype();
        setBackgroundColor(Color.TRANSPARENT);
        setClipChildren(false);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 尖角path
     */
    private void renderBubbleLegPrototype() {
        mBubbleArrowPath.moveTo(0, 0);
        mBubbleArrowPath.lineTo(PADDING, -PADDING);
        mBubbleArrowPath.lineTo(PADDING, PADDING);
        mBubbleArrowPath.close();

        mBorderBubbleArrowPath.moveTo(0, 0);
        mBorderBubbleArrowPath.lineTo((float) (PADDING + mBorderWidth * Math.sqrt(2)), (float) (-PADDING - mBorderWidth * Math
                .sqrt(2)));
        mBorderBubbleArrowPath.lineTo((float) (PADDING + mBorderWidth * Math.sqrt(2)), (float) (PADDING + mBorderWidth * Math
                .sqrt(2)));
        mBorderBubbleArrowPath.close();
    }

    public void setBubbleParams(final @BubbleOrientation int bubbleOrientation, final float bubbleOffset) {
        mBubbleArrowOffset = bubbleOffset;
        mBubbleOrientation = bubbleOrientation;
    }

    public void setBubbleOrientation(final @BubbleOrientation int bubbleOrientation) {
        mBubbleOrientation = bubbleOrientation;
    }

    /**
     * 根据显示方向，获取尖角位置矩阵
     *
     * @param width
     * @param height
     * @return
     */
    private Matrix renderBubbleArrowMatrix(final float width, final float height) {

        final float offset = Math.max(mBubbleArrowOffset, MIN_ARROW_DISTANCE);

        float dstX = 0;
        float dstY = Math.min(offset, height - MIN_ARROW_DISTANCE);
        final Matrix matrix = new Matrix();
        mBorderMatrix = new Matrix();
        // CHECKSTYLE:OFF
        switch (mBubbleOrientation) {
            case TOP:
                dstX = Math.min(offset, width - MIN_ARROW_DISTANCE);
                dstY = 0;
                matrix.postRotate(90);
                mBorderMatrix.postRotate(90);
                mBorderMatrix.postTranslate(dstX + mBorderWidth * 3 / 2, dstY + mBorderWidth);
                setPadding(0, PADDING, 0, 0);
                setGravity(Gravity.CENTER);
                mRoundRect = new RectF(0, PADDING, mWidth, mHeight);
                break;

            case RIGHT:
                dstX = width;
                dstY = Math.min(offset, height - MIN_ARROW_DISTANCE);
                matrix.postRotate(180);
                mBorderMatrix.postRotate(180);
                mBorderMatrix.postTranslate(dstX + mBorderWidth * 2, dstY + mBorderWidth * 3 / 2);
                setPadding(0, 0, PADDING, 0);
                setGravity(Gravity.CENTER);
                mRoundRect = new RectF(0, 0, mWidth - PADDING, mHeight);
                break;

            case LEFT:
                dstX = 0;
                dstY = Math.min(offset, height - MIN_ARROW_DISTANCE);
                setPadding(PADDING, 0, 0, 0);
                setGravity(Gravity.CENTER);
                mBorderMatrix.postTranslate(dstX + mBorderWidth, dstY + mBorderWidth * 3 / 2);
                mRoundRect = new RectF(PADDING, 0, mWidth, mHeight);
                break;

            case BOTTOM:
                dstX = Math.min(offset, width - MIN_ARROW_DISTANCE);
                dstY = height;
                matrix.postRotate(270);
                mBorderMatrix.postRotate(270);
                mBorderMatrix.postTranslate(dstX + mBorderWidth * 3 / 2, dstY + mBorderWidth * 2);
                setPadding(0, 0, 0, PADDING);
                setGravity(Gravity.CENTER);
                mRoundRect = new RectF(0, 0, mWidth, mHeight - PADDING);
                break;
        }
        // CHECKSTYLE:ON
        mRoundRect.left += mBorderWidth * 3 / 2;
        mRoundRect.top += mBorderWidth * 3 / 2;
        mRoundRect.right += mBorderWidth * 3 / 2;
        mRoundRect.bottom += mBorderWidth * 3 / 2;

        mBorderRoundRect = new RectF();
        mBorderRoundRect.left = mRoundRect.left - mBorderWidth / 2;
        mBorderRoundRect.top = mRoundRect.top - mBorderWidth / 2;
        mBorderRoundRect.right = mRoundRect.right + mBorderWidth / 2;
        mBorderRoundRect.bottom = mRoundRect.bottom + mBorderWidth / 2;
        matrix.postTranslate(dstX + mBorderWidth * 3 / 2, dstY + mBorderWidth * 3 / 2);
        return matrix;
    }
    // CHECKSTYLE:OFF
    public float getBubbleOffset() {
        final float offset = Math.max(mBubbleArrowOffset, MIN_ARROW_DISTANCE);
        float bubbleOffset = 0;
        switch (mBubbleOrientation) {
            case TOP:
                bubbleOffset = Math.min(offset, mWidth - MIN_ARROW_DISTANCE);
                break;

            case RIGHT:
                bubbleOffset = Math.min(offset, mHeight - MIN_ARROW_DISTANCE);
                break;

            case LEFT:
                bubbleOffset = Math.min(offset, mHeight - MIN_ARROW_DISTANCE);
                break;

            case BOTTOM:
                bubbleOffset = Math.min(offset, mWidth - MIN_ARROW_DISTANCE);
                break;
        }
        return bubbleOffset;
    }
    // CHECKSTYLE:ON
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //手指抬起恢复透明度
                if (mNeedPressFade) {
                    animFadeIn();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (mNeedPressFade) {
                    animFadeOut();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mNeedPressFade) {
                    animFadeIn();
                }
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        TextView view = null;
        if (getChildAt(0) instanceof TextView) {
            view = (TextView) getChildAt(0);
        }
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = 0;
        if (view != null) {
            size = (int) view.getPaint().measureText(view.getText().toString()) + view.getPaddingLeft() + view.getPaddingRight();
        } else {
            size = getMeasuredWidth();
        }
        int width;
        int height;
        if (mBubbleOrientation == RIGHT || mBubbleOrientation == LEFT) {
            width = (size > DEFAULT_WIDTH ? (size + DEFAULT_PADDING * 2) : DEFAULT_WIDTH) + PADDING;
            height = DEFAULT_HEIGHT;
        } else {
            width = size > DEFAULT_WIDTH ? (size + DEFAULT_PADDING * 2) : DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        }

        width += mBorderWidth * 3;
        height += mBorderWidth * 3;
        if (mNeedPath) {
            height -= mBorderWidth * 2;
        }

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height);
        }

        mWidth = getMeasuredWidth() - mBorderWidth * 3;
        mHeight = getMeasuredHeight() - mBorderWidth * 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            if (mBitmap.getDensity() != canvas.getDensity()) {
                mBitmap.setDensity(canvas.getDensity());
            }
            mCanvas = new Canvas(mBitmap);
        }

        Matrix matrix = renderBubbleArrowMatrix(mWidth, mHeight);

        mFillPaint.setColor(mBorderColor);
        mFillPaint.setStyle(Paint.Style.STROKE);
        mFillPaint.setStrokeWidth(mBorderWidth);


        if (mNeedPath) {
            mPath.reset();
            mPath.addRoundRect(mBorderRoundRect, CORNER_RADIUS + mBorderWidth / 2, CORNER_RADIUS + mBorderWidth / 2, Path.Direction
                    .CW);
            mPath.addPath(mBorderBubbleArrowPath, mBorderMatrix);
            mCanvas.drawPath(mPath, mFillPaint);
        }

        mFillPaint.setXfermode(porterDuffXfermode);

        mFillPaint.setColor(mBgColor);
        mFillPaint.setStyle(Paint.Style.FILL);
        mPath.reset();
        mPath.addRoundRect(mRoundRect, CORNER_RADIUS, CORNER_RADIUS, Path.Direction.CW);
        mPath.addPath(mBubbleArrowPath, matrix);
        mCanvas.drawPath(mPath, mFillPaint);
        mFillPaint.setXfermode(null);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    public void setNeedPath(boolean need) {
        mNeedPath = need;
    }

    public void setNeedPressFade(boolean needPressFade) {
        this.mNeedPressFade = needPressFade;
    }

    private void animFadeIn() {
        clearAnimation();
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 1f);
        fadeOut.setDuration(100);
        fadeOut.start();
        fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) { }
        });
    }

    private void animFadeOut() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f);
        fadeOut.setDuration(100);
        fadeOut.start();
        fadeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) { }
        });
    }
}