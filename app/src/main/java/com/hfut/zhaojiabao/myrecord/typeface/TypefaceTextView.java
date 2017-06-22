package com.hfut.zhaojiabao.myrecord.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @author zhaojiabao 2017/6/22
 * 自定义字体的TextView
 */

public class TypefaceTextView extends AppCompatTextView {
    public TypefaceTextView(Context context) {
        this(context, null);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, new int[] {android.R.attr.fontFamily});
        String fontFamily = ta.getString(0);
        FontManager.applyTypeface(context, this, fontFamily);
        ta.recycle();
    }
}
