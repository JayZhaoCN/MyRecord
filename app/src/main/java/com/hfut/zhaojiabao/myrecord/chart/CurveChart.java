package com.hfut.zhaojiabao.myrecord.chart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线图
 *
 * @author zhaojiabao 2017/9/14
 */

//TODO 太长的代码需要分段写
public class CurveChart extends View {
    private static final int DEFAULT_ANIM_DURATION = 3000;
    private static final int DEFAULT_LINE_WIDTH = 5;
    private static final String TAG = "CurveChart";

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
    private Paint mLabelTextPaint;

    private List<Path> mLabelPaths;

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
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.sunflower));
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

        mLabelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLabelTextPaint.setColor(ContextCompat.getColor(mContext, R.color.white));
        mLabelTextPaint.setTextAlign(Paint.Align.CENTER);
        mLabelTextPaint.setTextSize(Utils.sp2px(mContext, 10));

        mTopBlankY = (int) Utils.dp2px(mContext, 20);
        mRightBlankX = (int) Utils.dp2px(mContext, 20);
    }

    public void provideData(@NonNull DataProvider dataProvider) {
        mDataProvider = dataProvider;
        invalidate();
    }

    private void initPath() {
        float xAxisInterval = mDrawAreaWidth / (float) (mDataProvider.mDatas.size() - 1);

        mDataProvider.mPoints = new ArrayList<>();

        for (int i = 0; i < mDataProvider.mDatas.size(); i++) {
            float data = mDataProvider.mDatas.get(i);
            mDataProvider.mPoints.add(new PointF(xAxisInterval * i,
                    mDrawAreaHeight - data / mDataProvider.mMaxValue * mDrawAreaHeight));
        }

        mPath = PathProvider.provideBezierPath(mDataProvider.mPoints, 0.25f, 0.25f);
        if (mPath != null) {
            mBgPath = new Path(mPath);
            mBgPath.lineTo(mDrawAreaWidth, mDrawAreaHeight);
            mBgPath.lineTo(0, mDrawAreaHeight);
            mBgPath.close();
        }

        List<Integer> labelWidths = new ArrayList<>();
        for (int i = 0; i < mDataProvider.mDatas.size(); i++) {
            String label = String.valueOf(mDataProvider.mDatas.get(i));
            //+6是为了让label两边稍微空出一点距离
            labelWidths.add((int) mLabelTextPaint.measureText(label, 0, label.length()) + 6);
        }

        mLabelPaths = PathProvider.provideLabelPath(mDataProvider.mPoints, labelWidths);

        invalidate();
    }

    public void startAnim() {
        if (mDataProvider.mDatas.size() <= 1) {
            Log.w(TAG, "data source size should be more than one.");
            return;
        }

        mGradientBgPaint.setShader(
                new LinearGradient(
                        0, 0, 0, mDrawAreaHeight,
                        ContextCompat.getColor(mContext, R.color.stp_bg_month_best),
                        ContextCompat.getColor(mContext, R.color.transparent), Shader.TileMode.CLAMP));
        initPath();

        final PathMeasure measure = new PathMeasure(mPath, false);
        final float length = measure.getLength();

        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(1, 0);

            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                int count = 1;
                float lastX = 0;
                float xAxisInterval = mDrawAreaWidth / (float) (mDataProvider.mDatas.size() - 1);

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    DashPathEffect effect = new DashPathEffect(new float[]{length, length}, length * animatedValue);
                    float[] pos = new float[2];
                    measure.getPosTan(length * (1 - animatedValue), pos, null);

                    if (pos[0] - lastX > xAxisInterval) {
                        lastX = pos[0];
                        //TODO 动画执行到第count个点
                        System.out.println("JayLog, x changed." + count++);
                    }

                    mPaint.setPathEffect(effect);
                    postInvalidateOnAnimation();
                }
            });

            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //TODO 动画执行到第1个点
                    System.out.println("JayLog, x started.");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //TODO 动画执行到最后一个点
                    System.out.println("JayLog, x ended.");
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

        if (mPath == null) {
            return;
        }

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

        //draw labels
        for (int i = 0; i < mLabelPaths.size(); i++) {
            canvas.drawPath(mLabelPaths.get(i), mAxisPaint);
            float baseline = (mDataProvider.mPoints.get(i).y * 2
                    - 20 - 35 - 20 - mLabelTextPaint.getFontMetrics().bottom
                    - mLabelTextPaint.getFontMetrics().top) / 2;
            String label = String.valueOf(mDataProvider.mDatas.get(i));
            canvas.drawText(label, mDataProvider.mPoints.get(i).x, baseline, mLabelTextPaint);
        }

        canvas.restore();

        //x-axis
        canvas.drawLine(mBlankX, mHeight - mBlankY, mWidth, mHeight - mBlankY, mAxisPaint);
        //y-axis
        canvas.drawLine(mBlankX, mHeight - mBlankY, mBlankX, 0, mAxisPaint);

        //draw x-axis text
        for (int i = 0; i < mDataProvider.mPoints.size(); i++) {
            String label = mDataProvider.mTexts.get(i);
            canvas.drawText(label,
                    mDataProvider.mPoints.get(i).x + mBlankX,
                    (mHeight * 2 - mBlankY - mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top) / 2,
                    mTextPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mDrawAreaHeight = h - (mBlankY = (int) Utils.dp2px(mContext, 20)) - mTopBlankY;
        mDrawAreaWidth = w - (mBlankX = (int) Utils.dp2px(mContext, 20)) - mRightBlankX;
    }
}
