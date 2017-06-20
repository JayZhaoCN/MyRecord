package com.hfut.zhaojiabao.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONObject;

/**
 * @author zhaojiabao 2017/5/19
 */

@Entity
public class Category {
    @Id(autoincrement = true)
    private Long id;
    private String category;

    public String toJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("category", category);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Category fromJSONString(String categoryStr) {
        if (categoryStr == null || categoryStr.equals("")) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(categoryStr);
            Category category = new Category();
            category.setId(jsonObject.getLong("id"));
            category.setCategory(jsonObject.getString("category"));
            return category;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Generated(hash = 1173379396)
    public Category(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
