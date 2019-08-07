package com.hfut.zhaojiabao.myrecord.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.hfut.zhaojiabao.myrecord.JayApp;
import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/5/22
 */

public class ToastUtil {

    private static Toast sToast;

    //这里传Application的上下文
    @SuppressLint("ShowToast")
    public static void showToast(String text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(JayApp.getInstance(), text, duration);
        }
        sToast.setText(text);
        sToast.setDuration(duration);
        sToast.show();
    }

    public static void showToast(@StringRes int text, int duration) {
        showToast(JayApp.getInstance().getString(text), duration);
    }
}
