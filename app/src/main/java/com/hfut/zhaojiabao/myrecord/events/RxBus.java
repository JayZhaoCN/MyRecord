package com.hfut.zhaojiabao.myrecord.events;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class RxBus {
    private final Subject<Object> mBus;

    private static class Holder {
        private static final RxBus INSTANCE = new RxBus();
    }

    public static RxBus getDefault() {
        return Holder.INSTANCE;
    }

    private RxBus() {
        mBus = PublishSubject.create().toSerialized();
    }

    public <T> Observable<T> toObserver(Class<T> eventType) {
        return mBus.ofType(eventType);
    }

    public void post(Object object) {
        mBus.onNext(object);
    }
}
