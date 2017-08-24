package com.hfut.zhaojiabao.myrecord.file_operation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.database.User;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.JayLoadingDialog;
import com.hfut.zhaojiabao.myrecord.events.RecordRecoveryEvent;
import com.hfut.zhaojiabao.myrecord.greendao.CategoryDao;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;
import com.hfut.zhaojiabao.myrecord.greendao.UserDao;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * all operations about file.
 * data backup and recovery.
 *
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

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
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
                .subscribe(new Subscriber<Void>() {
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
                    public void onNext(Void aVoid) {

                    }
                });
    }

    /**
     * backup data
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

        //write user data to sdcard
        //先写入分割标志
        writer.write(FILE_DIVIDER);
        writer.newLine();
        List<User> users = JayDaoManager.getInstance().getDaoSession().getUserDao().loadAll();
        writer.write(users.get(0).toJSONString());
        writer.newLine();

        writer.close();
        fileWriter.close();
    }

    public static void recoveryData(final AppCompatActivity context, final String filePath) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    recoveryFile(filePath);
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
                        //每次恢复数据之前需要请求读写存储权限
                        IOManager.verifyStoragePermissions(context);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        ToastUtil.showToast(context.getString(R.string.recovery_done), Toast.LENGTH_SHORT);
                        EventBus.getDefault().postSticky(new RecordRecoveryEvent(true));
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(context.getString(R.string.recovery_fail), Toast.LENGTH_SHORT);
                        EventBus.getDefault().postSticky(new RecordRecoveryEvent(false));
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }

    /**
     * recovery data
     * SYNC
     * throws exceptions
     */
    private static void recoveryFile(String filePath) throws Exception {
        Log.i(TAG, "recovery from: " + filePath);
        final RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        //从文件中恢复之前，要删除本地数据库所有记录
        final List<Record> records = new ArrayList<>();

        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        String line;
        //恢复Record数据
        while ((line = reader.readLine()) != null) {
            if (!line.equals(IOManager.FILE_DIVIDER)) {
                Record record = Record.fromJSONString(line);
                records.add(record);
            } else {
                break;
            }
        }

        //恢复Category数据
        final List<Category> categories = new ArrayList<>();
        final CategoryDao categoryDao = JayDaoManager.getInstance().getDaoSession().getCategoryDao();
        while ((line = reader.readLine()) != null) {
            if (!line.equals(IOManager.FILE_DIVIDER)) {
                Category category = Category.fromJSONString(line);
                categories.add(category);
            } else {
                break;
            }
        }

        //恢复User数据
        final List<User> users = new ArrayList<>();
        final UserDao userDao = JayDaoManager.getInstance().getDaoSession().getUserDao();
        while ((line = reader.readLine()) != null) {
            User user = User.fromJSONString(line);
            users.add(user);
        }

        JayDaoManager.getInstance().getDaoSession().runInTx(new Runnable() {
            @Override
            public void run() {
                recordDao.deleteAll();
                categoryDao.deleteAll();
                userDao.deleteAll();

                if (records.size() > 0) {
                    recordDao.insertOrReplaceInTx(records);
                }
                if (categories.size() > 0) {
                    categoryDao.insertOrReplaceInTx(categories);
                }
                if (users.size() > 0) {
                    userDao.insertOrReplaceInTx(users);
                }
            }
        });

        reader.close();
        fileReader.close();
    }

    /**
     * 请求读写权限
     */
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
