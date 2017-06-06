package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hfut.zhaojiabao.myrecord.chart.SectorChart;

import java.util.ArrayList;
import java.util.List;

public class SectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectorChart chart = (SectorChart) findViewById(R.id.sector_chart);
        List<SectorChart.SectorChartItem> datas = new ArrayList<>();
        datas.add(new SectorChart.SectorChartItem("A", 10));
        datas.add(new SectorChart.SectorChartItem("B", 20));
        datas.add(new SectorChart.SectorChartItem("C", 30));
        datas.add(new SectorChart.SectorChartItem("D", 40));
        datas.add(new SectorChart.SectorChartItem("E", 50));
        datas.add(new SectorChart.SectorChartItem("F", 60));
        chart.provideData(datas);
    }

}
