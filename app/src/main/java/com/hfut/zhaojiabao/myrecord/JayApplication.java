package com.hfut.zhaojiabao.myrecord;

import android.app.Application;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.myrecord.greendao.CategoryDao;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/19
 */

public class JayApplication extends Application {

    private static JayApplication sInstance;

    public static JayApplication getApplication() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JayDaoManager.init(this);
        initCategory();
        sInstance = this;
    }

    /**
     * load default category.
     */
    private void initCategory() {
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
    }
}
