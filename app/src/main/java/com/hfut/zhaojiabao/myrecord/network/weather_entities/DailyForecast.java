package com.hfut.zhaojiabao.myrecord.network.weather_entities;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class DailyForecast {
    /**
     * 白天天气状况代码
     */
    @SerializedName("cond_code_d")
    @SuppressWarnings("unused")
    public String condCodeD;

    /**
     * 晚间天气状况代码
     */
    @SerializedName("cond_code_n")
    @SuppressWarnings("unused")
    public String condCodeN;

    /**
     * 白天天气状况描述
     */
    @SerializedName("cond_txt_d")
    @SuppressWarnings("unused")
    public String condTxtD;

    /**
     * 晚间天气状况描述
     */
    @SerializedName("cond_txt_n")
    @SuppressWarnings("unused")
    public String condTxtN;

    /**
     * 预报日期
     */
    public String date;

    /**
     * 相对湿度
     */
    @SuppressWarnings("unused")
    public String hum;

    /**
     * 月升时间
     */
    @SuppressWarnings("unused")
    public String mr;

    /**
     * 月落时间
     */
    @SuppressWarnings("unused")
    public String ms;

    /**
     * 降水量
     */
    @SuppressWarnings("unused")
    public String pcpn;

    /**
     * 降水概率
     */
    @SuppressWarnings("unused")
    public String pop;

    /**
     * 大气压强
     */
    @SuppressWarnings("unused")
    public String pres;

    /**
     * 日出时间
     */
    @SuppressWarnings("unused")
    public String sr;

    /**
     * 日落时间
     */
    @SuppressWarnings("unused")
    public String ss;

    /**
     * 最高温度
     */
    @SerializedName("tmp_max")
    @SuppressWarnings("unused")
    public String tmpMax;

    /**
     * 最低温度
     */
    @SerializedName("tmp_min")
    @SuppressWarnings("unused")
    public String tmpMin;

    /**
     * 紫外线强度指数
     */
    @SerializedName("uv_index")
    @SuppressWarnings("unused")
    public String uvIndex;

    /**
     * 能见度, 单位: 公里
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
    public String wind_sc;

    /**
     * 风速, 公里/小时
     */
    @SerializedName("wind_spd")
    @SuppressWarnings("unused")
    public String windSpd;
}
