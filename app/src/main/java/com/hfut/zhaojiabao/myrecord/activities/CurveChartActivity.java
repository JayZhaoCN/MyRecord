package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.chart.BaseRectChart;
import com.hfut.zhaojiabao.myrecord.chart.CurveChart;
import com.hfut.zhaojiabao.myrecord.chart.DataProvider;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;
import com.hfut.zhaojiabao.myrecord.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class CurveChartActivity extends AppCompatActivity {
    private List<DayRecord> mDayRecords = new ArrayList<>();

    private CurveChart mCurveChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve);

        ValueTransfer
                .getDayRecords()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DayRecord>>() {
                    @Override
                    public void accept(List<DayRecord> dayRecords) throws Exception {
                        provideData(dayRecords);
                    }
                });
    }

    private void provideData(List<DayRecord> dayRecords) {
        final List<Float> datas = new ArrayList<>();
        final List<String> texts = new ArrayList<>();

        //剔除掉没有记录的日期
        for (DayRecord dayRecord : dayRecords) {
            if (dayRecord.expendSum > 0 || dayRecord.incomeSum > 0) {
                mDayRecords.add(dayRecord);
                datas.add(dayRecord.expendSum);
                texts.add(dayRecord.month + "-" + dayRecord.day);
            }
        }

        mCurveChart = (CurveChart) findViewById(R.id.curve_chart);
        int blank = (int) Utils.dp2px(this, 20);
        mCurveChart.setBuilder(new BaseRectChart.Builder(blank, blank, blank, blank));
        mCurveChart.post(new Runnable() {
            @Override
            public void run() {
                mCurveChart.provideData(new DataProvider(datas, texts));
                mCurveChart.startAnim();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurveChart.cancelAnim();
    }
}
