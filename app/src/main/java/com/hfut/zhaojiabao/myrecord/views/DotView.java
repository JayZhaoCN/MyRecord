package com.hfut.zhaojiabao.myrecord.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/7/7
 */

public class DotView extends View {
    private Context mContext;
    private Paint mPaint;

    private float mCenterX, mCenterY;
    private float mRadius;

    public DotView(Context context) {
        this(context, null);
    }

    public DotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.DotView, defStyleAttr, 0);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ta.getColor(R.styleable.DotView_dot_color, ContextCompat.getColor(mContext, R.color.colorAccent)));
        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mCenterY = h / 2f;
        mRadius = w / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
    }
}
