package com.hfut.zhaojiabao.myrecord.chart;

import android.graphics.PointF;

import java.util.List;

/**
 * @author zhaojiabao 2017/9/14
 */

public class DataProvider {

    public List<Float> mData;
    public float mMaxValue = Float.MIN_VALUE;
    public float mMinValue = Float.MAX_VALUE;
    public List<PointF> mPoints;

    public DataProvider(List<Float> data) {
        mData = data;

        for (Float aFloat : data) {
            if (aFloat > mMaxValue) {
                mMaxValue = aFloat;
            }

            if (aFloat < mMinValue) {
                mMinValue = aFloat;
            }
        }
    }

}
