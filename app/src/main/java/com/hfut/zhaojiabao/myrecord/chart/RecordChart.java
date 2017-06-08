package com.hfut.zhaojiabao.myrecord.chart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.hfut.zhaojiabao.myrecord.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao 2017/5/23
 *         柱状图
 */

public class RecordChart extends View implements BaseChart {

    private Context mContext;
    private int mWidth, mHeight;
    //一屏显示的柱子数量
    private int mCount = 5;
    //每根柱子的宽度
    private float mPerWidth;
    //底部的高度
    private float mXHeight;

    private float mExcursion = 0;
    private float mMaxExcursion;

    private float mInterval = 0;

    private float mCurrX = 0;

    private List<ChartItem> mDatas;

    private Scroller mScroller;

    //x轴画笔
    private Paint mXPaint;
    //柱子画笔
    private Paint mColumnPaint;
    //文字画笔
    private Paint mTextPaint;

    private Paint.FontMetrics mFontMetrics;

    private GestureDetectorCompat mDetector;

    //选中时的颜色
    private int mSelectedColor;
    //未选中时的颜色
    private int mNormalColor;

    private float left, top, right, bottom;
    private int max, min;

    private ValueAnimator mAnimator;

    //标识是否向左滑动
    private boolean mFlingLeft = false;

    private int mSelectedIndex = -1;
    private float mSelectedLeft;
    private float mSelectedRight;

    private OnColumnSelectedListener mOnColumnSelectedListener;

    private boolean mShouldFly = false;

    private ValueAnimator mEnterAnimator;
    private float mHeightPercent = 0;
    private boolean mShouldEnd = false;

