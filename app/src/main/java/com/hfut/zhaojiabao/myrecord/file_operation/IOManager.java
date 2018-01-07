package com.hfut.zhaojiabao.myrecord.file_operation;

import android.util.Log;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.database.User;
import com.hfut.zhaojiabao.myrecord.JayApp;
import com.hfut.zhaojiabao.myrecord.greendao.CategoryDao;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;
import com.hfut.zhaojiabao.myrecord.greendao.UserDao;
import com.hfut.zhaojiabao.myrecord.utils.FileUtil;
import com.hfut.zhaojiabao.myrecord.utils.RxUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
    private static final String FILE_DIVIDER = "---------";

    public static Observable<Void> backupFile() {
        return Observable
                .create(e -> {
                    backupFile(IOUtils.getBackupFilePath());
                    e.onComplete();
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

    public static Observable<Void> recoveryData(final String filePath) {
        return Observable
                .create(e -> {
                    recoveryFile(filePath);
                    e.onComplete();
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

        JayDaoManager.getInstance().getDaoSession().runInTx(() -> {
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
        });

        reader.close();
        fileReader.close();
    }

    public static Disposable traverseFile(Consumer<List<File>> consumer) {
        return Observable
                .create((ObservableOnSubscribe<List<File>>) e -> {
                    List<File> files = traverseFileInternal();
                    e.onNext(files);
                })
                .compose(RxUtil.ioToMain())
                .subscribe(consumer);
    }

    /**
     * get all backup files
     * SYNC
     */
    private static List<File> traverseFileInternal() {
        List<File> files = new ArrayList<>();

        File file = FileUtil.getAppExternalDir(JayApp.getInstance(), IOUtils.BACKUP_FOLDER_NAME);
        if (!file.exists()) {
            Log.i(TAG, "file not exist, return.");
            return files;
        }

        File[] children = file.listFiles();
        if (children == null) {
            return files;
        }

        for (File child : children) {
            if (child.getPath().endsWith(".jay")) {
                files.add(child);
            }
        }

        return files;
    }
}
