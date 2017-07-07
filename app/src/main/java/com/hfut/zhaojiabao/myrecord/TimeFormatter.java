package com.hfut.zhaojiabao.myrecord;


import android.content.Context;

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
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(time);
        int yearThen = calendar.get(Calendar.YEAR);
        int monthThen = calendar.get(Calendar.MONTH);
        int dayThen = calendar.get(Calendar.DAY_OF_MONTH);
        int hourThen = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteThen = calendar.get(Calendar.MINUTE);

        //今天
        if (yearThen == year && monthThen == month && dayThen == day) {
            return context.getString(R.string.today_item) + " " + hourThen + ":" + minuteThen;
        } else if (yearThen == year) {
            return monthThen + "/" + dayThen + " " + hourThen + ":" + minuteThen;
        } else {
            return yearThen + "/" + monthThen + "/" + dayThen + " " + hourThen + ":" + minuteThen;
        }
    }
}
