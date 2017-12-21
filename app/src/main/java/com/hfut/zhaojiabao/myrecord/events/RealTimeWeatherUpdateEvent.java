package com.hfut.zhaojiabao.myrecord.events;

import com.hfut.zhaojiabao.myrecord.network.weather_entities.RealTimeWeatherEntity;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class RealTimeWeatherUpdateEvent {
    public RealTimeWeatherEntity entity;

    public RealTimeWeatherUpdateEvent(RealTimeWeatherEntity entity) {
        this.entity = entity;
    }
}
