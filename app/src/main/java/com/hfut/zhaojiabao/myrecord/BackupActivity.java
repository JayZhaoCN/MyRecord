package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.hfut.zhaojiabao.myrecord.file_operation.BackupTask;

public class BackupActivity extends AppCompatActivity {

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
                new BackupTask(BackupActivity.this).execute();
            }
        });
    }

}
