package com.hfut.zhaojiabao.myrecord.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.hfut.zhaojiabao.myrecord.R;

/**
 * 柱状图
 *
 * @author zhaojiabao 2017/9/21
 */

public class BarChart extends BaseRectChart {

    private Paint mBarPaint;

    public BarChart(Context context) {
        this(context, null);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(ContextCompat.getColor(mContext, R.color.bittersweet));
    }

    @Override
    public void drawInner(Canvas canvas) {
        super.drawInner(canvas);

        DataProvider dataProvider = mBuilder.dataProvider;
        for (int i = 0; i < dataProvider.mPoints.size(); i++) {
            float barWidth = mHeight / (float) dataProvider.mPoints.size();
            float left = dataProvider.mPoints.get(i).x - barWidth / 2;
            float right = left + barWidth;
            float top = dataProvider.mPoints.get(i).y;
            float bottom = mHeight;
            canvas.drawRect(left, top, right, bottom, mBarPaint);
        }
    }
}
