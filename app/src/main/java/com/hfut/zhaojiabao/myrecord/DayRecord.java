package com.hfut.zhaojiabao.myrecord;

/**
 * @author zhaojiabao 2017/5/24
 */

public class DayRecord {
    //日期(2017-05-24)
    public String date = "";
    public int year = -1;
    public int month = -1;
    public int day = -1;
    public long timeMillis = -1;
    //支出总计
    public float incomeSum = -1;
    //收入总计
    public float expendSum = -1;
    //总计
    public float sum = -1;

    @Override
    public String toString() {
        return "year: " + year
                + " month: " + month
                + " day: " + day
                + " incomeSum: " + incomeSum
                + " expendSum: " + expendSum
                + " sum: " + sum;
    }
}
