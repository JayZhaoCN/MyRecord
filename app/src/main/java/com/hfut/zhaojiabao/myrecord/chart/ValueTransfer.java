package com.hfut.zhaojiabao.myrecord.chart;

import android.util.Log;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author zhaojiabao 2017/5/24
 *         数值转换工具
 */

public class ValueTransfer {
    public static List<Float> transform(List<RecordChart.ChartItem> data, float height) {

        List<Float> output = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            data.get(i).value = (float) Math.log(data.get(i).value);
        }

        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;

        RecordChart.ChartItem item;
        for (int i = 0; i < data.size(); i++) {
            item = data.get(i);
            if (item.value > max) {
                max = item.value;
            }
            if (item.value < min) {
                min = item.value;
            }
        }

        Log.i("JayTest", "max: " + max + " min: " + min);

        float sub = max - min;

        for (int i = 0; i < data.size(); i++) {
            item = data.get(i);
            float x = (item.value - min) / sub;
            //+30是为了给一个最低高度。
            //data.get(i).value = (float) (height * Math.sin(Math.PI / 2 * x)) + 30;
            //data.get(i).value = (float) 0.01 * height;
            data.get(i).value = (float) x * height + 10;
            Log.i("JayTest", "value: " + data.get(i).value);
        }

        return output;
    }

    //TODO 数据量小的时候，暂且这样做，后面肯定要做完善
    public static List<DayRecord> getDayRecords() {
        List<DayRecord> result = new ArrayList<>();

        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        List<Record> records
                = recordDao.queryBuilder()
                .orderAsc(RecordDao.Properties.ConsumeTime)
                .list();


        Calendar calender = Calendar.getInstance(Locale.getDefault());

        int year, month, day;
        DayRecord dayRecord = new DayRecord();
        if (records != null && records.size() >= 1) {
            Record record = records.get(0);

            calender.setTimeInMillis(record.getConsumeTime());
            year = calender.get(Calendar.YEAR);
            month = calender.get(Calendar.MONTH);
            day = calender.get(Calendar.DAY_OF_MONTH);

            dayRecord.year = year;
            dayRecord.month = month + 1;
            dayRecord.day = day;
            dayRecord.date = year + "-" + (month + 1) + "-" + day;
            if (record.getIncome()) {
                dayRecord.incomeSum = record.getSum();
                dayRecord.sum = record.getSum();
            } else {
                dayRecord.expendSum = record.getSum();
                dayRecord.sum = -record.getSum();
            }
        } else {
            return null;
        }

        if (records.size() == 1) {
            return result;
        }

        for (Record record : records) {
            calender.setTimeInMillis(record.getConsumeTime());
            year = calender.get(Calendar.YEAR);
            month = calender.get(Calendar.MONTH);
            day = calender.get(Calendar.DAY_OF_MONTH);

            if (dayRecord.year == year && dayRecord.month == (month + 1) && dayRecord.day == day) {
                if (record.getIncome()) {
                    dayRecord.incomeSum += record.getSum();
                    dayRecord.sum += record.getSum();
                } else {
                    dayRecord.expendSum += record.getSum();
                    dayRecord.sum -= record.getSum();
                }
            } else {
                result.add(dayRecord);
                dayRecord = new DayRecord();
                dayRecord.year = year;
                dayRecord.month = month + 1;
                dayRecord.day = day;
                dayRecord.date = year + "-" + (month + 1) + "-" + day;
                if (record.getIncome()) {
                    dayRecord.incomeSum = record.getSum();
                    dayRecord.sum = record.getSum();
                } else {
                    dayRecord.expendSum = record.getSum();
                    dayRecord.sum = -record.getSum();
                }
            }
        }

        for (DayRecord record : result) {
            Log.i("JayTest", "record: " + record.toString());
        }

        return result;
    }


    public static List<SectorChart.SectorChartItem> getTypePercent(boolean income) {
        List<SectorChart.SectorChartItem> result = new ArrayList<>();
        List<Category> categories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();

        for (Category category : categories) {
            result.add(new SectorChart.SectorChartItem(category.getCategory(), 0));
        }

        List<Record> records = JayDaoManager.getInstance().getDaoSession().getRecordDao()
                .queryBuilder().where(RecordDao.Properties.Income.eq(income)).list();

        float totalValue = 0;
        for (Record record : records) {
            totalValue += record.getSum();
            for (SectorChart.SectorChartItem item : result) {
                if (record.getCategory().equals(item.text)) {
                        item.value += record.getSum();
                }
            }
        }

        for(SectorChart.SectorChartItem item : result) {
            //百分比保留两位小数
            item.percent = item.value / totalValue * 100;
        }

        return result;
    }
}
