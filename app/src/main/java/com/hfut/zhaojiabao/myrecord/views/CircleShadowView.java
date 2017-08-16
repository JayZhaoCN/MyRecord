package com.hfut.zhaojiabao.myrecord.views;

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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.TintUtils;

/**
 * Created by Jay on 2016/12/29.
 * circle shadow view
 */

//TODO RippleEffect更合理的写法是先写一个BaseView，有RippleEffect，然后需要有RippleEffect的CustomView继承BaseView即可。先放着，有时间过来重构下
public class CircleShadowView extends View {

    private Paint mCirclePaint;
    private Paint mShadowPaint;

    private Context mContext;

    private Drawable mIconDrawable;
    private Rect mIconBounds;

    private int mBackgroundColor;
    private int mRippleColor;
    private int mMaxRippleRadius;
    private int mRippleRadius;

    private float centerX;
    private float centerY;

    private ValueAnimator mRippleAnimator;
    private ValueAnimator mRippleColorAnimator;

    @ColorInt private int mTintColor;

    private static final int RIPPLE_DURATION = 400;

    private PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    public CircleShadowView(Context context) {
        this(context, null);
    }

    public CircleShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initAttrs(attrs, defStyleAttr);
        init();
        //默认是不展示的
        setAlpha(0);
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CircleShadowView, 0, defStyleAttr);
        mIconDrawable = ta.getDrawable(R.styleable.CircleShadowView_icon);
        mTintColor = ta.getColor(R.styleable.CircleShadowView_tint, ContextCompat.getColor(mContext, R.color.black));

        //为什么TypedArray.getDrawable()不提供一个默认值？
        if (mIconDrawable == null) {
            mIconDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_fab_write);
        }

        TintUtils.tintDrawable(mIconDrawable, mTintColor);
        mBackgroundColor = ta.getColor
                (R.styleable.CircleShadowView_circle_color, ContextCompat.getColor(mContext, R.color.colorAccent));
        ta.recycle();
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mBackgroundColor);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.TRANSPARENT);
        //阴影颜色
        mShadowPaint.setShadowLayer(10, 0, 5, ContextCompat.getColor(mContext, R.color.black30));

        //关闭硬件加速,否则无法setShadowLayer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mIconBounds = new Rect(w / 4, h / 4, 3 * w / 4, 3 * h / 4);
        mMaxRippleRadius = (int) Math.sqrt(w * w + h * h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw shadow
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 15, mShadowPaint);

        //draw background and ripple
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mCirclePaint.setColor(mBackgroundColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 15, mCirclePaint);
        mCirclePaint.setXfermode(mode);
        mCirclePaint.setColor(mRippleColor);
        canvas.drawCircle(centerX, centerY, mRippleRadius, mCirclePaint);
        mCirclePaint.setXfermode(null);
        canvas.restoreToCount(sc);

        //draw icon
        mIconDrawable.setBounds(mIconBounds);
        mIconDrawable.draw(canvas);
    }

    private void initRippleAnim() {
        if (mRippleAnimator == null) {
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

        if (mRippleColorAnimator == null) {
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                centerX = event.getX();
                centerY = event.getY();
                initRippleAnim();
                break;
        }
        return true;
    }
}









