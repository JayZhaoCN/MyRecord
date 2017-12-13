package com.hfut.zhaojiabao.myrecord.activities;

import android.graphics.Color;
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
import com.hfut.zhaojiabao.myrecord.DayRecord;
import com.hfut.zhaojiabao.myrecord.JayDialogManager;
import com.hfut.zhaojiabao.myrecord.JayRecordAdapter;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.TimeFormatter;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;
import com.hfut.zhaojiabao.myrecord.events.CategoryUpdateEvent;
import com.hfut.zhaojiabao.myrecord.events.RecordUpdateEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    private List<DayRecord> mDayRecords;
    private List<Record> mRecordList;
    private List<Record> mCertainDayRecords = new ArrayList<>();

    @BindView(R.id.summary_tv) TextView mSummaryTv;
    @BindView(R.id.indicator_recycler) RecyclerView mIndicatorList;
    private View mSelectedView;

    private JayRecordAdapter mRecordAdapter;
    private IndicatorAdapter mIndicatorAdapter;

    private SelectDate mSelectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.detail);
        setSupportActionBar(toolbar);
        initData();

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
        initData();
        if (mDayRecords == null) {
            //所有记录都删完了，直接返回
            return;
        }
        //日期栏数据变化
        mIndicatorAdapter.notifyDataSetChanged();
        DayRecord dayRecord = getCertainDayRecord(mSelectDate);
        if (dayRecord == null) {
            //如果当前日期的数据被删完了，获取列表中第一个日期的数据。
            dayRecord = mDayRecords.get(0);
            mSelectedView.setBackgroundColor(Color.WHITE);
            mSelectedView = mIndicatorList.getChildAt(0);
            mSelectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.aqua));
            mSelectDate.set(dayRecord.year, dayRecord.month, dayRecord.day);
        }
        //更新Toolbar上的Summary
        setDetailSummary(getCertainDayRecord(mSelectDate));
        //更新详情栏数据
        getDataFromCertainDay(mSelectDate.year, mSelectDate.month, mSelectDate.day);
        //通知详情栏数据变化
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

    private void initData() {
        ValueTransfer.getDayRecords()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dayRecords -> {
                    //剔除掉没有任何记录的日期
                    mDayRecords = new ArrayList<>();
                    for (DayRecord record : dayRecords) {
                        if (record.incomeSum > 0 || record.expendSum > 0) {
                            mDayRecords.add(record);
                        }
                    }
                    initUI();
                }, throwable -> {
                    //当下还没有数据, 则展示无数据提示UI
                    findViewById(R.id.empty_tv).setVisibility(View.VISIBLE);
                    findViewById(R.id.indicator_recycler).setVisibility(View.GONE);
                    findViewById(R.id.divider).setVisibility(View.GONE);
                    findViewById(R.id.detail_recyler).setVisibility(View.GONE);
                });

        //取所有记录数据
        mRecordList = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();
    }

    private void initUI() {
        RecyclerView detailList = (RecyclerView) findViewById(R.id.detail_recyler);

        mIndicatorList.setLayoutManager(new LinearLayoutManager(this));
        mIndicatorList.setAdapter(mIndicatorAdapter = new IndicatorAdapter());

        //这里需要在post里才能拿到RecyclerView.childView
        mIndicatorList.post(new Runnable() {
            @Override
            public void run() {
                mSelectedView = mIndicatorList.getChildAt(0);
                if (mSelectedView != null) {
                    mSelectedView.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, R.color.aqua));
                }
            }
        });

        detailList.setLayoutManager(new LinearLayoutManager(this));

        if (mDayRecords.size() > 0 && mRecordList.size() > 0) {
            //初次进入，选中第一个
            setDetailSummary(mDayRecords.get(0));
            mSelectDate = new SelectDate(mDayRecords.get(0).year, mDayRecords.get(0).month, mDayRecords.get(0).day);
            getDataFromCertainDay(mSelectDate.year, mSelectDate.month, mSelectDate.day);
        }
        detailList.setAdapter(mRecordAdapter = new JayRecordAdapter(this, mCertainDayRecords));
        mRecordAdapter.setRecordManager(new JayDialogManager(this, mRecordAdapter, mCertainDayRecords));
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
            holder.indicatorTv.setText(TimeFormatter.formatDate(mDayRecords.get(position).timeMillis));
            System.out.println("JayLog, " + TimeFormatter.formatDate(mDayRecords.get(position).timeMillis));

            holder.indicatorTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedView.setBackgroundColor(Color.WHITE);
                    mSelectedView = v;
                    mSelectedView.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, R.color.aqua));
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
        mSummaryTv.setText(getString(R.string.day_summary, String.valueOf(incomeSum), String.valueOf(expendSum)));
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
