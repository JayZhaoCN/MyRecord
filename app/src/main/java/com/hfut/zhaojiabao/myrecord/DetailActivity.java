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
    }

    private RecordAdapter mRecordAdapter;

    private void initUI() {
        RecyclerView indicatorList = (RecyclerView) findViewById(R.id.indicator_recycler);
        indicatorList.setLayoutManager(new LinearLayoutManager(this));
        indicatorList.setAdapter(new IndicatorAdapter());
        RecyclerView detailList = (RecyclerView) findViewById(R.id.detail_recyler);
        detailList.setLayoutManager(new LinearLayoutManager(this));
        detailList.setAdapter(mRecordAdapter = new RecordAdapter());
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
                    Log.i("JayLog", "onClick: " + dayRecord.toString());
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

    private class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
        @Override
        public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecordAdapter.RecordViewHolder
                    (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_record, parent, false));
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, int position) {
            final Record record = mCertainDayRecords.get(position);

            float sum = record.getSum();
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
            nf.setGroupingUsed(false);

            holder.titleTV.setText(nf.format(sum));
            holder.remarkTv.setText(record.getRemark());
            holder.timeTv.setText(TimeFormatter.getInstance()
                    .niceFormat(DetailActivity.this, record.getConsumeTime()));
            holder.incomeTv.setText(getString(record.getIncome() ? R.string.income : R.string.expend));
            holder.incomeDot.setColor(ContextCompat.getColor(DetailActivity.this,
                    record.getIncome() ? R.color.colorAccent : R.color.mint));
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            holder.incomeContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.typeContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCertainDayRecords.size();
        }

        class RecordViewHolder extends RecyclerView.ViewHolder {
            TextView titleTV, remarkTv, typeTv, timeTv, incomeTv;
            ImageView deleteImg;
            DotView incomeDot, typeDot;
            ViewGroup typeContainer, incomeContainer;

            RecordViewHolder(View itemView) {
                super(itemView);
                titleTV = (TextView) itemView.findViewById(R.id.title_tv);
                remarkTv = (TextView) itemView.findViewById(R.id.remark_tv);
                typeTv = (TextView) itemView.findViewById(R.id.type_tv);
                timeTv = (TextView) itemView.findViewById(R.id.time_tv);
                incomeTv = (TextView) itemView.findViewById(R.id.income_tv);
                deleteImg = (ImageView) itemView.findViewById(R.id.delete_img);

                incomeDot = (DotView) itemView.findViewById(R.id.income_dot);
                typeDot = (DotView) itemView.findViewById(R.id.type_dot);

                typeContainer = (ViewGroup) itemView.findViewById(R.id.type_container);
                incomeContainer = (ViewGroup) itemView.findViewById(R.id.income_container);
            }
        }
    }
}
