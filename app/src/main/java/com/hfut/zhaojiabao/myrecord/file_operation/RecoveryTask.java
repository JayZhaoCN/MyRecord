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
import com.hfut.zhaojiabao.myrecord.greendao.CategoryDao;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao 2017/6/17
 */

public class RecoveryTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "RecoveryTask";
    private static final String FILE_NAME = "my_backup.jay";
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

    public RecoveryTask(Context context) {
        mContext = context;
        verifyStoragePermissions(mContext);
        mFilePath = Environment.getExternalStorageDirectory() + File.separator + FILE_NAME;
        mCategoryFilePath = Environment.getExternalStorageDirectory() + File.separator + CATEGORY_FILE_NAME;
        Log.i(TAG, "filePath: " + mFilePath);
        Log.i(TAG, "category filePath: " + mCategoryFilePath);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
            //从文件中恢复之前，要删除本地数据库所有记录
            recordDao.deleteAll();
            List<Record> records = new ArrayList<>();

            File file = new File(mFilePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                Record record = Record.fromJSONString(line);
                records.add(record);
            }
            if (records.size() > 0) {
                recordDao.insertInTx(records);
            }

            File categoryFile = new File(mCategoryFilePath);
            FileReader categoryFileReader = new FileReader(categoryFile);
            BufferedReader categoryReader = new BufferedReader(categoryFileReader);
            List<Category> categories = new ArrayList<>();
            CategoryDao categoryDao = JayDaoManager.getInstance().getDaoSession().getCategoryDao();
            //从文件中恢复之前，要删除本地数据库所有记录
            categoryDao.deleteAll();

            while ((line = categoryReader.readLine()) != null) {
                Category category = Category.fromJSONString(line);
                categories.add(category);
            }
            if (categories.size() > 0) {
                categoryDao.insertInTx(categories);
            }

            reader.close();
            fileReader.close();
            categoryReader.close();
            categoryFileReader.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.i(TAG, "backup success: " + success);
        ToastUtil.showToast(mContext, success ? "恢复完成" : "恢复失败", Toast.LENGTH_SHORT);
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
