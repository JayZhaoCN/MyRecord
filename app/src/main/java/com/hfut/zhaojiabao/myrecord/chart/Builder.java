package com.hfut.zhaojiabao.myrecord.chart;

/**
 * @author zhaojiabao 2017/9/22
 */

public class Builder {
    /**
     * 不同的图表类型决定不同的x轴取点方式
     */
    static final int BAR_CHART = 0;
    public static final int CURVE_CHART = 1;

    int mChartType = CURVE_CHART;

    /**
     * 上方留白
     */
    int mTopBlank = 0;

    /**
     * 下方留白
     */
    int mBottomBlank = 0;

    /**
     * 左边留白
     */
    int mLeftBlank = 0;

    /**
     * 右边留白
     */
    int mRightBlank = 0;

    /**
     * 轴线样式
     */
    AxisStyle axisStyle;

    /**
     * 图表数据
     */
    DataProvider dataProvider;

    public Builder(int chartType, int leftBlank, int topBlank, int rightBlank, int bottomBlank) {
        mChartType = chartType;
        mLeftBlank = leftBlank;
        mTopBlank = topBlank;
        mRightBlank = rightBlank;
        mBottomBlank = bottomBlank;
    }

    public Builder setAxisStyle(AxisStyle style) {
        this.axisStyle = style;
        return this;
    }

    public Builder setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        return this;
    }
}