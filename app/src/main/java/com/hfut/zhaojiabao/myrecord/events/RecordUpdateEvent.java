package com.hfut.zhaojiabao.myrecord.events;

import com.hfut.zhaojiabao.database.Record;

/**
 * @author zhaojiabao 2017/8/16
 *         记录发生变化发出的事件
 */

public class RecordUpdateEvent {
    public static final int STATE_ADD = 0;
    public static final int STATE_DELETE = 1;
    public static final int STATE_UPDATE = 2;

    public int state;
    public Record record;

    public RecordUpdateEvent(Record record, int state) {
        this.record = record;
        this.state = state;
    }
}
