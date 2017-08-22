package com.hfut.zhaojiabao.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONObject;

/**
 * @author zhaojiabao 2017/7/11
 */
@Entity
public class User {
    @Id
    private Long id;
    private String userName;
    /**
     * 每日预算
     */
    private Float budget = 2000f;

    @Generated(hash = 1849956244)
    public User(Long id, String userName, Float budget) {
        this.id = id;
        this.userName = userName;
        this.budget = budget;
    }

    @Generated(hash = 586692638)
    public User() {
    }


    public String toJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("userName", userName);
            jsonObject.put("budget", budget);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User fromJSONString(String userStr) {
        if (userStr == null || userStr.equals("")) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(userStr);
            User user = new User();
            user.setId(jsonObject.getLong("id"));
            user.setUserName(jsonObject.getString("userName"));
            user.setBudget((float) jsonObject.getDouble("budget"));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Float getBudget() {
        return this.budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }
}
