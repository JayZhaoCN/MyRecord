package com.hfut.zhaojiabao.myrecord.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

/**
 * @author zhaojiabao 2017/6/6
 */

public class TintUtils {
    public static Drawable tintDrawable(Drawable drawable, @ColorInt int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static void tintImageView(ImageView imageView, @ColorInt int color) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(imageView.getDrawable().mutate());
        DrawableCompat.setTint(wrappedDrawable, color);
        imageView.setImageDrawable(wrappedDrawable);
    }

}
