package com.taiqiwen.base_framework.ui.countdown;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;

import com.taiqiwen.base_framework.GiftApplication;

import androidx.annotation.NonNull;

public class CustomerViewUtils {

    public static SpannableString getMixedText(String str, int[][] indexs, boolean isBig) {
        if (TextUtils.isEmpty(str) || indexs == null || indexs.length <= 0) {
            return new SpannableString("");
        }
        SpannableString spannableString = new SpannableString(str);
        int fontSizePx1 = SysUtils.convertSpToPixel(GiftApplication.getContext(), isBig ? 20 : 16);
        for (int[] index : indexs) {
            if (index.length < 2) {
                return new SpannableString("");
            }
            if (index[0] >= 0 && (index[0] + index[1]) < spannableString.length()) {
                spannableString.setSpan(new VerticalCenterSpan(fontSizePx1, Color.parseColor("#D6B189")), index[0], index[0] + index[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 使TextView中不同大小字体垂直居中
     */
    public static class VerticalCenterSpan extends ReplacementSpan {

        private float mFontSizePx;

        private int mTextColor;

        private VerticalCenterSpan(float fontSizePx, int textColor) {
            this.mFontSizePx = fontSizePx;
            this.mTextColor = textColor;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            text = text.subSequence(start, end);
            Paint p = getCustomTextPaint(paint);
            return (int) p.measureText(text.toString());
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            text = text.subSequence(start, end);
            Paint p = getCustomTextPaint(paint);
            Paint.FontMetricsInt fm = p.getFontMetricsInt();
            p.setColor(mTextColor);
            // 此处重新计算y坐标，使字体居中
            canvas.drawText(text.toString(), x, y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2), p);
        }

        private TextPaint getCustomTextPaint(Paint srcPaint) {
            TextPaint paint = new TextPaint(srcPaint);
            //设定字体大小, sp转换为px
            paint.setTextSize(mFontSizePx);
            return paint;
        }
    }
}
