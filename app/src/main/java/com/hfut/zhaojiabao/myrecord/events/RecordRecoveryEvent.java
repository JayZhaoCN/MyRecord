package com.hfut.zhaojiabao.myrecord.events;

/**
 * @author zhaojiabao 2017/8/15
 *         记录恢复事件
 */

public class RecordRecoveryEvent {
    public boolean success;

    public RecordRecoveryEvent(boolean success) {
        this.success = success;
    }
}
