package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

    private List<SectorChart.SectorChartItem> mDatas;
    private List<TypeItem> mTypeDatas;

    private SectorChart mChart;
    private Toolbar mToolbar;

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
        mDatas = ValueTransfer.getTypePercent(income);
        mChart.provideData(mDatas);

        bindDataAndType();
        initTypeIndicator();
    }

    private void bindDataAndType() {
        mTypeDatas = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).value != 0) {
                mTypeDatas.add(new TypeItem(mDatas.get(i).text, SectorChart.mColors[i], mDatas.get(i).percent));
            }
        }
    }

    private void initTypeIndicator() {
        RecyclerView typeIndicator = (RecyclerView) findViewById(R.id.type_indicator);
        typeIndicator.setLayoutManager(new GridLayoutManager(this, 2));
        typeIndicator.setAdapter(new IndicatorAdapter());
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
            holder.indicatorView.setBackgroundColor(mTypeDatas.get(position).color);
            holder.typeTv.setText(mTypeDatas.get(position).text + "  " + mTypeDatas.get(position).percent + "%");
        }

        @Override
        public int getItemCount() {
            return mTypeDatas.size();
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
