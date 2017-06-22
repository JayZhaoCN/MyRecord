package com.hfut.zhaojiabao.myrecord.typeface;

/**
 * 字体枚举
 */
public enum Font {
    KM("km", "fonts/KMedium.ttf"),
    DIN_MED("din-med", "fonts/dincond_medium.otf");

    public String mFontName = null;
    public String mFontPath = null;

    Font(String fontName, String fontPath) {
        this.mFontName = fontName;
        this.mFontPath = fontPath;
    }

    public String getName() {
        return mFontName;
    }

    public String getPath() {
        return mFontPath;
    }
}