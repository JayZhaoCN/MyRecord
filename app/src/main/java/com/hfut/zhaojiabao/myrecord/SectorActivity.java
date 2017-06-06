package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.chart.SectorChart;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SectorActivity extends AppCompatActivity {

    private List<SectorChart.SectorChartItem> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectorChart chart = (SectorChart) findViewById(R.id.sector_chart);

        mDatas = ValueTransfer.getTypePercent(false);
        chart.provideData(mDatas);

        initTypeIndicator();
    }

    private void initTypeIndicator() {
        RecyclerView typeIndicator = (RecyclerView) findViewById(R.id.type_indicator);
        typeIndicator.setLayoutManager(new LinearLayoutManager(this));
        typeIndicator.setAdapter(new IndicatorAdapter());
    }

    private class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder> {

        @Override
        public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
            return new IndicatorViewHolder(v);
        }

        @Override
        public void onBindViewHolder(IndicatorViewHolder holder, int position) {
            holder.indicatorView.setBackgroundColor(SectorChart.mColors[position]);
            holder.typeTv.setText(mDatas.get(position).text + "  " + mDatas.get(position).percent + "%");
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class IndicatorViewHolder extends RecyclerView.ViewHolder {
            View indicatorView;
            TextView typeTv;

            IndicatorViewHolder(View itemView) {
                super(itemView);
                indicatorView = itemView.findViewById(R.id.type_indicator);
                typeTv = (TextView) itemView.findViewById(R.id.type_tv);
            }
        }
    }

}
