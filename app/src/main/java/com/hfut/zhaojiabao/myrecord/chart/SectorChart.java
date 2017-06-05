package com.hfut.zhaojiabao.myrecord.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hfut.zhaojiabao.myrecord.R;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/28
 */

public class SectorChart extends View implements BaseChart {
    private static final String TAG = "SectorChart";

    private List<SectorChartItem> mDatas;

    private Context mContext;

    private int[] mColors;
    private Paint mBorderPaint;
    private Paint mSectorPaint;

    private RectF mRectF;
    private float mWidth, mHeight;
    private float mAngle;

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
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mColors = new int[] {
                ContextCompat.getColor(mContext, R.color.pale_grey_three),
                ContextCompat.getColor(mContext, R.color.deep_lavender),
                ContextCompat.getColor(mContext, R.color.dark_sky_blue),
                ContextCompat.getColor(mContext, R.color.golden),
                ContextCompat.getColor(mContext, R.color.coral),
                ContextCompat.getColor(mContext, R.color.black_80),
                ContextCompat.getColor(mContext, R.color.colorAccent),
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRectF = new RectF(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i=0; i<mDatas.size(); i++) {
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
        parseData();
        invalidate();
    }

    private void parseData() {
        if(mDatas == null || mDatas.size() == 0) {
            return;
        }

        float totalValue = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            totalValue += mDatas.get(i).value;
        }

        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).angle = mDatas.get(i).value / totalValue * 360;
        }
    }

    public static class SectorChartItem extends BaseChartItem {
        //所占的角度
        float angle = 0;

        public SectorChartItem(String text, float percent, float angle) {
            super(text, percent);
            this.angle = angle;
        }

        public SectorChartItem(String text, float percent) {
            super(text, percent);
        }
    }
}
