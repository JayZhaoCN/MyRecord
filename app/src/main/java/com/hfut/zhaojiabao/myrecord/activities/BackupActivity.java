package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.file_operation.IOManager;

import rx.Subscription;

public class BackupActivity extends AppCompatActivity {
    private Subscription mSubscription;

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
        startBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubscription = IOManager.backupFile(BackupActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unsubscribe when activity destroyed, in case memory leak.
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
