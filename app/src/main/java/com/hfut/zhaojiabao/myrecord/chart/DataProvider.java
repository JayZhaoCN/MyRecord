package com.hfut.zhaojiabao.myrecord.chart;

import android.graphics.PointF;

import com.hfut.zhaojiabao.myrecord.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao 2017/9/14
 */

public class DataProvider {
    List<PointF> mPoints;
    float mMaxValue = Float.MIN_VALUE;

    /**
     * 取以10为底的对数后的数据
     */
    public List<Float> data;

    /**
     * 原始数据
     */
    List<Float> rawData;

    /**
     * x轴label
     */
    List<String> xScaleTexts;

    /**
     * y轴label数
     */
    int yScaleNum;

    /**
     * y轴label
     */
    List<String> yScaleTexts;

    /**
     * y轴label高度
     */
    List<Float> yScaleHeights;
    /**
     * 是否取对数
     */
    @SuppressWarnings("unused")
    private boolean logarithm;


    /**
     * DataProvider Constructor
     *
     * @param data        原始数据
     * @param xScaleTexts x轴label
     * @param logarithm   是否对原始数据取对数
     * @param yScaleNum   y轴label数, 0表示不画
     */
    public DataProvider(List<Float> data, List<String> xScaleTexts, boolean logarithm, int yScaleNum) {
        this.rawData = data;
        this.xScaleTexts = xScaleTexts;
        this.logarithm = logarithm;
        this.yScaleNum = yScaleNum;

        if (logarithm) {
            this.data = new ArrayList<>();
            for (int i = 0; i < rawData.size(); i++) {
                float value = (float) Math.log10(rawData.get(i) + 1);
                this.data.add(value);
            }
        } else {
            this.data = data;
        }

        for (Float aFloat : this.data) {
            if (aFloat > mMaxValue) {
                mMaxValue = aFloat;
            }
        }

        if (yScaleNum > 0) {
            yScaleTexts = new ArrayList<>();
            float perScale = mMaxValue / (yScaleNum + 1);
            if (logarithm) {
                for (int i = 0; i < yScaleNum; i++) {
                    yScaleTexts.add(String.valueOf(NumberUtils.format(Math.pow(10, perScale * (i + 1)), 2)));
                }
            } else {
                for (int i = 0; i < yScaleNum; i++) {
                    yScaleTexts.add(String.valueOf(NumberUtils.format(perScale * (i + 1), 2)));
                }
            }
        }
    }
}
