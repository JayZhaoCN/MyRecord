package com.hfut.zhaojiabao.myrecord.chart;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class BarChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        ValueTransfer
                .getDayRecords()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(dayRecords -> provideData(dayRecords));
    }

    private void provideData(List<DayRecord> dayRecords) {
        final List<Float> datas = new ArrayList<>();
        final List<String> texts = new ArrayList<>();

        //剔除掉没有记录的日期
        for (DayRecord dayRecord : dayRecords) {
            if (dayRecord.expendSum > 0 || dayRecord.incomeSum > 0) {
                datas.add(dayRecord.expendSum);
                texts.add(dayRecord.month + "-" + dayRecord.day);
            }
        }

        BarChart mBarChart = (BarChart) findViewById(R.id.bar_chart);

        Builder builder = new Builder(
                Builder.BAR_CHART,
                (int) Utils.dp2px(this, 50),
                (int) Utils.dp2px(this, 20),
                (int) Utils.dp2px(this, 15),
                (int) Utils.dp2px(this, 20));

        builder.setAxisStyle(new AxisStyle(
                ContextCompat.getColor(this, R.color.origin100),
                (int) Utils.dp2px(this, 1), 0))
                .setDataProvider(new DataProvider(datas, texts, true, 3));

        mBarChart.setBuilder(builder);
    }
}
