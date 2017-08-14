package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;
import com.hfut.zhaojiabao.myrecord.views.DotView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private List<DayRecord> mDayRecords;
    private List<Record> mRecordList;
    private List<Record> mCertainDayRecords = new ArrayList<>();

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
        mDayRecords = ValueTransfer.getDayRecords();
        if (mDayRecords == null) {
            mDayRecords = new ArrayList<>();
        }
        mRecordList = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();
        if (mDayRecords.size() > 0 && mRecordList.size() > 0) {
            getDateFromCertainDay(mDayRecords.get(0).year, mDayRecords.get(0).month, mDayRecords.get(0).day);
        }
    }

    private JayRecordAdapter mRecordAdapter;

    private void initUI() {
        RecyclerView indicatorList = (RecyclerView) findViewById(R.id.indicator_recycler);
        indicatorList.setLayoutManager(new LinearLayoutManager(this));
        indicatorList.setAdapter(new IndicatorAdapter());
        RecyclerView detailList = (RecyclerView) findViewById(R.id.detail_recyler);
        detailList.setLayoutManager(new LinearLayoutManager(this));
        detailList.setAdapter(mRecordAdapter = new JayRecordAdapter(this, mRecordList));
        mRecordAdapter.setRecordManager(new JayRecordManager(this, mRecordAdapter, mRecordList));
    }

    private void getDateFromCertainDay(int year, int month, int day) {
        mCertainDayRecords.clear();
        Calendar calendar = Calendar.getInstance();
        for (Record record : mRecordList) {
            calendar.setTimeInMillis(record.getConsumeTime());
            Log.i("JayLog", "add record: " + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DATE));
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
        public void onBindViewHolder(IndicatorViewHolder holder, final int position) {
            holder.indicatorTv.setText(mDayRecords.get(position).date);
            holder.indicatorTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DayRecord dayRecord = mDayRecords.get(position);
                    getDateFromCertainDay(dayRecord.year, dayRecord.month, dayRecord.day);
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
}
