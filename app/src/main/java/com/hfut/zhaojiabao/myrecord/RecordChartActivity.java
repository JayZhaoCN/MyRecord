package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hfut.zhaojiabao.myrecord.chart.RecordChart;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;

import java.util.ArrayList;
import java.util.List;

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

        List<DayRecord> datas = ValueTransfer.getDayRecords();
        List<RecordChart.ChartItem> chartItems = new ArrayList<>();

        if (datas != null) {
            for (DayRecord record : datas) {
                chartItems.add(new RecordChart.ChartItem(record.expendSum, record.date));
            }
        }

        chart.provideData(chartItems);

        chart.setOnColumnSelectedListener(new RecordChart.OnColumnSelectedListener() {
            @Override
            public void onColumnSelected(int position) {
                Log.i("JayTest", "selected: " + position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
