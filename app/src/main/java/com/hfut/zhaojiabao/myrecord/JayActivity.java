package com.hfut.zhaojiabao.myrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.dialogs.PickDateDialog;
import com.hfut.zhaojiabao.myrecord.dialogs.PickTimeDialog;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;

import java.util.List;

public class JayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox mIncomeBtn;
    private CheckBox mExpendBtn;
    private TextView mCategoryTv;
    private TextView mDateTv;
    private TextView mTimeTv;
    private EditText mSumEdit;
    private EditText mRemarkEdit;

    private List<Record> mList;

    private String mAccount;
    private String mCategory;
    private String mTime;
    private String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jay);
        initToolbarAndDrawer();
        initUI();
        loadRecords();
    }

    private void initUI() {
        mCategoryTv = (TextView) findViewById(R.id.category_tv);
        mDateTv = (TextView) findViewById(R.id.date_tv);
        mTimeTv = (TextView) findViewById(R.id.time_tv);
        mSumEdit = (EditText) findViewById(R.id.sum_edit);
        mRemarkEdit = (EditText) findViewById(R.id.remark_edit);

        mIncomeBtn = (CheckBox) findViewById(R.id.income_btn);
        mExpendBtn = (CheckBox) findViewById(R.id.expend_btn);
        mIncomeBtn.setOnClickListener(this);
        mExpendBtn.setOnClickListener(this);
        mIncomeBtn.setOnCheckedChangeListener(this);
        mExpendBtn.setOnCheckedChangeListener(this);

        findViewById(R.id.account_container).setOnClickListener(this);
        findViewById(R.id.type_container).setOnClickListener(this);
        findViewById(R.id.date_container).setOnClickListener(this);
        findViewById(R.id.time_container).setOnClickListener(this);
        findViewById(R.id.save_btn).setOnClickListener(this);

        RecyclerView recordList = (RecyclerView) findViewById(R.id.today_record);
        recordList.setLayoutManager(new LinearLayoutManager(this));
        recordList.setAdapter(new RecordAdapter());
        recordList.setNestedScrollingEnabled(false);

        final PopLayout popLayout = (PopLayout) findViewById(R.id.pop_Layout);
        popLayout.setTips(0, "侧滑");
        popLayout.setTips(1, "回答");
        popLayout.setTips(2, "提问");

        findViewById(R.id.item_1).setOnClickListener(this);
        findViewById(R.id.item_2).setOnClickListener(this);
        findViewById(R.id.item_3).setOnClickListener(this);

        findViewById(R.id.calculator_img).setOnClickListener(this);
    }

    private void loadRecords() {
        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        mList = recordDao.loadAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.income_btn:
                mExpendBtn.setChecked(!mIncomeBtn.isChecked());
                break;
            case R.id.expend_btn:
                mIncomeBtn.setChecked(!mExpendBtn.isChecked());
                break;
            case R.id.account_container:
                Toast.makeText(this, "目前只支持默认账户，更多功能开发中~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.type_container:
                final CategoryDialog categoryDialog = new CategoryDialog();
                categoryDialog.setOnCategorySelectedListener(new CategoryDialog.OnCategorySelectedListener() {
                    @Override
                    public void onSelect(String category) {
                        mCategoryTv.setText(category);
                    }
                });
                categoryDialog.show(getFragmentManager(), "categoryDialog");
                break;
            case R.id.date_container:
                PickDateDialog pickDateDialog = new PickDateDialog();
                pickDateDialog.setOnDatePickListener(new PickDateDialog.OnDatePickListener() {
                    @Override
                    public void onDatePick(String year, String month, String day) {
                        mDateTv.setText(year + "-" + month + "-" + day);
                    }
                });
                pickDateDialog.show(getFragmentManager(), "pickDateDialog");
                break;
            case R.id.time_container:
                PickTimeDialog pickTimeDialog = new PickTimeDialog();
                pickTimeDialog.setOnTimePickListener(new PickTimeDialog.OnTimePickListener() {
                    @Override
                    public void onTimePick(String hour, String minute) {
                        mTimeTv.setText(hour + ":" + minute);
                    }
                });
                pickTimeDialog.show(getFragmentManager(), "pickTimeDialog");
                break;
            case R.id.item_1:
                Log.i("JayTest", "item 1 clicked");
                break;
            case R.id.item_2:
                break;
            case R.id.item_3:
                break;
            case R.id.calculator_img:
                startActivity(new Intent(this, CalculatorActivity.class));
                break;
            case R.id.save_btn:
                save();
                break;
            default:
                break;
        }
    }

    private void save() {
        boolean income = mIncomeBtn.isChecked();
        float sumFloat = 0;
        String sum = mSumEdit.getText().toString();
        try {
            sumFloat = Float.valueOf(sum);
        } catch (Exception e) {
            Toast.makeText(this, "金额有误！", Toast.LENGTH_SHORT).show();
        }

        String category = mCategoryTv.getText().toString();
        String remark = mRemarkEdit.getText().toString();
        String consumeTime = mDateTv.getText().toString() + " " + mTimeTv.getText().toString();

        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        recordDao.insert(new Record(System.currentTimeMillis(), income, remark, category, consumeTime, sumFloat));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        switch (buttonView.getId()) {
            case R.id.income_btn:
                //select income
                break;
            case R.id.expend_btn:
                //select expend
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.jay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initToolbarAndDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("账本");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

        @Override
        public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_record, parent, false);
            return new RecordViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, int position) {
            holder.titleTV.setText("支出:5");
            holder.remarkTv.setText(mList.get(position).getRemark());
            holder.typeTv.setText(mList.get(position).getCategory());
            holder.timeTv.setText(TimeFormatter.getInstance().format(mList.get(position).getRecordTime()));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class RecordViewHolder extends RecyclerView.ViewHolder {
            TextView titleTV, remarkTv, typeTv, timeTv;

            RecordViewHolder(View itemView) {
                super(itemView);
                titleTV = (TextView) itemView.findViewById(R.id.title_tv);
                remarkTv = (TextView) itemView.findViewById(R.id.remark_tv);
                typeTv = (TextView) itemView.findViewById(R.id.type_tv);
                timeTv = (TextView) itemView.findViewById(R.id.time_tv);
            }
        }
    }
}
