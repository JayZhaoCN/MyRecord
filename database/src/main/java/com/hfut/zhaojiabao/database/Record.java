package com.hfut.zhaojiabao.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author zhaojiabao 2017/5/19
 */

@Entity
public class Record {
    @Id
    private Long recordTime;
    private Boolean income;
    private String remark;
    private String category;
    private String cosumeTime;
    private Float sum;
    @Generated(hash = 2018706187)
    public Record(Long recordTime, Boolean income, String remark, String category,
            String cosumeTime, Float sum) {
        this.recordTime = recordTime;
        this.income = income;
        this.remark = remark;
        this.category = category;
        this.cosumeTime = cosumeTime;
        this.sum = sum;
    }
    @Generated(hash = 477726293)
    public Record() {
    }
    public Long getRecordTime() {
        return this.recordTime;
    }
    public void setRecordTime(Long recordTime) {
        this.recordTime = recordTime;
    }
    public Boolean getIncome() {
        return this.income;
    }
    public void setIncome(Boolean income) {
        this.income = income;
    }
    public String getRemark() {
        return this.remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCosumeTime() {
        return this.cosumeTime;
    }
    public void setCosumeTime(String cosumeTime) {
        this.cosumeTime = cosumeTime;
    }
    public Float getSum() {
        return this.sum;
    }
    public void setSum(Float sum) {
        this.sum = sum;
    }
}
