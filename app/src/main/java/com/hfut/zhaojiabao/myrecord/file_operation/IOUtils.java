package com.hfut.zhaojiabao.myrecord.file_operation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.hfut.zhaojiabao.myrecord.JayApp;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.TimeFormatter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author zhaojiabao 2017/7/10
 */

public class IOUtils {
    private static final String TAG = "IOUtils";

    public static final String BACKUP_FOLDER_NAME = "jay_backups";
    private static final String RECORD_FILE_SUFFIX_NAME = ".jay";
    public static final String CROP_IMG_FOLDER_NAME = "jay_crops";
    public static final String CAPTURE_IMG_FOLDER_NAME = "jay_capture";
    private static final String CROP_IMG_SUFFIX_NAME = ".png";
    public static final String AVATAR_IMG_FOLDER_NAME = "jay_avatar";

    //备份路径为外部存储应用私有目录下
    //
    public static String getBackupFilePath() {
        return getAppExternalDir(JayApp.getInstance(), BACKUP_FOLDER_NAME)
                + File.separator
                + TimeFormatter.formatHHmm(System.currentTimeMillis())
                + RECORD_FILE_SUFFIX_NAME;
    }

    public static File getCropImgFile(String imgFolder) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory())
                    .append(File.separator)
                    .append(BACKUP_FOLDER_NAME)
                    .append(File.separator)
                    .append(imgFolder)
                    .append(File.separator)
                    .append(System.currentTimeMillis())
                    .append(CROP_IMG_SUFFIX_NAME);
            File file = new File(sb.toString());
            if (!file.getParentFile().exists()) {
                Log.i(TAG, "parents not exist, so create: " + file.getParentFile().getParentFile().mkdir());
            }
            if (!file.getParentFile().exists()) {
                Log.i(TAG, "parents not exist, so create: " + file.getParentFile().mkdir());
            }
            if (!file.exists()) {
                Log.i(TAG, "file not exist, so create: " + file.createNewFile());
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getAvatarImgFile() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory())
                    .append(File.separator)
                    .append(BACKUP_FOLDER_NAME)
                    .append(File.separator)
                    .append(AVATAR_IMG_FOLDER_NAME)
                    .append(File.separator)
                    .append(AVATAR_IMG_FOLDER_NAME)
                    .append(CROP_IMG_SUFFIX_NAME);

            File file = new File(sb.toString());
            if (!file.getParentFile().exists()) {
                Log.i(TAG, "parents not exist, so create: " + file.getParentFile().getParentFile().mkdir());
            }
            if (!file.getParentFile().exists()) {
                Log.i(TAG, "parents not exist, so create: " + file.getParentFile().mkdir());
            }
            if (!file.exists()) {
                Log.i(TAG, "file not exist, so create: " + file.createNewFile());
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getAvatar(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory())
                .append(File.separator)
                .append(BACKUP_FOLDER_NAME)
                .append(File.separator)
                .append(AVATAR_IMG_FOLDER_NAME)
                .append(File.separator)
                .append("jay_avatar")
                .append(CROP_IMG_SUFFIX_NAME);
        File file = new File(sb.toString());
        if (!file.exists()) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
        }
        return BitmapFactory.decodeFile(file.toString());
    }

    //将裁剪后的图片保存起来
    public static void saveAvatar(final Bitmap bm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File avatarFile = getAvatarImgFile();
                try {
                    FileOutputStream fos;
                    if (avatarFile != null) {
                        fos = new FileOutputStream(avatarFile);
                        bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
                        fos.flush();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // 设置为true，画质更好一点，加载时间略长
        options.inPreferQualityOverSpeed = true;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 获取应用私有目录
     * /data/user/0/包名/files
     */
    public static File getInternalDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取应用在外部存储中的私有目录
     * /storage/emulated/0/Android/data/包名/files/目录名
     */
    public static File getAppExternalDir(Context context, String dir) {
        return context.getExternalFilesDir(dir);
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
}
