package com.hfut.zhaojiabao.myrecord.network.weather_entities;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class Basic {
    /**
     * 地区/城市名称
     */
    @SuppressWarnings("unused")
    public String location;

    /**
     * 地区/城市ID
     */
    @SuppressWarnings("unused")
    public String cid;

    /**
     * 该地区/城市的上级城市
     */
    @SerializedName("parent_city")
    @SuppressWarnings("unused")
    public String parentCity;

    /**
     * 该地区/城市所属行政区域
     */
    @SerializedName("admin_area")
    @SuppressWarnings("unused")
    public String adminArea;

    /**
     * 该地区/城市所属国家名称
     */
    @SuppressWarnings("unused")
    public String cnty;

    /**
     * 地区/城市经度
     */
    @SuppressWarnings("unused")
    public String lat;

    /**
     * 地区/城市纬度
     */
    @SuppressWarnings("unused")
    public String lon;

    /**
     * 该地区/城市所在时区
     */
    @SuppressWarnings("unused")
    public String tz;
}
