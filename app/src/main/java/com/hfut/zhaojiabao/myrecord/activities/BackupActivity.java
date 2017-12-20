package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.JayLoadingDialog;
import com.hfut.zhaojiabao.myrecord.file_operation.IOManager;
import com.hfut.zhaojiabao.myrecord.file_operation.IOUtils;
import com.hfut.zhaojiabao.myrecord.utils.RxUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class BackupActivity extends AppCompatActivity {
    private static final String TAG = "BackupActivity";

    private Disposable mDisposable;
    private JayLoadingDialog mDialog = new JayLoadingDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.backup);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        Button startBackupBtn = (Button) findViewById(R.id.start_backup_btn);
        startBackupBtn.setOnClickListener(v ->
                mDisposable = IOManager
                        .backupFile()
                        .compose(RxUtil.ioToMain())
                        .doOnSubscribe(disposable -> {
                            mDialog.setCancelable(false);
                            mDialog.showLoading(getString(R.string.back_uping));
                            mDialog.show(getSupportFragmentManager(), "backup");

                            Log.i(TAG, "backup filePath: " + IOUtils.getBackupFilePath());
                        })
                        .subscribeWith(new DisposableObserver<Void>() {
                            @Override
                            public void onNext(Void value) {
                            }

                            @Override
                            public void onError(Throwable e) {
                                mDialog.showError(getString(R.string.backup_fail));
                                mDialog.delayClose(1000);
                            }

                            @Override
                            public void onComplete() {
                                mDialog.showSuccess(getString(R.string.backup_done));
                                mDialog.delayClose(1000);
                            }
                        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //dispose when activity destroyed, in case memory leak.
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
