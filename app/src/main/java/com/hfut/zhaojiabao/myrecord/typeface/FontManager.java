package com.hfut.zhaojiabao.myrecord.typeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @author zhaojiabao 2017/6/22
 *         字体管理类
 */

class FontManager {
    private static final String TAG = "FontManager";
    private static HashMap<String, WeakReference<Typeface>> TYPEFACES = new HashMap<>();

    static void applyTypeface(Context context, TextView textView, String fontFamily) {
        if (TYPEFACES.containsKey(fontFamily)) {
            Log.i(TAG, "TYPEFACES contains.");
            textView.setTypeface(TYPEFACES.get(fontFamily).get());
            return;
        }
        try {
            Log.i(TAG, "TYPEFACES do not contains, so load from typeface file.");
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), Font.fromName(fontFamily).mFontPath);
            TYPEFACES.put(fontFamily, new WeakReference<>(typeface));
            textView.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
