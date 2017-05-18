package com.hfut.zhaojiabao.myrecord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Jay on 2017/1/3.
 * CrossBottomView
 */

public class CrossBottomView extends View {
    private Paint mPaint;
    private Paint mBackgroundPaint;
    private Paint mShadowPaint;

    private Context mContext;
    private int mBackgroundColor;
    private int mCrossColor;
    private int mRotateDegree;
    private int mRippleRadius;
    private int mRippleColor;

    private float centerX;
    private float centerY;

    //default state is plus
    private boolean mIsCross = false;

    private int mMaxRippleRadius;

    private ValueAnimator mRippleAnimator;
    private ValueAnimator mRippleColorAnimator;

    private PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private static final double HALF_CROSS_LENGTH_PERCENT = 0.1;
    private static final double LINE_WIDTH_PERCENT = 0.028;

    private static final int RIPPLE_DURATION = 420;
    private static final int ANIMATION_DURATION = 500;

    public CrossBottomView(Context context) {
        this(context, null);
    }

    public CrossBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs, defStyleAttr);
        init();
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CrossBottomView, 0, defStyleAttr);
        mBackgroundColor = ta.getColor
                (R.styleable.CrossBottomView_background_color, ContextCompat.getColor(mContext, R.color.colorAccent));
        mCrossColor = ta.getColor
                (R.styleable.CrossBottomView_cross_color, ContextCompat.getColor(mContext, R.color.half_black_dark));
        ta.recycle();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mCrossColor);
        mPaint.setStyle(Paint.Style.STROKE);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);

        Paint mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRipplePaint.setColor(ContextCompat.getColor(mContext, R.color.light_white));

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setShadowLayer(10, 0, 5, ContextCompat.getColor(mContext, R.color.black30));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mRippleColor = ContextCompat.getColor(mContext, R.color.light_white);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint.setStrokeWidth((float) (w*LINE_WIDTH_PERCENT));
        mMaxRippleRadius = (int) Math.sqrt(w*w + h*h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2 - 15, mShadowPaint);

        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2 - 15, mBackgroundPaint);
        mBackgroundPaint.setXfermode(mode);
        mBackgroundPaint.setColor(mRippleColor);
        canvas.drawCircle(centerX, centerY, mRippleRadius, mBackgroundPaint);
        mBackgroundPaint.setXfermode(null);
        canvas.restoreToCount(sc);

        canvas.save();
        canvas.rotate(mRotateDegree, getWidth()/2, getHeight()/2);
        drawLines(canvas);
        canvas.restore();
    }

    private void initAnim(boolean isChangeToCross) {
        ValueAnimator mAnimator = ValueAnimator.ofInt(0, isChangeToCross ? 225 : -180);
        mAnimator.setInterpolator(new OvershootInterpolator());
        mAnimator.setDuration(ANIMATION_DURATION);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRotateDegree = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.start();
    }

    private void drawLines(Canvas canvas) {
        canvas.drawLine((float) (getWidth()/2 - getWidth()* HALF_CROSS_LENGTH_PERCENT), getHeight()/2,
                (float) (getWidth()/2 + getWidth()* HALF_CROSS_LENGTH_PERCENT), getHeight()/2, mPaint);

        canvas.drawLine(getWidth()/2, (float) (getHeight()/2 - getHeight()* HALF_CROSS_LENGTH_PERCENT),
                getWidth()/2, (float) (getHeight()/2 + getHeight()* HALF_CROSS_LENGTH_PERCENT), mPaint);
    }

    private void initRippleAnim() {
        if(mRippleAnimator == null) {
            mRippleColor = ContextCompat.getColor(mContext, R.color.ripple_color);
            mRippleAnimator = ValueAnimator.ofInt(0, mMaxRippleRadius);
            mRippleAnimator.setDuration(RIPPLE_DURATION);
            mRippleAnimator.setInterpolator(new LinearInterpolator());
            mRippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mRippleRadius = (int) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });

            mRippleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRippleRadius = 0;
                    invalidate();
                }
            });
        }

        if(mRippleColorAnimator == null) {
            mRippleColorAnimator = ValueAnimator.ofInt(ContextCompat.getColor(mContext, R.color.ripple_color), Color.TRANSPARENT);
            mRippleColorAnimator.setDuration(RIPPLE_DURATION);
            mRippleColorAnimator.setInterpolator(new LinearInterpolator());
            mRippleColorAnimator.setEvaluator(new ArgbEvaluator());
            mRippleColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mRippleColor = (int) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
        }

        mRippleAnimator.start();
        mRippleColorAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                centerX = event.getX();
                centerY = event.getY();
                initRippleAnim();
                break;
            case MotionEvent.ACTION_UP:
                initAnim(mIsCross = !mIsCross);
                break;
        }
        return true;
    }
}
