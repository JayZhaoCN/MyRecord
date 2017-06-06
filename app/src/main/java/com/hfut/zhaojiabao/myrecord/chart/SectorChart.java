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
    private static final String TAG = "SectorChart";

    private List<SectorChartItem> mDatas;

    private Context mContext;

    public static int[] mColors;
    private Paint mSectorPaint;

    private RectF mRectF;
    private float mWidth, mHeight;
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
        mWidth = w;
        mHeight = h;
        mRectF = new RectF(mBorderSize, mBorderSize, w - mBorderSize, h - mBorderSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        for (int i = 0; i < mDatas.size(); i++) {
            mSectorPaint.setColor(mColors[i]);
            canvas.drawArc(mRectF, mAngle, mDatas.get(i).angle, true, mSectorPaint);
            mAngle += mDatas.get(i).angle;
        }
        mAngle = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void provideData(List<? extends BaseChartItem> data) {
        mDatas = (List<SectorChartItem>) data;
        initAnim();
        invalidate();
    }

    private void initAnim() {
        clearAnim();
        mAnimator = ValueAnimator.ofInt(0, 360);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                parseData((int) animation.getAnimatedValue());
                postInvalidateOnAnimation();
            }
        });
        mAnimator.setRepeatCount(0);
        mAnimator.setDuration(1200);
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
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }

        float totalValue = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            totalValue += mDatas.get(i).value;
        }

        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).angle = mDatas.get(i).value / totalValue * totalAngle;
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
