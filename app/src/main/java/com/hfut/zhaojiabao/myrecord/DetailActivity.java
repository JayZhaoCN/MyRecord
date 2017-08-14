package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private List<DayRecord> mDayRecords;
    private List<Record> mRecordList;
    private List<Record> mCertainDayRecords = new ArrayList<>();

    private TextView mSummaryTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.detail);
        setSupportActionBar(toolbar);
        initDatas();
        initUI();
    }

    private void initDatas() {
        //TODO 后面考虑做成static的，因为图表界面也会用到一样的数据，不必加载两次
        List<DayRecord> dayRecord = ValueTransfer.getDayRecords();
        //剔除掉没有任何记录的日期
        mDayRecords = new ArrayList<>();
        if (dayRecord == null) {
            //当下还没有数据, 则展示无数据提示UI
            findViewById(R.id.empty_tv).setVisibility(View.VISIBLE);
            findViewById(R.id.indicator_recycler).setVisibility(View.GONE);
            findViewById(R.id.divider).setVisibility(View.GONE);
            findViewById(R.id.detail_recyler).setVisibility(View.GONE);
            return;
        }
        for (DayRecord record : dayRecord) {
            if (record.incomeSum > 0 || record.expendSum > 0) {
                mDayRecords.add(record);
            }
        }

        mRecordList = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();
    }

    private JayRecordAdapter mRecordAdapter;

    private void initUI() {
        mSummaryTv = (TextView) findViewById(R.id.summary_tv);
        RecyclerView indicatorList = (RecyclerView) findViewById(R.id.indicator_recycler);
        RecyclerView detailList = (RecyclerView) findViewById(R.id.detail_recyler);

        indicatorList.setLayoutManager(new LinearLayoutManager(this));
        indicatorList.setAdapter(new IndicatorAdapter());
        detailList.setLayoutManager(new LinearLayoutManager(this));

        if (mDayRecords.size() > 0 && mRecordList.size() > 0) {
            setDetailSummary(mDayRecords.get(0));
            getDateFromCertainDay(mDayRecords.get(0).year, mDayRecords.get(0).month, mDayRecords.get(0).day);
        }
        detailList.setAdapter(mRecordAdapter = new JayRecordAdapter(this, mCertainDayRecords));
        mRecordAdapter.setRecordManager(new JayRecordManager(this, mRecordAdapter, mCertainDayRecords));
    }

    private void getDateFromCertainDay(int year, int month, int day) {
        mCertainDayRecords.clear();
        Calendar calendar = Calendar.getInstance();
        for (Record record : mRecordList) {
            calendar.setTimeInMillis(record.getConsumeTime());
            if (calendar.get(Calendar.YEAR) == year
                    && (calendar.get(Calendar.MONTH) + 1) == month
                    && calendar.get(Calendar.DATE) == day) {
                mCertainDayRecords.add(record);
            }
        }
    }

    private class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder> {
        private TextView slectedTv;

        @Override
        public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new IndicatorViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_indicator, parent, false));
        }

        @Override
        public void onBindViewHolder(IndicatorViewHolder holder, final int position) {
            holder.indicatorTv.setText(TimeFormatter.getInstance().formatDate(mDayRecords.get(position).timeMillis));

            //第一次进来选中的是第一个
            if (position == 0) {
                slectedTv = holder.indicatorTv;
                slectedTv.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, R.color.aqua));
            }

            holder.indicatorTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (slectedTv != null) {
                        slectedTv.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, R.color.white));
                    }

                    slectedTv = (TextView) v;
                    slectedTv.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, R.color.aqua));
                    DayRecord dayRecord = mDayRecords.get(position);
                    setDetailSummary(dayRecord);
                    getDateFromCertainDay(dayRecord.year, dayRecord.month, dayRecord.day);
                    mRecordAdapter.setData(mCertainDayRecords);
                    mRecordAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDayRecords.size();
        }

        class IndicatorViewHolder extends RecyclerView.ViewHolder {
            TextView indicatorTv;

            IndicatorViewHolder(View itemView) {
                super(itemView);
                indicatorTv = (TextView) itemView.findViewById(R.id.indicator_tv);
            }
        }
    }

    private void setDetailSummary(DayRecord record) {
        float incomeSum = record.incomeSum;
        if (incomeSum < 0) {
            incomeSum = 0;
        }
        float expendSum = record.expendSum;
        if (expendSum < 0) {
            expendSum = 0;
        }
        mSummaryTv.setText(getString(R.string.day_summary, String.valueOf(incomeSum), String.valueOf(expendSum)));
    }
}