    private GestureDetector.OnGestureListener mListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            mShouldEnd = false;
            endAnimation();
            mShouldFly = false;
            mCurrX = 0;
            mScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!judgeExcursion(distanceX > 0)) {
                return false;
            }
            mShouldFly = false;
            mExcursion -= distanceX;
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mFlingLeft = velocityX < 0;
            mShouldEnd = false;
            endAnimation();
            mShouldFly = true;
            int max = 5400;
            int min = -max;
            mScroller.fling(0, 0, (int) velocityX, 0, min, max, 0, 0);
            return true;
        }
    };

    public RecordChart(Context context) {
        this(context, null);
    }

    public RecordChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mXPaint = new Paint();
        mXPaint.setColor(Color.parseColor("#F6F7F8"));

        mColumnPaint = new Paint();
        mColumnPaint.setColor(Color.BLUE);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(DisplayUtil.sp2px(mContext, 13));
        mTextPaint.setColor(Color.parseColor("#66000000"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mFontMetrics = mTextPaint.getFontMetrics();

        //默认高度是28dp
        mXHeight = DisplayUtil.dp2px(mContext, 28);

        mDatas = new ArrayList<>();

        mDetector = new GestureDetectorCompat(mContext, mListener);
        mScroller = new Scroller(mContext);

        //TODO test code
        mDatas.add(new ChartItem(10000000, "一月"));
        mDatas.add(new ChartItem(10000, "二月"));
        mDatas.add(new ChartItem(1000, "三月"));
        mDatas.add(new ChartItem(100, "四月"));
        mDatas.add(new ChartItem(10, "五月"));
        mDatas.add(new ChartItem(2, "六月"));
        mDatas.add(new ChartItem(10, "七月"));
        mDatas.add(new ChartItem(150000, "八月"));
        mDatas.add(new ChartItem(1500000, "九月"));
        mDatas.add(new ChartItem(90, "十月"));
        mDatas.add(new ChartItem(100, "十一月"));
        mDatas.add(new ChartItem(2, "十二月"));

        mInterval = DisplayUtil.dp2px(mContext, 1.5f);
        mNormalColor = Color.parseColor("#ABD2F1");
        mSelectedColor = Color.parseColor("#FFFFFF");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPerWidth = (float) w / mCount;
        mMaxExcursion = mPerWidth * (mDatas.size() - 1);

        //-60是为了防止柱子和图表一样高
        ValueTransfer.transform(mDatas, mHeight - 60 - mXHeight);
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, mHeight - mXHeight, mWidth, mHeight, mXPaint);

        min = (int) (mDatas.size() - (1.5 * mPerWidth + mWidth / 2 + mExcursion) / mPerWidth - 1);
        max = (int) ((2.5 * mPerWidth + mWidth / 2 - mExcursion) / mPerWidth + mDatas.size() - 1);

        if (max >= mDatas.size()) {
            max = mDatas.size() - 1;
        }
        if (min >= mDatas.size()) {
            min = mDatas.size() - 1;
        }
        if (min < 0) {
            min = 0;
        }

        for (int i = min; i <= max; i++) {
            left = mWidth / 2 - mPerWidth / 2 - mPerWidth * (mDatas.size() - i - 1) + mExcursion;
            right = left + mPerWidth;
            top = mHeight - mDatas.get(i).value * mHeightPercent - mXHeight;
            bottom = mHeight - mXHeight;

            if (judgeCenter(left, right)) {
                if (mSelectedIndex != i) {
                    mSelectedIndex = i;
                }
                mSelectedLeft = left;
                mSelectedRight = right;
                mColumnPaint.setColor(mSelectedColor);
            } else {
                mColumnPaint.setColor(mNormalColor);
            }

            canvas.drawRect(left, top, right - mInterval, bottom, mColumnPaint);
            //draw text
            canvas.drawText(mDatas.get(i).text, (left + right) / 2,
                    (2 * mHeight - mXHeight - mFontMetrics.bottom - mFontMetrics.top) / 2, mTextPaint);
        }
    }

    private boolean judgeCenter(float left, float right) {
        return left < mWidth / 2 && right > mWidth / 2;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (!mShouldFly) return;

        if (mScroller.isFinished()) {
            mCurrX = 0;
            mShouldFly = false;
            getExcursion();
            return;
        }

        if (mScroller.computeScrollOffset()) {
            mExcursion += mScroller.getCurrX() - mCurrX;
            mCurrX = mScroller.getCurrX();
            if (!judgeExcursion(mFlingLeft)) {
                return;
            }
            invalidate();
        } else {
            mCurrX = 0;
        }
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        cleanAnim();

        mEnterAnimator = ValueAnimator.ofFloat(0, 1);
        mEnterAnimator.setRepeatCount(0);
        mEnterAnimator.setDuration(1000);
        mEnterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mHeightPercent = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });
        mEnterAnimator.start();
    }

    /**
     * 清除动画
     */
    private void cleanAnim() {
        if(mEnterAnimator != null && mEnterAnimator.isRunning()) {
            mEnterAnimator.end();
        }
        mEnterAnimator = null;
    }

    private int mSelectedIndexForListener = -1;

    private boolean judgeExcursion(boolean left) {
        if (mExcursion >= mMaxExcursion && !left) {
            if (mSelectedIndexForListener != 0 && mOnColumnSelectedListener != null) {
                mOnColumnSelectedListener.onColumnSelected(0);
                mSelectedIndexForListener = 0;
            }

            mExcursion = mMaxExcursion;
            mScroller.forceFinished(true);
            return false;
        }
        if (mExcursion <= 0 && left) {
            if (mSelectedIndexForListener != mDatas.size() - 1 && mOnColumnSelectedListener != null) {
                mOnColumnSelectedListener.onColumnSelected(mDatas.size() - 1);
                mSelectedIndexForListener = mDatas.size() - 1;
            }
            mExcursion = 0;
            mScroller.forceFinished(true);
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //如果有onFling，会把这步操作冲掉，不用担心
                getExcursion();
                break;
        }
        return mDetector.onTouchEvent(event);
    }

    private void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
            mAnimator = null;
        }
    }

    private void getExcursion() {
        float centerX = (mSelectedLeft + mSelectedRight) / 2;
        mAnimator = ValueAnimator.ofFloat(mExcursion, mExcursion - centerX + mWidth / 2);
        mAnimator.setDuration(300);
        mAnimator.setRepeatCount(0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mExcursion = (float) animation.getAnimatedValue();

                postInvalidateOnAnimation();
            }
        });
        mShouldEnd = true;
        mAnimator.start();
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mShouldEnd) {
                    if (mOnColumnSelectedListener != null) {
                        mSelectedIndexForListener = mSelectedIndex;
                        mOnColumnSelectedListener.onColumnSelected(mSelectedIndex);
                    }
                }
            }
        });
    }

    public void setOnColumnSelectedListener(OnColumnSelectedListener listener) {
        mOnColumnSelectedListener = listener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void provideData(List<? extends BaseChartItem> data) {
        mDatas = (List<ChartItem>) data;
        invalidate();
    }

    public interface OnColumnSelectedListener {
        void onColumnSelected(int position);
    }

    public static class ChartItem extends BaseChartItem {
        public ChartItem(float value, String text) {
            super(text, value);
        }
    }
}
