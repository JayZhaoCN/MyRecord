package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
                updateValues(position);
            }
        });
    }

    private void updateValues(int position) {
        DayRecord dayRecord = mDatas.get(position);

        //收入
        float income = dayRecord.incomeSum;
        if (income == -1) {
            income = 0;
        }
        mIncomeValueTv.setText(String.valueOf(income));

        //支出
        float expend = dayRecord.expendSum;
        if (expend == -1) {
            expend = 0;
        }
        mExpendValueTv.setText(String.valueOf(expend));

        //总计
        float sum = dayRecord.sum;
        if (sum == -1) {
            sum = 0;
        }
        mTotalTv.setText(String.valueOf(sum));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
