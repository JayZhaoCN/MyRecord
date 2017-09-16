package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.chart.CurveChart;
import com.hfut.zhaojiabao.myrecord.chart.DataProvider;

import java.util.ArrayList;
import java.util.List;

public class CurveActivity extends AppCompatActivity {

    private CurveChart mCurveChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve);

        mCurveChart = (CurveChart) findViewById(R.id.curve_chart);

        List<Float> data = new ArrayList<>();
        data.add(600f);
        data.add(300f);
        data.add(100f);
        data.add(200f);
        data.add(300f);
        data.add(600f);
        data.add(300f);
        data.add(100f);
        data.add(200f);
        data.add(300f);

        List<String> texts = new ArrayList<>();
        texts.add("一月");
        texts.add("二月");
        texts.add("三月");
        texts.add("四月");
        texts.add("五月");
        texts.add("六月");
        texts.add("七月");
        texts.add("八月");
        texts.add("九月");
        texts.add("十月");

        DataProvider provider = new DataProvider(new DataProvider.ChartData(data, texts));
        mCurveChart.provideData(provider);
    }

}
