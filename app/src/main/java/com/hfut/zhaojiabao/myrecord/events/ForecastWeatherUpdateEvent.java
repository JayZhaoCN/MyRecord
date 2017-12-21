package com.hfut.zhaojiabao.myrecord.events;

import com.hfut.zhaojiabao.myrecord.network.weather_entities.ForecastWeatherEntity;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class ForecastWeatherUpdateEvent {
    public ForecastWeatherEntity entity;

    public ForecastWeatherUpdateEvent(ForecastWeatherEntity entity) {
        this.entity = entity;
    }
}
