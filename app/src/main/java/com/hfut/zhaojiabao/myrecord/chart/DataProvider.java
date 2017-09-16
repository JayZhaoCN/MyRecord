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

    public List<Float> mDatas;
    public List<String> mTexts;

    public DataProvider(List<Float> datas, List<String> texts) {
        mDatas = datas;
        mTexts = texts;

        for (Float aFloat : mDatas) {
            if (aFloat > mMaxValue) {
                mMaxValue = aFloat;
            }

            if (aFloat < mMinValue) {
                mMinValue = aFloat;
            }
        }
    }
}
