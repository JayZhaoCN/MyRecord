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
        canvas.translate(mBuilder.mLeftBlack, mBuilder.mTopBlack);
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
        if (mBuilder.axisStyle.scaleHeight > 0) {
            for (PointF point : mBuilder.dataProvider.mPoints) {
                canvas.drawLine(point.x, mHeight, point.x, mHeight - mBuilder.axisStyle.scaleHeight, mAxisPaint);
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
            canvas.drawText(dataProvider.mTexts.get(i),
                    dataProvider.mPoints.get(i).x + mBuilder.mLeftBlack,
                    (mRealHeight * 2 - mBuilder.mBottomBlack
                            - mScalePaint.getFontMetrics().bottom - mScalePaint.getFontMetrics().top) / 2, mScalePaint);
        }
    }

    public void setBuilder(final Builder builder) {
        this.post(new Runnable() {
            @Override
            public void run() {
                mBuilder = builder;
                mWidth = mRealWidth - mBuilder.mLeftBlack - mBuilder.mRightBlack;
                mHeight = mRealHeight - mBuilder.mTopBlack - mBuilder.mBottomBlack;

                if (mBuilder.axisStyle != null) {
                    mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    mAxisPaint.setColor(mBuilder.axisStyle.color);
                    mAxisPaint.setStrokeWidth(mBuilder.axisStyle.width);
                    mAxisPaint.setStrokeCap(Paint.Cap.ROUND);
                }

                DataProvider dataProvider = mBuilder.dataProvider;

                float xAxisInterval = mWidth / (float) (dataProvider.mDatas.size() - 1);

                dataProvider.mPoints = new ArrayList<>();
                for (int i = 0; i < dataProvider.mDatas.size(); i++) {
                    float data = dataProvider.mDatas.get(i);
                    dataProvider.mPoints.add(new PointF(xAxisInterval * i,
                            mHeight - data / dataProvider.mMaxValue * mHeight));
                }

                invalidate();
            }
        });
    }

    public static class Builder {
        /**
         * 上方留白
         */
        public int mTopBlack = 0;

        /**
         * 下方留白
         */
        public int mBottomBlack = 0;

        /**
         * 左边留白
         */
        public int mLeftBlack = 0;

        /**
         * 右边留白
         */
        public int mRightBlack = 0;

        /**
         * 轴线样式
         */
        public AxisStyle axisStyle;

        /**
         * 图表数据
         */
        public DataProvider dataProvider;

        public Builder(int leftBlack, int topBlack, int rightBlack, int bottomBlack) {
            mLeftBlack = leftBlack;
            mTopBlack = topBlack;
            mRightBlack = rightBlack;
            mBottomBlack = bottomBlack;
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
