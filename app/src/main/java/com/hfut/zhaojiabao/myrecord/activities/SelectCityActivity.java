package com.hfut.zhaojiabao.myrecord.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.events.CityChangedEvent;
import com.hfut.zhaojiabao.myrecord.events.RxBus;
import com.hfut.zhaojiabao.myrecord.typeface.TypefaceTextView;
import com.hfut.zhaojiabao.myrecord.utils.CityDBManager;
import com.hfut.zhaojiabao.myrecord.utils.JayKeeper;
import com.hfut.zhaojiabao.myrecord.utils.RxUtil;
import com.hfut.zhaojiabao.myrecord.weather_db.City;
import com.hfut.zhaojiabao.myrecord.weather_db.Province;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class SelectCityActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private CityAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select City");
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initList();
    }

    private void initList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CityAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        queryProvinces();
    }

    private void queryProvinces() {
        Observable
                .fromCallable(() -> CityDBManager.getInstance().getProvinces())
                .map(provinces -> {
                    List<CityAdapter.CityItem> cityItems = new ArrayList<>();
                    if (provinces == null) {
                        return cityItems;
                    }
                    for (Province province : provinces) {
                        CityAdapter.CityItem cityItem = new CityAdapter.CityItem();
                        cityItem.cityName = province.proName;
                        cityItem.cityId = province.proSort;
                        cityItems.add(cityItem);
                    }
                    return cityItems;
                })
                .compose(RxUtil.ioToMain())
                .subscribe(cityItems -> {
                    mAdapter.setData(cityItems);
                    mAdapter.setLevel(CityAdapter.LEVEL.PROVINCE);
                    mAdapter.notifyDataSetChanged();
                }, throwable -> {
                });
    }

    private static class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
        private List<CityItem> data;
        private Activity activity;

        enum LEVEL {PROVINCE, CITY}

        private LEVEL level = LEVEL.PROVINCE;

        public CityAdapter(Activity activity) {
            this.activity = activity;
        }

        LEVEL getLevel() {
            return level;
        }

        void setLevel(LEVEL level) {
            this.level = level;
        }

        public void setData(List<CityItem> data) {
            this.data = data;
        }

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_city_item, parent, false));
        }

        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            holder.cityTv.setText(data.get(position).cityName);
            holder.cityCard.setOnClickListener(v -> {
                if (level == LEVEL.PROVINCE) {
                    queryCities(data.get(position).cityId);
                } else {
                    JayKeeper.setCity(data.get(position).cityName);
                    RxBus.getDefault().post(new CityChangedEvent(data.get(position).cityName));
                    activity.finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class CityViewHolder extends RecyclerView.ViewHolder {
            TypefaceTextView cityTv;
            CardView cityCard;

            CityViewHolder(View itemView) {
                super(itemView);
                cityTv = (TypefaceTextView) itemView.findViewById(R.id.city_tv);
                cityCard = (CardView) itemView.findViewById(R.id.city_card);
            }
        }

        static class CityItem {
            String cityName;
            int cityId;
        }

        private void queryCities(int proId) {
            Observable
                    .fromCallable(() -> CityDBManager.getInstance().getCities(proId))
                    .map(cities -> {
                        List<CityAdapter.CityItem> cityItems = new ArrayList<>();
                        if (cities == null) {
                            return cityItems;
                        }
                        for (City city : cities) {
                            CityAdapter.CityItem cityItem = new CityAdapter.CityItem();
                            cityItem.cityName = city.cityName;
                            cityItem.cityId = city.citySort;
                            cityItems.add(cityItem);
                        }
                        return cityItems;
                    })
                    .compose(RxUtil.ioToMain())
                    .subscribe(cityItems -> {
                        setData(cityItems);
                        setLevel(LEVEL.CITY);
                        notifyDataSetChanged();
                    }, throwable -> {
                    });
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.getLevel() == CityAdapter.LEVEL.PROVINCE) {
            finish();
        } else {
            queryProvinces();
            mRecyclerView.smoothScrollToPosition(0);
        }
    }
}
