package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.chart.RecordChart;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RecordChartActivity extends AppCompatActivity {
    private List<DayRecord> mDatas;

    //支出
    @BindView(R.id.income_value_tv) TextView mIncomeValueTv;
    //收入
    @BindView(R.id.expend_value_tv) TextView mExpendValueTv;
    //总计
    @BindView(R.id.total_value_tv) TextView mTotalTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.chart);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initChart();
    }

    private void initChart() {
        final RecordChart chart = (RecordChart) findViewById(R.id.record_chart);

        ValueTransfer.getDayRecords()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<DayRecord>>() {
                    @Override
                    public void call(List<DayRecord> dayRecords) {
                        mDatas = dayRecords;
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
