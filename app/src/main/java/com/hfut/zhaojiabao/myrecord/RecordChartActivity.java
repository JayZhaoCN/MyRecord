package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.chart.RecordChart;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;

import java.util.ArrayList;
import java.util.List;

public class RecordChartActivity extends AppCompatActivity {

    private List<DayRecord> mDatas;

    //支出
    private TextView mIncomeValueTv;
    //收入
    private TextView mExpendValueTv;
    //恩格尔系数
    private TextView mEngleTv;
    //总计
    private TextView mTotalTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.chart);
        setSupportActionBar(toolbar);

        initChart();
        initViews();
    }

    private void initViews() {
        mIncomeValueTv = (TextView) findViewById(R.id.income_value_tv);
        mExpendValueTv = (TextView) findViewById(R.id.expend_value_tv);
        mEngleTv = (TextView) findViewById(R.id.engel_coefficient_value_tv);
        mTotalTv = (TextView) findViewById(R.id.total_value_tv);
    }

    private void initChart() {
        RecordChart chart = (RecordChart) findViewById(R.id.record_chart);

        mDatas = ValueTransfer.getDayRecords();
        List<RecordChart.ChartItem> chartItems = new ArrayList<>();

        if (mDatas != null) {
            for (DayRecord record : mDatas) {
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
