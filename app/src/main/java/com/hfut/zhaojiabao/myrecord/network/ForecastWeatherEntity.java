package com.hfut.zhaojiabao.myrecord.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class ForecastWeatherEntity {

    public Basic basic;

    @SuppressWarnings("unused")
    public Update update;

    @SuppressWarnings("unused")
    public String status;

    @SerializedName("daily_forecast")
    @SuppressWarnings("unused")
    public List<DaliyForecast> dailyForecast;

    public static class DaliyForecast {
        @SerializedName("cond_code_d")
        @SuppressWarnings("unused")
        public String condCodeD;

        @SerializedName("cond_code_n")
        @SuppressWarnings("unused")
        public String condCodeN;

        @SerializedName("cond_txt_d")
        @SuppressWarnings("unused")
        public String condTxtD;

        @SerializedName("cond_txt_n")
        @SuppressWarnings("unused")
        public String condTxtN;

        public String date;

        @SuppressWarnings("unused")
        public String hum;

        @SuppressWarnings("unused")
        public String mr;

        @SuppressWarnings("unused")
        public String ms;

        @SuppressWarnings("unused")
        public String pcpn;

        @SuppressWarnings("unused")
        public String pop;

        @SuppressWarnings("unused")
        public String pres;

        @SuppressWarnings("unused")
        public String sr;

        @SuppressWarnings("unused")
        public String ss;

        @SerializedName("tmp_max")
        @SuppressWarnings("unused")
        public String tmpMax;

        @SerializedName("tmp_min")
        @SuppressWarnings("unused")
        public String tmpMin;

        @SerializedName("uv_index")
        @SuppressWarnings("unused")
        public String uvIndex;

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
        public String wind_sc;

        @SerializedName("wind_spd")
        @SuppressWarnings("unused")
        public String windSpd;
    }

}
