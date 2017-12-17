package com.hfut.zhaojiabao.myrecord.network.weather_entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class ForecastWeatherEntity {

    /**
     * 基础信息
     */
    public Basic basic;

    /**
     * 接口更新时间
     */
    @SuppressWarnings("unused")
    public Update update;

    /**
     * 接口状态
     */
    @SuppressWarnings("unused")
    public String status;

    /**
     * 天气预报
     */
    @SerializedName("daily_forecast")
    @SuppressWarnings("unused")
    public List<DailyForecast> dailyForecast;
}
