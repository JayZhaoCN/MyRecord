package com.hfut.zhaojiabao.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.json.JSONObject;

/**
 * @author zhaojiabao 2017/7/11
 */
@Entity
public class User {
    @Id
    private Long id;
    private String userName;
    @Generated(hash = 152095471)
    public User(Long id, String userName) {
        this.id = id;
        this.userName = userName;
    }
    @Generated(hash = 586692638)
    public User() {
    }

    public String toJSONString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("userName", userName);
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
}
