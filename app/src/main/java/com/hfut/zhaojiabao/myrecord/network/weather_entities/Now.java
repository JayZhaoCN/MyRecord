package com.hfut.zhaojiabao.myrecord.network.weather_entities;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class Now {
    /**
     * 云量
     */
    public String cloud;

    /**
     * 实况天气状况代码
     */
    @SerializedName("cond_code")
    @SuppressWarnings("unused")
    public String condCode;

    /**
     * 实况天气状况描述
     */
    @SerializedName("cond_txt")
    @SuppressWarnings("unused")
    public String condTxt;

    /**
     * 体感温度, 默认单位:摄氏度
     */
    @SuppressWarnings("unused")
    public String fl;

    /**
     * 相对湿度
     */
    @SuppressWarnings("unused")
    public String hum;

    /**
     * 降水量
     */
    @SuppressWarnings("unused")
    public String pcpn;

    /**
     * 大气压强
     */
    @SuppressWarnings("unused")
    public String pres;

    /**
     * 温度, 默认单位: 摄氏度
     */
    @SuppressWarnings("unused")
    public String tmp;

    /**
     * 	能见度, 默认单位: 公里
     */
    @SuppressWarnings("unused")
    public String vis;

    /**
     * 风向360角度
     */
    @SerializedName("wind_deg")
    @SuppressWarnings("unused")
    public String windDeg;

    /**
     * 风向
     */
    @SerializedName("wind_dir")
    @SuppressWarnings("unused")
    public String windDir;

    /**
     * 风力
     */
    @SerializedName("wind_sc")
    @SuppressWarnings("unused")
    public String windSc;

    /**
     * 风速, 公里/小时
     */
    @SerializedName("wind_spd")
    @SuppressWarnings("unused")
    public String windSpd;

}
