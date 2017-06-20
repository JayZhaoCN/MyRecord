package com.hfut.zhaojiabao.myrecord.file_operation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * @author zhaojiabao 2017/6/14
 */

public class BackupTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "BackupTask";
    private static final String RECORD_FILE_NAME = "my_backup.jay";
    private static final String CATEGORY_FILE_NAME = "my_category.jay";

    private String mFilePath;
    private String mCategoryFilePath;
    private Context mContext;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public BackupTask(Context context) {
        mContext = context;
        verifyStoragePermissions(mContext);
        mFilePath = Environment.getExternalStorageDirectory() + File.separator + RECORD_FILE_NAME;
        mCategoryFilePath = Environment.getExternalStorageDirectory() + File.separator + CATEGORY_FILE_NAME;
        Log.i(TAG, "filePath: " + mFilePath);
        Log.i(TAG, "category filePath: " + mCategoryFilePath);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            File file = new File(mFilePath);
            if (file.exists()) {
                Log.i(TAG, "delete exist file: " + file.delete());
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            //write record to sdcard
            List<Record> records = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();
            for (Record record : records) {
                writer.write(record.toJSONString());
                writer.newLine();
            }

            writer.close();
            fileWriter.close();

            File categoryFile = new File(mCategoryFilePath);
            if (categoryFile.exists()) {
                Log.i(TAG, "delete exist category file: " + categoryFile.delete());
            }
            FileWriter categoryFileWriter = new FileWriter(categoryFile, true);
            BufferedWriter categoryWriter = new BufferedWriter(categoryFileWriter);
            List<Category> categories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
            for (Category category : categories) {
                categoryWriter.write(category.toJSONString());
                categoryWriter.newLine();
            }

            categoryWriter.close();
            categoryFileWriter.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.i(TAG, "backup success: " + success);
        ToastUtil.showToast(mContext, success ? "备份完成" : "备份失败", Toast.LENGTH_SHORT);
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
