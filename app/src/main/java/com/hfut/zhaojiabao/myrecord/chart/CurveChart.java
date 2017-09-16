package com.hfut.zhaojiabao.myrecord.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.Utils;

import java.util.ArrayList;

/**
 * 曲线图
 *
 * @author zhaojiabao 2017/9/14
 */

public class CurveChart extends View {
    private static final int DEFAULT_ANIM_DURATION = 6000;
    private static final int DEFAULT_LINE_WIDTH = 5;

    private int mDrawAreaWidth, mDrawAreaHeight;
    private int mWidth, mHeight;
    private int mBlankX, mBlankY;
    //上方留白
    private int mTopBlankY;
    //右方留白
    private int mRightBlankX;
    private DataProvider mDataProvider;

    private Context mContext;

    private Path mPath;
    private Path mBgPath;

    private Paint mPaint;
    private Paint mPointPaint;
    private Paint mGradientBgPaint;
    private Paint mAxisPaint;
    private Paint mTextPaint;

    private Paint.FontMetrics mFontMetrics;

    private ValueAnimator mAnimator;

    public CurveChart(Context context) {
        this(context, null);
    }

    public CurveChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurveChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.grapefruit));
        mPaint.setStrokeWidth(DEFAULT_LINE_WIDTH);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);
        mPointPaint.setStrokeWidth(10);
        mPointPaint.setColor(ContextCompat.getColor(mContext, R.color.stp_bg_month));

        mGradientBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisPaint.setColor(ContextCompat.getColor(mContext, R.color.stp_bg_month));
        mAxisPaint.setStrokeWidth(4);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.black60));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(Utils.sp2px(mContext, 14));

        mFontMetrics = mTextPaint.getFontMetrics();

        mTopBlankY = (int) Utils.dp2px(mContext, 20);
        mRightBlankX = (int) Utils.dp2px(mContext, 20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mDrawAreaHeight = h - (mBlankY = (int) Utils.dp2px(mContext, 20)) - mTopBlankY;
        mDrawAreaWidth = w - (mBlankX = (int) Utils.dp2px(mContext, 20)) - mRightBlankX;

        mGradientBgPaint.setShader(
                new LinearGradient(
                        0, 0, 0, mDrawAreaHeight,
                        ContextCompat.getColor(mContext, R.color.stp_bg_month_best),
                        ContextCompat.getColor(mContext, R.color.transparent), Shader.TileMode.CLAMP));
        initPath();
        startAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnim();
    }

    public void provideData(DataProvider dataProvider) {
        mDataProvider = dataProvider;
        invalidate();
    }

    private void initPath() {
        if (mPath == null) {
            mPath = new Path();
        }

        float xAxisInterval = mDrawAreaWidth / (float) (mDataProvider.mData.datas.size() - 1);

        mDataProvider.mPoints = new ArrayList<>();

        for (int i = 0; i < mDataProvider.mData.datas.size(); i++) {
            mDataProvider.mPoints.add(new PointF(xAxisInterval * i, mDrawAreaHeight - mDataProvider.mData.datas.get(i) / mDataProvider.mMaxValue * mDrawAreaHeight));
        }

        mPath = BezierProvider.provideBezierPath(mDataProvider.mPoints, 0.25f, 0.25f);
        mBgPath = new Path(mPath);
        mBgPath.lineTo(mDrawAreaWidth, mDrawAreaHeight);
        mBgPath.lineTo(0, mDrawAreaHeight);
        mBgPath.close();

        invalidate();
    }

    public void startAnim() {
        PathMeasure measure = new PathMeasure(mPath, false);
        final float length = measure.getLength();

        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(1, 0);

            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    DashPathEffect effect = new DashPathEffect(new float[]{length, length}, length * (float) animation.getAnimatedValue());
                    mPaint.setPathEffect(effect);
                    postInvalidateOnAnimation();
                }
            });
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration(DEFAULT_ANIM_DURATION);
            mAnimator.setRepeatCount(0);
        }
        mAnimator.start();
    }

    public void cancelAnim() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mBlankX, mBlankY);

        canvas.drawPath(mPath, mPaint);

        for (PointF point : mDataProvider.mPoints) {
            canvas.drawPoint(point.x, point.y, mPointPaint);
        }
        canvas.save();
        canvas.translate(0, DEFAULT_LINE_WIDTH / 2);
        canvas.drawPath(mBgPath, mGradientBgPaint);
        canvas.restore();

        canvas.restore();

        //x-axis
        canvas.drawLine(mBlankX, mHeight - mBlankY, mWidth, mHeight - mBlankY, mAxisPaint);
        //y-axis
        canvas.drawLine(mBlankX, mHeight - mBlankY, mBlankX, 0, mAxisPaint);

        //draw x-axis text
        for (int i = 0; i < mDataProvider.mPoints.size(); i++) {
            canvas.drawText(mDataProvider.mData.texts.get(i), mDataProvider.mPoints.get(i).x + mBlankX, (mHeight * 2 - mBlankY - mFontMetrics.bottom - mFontMetrics.top) / 2, mTextPaint);
        }
    }
}
