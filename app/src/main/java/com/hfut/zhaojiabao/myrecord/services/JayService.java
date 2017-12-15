package com.hfut.zhaojiabao.myrecord.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class JayService extends Service {
    private static final String TAG = "JayService";
    /** unit: second*/
    private static final int HEARTBEAT_INTERVAL = 10;

    private JayBinder mBinder = new JayBinder();

    private Disposable mDisposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO onStartCommand()难道不是运行在主线程的?
        Log.i(TAG, "onStartCommand, Thread: " + Thread.currentThread());
        startCount();
        return START_REDELIVER_INTENT;
    }

    private void startCount() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        //interval()默认使用Schedulers.computation().
        mDisposable = Observable.interval(HEARTBEAT_INTERVAL, TimeUnit.SECONDS)
                .subscribe(aLong -> Log.i(TAG, "I'm still alive: " + aLong));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
