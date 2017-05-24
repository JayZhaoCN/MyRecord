package com.hfut.zhaojiabao.myrecord.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/**
 * @author zhaojiabao 2017/5/23
 */

public class DisplayUtil {
    public static float dp2px(@NonNull Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(@NonNull Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
