package com.hfut.zhaojiabao.myrecord.network.weather_entities;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class RealTimeWeatherEntity {
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
     *  实况天气
     */
    public Now now;
}