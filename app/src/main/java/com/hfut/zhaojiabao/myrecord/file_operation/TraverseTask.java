package com.hfut.zhaojiabao.myrecord.file_operation;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @author zhaojiabao 2017/6/20
 */

public class TraverseTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "TraverseTask";

    @Override
    protected Void doInBackground(Void... params) {

        File file = Environment.getExternalStorageDirectory();
        File[] children = file.listFiles();
        for (File child : children) {
            if (child.getPath().endsWith(".jay")) {
                Log.i(TAG, "child path: " + child);
            }
        }

        return null;
    }
}
