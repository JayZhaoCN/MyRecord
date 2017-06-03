package com.hfut.zhaojiabao.myrecord.chart;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/28
 */

public interface BaseChart {

    void provideData(List<? extends BaseChartItem> data);

}
