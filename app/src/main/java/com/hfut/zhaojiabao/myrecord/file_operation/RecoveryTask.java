package com.hfut.zhaojiabao.myrecord.file_operation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.R;
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

public class RecoveryTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = "RecoveryTask";

    private Context mContext;

    public RecoveryTask(Context context) {
        mContext = context;
        BackupTask.verifyStoragePermissions(mContext);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String filePath = params[0];
            Log.i(TAG, "recovery from: " + filePath);
            RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
            //从文件中恢复之前，要删除本地数据库所有记录
            recordDao.deleteAll();
            List<Record> records = new ArrayList<>();

            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            //备份Record数据
            while ((line = reader.readLine()) != null) {
                if (!line.equals(BackupTask.FILE_DIVIDER)) {
                    Record record = Record.fromJSONString(line);
                    records.add(record);
                } else {
                    break;
                }
            }
            if (records.size() > 0) {
                recordDao.insertInTx(records);
            }

            //备份Category数据
            List<Category> categories = new ArrayList<>();
            CategoryDao categoryDao = JayDaoManager.getInstance().getDaoSession().getCategoryDao();
            categoryDao.deleteAll();
            while ((line = reader.readLine()) != null) {
                Category category = Category.fromJSONString(line);
                categories.add(category);
            }
            if (categories.size() > 0) {
                categoryDao.insertInTx(categories);
            }

            reader.close();
            fileReader.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.i(TAG, "backup success: " + success);
        ToastUtil.showToast(mContext, mContext.getString
                (success ? R.string.recovery_done : R.string.recovery_fail), Toast.LENGTH_SHORT);
    }
}
