package com.hfut.zhaojiabao.myrecord.weather_db;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class Province {

    public String proName;

    public int proSort;

    public String proRemark;

    @Override
    public String toString() {
        return proName + " " + proSort + " " + proRemark;
    }
}
