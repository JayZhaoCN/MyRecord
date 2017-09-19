package com.hfut.zhaojiabao.myrecord.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * @author zhaojiabao 2017/9/19
 */

public class TestChart extends BaseRectChart {

    Paint mPaint;

    public TestChart(Context context) {
        this(context, null);
    }

    public TestChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.CYAN);
    }

    @Override
    public void drawInner(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
    }

    @Override
    public void drawOuter(Canvas canvas) {

    }
}
