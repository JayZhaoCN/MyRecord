package com.hfut.zhaojiabao.myrecord.chart;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.utils.NumberUtils;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author zhaojiabao 2017/5/24
 *         数值转换工具
 */

public class ValueTransfer {
    public static void transform(List<RecordChart.ChartItem> data, float height) {

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).value == 0) {
                continue;
            }
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

        float sub = max - min;
        if (data.size() == 1) {
            data.get(0).value = height + 10;
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            item = data.get(i);
            float x = (item.value - min) / sub;
            data.get(i).value = x * height + 10;
        }
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
        long timeMillis;
        DayRecord dayRecord = new DayRecord();
        if (records != null && records.size() >= 1) {
            Record record = records.get(0);

            calender.setTimeInMillis(record.getConsumeTime());
            year = calender.get(Calendar.YEAR);
            month = calender.get(Calendar.MONTH);
            day = calender.get(Calendar.DAY_OF_MONTH);
            calender.set(year, month, day);
            timeMillis = calender.getTimeInMillis();


            dayRecord.year = year;
            dayRecord.month = month + 1;
            dayRecord.day = day;
            dayRecord.date = year + "-" + (month + 1) + "-" + day;
            dayRecord.timeMillis = timeMillis;
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
            result.add(dayRecord);
            return result;
        }

        for (int i = 1; i < records.size(); i++) {
            Record record = records.get(i);
            calender.setTimeInMillis(record.getConsumeTime());
            year = calender.get(Calendar.YEAR);
            month = calender.get(Calendar.MONTH);
            day = calender.get(Calendar.DAY_OF_MONTH);
            calender.set(year, month, day);
            timeMillis = calender.getTimeInMillis();

            if (dayRecord.year == year && dayRecord.month == (month + 1) && dayRecord.day == day) {
                if (record.getIncome()) {
                    if (dayRecord.incomeSum == -1) {
                        dayRecord.incomeSum = 0;
                    }
                    dayRecord.incomeSum += record.getSum();
                    dayRecord.sum += record.getSum();
                } else {
                    if (dayRecord.expendSum == -1) {
                        dayRecord.expendSum = 0;
                    }
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
                dayRecord.timeMillis = timeMillis;
                if (record.getIncome()) {
                    dayRecord.incomeSum = record.getSum();
                    dayRecord.sum = record.getSum();
                } else {
                    dayRecord.expendSum = record.getSum();
                    dayRecord.sum = -record.getSum();
                }
            }

            if (i == records.size() - 1) {
                result.add(dayRecord);
            }
        }

        //TODO 没有记录的日期也要保存起来
        Calendar calendarJ = Calendar.getInstance(Locale.getDefault());

        DayRecord startRecord = result.get(0);
        DayRecord endRecord = result.get(result.size() - 1);

        calendarJ.set(startRecord.year, startRecord.month - 1, startRecord.day);
        Date startDate = calendarJ.getTime();
        calendarJ.set(endRecord.year, endRecord.month - 1, endRecord.day);
        Date endDate = calendarJ.getTime();

        calendarJ.setTime(startDate);
        Date middleDate = calendarJ.getTime();

        List<DayRecord> tmpRecord = new ArrayList<>();

        while (middleDate.before(endDate)) {
            if (!recordContains(result, middleDate)) {
                DayRecord record = new DayRecord();
                record.year = calendarJ.get(Calendar.YEAR);
                record.month = calendarJ.get(Calendar.MONTH) + 1;
                record.day = calendarJ.get(Calendar.DAY_OF_MONTH);
                record.date = record.year + "-" + record.month + "-" + record.day;
                record.timeMillis = middleDate.getTime();
                tmpRecord.add(record);
            }

            calendarJ.add(Calendar.DAY_OF_MONTH, 1);
            middleDate = calendarJ.getTime();
        }
        result.addAll(tmpRecord);
        Collections.sort(result, new Comparator<DayRecord>() {
            @Override
            public int compare(DayRecord o1, DayRecord o2) {
                if (o1.timeMillis > o2.timeMillis) {
                    return 1;
                } else if (o1.timeMillis < o2.timeMillis) {
                    return -1;
                }
                return 0;
            }
        });

        for (DayRecord record : result) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(record.timeMillis);
        }

        return result;
    }

    public static float getRemainSum() {
        List<Record> records = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();

        float remain = 0;
        for (Record record : records) {
            if (record.getIncome()) {
                remain += record.getSum();
            } else {
                remain -= record.getSum();
            }
        }
        return remain;
    }

    private static boolean recordContains(List<DayRecord> records, Date date) {
        for (DayRecord record : records) {
            if (record.year == date.getYear() + 1900 && record.month == date.getMonth() + 1 && record.day == date.getDate()) {
                return true;
            }
        }
        return false;
    }


    public static List<SectorChart.SectorChartItem> getTypePercent(boolean income) {
        List<SectorChart.SectorChartItem> result = new ArrayList<>();
        List<Category> categories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();

        for (Category category : categories) {
            result.add(new SectorChart.SectorChartItem(category.getCategory(), 0));
        }

        List<Record> records = JayDaoManager.getInstance().getDaoSession().getRecordDao()
                .queryBuilder().where(RecordDao.Properties.Income.eq(income)).list();

        if (records == null || records.size() == 0) {
            return null;
        }

        float totalValue = 0;
        for (Record record : records) {
            totalValue += record.getSum();
            for (SectorChart.SectorChartItem item : result) {
                if (record.getCategory().equals(item.text)) {
                    item.value += record.getSum();
                }
            }
        }

        for (SectorChart.SectorChartItem item : result) {
            //百分比保留两位小数
            item.percent = NumberUtils.format(item.value / totalValue * 100, 2);
        }

        return result;
    }
}
