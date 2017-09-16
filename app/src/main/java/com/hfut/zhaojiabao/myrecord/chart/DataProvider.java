package com.hfut.zhaojiabao.myrecord.chart;

import android.graphics.PointF;

import java.util.List;

/**
 * @author zhaojiabao 2017/9/14
 */

public class DataProvider {
    public List<PointF> mPoints;
    public float mMaxValue = Float.MIN_VALUE;
    public float mMinValue = Float.MAX_VALUE;

    public ChartData mData;

    public DataProvider(ChartData data) {
        mData = data;

        for (Float aFloat : data.datas) {
            if (aFloat > mMaxValue) {
                mMaxValue = aFloat;
            }

            if (aFloat < mMinValue) {
                mMinValue = aFloat;
            }
        }
    }

    public static class ChartData {
        public List<Float> datas;
        public List<String> texts;

        public ChartData(List<Float> datas, List<String> texts) {
            this.datas = datas;
            this.texts = texts;
        }
    }

}
