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

        //画x轴
        canvas.drawLine(0, mHeight, mWidth, mHeight, mAxisPaint);
        //画y轴
        canvas.drawLine(0, mHeight, 0, 0, mAxisPaint);
        //画刻度线
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
        //画x轴刻度值
        for (int i = 0; i < dataProvider.mPoints.size(); i++) {
            canvas.drawText(dataProvider.xScaleTexts.get(i),
                    dataProvider.mPoints.get(i).x + mBuilder.mLeftBlank,
                    (mRealHeight * 2 - mBuilder.mBottomBlank
                            - mScalePaint.getFontMetrics().bottom - mScalePaint.getFontMetrics().top) / 2, mScalePaint);
        }

        //画y轴刻度值
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
                mHeight = mRealHeight - mBuilder.mTopBlank - mBuilder.mBottomBlank;

                DataProvider dataProvider = mBuilder.dataProvider;

                //初始化y轴刻度点
                if (dataProvider.yScaleNum > 0) {
                    dataProvider.yScaleHeights = new ArrayList<>();
                    for (int i = 0; i < dataProvider.yScaleNum; i++) {
                        dataProvider.yScaleHeights.add(mHeight - mHeight / (float) (dataProvider.yScaleNum + 1) * (i + 1));
                    }

                    float max = Float.MIN_VALUE;
                    for (String yText : dataProvider.yScaleTexts) {
                        float width = mScalePaint.measureText(yText, 0, yText.length());
                        if (width > max) {
                            max = width;
                        }
                    }

                    //加10是为了两边空出一点距离
                    mBuilder.mLeftBlank = (int) max + 10;
                }

                mWidth = mRealWidth - mBuilder.mLeftBlank - mBuilder.mRightBlank;

                //初始化轴线样式
                if (mBuilder.axisStyle != null) {
                    mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    mAxisPaint.setColor(mBuilder.axisStyle.color);
                    mAxisPaint.setStrokeWidth(mBuilder.axisStyle.width);
                    mAxisPaint.setStrokeCap(Paint.Cap.ROUND);
                }

                //初始化数据点
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

                invalidate();
            }
        });
    }
}
