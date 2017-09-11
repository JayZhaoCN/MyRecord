package com.hfut.zhaojiabao.myrecord.utils;

import android.content.Context;

import com.hfut.zhaojiabao.myrecord.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author zhaojiabao 2017/5/18
 */

public class TimeFormatter {

    /**
     * format like below:
     * 17:14
     */
    public static String formatHHmm(long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(timeInMillis);
    }

    /**
     * format like below:
     * 今天 17:14
     * 8/29 09:13
     * 2016/9/11 17:49
     */
    public static String niceFormat(Context context, long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String hmStr = format.format(timeInMillis);

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(timeInMillis);
        int yearThen = calendar.get(Calendar.YEAR);
        int monthThen = calendar.get(Calendar.MONTH) + 1;
        int dayThen = calendar.get(Calendar.DAY_OF_MONTH);

        //今天
        if (yearThen == year && monthThen == month && dayThen == day) {
            return context.getString(R.string.today_item) + " " + hmStr;
        } else if (yearThen == year) {
            return monthThen + "/" + dayThen + " " + hmStr;
        } else {
            return yearThen + "/" + monthThen + "/" + dayThen + " " + hmStr;
        }
    }

    /**
     * format like below:
     * 2016-9-11
     * 08-29
     * 09-06
     */
    public static String formatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
        String mmDDStr = format.format(time);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(time);
        int recordYear = calendar.get(Calendar.YEAR);

        return year == recordYear ? mmDDStr : recordYear + "-" + mmDDStr;
    }

    /**
     * get timeMillis for the start and end of today.
     */
    public static long[] getTodayBounds() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long startMillis = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long endMillis = calendar.getTimeInMillis() - 1;

        long[] bounds = new long[2];
        bounds[0] = startMillis;
        bounds[1] = endMillis;

        return bounds;
    }
}
