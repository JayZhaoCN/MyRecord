package com.hfut.zhaojiabao.myrecord.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class FileUtil {
    /*
     * 私有目录结构:
     * 1.模拟器
     *  /data/user/0/包名/cache
     *  /data/user/0/包名/code_cache
     *  /data/user/0/包名/databases
     *  /data/user/0/包名/shared_prefs
     *  /data/user/0/包名/files
     *
     * 2.真机
     *  /data/data/包名/lib
     *  /data/data/包名/cache
     *  /data/data/包名/databases
     *  /data/data/包名/files
     *  /data/data/包名/shared_prefs
     *
     *  Google官方建议我们将App的数据存储在外部存储的私有目录中该App的包名下,
     * 当用户卸载App后, 这些数据会一并删除.
     */

    /*----------------------------内部存储----------------------------*/

    /**
     * 获取应用私有目录
     * /data/data/包名/files
     * 或
     * /data/user/0/包名/files
     */
    public static File getFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取应用缓存目录
     * /data/data/包名/cache
     * 或
     * /data/user/0/包名/cache
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }
    /*----------------------------内部存储----------------------------*/

    /**
     * 获取应用在外部存储的私有目录下的文件目录
     * /storage/emulated/0/Android/data/包名/files/目录名
     * 目录名可以选择Environment中的多个常量
     */
    public static File getAppExternalDir(Context context, String dir) {
        return context.getExternalFilesDir(dir);
    }

    /**
     * 获取应用在外部存储的私有目录下的缓存目录
     * storage/emulated/0/Android/data/包名/cache
     */
    public static File getAppCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 获取内部存储根目录
     * /data
     */
    public static File getInternalRootDir() {
        return Environment.getDataDirectory();
    }

    /**
     * 获取内部存储下载目录
     * /data/cache
     */
    public static File getInternalDownloadDir() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * 获取外部存储根目录
     * /storage/emulated/0
     */
    public static File getExternalRootDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取外部存储指定目录
     * /storage/emulated/0/目录名
     */
    public static File getExternalDir(String dir) {
        return Environment.getExternalStoragePublicDirectory(dir);
    }

    /**
     * 判断SD卡是否被挂载
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡根目录
     *
     * @return /storage/emulated/0
     */
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡大小(MB), 若未挂载, 返回-1
     */
    public static long getSDCardSize() {
        if (isSDCardMounted()) {
            StatFs statFs = new StatFs(getSDCardBaseDir());
            long count = statFs.getBlockCountLong();
            long size = statFs.getBlockSizeLong();
            return count * size / 1024 / 1024;
        }
        return -1;
    }

    /**
     * 获取SD卡空闲空间大小(MB), 若未挂载, 返回-1
     */
    public static long getSDCardFreeSize() {
        if (isSDCardMounted()) {
            StatFs statFs = new StatFs(getSDCardBaseDir());
            long freeCount = statFs.getFreeBlocksLong();
            long size = statFs.getBlockSizeLong();
            return freeCount * size / 1024 / 1024;
        }
        return -1;
    }

    /**
     * 获取SD卡可用空间大小(MB), 若未挂载, 返回-1
     */
    public static long getSDCardAvailableSize() {
        if (isSDCardMounted()) {
            StatFs statFs = new StatFs(getSDCardBaseDir());
            long availableCount = statFs.getAvailableBlocksLong();
            long size = statFs.getBlockSizeLong();
            return availableCount * size / 1024 / 1024;
        }
        return -1;
    }
}