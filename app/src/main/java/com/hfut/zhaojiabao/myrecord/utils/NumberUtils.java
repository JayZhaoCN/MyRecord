package com.hfut.zhaojiabao.myrecord.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author zhaojiabao 2017/6/6
 */

public class NumberUtils {

    /**
     * 四舍五入
     * @param value 数值
     * @param scale 位数
     * @return 结果
     */
    public static double format(double value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static float format(float value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    public static String getFormattedNumber(double number) {
        int numberInt = (int) number;
        if (numberInt == number) {
            return String.valueOf(numberInt);
        }
        return String.valueOf(number);
    }
}
