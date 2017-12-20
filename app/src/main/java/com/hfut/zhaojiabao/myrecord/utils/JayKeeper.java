package com.hfut.zhaojiabao.myrecord.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hfut.zhaojiabao.myrecord.JayApp;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class JayKeeper {
    private static final String KEEPER = "keeper";

    private static SharedPreferences SP;


    public static void init(Context context) {
        SP = context.getSharedPreferences(KEEPER, Context.MODE_PRIVATE);
    }

    private static void refresh() {
        if (SP == null) {
            init(JayApp.getInstance());
        }
    }

    public static void setCity(String cityName) {
        refresh();
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(KeeperConstant.KEY_CITY, cityName);
        editor.apply();
    }

    public static String getCity() {
        refresh();
        return SP.getString(KeeperConstant.KEY_CITY, KeeperConstant.DEFAULT_CITY);
    }
}
