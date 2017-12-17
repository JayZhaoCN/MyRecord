package com.hfut.zhaojiabao.myrecord.network;

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

    @SerializedName("parent_city")
    @SuppressWarnings("unused")
    public String parentCity;

    @SerializedName("admin_area")
    @SuppressWarnings("unused")
    public String adminArea;

    @SuppressWarnings("unused")
    public String cnty;

    @SuppressWarnings("unused")
    public String lat;

    @SuppressWarnings("unused")
    public String lon;

    @SuppressWarnings("unused")
    public String tz;
}
