package com.hfut.zhaojiabao.myrecord.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hfut.zhaojiabao.myrecord.JayApp;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.file_operation.IOUtils;
import com.hfut.zhaojiabao.myrecord.weather_db.City;
import com.hfut.zhaojiabao.myrecord.weather_db.Province;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao (zhaojiabao@huami.com)
 */

public class CityDBManager {
    public static final String TAG = "CityDBManager";

    private static final String TABLE_CITY = "T_City";
    private static final String COLUMN_CITY_NAME = "CityName";
    private static final String COLUMN_PRO_ID = "ProID";
    private static final String COLUMN_CITY_SORT = "CitySort";

    private static final String TABLE_PROVINCE = "T_Province";
    private static final String COLUMN_PRO_NAME = "ProName";
    private static final String COLUMN_PRO_SORT = "ProSort";
    private static final String COLUMN_PRO_REMARK = "ProRemark";

    private static final String TABLE_ZONE = "T_Zone";
    private static final String COLUMN_ZONE_ID = "ZoneID";
    private static final String COLUMN_ZONE_NAME = "ZoneName";
    private static final String COLUMN_CITY_ID = "CityID";

    private static final String DB_FILE_NAME = "city.db";

    private SQLiteDatabase mDatabase;

    private static class Holder {
        private static final CityDBManager sInstance = new CityDBManager();
    }

    private CityDBManager() {
        mDatabase = initDatabase();
    }

    public static CityDBManager getInstance() {
        return Holder.sInstance;
    }

    private static SQLiteDatabase initDatabase() {
        try {
            String dbFilePath = FileUtil.getAppExternalDir(JayApp.getInstance(), IOUtils.DATABASE_FOLDER_NAME)
                    + File.pathSeparator + DB_FILE_NAME;
            File dbFile = new File(dbFilePath);
            Log.i(TAG, "db file path: " + dbFile.getPath());
            Log.i(TAG, "db file exist: " + dbFile.exists());
            if (!dbFile.exists()) {
                Log.i(TAG, "create db file: " + dbFile.createNewFile());

                InputStream is = JayApp.getInstance().getResources().openRawResource(R.raw.china_city);
                FileOutputStream fos = new FileOutputStream(dbFile);

                byte[] buffer = new byte[40000];
                int count;
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Province> getProvinces() {
        if (mDatabase == null) {
            Log.i(TAG, "database null");
            return null;
        }
        if (!mDatabase.isOpen()) {
            mDatabase = initDatabase();
        }
        Cursor cursor = mDatabase.query(TABLE_PROVINCE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            List<Province> provinces = new ArrayList<>();
            do {
                Province province = new Province();
                province.proName = cursor.getString(cursor.getColumnIndex(COLUMN_PRO_NAME));
                province.proSort = cursor.getInt(cursor.getColumnIndex(COLUMN_PRO_SORT));
                province.proRemark = cursor.getString(cursor.getColumnIndex(COLUMN_PRO_REMARK));
                provinces.add(province);
            } while (cursor.moveToNext());
            return provinces;
        }
        cursor.close();
        return null;
    }

    public List<City> getCities(int proId) {
        if (mDatabase == null) {
            Log.i(TAG, "database null.");
            return null;
        }
        if (!mDatabase.isOpen()) {
            mDatabase = initDatabase();
        }
        Cursor cursor = mDatabase.query(TABLE_CITY, null, COLUMN_PRO_ID + " = ?", new String[]{String.valueOf(proId)}, null, null, null);
        if (cursor.moveToFirst()) {
            List<City> cities = new ArrayList<>();
            do {
                City city = new City();
                city.cityName = cursor.getString(cursor.getColumnIndex(COLUMN_CITY_NAME));
                city.proId = cursor.getInt(cursor.getColumnIndex(COLUMN_PRO_ID));
                city.citySort = cursor.getInt(cursor.getColumnIndex(COLUMN_CITY_SORT));
                cities.add(city);
            } while (cursor.moveToNext());
            return cities;
        }
        cursor.close();
        return null;
    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }
}
