package com.hfut.zhaojiabao.myrecord.chart;

import android.support.annotation.ColorInt;

/**
 * 轴线样式
 *
 * @author zhaojiabao 2017/9/19
 */

public class AxisStyle {
    /**
     * 轴线颜色
     */
    public @ColorInt int color;

    /**
     * 轴线宽度(in px)
     */
    public int width;

    /**
     * 刻度线高度(in px)
     */
    public int scaleHeight;

    public AxisStyle(@ColorInt int color, int width, int scaleHeight) {
        this.color = color;
        this.width = width;
        this.scaleHeight = scaleHeight;
    }
}
