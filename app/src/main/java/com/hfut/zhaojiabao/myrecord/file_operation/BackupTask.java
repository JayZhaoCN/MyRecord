package com.hfut.zhaojiabao.myrecord.file_operation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author zhaojiabao 2017/6/14
 */

public class BackupTask extends AsyncTask<Void, Void, Void> {
    private static final String FILE_NAME = "my_backup.jay";

    private String mFilePath;
    private Context mContext;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public BackupTask(Context context) {
        mContext = context;
        mFilePath = Environment.getExternalStorageDirectory() + File.separator + FILE_NAME;
        verifyStoragePermissions(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            File file = new File(mFilePath);
            Log.i("JayTest", "filePath: " + mFilePath);

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("Hello World.");
            writer.newLine();
            writer.write("Hello Android.");

            fileWriter.close();
            writer.close();
        } catch (Exception e) {
            Log.i("JayTest", "exception.");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public static void verifyStoragePermissions(Context context) {
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
