package com.hfut.zhaojiabao.myrecord.typeface;

/**
 * 字体枚举
 */
public enum Font {
    PT_DIN("pt_din", "fonts/pt_din_condensed_cyrillic.ttf"),
    DIN_MED("din_med", "fonts/dincond_medium.otf"),
    KM("km", "fonts/KMedium.ttf");

    public String mFontName = null;
    public String mFontPath = null;

    Font(String fontName, String fontPath) {
        this.mFontName = fontName;
        this.mFontPath = fontPath;
    }

    public String getName() {
        return mFontName;
    }

    public static Font fromName(String name) {
        for (Font font : Font.values()) {
            if (name.equals(font.getName())) {
                return font;
            }
        }
        return KM;
    }
}