package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.chart.SectorChart;
import com.hfut.zhaojiabao.myrecord.chart.ValueTransfer;

import java.util.ArrayList;
import java.util.List;

public class SectorActivity extends AppCompatActivity {

    private List<SectorChart.SectorChartItem> mData;
    private List<TypeItem> mTypeData;

    private SectorChart mChart;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sector);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mChart = (SectorChart) findViewById(R.id.sector_chart);
        updateUI(false);
    }

    private void updateUI(boolean income) {
        mToolbar.setTitle(income ? R.string.income_percent : R.string.expend_percent);
        setSupportActionBar(mToolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.type_indicator);

        mData = ValueTransfer.getTypePercent(income);
        if (mData == null) {
            mChart.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_tips_tv).setVisibility(View.VISIBLE);
            return;
        }
        mChart.provideData(mData);

        bindDataAndType();
        initTypeIndicator();
    }

    private void bindDataAndType() {
        mTypeData = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).value != 0) {
                mTypeData.add(new TypeItem(mData.get(i).text, SectorChart.mColors[i], mData.get(i).percent));
            }
        }
    }

    private void initTypeIndicator() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(new IndicatorAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sector_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_income:
                updateUI(true);
                return true;
            case R.id.action_expend:
                updateUI(false);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class TypeItem {
        String text;
        int color;
        float percent;

        TypeItem(String text, int color, float percent) {
            this.text = text;
            this.color = color;
            this.percent = percent;
        }
    }

    private class IndicatorAdapter extends RecyclerView.Adapter<IndicatorAdapter.IndicatorViewHolder> {

        @Override
        public IndicatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
            return new IndicatorViewHolder(v);
        }

        @Override
        public void onBindViewHolder(IndicatorViewHolder holder, int position) {
            holder.indicatorView.setBackgroundColor(mTypeData.get(position).color);
            holder.typeTv.setText(mTypeData.get(position).text + "  " + mTypeData.get(position).percent + "%");
        }

        @Override
        public int getItemCount() {
            return mTypeData.size();
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
