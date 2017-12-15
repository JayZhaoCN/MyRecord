package com.hfut.zhaojiabao.myrecord.utils;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class RxUtils {
    public static <T> ObservableTransformer<T, T> ioToMain() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }
}
