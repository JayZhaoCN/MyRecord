package com.hfut.zhaojiabao.myrecord.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 所有矩形图表的父类
 * @author zhaojiabao 2017/9/19
 */

public abstract class BaseRectChart extends View {
    protected Context mContext;

    private Builder mBuilder;

    /**
     * 图表真实宽度
     */
    protected int mRealWidth;
    /**
     * 图表真实高度
     */
    protected int mRealHeight;
    /**
     * 图表宽度
     */
    protected int mWidth;
    /**
     * 图表高度
     */
    protected int mHeight;

    public BaseRectChart(Context context) {
        this(context, null);
    }

    public BaseRectChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRectChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRealWidth = w;
        mRealHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBuilder == null) {
            return;
        }
        canvas.save();
        canvas.translate(mBuilder.mLeftBlack, mBuilder.mTopBlack);
        drawInner(canvas);
        canvas.restore();

        drawOuter(canvas);
    }

    /**
     * 继承该方法绘制图表内部
     */
    public abstract void drawInner(Canvas canvas);

    /**
     * 继承该方法绘制图表外部
     */
    protected void drawOuter(Canvas canvas) {

    }

    public void setBuilder(final Builder builder) {
        this.post(new Runnable() {
            @Override
            public void run() {
                mBuilder = builder;
                mWidth = mRealWidth - mBuilder.mLeftBlack - mBuilder.mRightBlack;
                mHeight = mRealHeight - mBuilder.mTopBlack - mBuilder.mBottomBlack;
                invalidate();
            }
        });
    }

    public static class Builder {
        /**
         * 上方留白
         */
        private int mTopBlack = 0;
        /**
         * 下方留白
         */
        private int mBottomBlack = 0;
        /**
         * 左边留白
         */
        private int mLeftBlack = 0;
        /**
         * 右边留白
         */
        private int mRightBlack = 0;
        /**
         * 图表标题
         */
        private String mChartTitle;

        public Builder(int leftBlack, int topBlack, int rightBlack, int bottomBlack, String chartTitle) {
            mLeftBlack = leftBlack;
            mTopBlack = topBlack;
            mRightBlack = rightBlack;
            mBottomBlack = bottomBlack;
            mChartTitle = chartTitle;
        }
    }
}
