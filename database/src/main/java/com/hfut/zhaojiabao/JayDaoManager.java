package com.hfut.zhaojiabao;

import android.annotation.SuppressLint;
import android.content.Context;

import com.hfut.zhaojiabao.myrecord.greendao.DaoMaster;
import com.hfut.zhaojiabao.myrecord.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * @author zhaojiabao 2017/5/19
 */

public class JayDaoManager {

    @SuppressLint("StaticFieldLeak")
    private static JayDaoManager sInstance;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private static final String DB_NAME = "origin";

    private DaoSession daoSession;

    private JayDaoManager() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static void init(Context context) {
        mContext = context;
        sInstance = new JayDaoManager();
    }

    public static JayDaoManager getInstance() {
        if(sInstance == null) {
            sInstance = new JayDaoManager();
        }
        return sInstance;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
