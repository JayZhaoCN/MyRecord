package com.hfut.zhaojiabao.myrecord.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.Utils;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/28
 */

public class SectorChart extends View implements BaseChart {
    private static final int START_ANIM_DURATION = 1200;

    private List<SectorChartItem> mData;

    private Context mContext;

    public static int[] mColors;
    private Paint mSectorPaint;

    private RectF mRectF;
    private float mAngle;
    private float mBorderSize;

    private ValueAnimator mAnimator;

    public SectorChart(Context context) {
        this(context, null);
    }

    public SectorChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectorChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mBorderSize = Utils.dp2px(mContext, 1);

        mSectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mColors = new int[] {
                ContextCompat.getColor(mContext, R.color.grapefruit),
                ContextCompat.getColor(mContext, R.color.sunflower),
                ContextCompat.getColor(mContext, R.color.grass),
                ContextCompat.getColor(mContext, R.color.mint),
                ContextCompat.getColor(mContext, R.color.aqua),
                ContextCompat.getColor(mContext, R.color.blue_jeans),
                ContextCompat.getColor(mContext, R.color.lavender),
                ContextCompat.getColor(mContext, R.color.pink_rose),
                ContextCompat.getColor(mContext, R.color.light_gray),
                ContextCompat.getColor(mContext, R.color.dark_gray),
                ContextCompat.getColor(mContext, R.color.bittersweet),
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(mBorderSize, mBorderSize, w - mBorderSize, h - mBorderSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mData == null || mData.size() == 0) {
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            mSectorPaint.setColor(mColors[i]);
            canvas.drawArc(mRectF, mAngle, mData.get(i).angle * mAnimPercent, true, mSectorPaint);
            mAngle += mData.get(i).angle * mAnimPercent;
        }
        mAngle = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void provideData(List<? extends BaseChartItem> data) {
        mData = (List<SectorChartItem>) data;
        initAnim();
        parseData(360);
        invalidate();
    }

    private float mAnimPercent = 0;

    private void initAnim() {
        clearAnim();
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimPercent = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });
        mAnimator.setRepeatCount(0);
        mAnimator.setDuration(START_ANIM_DURATION);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
    }

    private void clearAnim() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
            mAnimator = null;
        }
    }

    private void parseData(int totalAngle) {
        if (mData == null || mData.size() == 0) {
            return;
        }

        float totalValue = 0;
        for (int i = 0; i < mData.size(); i++) {
            totalValue += mData.get(i).value;
        }

        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).angle = mData.get(i).value / totalValue * totalAngle;
        }
    }

    public static class SectorChartItem extends BaseChartItem {
        //所占的角度
        float angle = 0;
        public float percent = 0;

        public SectorChartItem(String text, float value, float angle) {
            super(text, value);
            this.angle = angle;
        }

        public SectorChartItem(String text, float percent) {
            super(text, percent);
        }
    }
}
