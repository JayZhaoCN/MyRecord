package com.hfut.zhaojiabao.myrecord.file_operation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author zhaojiabao 2017/6/17
 */

public class RecoveryTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "RecoveryTask";
    private static final String FILE_NAME = "my_backup.txt";

    private String mFilePath;
    private Context mContext;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public RecoveryTask(Context context) {
        mContext = context;
        verifyStoragePermissions(mContext);
        mFilePath = Environment.getExternalStorageDirectory() + File.separator + FILE_NAME;
        Log.i(TAG, "filePath: " + mFilePath);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            File file = new File(mFilePath);

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                Log.i(TAG, "read line: " + line);
            }

            fileReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private static void verifyStoragePermissions(Context context) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
