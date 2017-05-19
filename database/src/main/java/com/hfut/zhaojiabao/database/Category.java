package com.hfut.zhaojiabao.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author zhaojiabao 2017/5/19
 */

@Entity
public class Category {
    @Id(autoincrement = true)
    private Long id;
    private String category;
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
