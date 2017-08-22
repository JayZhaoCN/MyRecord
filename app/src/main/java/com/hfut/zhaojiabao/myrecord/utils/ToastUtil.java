package com.hfut.zhaojiabao.myrecord.utils;

import android.content.Context;
import android.widget.Toast;

import com.hfut.zhaojiabao.myrecord.JayApp;

/**
 * @author zhaojiabao 2017/5/22
 */

public class ToastUtil {

    private static Toast sToast;

    //这里传Application的上下文
    public static void showToast(String text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(JayApp.getInstance(), text, duration);
        }
        sToast.setText(text);
        sToast.setDuration(duration);
        sToast.show();
    }

}
