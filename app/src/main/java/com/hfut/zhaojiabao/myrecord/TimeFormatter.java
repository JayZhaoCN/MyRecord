package com.hfut.zhaojiabao.myrecord;


import java.text.SimpleDateFormat;
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

    public static final TimeFormatter getInstance() {
        if (sInstance == null) {
            sInstance = new TimeFormatter();
        }
        return sInstance;
    }

    public String format(long time) {
        Date d1 = new Date(time);
        return mFormatter.format(d1);
    }
}
