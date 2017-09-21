package com.hfut.zhaojiabao.myrecord.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.Utils;

import java.util.ArrayList;

/**
 * 所有矩形图表的父类
 *
 * @author zhaojiabao 2017/9/19
 */

//TODO 让基类做更多事
public abstract class BaseRectChart extends View {
    protected Context mContext;

    protected Builder mBuilder;

    private Paint mScalePaint;
    private Paint mAxisPaint;

    /**
     * 图表真实宽度
     */
    protected int mRealWidth;
    /**
     * 图表真实高度
     */
    protected int mRealHeight;
    /**
     * 绘制区域宽度
     */
    protected int mWidth;
    /**
     * 绘制区域高度
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

        init();
    }

    private void init() {
        mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScalePaint.setColor(ContextCompat.getColor(mContext, R.color.black60));
        mScalePaint.setTextAlign(Paint.Align.CENTER);
        mScalePaint.setTextSize(Utils.sp2px(mContext, 10));
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
        canvas.translate(mBuilder.mLeftBlank, mBuilder.mTopBlank);
        drawInner(canvas);
        canvas.restore();

        drawOuter(canvas);
    }

    /**
     * 继承该方法绘制图表内部
     * 父类默认实现，画x、y轴和刻度
     */
    @CallSuper
    public void drawInner(Canvas canvas) {
        if (mBuilder.axisStyle == null) {
            return;
        }

        //draw x-axis
        canvas.drawLine(0, mHeight, mWidth, mHeight, mAxisPaint);
        //draw y-axis
        canvas.drawLine(0, mHeight, 0, 0, mAxisPaint);
        //draw axis scale
        if (mBuilder.axisStyle.scaleLength > 0) {
            for (PointF point : mBuilder.dataProvider.mPoints) {
                canvas.drawLine(point.x, mHeight, point.x, mHeight - mBuilder.axisStyle.scaleLength, mAxisPaint);
            }

            for (int i = 0; i < mBuilder.dataProvider.yScaleNum; i++) {
                canvas.drawLine(0, mBuilder.dataProvider.yScaleHeights.get(i), mBuilder.axisStyle.scaleLength, mBuilder.dataProvider.yScaleHeights.get(i), mAxisPaint);
            }
        }
    }

    /**
     * 继承该方法绘制图表外部
     * 父类默认实现，画刻度值
     */
    @CallSuper
    protected void drawOuter(Canvas canvas) {
        DataProvider dataProvider = mBuilder.dataProvider;
        //draw x-axis scale
        for (int i = 0; i < dataProvider.mPoints.size(); i++) {
            canvas.drawText(dataProvider.xScaleTexts.get(i),
                    dataProvider.mPoints.get(i).x + mBuilder.mLeftBlank,
                    (mRealHeight * 2 - mBuilder.mBottomBlank
                            - mScalePaint.getFontMetrics().bottom - mScalePaint.getFontMetrics().top) / 2, mScalePaint);
        }

        //draw y-axis scale
        for (int i = 0; i < dataProvider.yScaleNum; i++) {
            float baseline = (2 * dataProvider.yScaleHeights.get(i) + 2 * mBuilder.mBottomBlank - mScalePaint.getFontMetrics().bottom - mScalePaint.getFontMetrics().top) / 2;
            canvas.drawText(dataProvider.yScaleTexts.get(i), mBuilder.mLeftBlank / 2, baseline, mScalePaint);
        }
    }

    public void setBuilder(final Builder builder) {
        this.post(new Runnable() {
            @Override
            public void run() {
                mBuilder = builder;
                mWidth = mRealWidth - mBuilder.mLeftBlank - mBuilder.mRightBlank;
                mHeight = mRealHeight - mBuilder.mTopBlank - mBuilder.mBottomBlank;

                if (mBuilder.axisStyle != null) {
                    mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    mAxisPaint.setColor(mBuilder.axisStyle.color);
                    mAxisPaint.setStrokeWidth(mBuilder.axisStyle.width);
                    mAxisPaint.setStrokeCap(Paint.Cap.ROUND);
                }

                DataProvider dataProvider = mBuilder.dataProvider;

                float xAxisInterval;
                if (mBuilder.mChartType == Builder.CURVE_CHART) {
                    xAxisInterval = mWidth / (float) (dataProvider.data.size() - 1);

                    dataProvider.mPoints = new ArrayList<>();
                    for (int i = 0; i < dataProvider.data.size(); i++) {
                        float data = dataProvider.data.get(i);
                        dataProvider.mPoints.add(new PointF(xAxisInterval * i,
                                mHeight - data / dataProvider.mMaxValue * mHeight));
                    }
                } else {
                    xAxisInterval = mWidth / (float) dataProvider.data.size();

                    dataProvider.mPoints = new ArrayList<>();
                    for (int i = 0; i < dataProvider.data.size(); i++) {
                        float data = dataProvider.data.get(i);
                        dataProvider.mPoints.add(new PointF(xAxisInterval * i + xAxisInterval / 2,
                                mHeight - data / dataProvider.mMaxValue * mHeight));
                    }
                }

                if (dataProvider.yScaleNum > 0) {
                    dataProvider.yScaleHeights = new ArrayList<>();
                    for (int i = 0; i < dataProvider.yScaleNum; i++) {
                        dataProvider.yScaleHeights.add(mHeight - mHeight / (float) (dataProvider.yScaleNum + 1) * (i + 1));
                    }
                }

                invalidate();
            }
        });
    }

    public static class Builder {
        /**
         * 不同的图表类型决定不同的x轴取点方式
         */
        public static final int BAR_CHART = 0;
        public static final int CURVE_CHART = 1;

        private int mChartType = CURVE_CHART;

        /**
         * 上方留白
         */
        public int mTopBlank = 0;

        /**
         * 下方留白
         */
        public int mBottomBlank = 0;

        /**
         * 左边留白
         */
        public int mLeftBlank = 0;

        /**
         * 右边留白
         */
        public int mRightBlank = 0;

        /**
         * 轴线样式
         */
        public AxisStyle axisStyle;

        /**
         * 图表数据
         */
        public DataProvider dataProvider;

        public Builder(int chartType, int leftBlank, int topBlank, int rightBlank, int bottomBlank) {
            mChartType = chartType;
            mLeftBlank = leftBlank;
            mTopBlank = topBlank;
            mRightBlank = rightBlank;
            mBottomBlank = bottomBlank;
        }

        public Builder setAxisStyle(AxisStyle style) {
            this.axisStyle = style;
            return this;
        }

        public Builder setDataProvider(DataProvider dataProvider) {
            this.dataProvider = dataProvider;
            return this;
        }
    }
}
