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
import com.hfut.zhaojiabao.myrecord.file_operation.BackupTask;
import com.hfut.zhaojiabao.myrecord.file_operation.RecoveryTask;
import com.hfut.zhaojiabao.myrecord.file_operation.TraverseTask;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private RecordAdapter mAdapter;

    private int mYear, mMonth, mDay, mHour, mMinute;

    private String mDefaultCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jay);
        initToolbarAndDrawer();
        initUI();
        initTime();
        loadRecords();
        new TraverseTask().execute();
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        mDateTv.setText(String.valueOf(mYear).substring(2) + "-" + (mMonth + 1) + "-" + mDay);
        mTimeTv.setText(mHour + ":" + mMinute);
    }

    private void initUI() {
        mCategoryTv = (TextView) findViewById(R.id.category_tv);

        mCategoryTv.setText(mDefaultCategory = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll().get(0).getCategory());

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
        recordList.setAdapter(mAdapter = new RecordAdapter());
        recordList.setNestedScrollingEnabled(false);

        PopLayout popLayout = (PopLayout) findViewById(R.id.pop_Layout);
        popLayout.setTips(0, "柱状图");
        popLayout.setTips(1, "饼图");
        popLayout.setTips(2, "折线图");

        findViewById(R.id.item_1).setOnClickListener(this);
        findViewById(R.id.item_2).setOnClickListener(this);
        findViewById(R.id.item_3).setOnClickListener(this);

        findViewById(R.id.calculator_img).setOnClickListener(this);
    }

    private void loadRecords() {
        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        mList = recordDao.loadAll();
        Collections.sort(mList, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o2.getRecordTime().compareTo(o1.getRecordTime());
            }
        });
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
                ToastUtil.showToast(JayApplication.getApplication(), getString(R.string.account_tips), Toast.LENGTH_SHORT);
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
                    public void onDatePick(int year, int month, int day) {
                        mDateTv.setText(String.valueOf(year).substring(2) + "-" + (month + 1) + "-" + day);
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                    }
                });
                pickDateDialog.show(getFragmentManager(), "pickDateDialog");
                break;
            case R.id.time_container:
                PickTimeDialog pickTimeDialog = new PickTimeDialog();
                pickTimeDialog.setOnTimePickListener(new PickTimeDialog.OnTimePickListener() {
                    @Override
                    public void onTimePick(int hour, int minute) {
                        mTimeTv.setText(hour + ":" + minute);
                        mHour = hour;
                        mMinute = minute;
                    }
                });
                pickTimeDialog.show(getFragmentManager(), "pickTimeDialog");
                break;
            case R.id.item_1:
                startActivity(new Intent(this, RecordChartActivity.class));
                break;
            case R.id.item_2:
                startActivity(new Intent(this, SectorActivity.class));
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
        float sumFloat;
        String sum = mSumEdit.getText().toString();
        try {
            sumFloat = Float.valueOf(sum);
        } catch (Exception e) {
            ToastUtil.showToast(JayApplication.getApplication(), getString(R.string.number_error), Toast.LENGTH_SHORT);
            return;
        }

        String category = mCategoryTv.getText().toString();
        String remark = mRemarkEdit.getText().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
        long time = calendar.getTimeInMillis();

        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        recordDao.insert(new Record(System.currentTimeMillis(), income, remark, category, time, sumFloat));
        //TODO 这里难道没有异常捕获吗，一定就插入成功了吗？
        ToastUtil.showToast(JayApplication.getApplication(), getString(R.string.new_record), Toast.LENGTH_SHORT);

        loadRecords();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mSumEdit.setText("");
        initTime();
        mCategoryTv.setText(mDefaultCategory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCategoryTv.setText(JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll().get(0).getCategory());
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
        int id = item.getItemId();

        if (id == R.id.action_backup) {
            new BackupTask(this).execute();
            return true;
        } else if (id == R.id.action_recovery) {
            startActivity(new Intent(this, RecoveryActivity.class));
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
        toolbar.setTitle(getString(R.string.record));
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
            Record record = mList.get(position);

            float sum = record.getSum();
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
            nf.setGroupingUsed(false);

            holder.titleTV.setText(getString(record.getIncome() ? R.string.income_str : R.string.expend_str, nf.format(sum)));
            holder.remarkTv.setText(mList.get(position).getRemark());
            holder.typeTv.setText(mList.get(position).getCategory());
            holder.timeTv.setText(TimeFormatter.getInstance().format(mList.get(position).getConsumeTime()));
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
