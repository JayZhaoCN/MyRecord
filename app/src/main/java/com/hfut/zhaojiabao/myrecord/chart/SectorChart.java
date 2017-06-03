package com.hfut.zhaojiabao.myrecord.chart;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/28
 */

public class SectorChart extends View implements BaseChart {
    private static final String TAG = "SectorChart";

    private List<SectorChartItem> mDatas;

    private Context mContext;

    public SectorChart(Context context) {
        this(context, null);
    }

    public SectorChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectorChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void provideData(List<? extends BaseChartItem> data) {
        mDatas = (List<SectorChartItem>) data;
        invalidate();
    }


    public static class SectorChartItem extends BaseChartItem {
        public SectorChartItem(String text, float percent) {
            super(text, percent);
        }
    }
}
