package com.hfut.zhaojiabao.myrecord.typeface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import com.hfut.zhaojiabao.myrecord.Utils;

import java.util.HashMap;

/**
 * Created by Jay on 2016/11/14.
 * custom typeface builder
 */

public class CustomTypefaceBuilder {

    //use application context to avoid memory leak.
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private SpannableStringBuilder mBuilder;

    @SuppressLint("StaticFieldLeak")
    private static CustomTypefaceBuilder sInstance;

    private int mIndex = 0;

    private HashMap<String, Typeface> mTypefaces = new HashMap<>();

    /**
     * invoke init() in application onCreate() method.
     * @param context application context
     */
    public static void init(Context context) {
        mContext = context;
        sInstance = new CustomTypefaceBuilder(mContext);
    }

    public static CustomTypefaceBuilder getInstance() {
        sInstance.clear();
        return sInstance;
    }

    private CustomTypefaceBuilder(Context context) {
        mContext = context;
        mBuilder = new SpannableStringBuilder();
    }
    
    public CustomTypefaceBuilder append(String content, @ColorInt int textColor, float textSize) {
        return append(content, null, textColor, textSize);
    }
    

    public CustomTypefaceBuilder append(String content, Typeface typeface, @ColorInt int textColor, float textSize) {
        mBuilder.append(content);
        int length = mBuilder.length();

        //set text size and color
        int[][] states = new int[1][];
        states[0] = new int[] {};
        int[] colors = new int[]{textColor};
        ColorStateList mColorStateList = new ColorStateList(states, colors);
        TextAppearanceSpan textAppearanceSpan =
                new TextAppearanceSpan("default", Typeface.NORMAL,
                        (int) Utils.sp2px(mContext, textSize), mColorStateList, null);
        mBuilder.setSpan(textAppearanceSpan,
                mIndex, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        if (typeface != null) {
            CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan(typeface);
            mBuilder.setSpan(typefaceSpan, mIndex, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mIndex = length;

        return this;
    }

    /**
     * after call append(), you should call this method to build a SpannableStringBuilder instance.
     * @return instance of SpannableStringBuilder
     */
    public SpannableStringBuilder build() {
        return mBuilder;
    }

    /**
     * clear the content of SpannableStringBuilder.
     * when you want reuse this CustomTypefaceBuilder instance, you should call this method first.
     */
    public void clear() {
        mBuilder.clear();
        mIndex = 0;
    }
}
