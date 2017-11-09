package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.chart.AxisStyle;
import com.hfut.zhaojiabao.myrecord.chart.Builder;
import com.hfut.zhaojiabao.myrecord.chart.CurveChart;
import com.hfut.zhaojiabao.myrecord.chart.DataProvider;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;
import com.hfut.zhaojiabao.myrecord.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class CurveChartActivity extends AppCompatActivity {
    private CurveChart mCurveChart;

    @OnClick(R.id.btn)
    void load() {
        ValueTransfer
                .getDayRecords()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(dayRecords -> provideData(dayRecords));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve);
        ButterKnife.bind(this);
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

        mCurveChart = (CurveChart) findViewById(R.id.curve_chart);

        Builder builder = new Builder(
                Builder.CURVE_CHART,
                (int) Utils.dp2px(this, 15),
                (int) Utils.dp2px(this, 20),
                (int) Utils.dp2px(this, 15),
                (int) Utils.dp2px(this, 20));

        builder.setAxisStyle(new AxisStyle(
                ContextCompat.getColor(this, R.color.origin100),
                (int) Utils.dp2px(this, 1),
                (int) Utils.dp2px(this, 2)))
                .setDataProvider(new DataProvider(datas, texts, false, 5));

        mCurveChart.setBuilder(builder);
        mCurveChart.initAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurveChart != null) {
            mCurveChart.cancelAnim();
        }
    }
}