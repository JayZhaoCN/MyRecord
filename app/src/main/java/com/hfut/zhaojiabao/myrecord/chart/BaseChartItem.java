package com.hfut.zhaojiabao.myrecord.chart;

/**
 * @author zhaojiabao 2017/5/28
 */

public abstract class BaseChartItem {
    public String text;
    public float value;

    public BaseChartItem(String text, float value) {
        this.text = text;
        this.value = value;
    }
}
