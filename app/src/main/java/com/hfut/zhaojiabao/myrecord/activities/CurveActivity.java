package com.hfut.zhaojiabao.myrecord.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        data.add(900f);
        data.add(200f);
        data.add(300f);
        data.add(600f);
        data.add(300f);
        data.add(900f);
        data.add(200f);
        data.add(300f);

        DataProvider provider = new DataProvider(data);
        mCurveChart.provideData(provider);
    }

}
