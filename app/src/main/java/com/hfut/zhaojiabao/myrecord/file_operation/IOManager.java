package com.hfut.zhaojiabao.myrecord.file_operation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.database.User;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.JayLoadingDialog;
import com.hfut.zhaojiabao.myrecord.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * @author zhaojiabao 2017/8/23
 */

public class IOManager {
    private static final String TAG = "IOManager";

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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void backupFile(final AppCompatActivity context) {
        final JayLoadingDialog dialog = new JayLoadingDialog();
        final String filePath = IOUtils.getBackupFilePath();

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    backupFile(filePath);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        verifyStoragePermissions(context);
                        dialog.setCancelable(false);
                        dialog.showLoading(context.getString(R.string.back_uping));
                        dialog.show(context.getSupportFragmentManager(), "backup");

                        Log.i(TAG, "backup filePath: " + filePath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        dialog.showSuccess(context.getString(R.string.backup_done));
                        dialog.delayClose(1000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.showError(context.getString(R.string.backup_fail));
                        dialog.delayClose(1000);
                    }

                    @Override
                    public void onNext(String s) {

                    }
                });
    }

    /**
     * backup file
     * SYNC
     * throws exception
     */
    private static void backupFile(String filePath) throws Exception {
        File file = new File(filePath);
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

        //write user name data to sdcard
        //先写入分割标志
        writer.write(FILE_DIVIDER);
        writer.newLine();
        List<User> users = JayDaoManager.getInstance().getDaoSession().getUserDao().loadAll();
        writer.write(users.get(0).toJSONString());
        writer.newLine();

        writer.close();
        fileWriter.close();
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
