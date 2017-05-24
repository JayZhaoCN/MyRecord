package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.hfut.zhaojiabao.myrecord.chart.RecordChart;

public class RecordChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.chart);
        setSupportActionBar(toolbar);

        init();
    }

    private void init() {
        RecordChart chart = (RecordChart) findViewById(R.id.record_chart);
        chart.setOnColumnSelectedListener(new RecordChart.OnColumnSelectedListener() {
            @Override
            public void onColumnSelected(int position) {
                Log.i("JayTest", "selected: " + position);
            }
        });


    }

}
