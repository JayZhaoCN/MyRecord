package com.hfut.zhaojiabao.myrecord.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author zhaojiabao 2017/5/22
 */

public class ToastUtil {

    private static Toast sToast;

    public static void showToast(Context context, String text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, text, duration);
        }
        sToast.setText(text);
        sToast.setDuration(duration);
        sToast.show();
    }

}
