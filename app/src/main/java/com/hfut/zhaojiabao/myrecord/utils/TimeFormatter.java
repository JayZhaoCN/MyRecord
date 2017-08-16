package com.hfut.zhaojiabao.myrecord.utils;


import android.content.Context;

import com.hfut.zhaojiabao.myrecord.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author zhaojiabao 2017/5/18
 */

public class TimeFormatter {

    private static SimpleDateFormat mFormatter;
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static TimeFormatter sInstance;

    private TimeFormatter() {
        if (mFormatter == null) {
            mFormatter = new SimpleDateFormat(FORMAT, Locale.getDefault());
        }
    }

    public static TimeFormatter getInstance() {
        if (sInstance == null) {
            sInstance = new TimeFormatter();
        }
        return sInstance;
    }

    public String format(long time) {
        Date d1 = new Date(time);
        return mFormatter.format(d1);
    }

    public String niceFormat(Context context, long time) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(time);
        int yearThen = calendar.get(Calendar.YEAR);
        int monthThen = calendar.get(Calendar.MONTH) + 1;
        int dayThen = calendar.get(Calendar.DAY_OF_MONTH);
        int hourThen = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteThen = calendar.get(Calendar.MINUTE);
        String hourThenStr;
        if (hourThen < 10) {
            hourThenStr = "0" + hourThen;
        } else {
            hourThenStr = String.valueOf(hourThen);
        }
        String minuteThenStr;
        if (minuteThen < 10) {
            minuteThenStr = "0" + minuteThen;
        } else {
            minuteThenStr = String.valueOf(minuteThen);
        }

        //今天
        if (yearThen == year && monthThen == month && dayThen == day) {
            return context.getString(R.string.today_item) + " " + hourThenStr + ":" + minuteThenStr;
        } else if (yearThen == year) {
            return monthThen + "/" + dayThen + " " + hourThenStr + ":" + minuteThenStr;
        } else {
            return yearThen + "/" + monthThen + "/" + dayThen + " " + hourThenStr + ":" + minuteThenStr;
        }
    }

    public String formatDate(long time) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(time);

        int recordYear = calendar.get(Calendar.YEAR);
        int recordMonth = calendar.get(Calendar.MONTH) + 1;
        int recordDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (year == recordYear) {
            return recordMonth + "-" + recordDay;
        } else {
            return recordYear + "-" + recordMonth + "-" + recordDay;
        }
    }

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