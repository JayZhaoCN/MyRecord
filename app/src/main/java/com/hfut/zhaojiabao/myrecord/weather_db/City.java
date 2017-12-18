package com.hfut.zhaojiabao.myrecord.weather_db;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class City {

    public String cityName;

    public int proId;

    public int citySort;

    @Override
    public String toString() {
        return cityName + " " + proId + " " + citySort;
    }
}
