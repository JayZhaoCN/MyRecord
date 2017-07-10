package com.hfut.zhaojiabao.myrecord.file_operation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.JayLoadingDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * @author zhaojiabao 2017/6/14
 */

public class BackupTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "BackupTask";
    private static final String RECORD_FILE_SUFFIX_NAME = ".jay";
    public static final String FOLDER_NAME = "jay_backups";

    /**
     * 记录顺序：
     * Record
     * ---------
     * Category
     * ---------
     * .
     * .
     * .
     */
    //分隔符,表示另一种记录的开始
    static final String FILE_DIVIDER = "---------";

    private String mFilePath;
    private AppCompatActivity mActivity;
    private JayLoadingDialog mDialog;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public BackupTask(AppCompatActivity activity) {
        mActivity = activity;
        verifyStoragePermissions(mActivity);

        mDialog = new JayLoadingDialog();
        mDialog.setCancelable(false);
        mDialog.showLoading(mActivity.getString(R.string.back_uping));
        mDialog.show(mActivity.getSupportFragmentManager(), "backup");

        mFilePath = getFilePath();
        Log.i(TAG, "backup filePath: " + mFilePath);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            File file = new File(mFilePath);
            //如果文件存在，则删除
            if (!file.getParentFile().exists()) {
                Log.i(TAG, "parents not exist, so create: " + file.getParentFile().mkdir());
            }
            if (!file.exists()) {
                Log.i(TAG, "file not exist, so create: " + file.createNewFile());
            }
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            //write record to sdcard
            List<Record> records = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();
            for (Record record : records) {
                writer.write(record.toJSONString());
                writer.newLine();
            }

            //write category data to sdcard
            //先写入分割标志
            writer.write(FILE_DIVIDER);
            writer.newLine();
            List<Category> categories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
            for (Category category : categories) {
                writer.write(category.toJSONString());
                writer.newLine();
            }

            writer.close();
            fileWriter.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.i(TAG, "backup success: " + success);
        if (success) {
            mDialog.showSuccess(mActivity.getString(R.string.backup_done));
        } else {
            mDialog.showError(mActivity.getString(R.string.backup_fail));
        }
        mDialog.delayClose(1000);
    }

    private static String getFilePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory())
                .append(File.separator)
                .append(FOLDER_NAME)
                .append(File.separator)
                .append(System.currentTimeMillis())
                .append(RECORD_FILE_SUFFIX_NAME);
        return sb.toString();
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
