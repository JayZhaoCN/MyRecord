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
import com.hfut.zhaojiabao.myrecord.events.CategoryUpdateEvent;
import com.hfut.zhaojiabao.myrecord.events.RecordUpdateEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DetailActivity extends AppCompatActivity {

    private List<DayRecord> mDayRecords;
    private List<Record> mRecordList;
    private List<Record> mCertainDayRecords = new ArrayList<>();

    private TextView mSummaryTv;

    private JayRecordAdapter mRecordAdapter;

    private SelectDate mSelectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.detail);
        setSupportActionBar(toolbar);
        initDatas();
        initUI();

        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(CategoryUpdateEvent event) {
        if (mRecordAdapter != null) {
            mRecordAdapter.invalidateCategoryList();
        }
    }

    public void onEventMainThread(RecordUpdateEvent event) {
        //当记录发生变化，重新加载一遍
        initDatas();
        if (mDayRecords == null) {
            return;
        }
        mIndicatorAdapter.notifyDataSetChanged();
        DayRecord dayRecord = getCertainDayRecord(mSelectDate);
        if (dayRecord == null) {
            dayRecord = mDayRecords.get(0);
            mSelectDate.set(dayRecord.year, dayRecord.month, dayRecord.day);
        }
        setDetailSummary(getCertainDayRecord(mSelectDate));
        getDataFromCertainDay(mSelectDate.year, mSelectDate.month, mSelectDate.day);
        mRecordAdapter.setData(mCertainDayRecords);
    }

    private DayRecord getCertainDayRecord(SelectDate date) {
        for (DayRecord dayRecord : mDayRecords) {
            if (dayRecord.year == date.year && dayRecord.month == date.month && dayRecord.day == date.day) {
                return dayRecord;
            }
        }
        return null;
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

    private IndicatorAdapter mIndicatorAdapter;

    private void initUI() {
        mSummaryTv = (TextView) findViewById(R.id.summary_tv);
        RecyclerView indicatorList = (RecyclerView) findViewById(R.id.indicator_recycler);
        RecyclerView detailList = (RecyclerView) findViewById(R.id.detail_recyler);

        indicatorList.setLayoutManager(new LinearLayoutManager(this));
        indicatorList.setAdapter(mIndicatorAdapter = new IndicatorAdapter());
        detailList.setLayoutManager(new LinearLayoutManager(this));

        if (mDayRecords.size() > 0 && mRecordList.size() > 0) {
            //初次进入，选中第一个
            setDetailSummary(mDayRecords.get(0));
            mSelectDate = new SelectDate(mDayRecords.get(0).year, mDayRecords.get(0).month, mDayRecords.get(0).day);
            getDataFromCertainDay(mSelectDate.year, mSelectDate.month, mSelectDate.day);
        }
        detailList.setAdapter(mRecordAdapter = new JayRecordAdapter(this, mCertainDayRecords));
        mRecordAdapter.setRecordManager(new JayRecordManager(this, mRecordAdapter, mCertainDayRecords));
    }

    private void getDataFromCertainDay(int year, int month, int day) {
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

        @Override
        public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new IndicatorViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_indicator, parent, false));
        }

        @Override
        public void onBindViewHolder(final IndicatorViewHolder holder, int position) {
            holder.indicatorTv.setText(TimeFormatter.getInstance().formatDate(mDayRecords.get(position).timeMillis));

            holder.indicatorTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DayRecord dayRecord = mDayRecords.get(holder.getAdapterPosition());
                    setDetailSummary(dayRecord);
                    mSelectDate.set(dayRecord.year, dayRecord.month, dayRecord.day);
                    getDataFromCertainDay(mSelectDate.year, mSelectDate.month, mSelectDate.day);
                    mRecordAdapter.setData(mCertainDayRecords);
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
        mSummaryTv.setText(record.date + " " + getString(R.string.day_summary, String.valueOf(incomeSum), String.valueOf(expendSum)));
    }

    private class SelectDate {
        int year, month, day;

        SelectDate(int year, int month, int day) {
            set(year, month, day);
        }

        void set(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
}
