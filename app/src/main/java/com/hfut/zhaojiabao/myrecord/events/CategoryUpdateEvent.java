package com.hfut.zhaojiabao.myrecord.events;

/**
 * @author zhaojiabao 2017/8/16
 */

public class CategoryUpdateEvent {
    public static final int STATE_ADD = 0;
    public static final int STATE_DELETE = 1;

    public int state;

    public CategoryUpdateEvent(int state) {
        this.state = state;
    }
}
