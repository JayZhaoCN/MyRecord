package com.hfut.zhaojiabao.myrecord.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class RxUtil {
    public static <T> ObservableTransformer<T, T> ioToMain() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static void runOnMainThread(Runnable action) {
        Observable
                .create((ObservableOnSubscribe<Void>) e -> action.run())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
