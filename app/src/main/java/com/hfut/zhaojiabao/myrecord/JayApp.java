package com.hfut.zhaojiabao.myrecord;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.User;
import com.hfut.zhaojiabao.myrecord.greendao.CategoryDao;
import com.hfut.zhaojiabao.myrecord.greendao.UserDao;
import com.squareup.leakcanary.LeakCanary;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/19
 */

public class JayApp extends Application {

    private static JayApp sInstance;

    public static JayApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JayDaoManager.init(this);
        initDatabase();
        sInstance = this;
        LeakCanary.install(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    /**
     * load default data.
     */
    private void initDatabase() {
        CategoryDao categoryDao = JayDaoManager.getInstance().getDaoSession().getCategoryDao();
        List<Category> list = categoryDao.loadAll();
        if (list.isEmpty()) {
            String[] defaultCategories = getResources().getStringArray(R.array.default_categories);
            for (String category : defaultCategories) {
                Category entity = new Category();
                entity.setCategory(category);
                categoryDao.insert(entity);
            }
        }
        UserDao userDao = JayDaoManager.getInstance().getDaoSession().getUserDao();
        List<User> userList = userDao.loadAll();
        if (userList.isEmpty()) {
            User defaultUser = new User();
            defaultUser.setUserName("JayZhaocc");
            userDao.insert(defaultUser);
        }
    }

}
