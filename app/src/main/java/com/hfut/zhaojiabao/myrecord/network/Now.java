package com.hfut.zhaojiabao.myrecord.network;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class Now {
    public String cloud;

    @SerializedName("cond_code")
    @SuppressWarnings("unused")
    public String condCode;

    @SerializedName("cond_txt")
    @SuppressWarnings("unused")
    public String condTxt;

    @SuppressWarnings("unused")
    public String fl;

    @SuppressWarnings("unused")
    public String hum;

    @SuppressWarnings("unused")
    public String pcpn;

    @SuppressWarnings("unused")
    public String pres;

    @SuppressWarnings("unused")
    public String tmp;

    @SuppressWarnings("unused")
    public String vis;

    @SerializedName("wind_deg")
    @SuppressWarnings("unused")
    public String windDeg;

    @SerializedName("wind_dir")
    @SuppressWarnings("unused")
    public String windDir;

    @SerializedName("wind_sc")
    @SuppressWarnings("unused")
    public String windSc;

    @SerializedName("wind_spd")
    @SuppressWarnings("unused")
    public String windSpd;

}
