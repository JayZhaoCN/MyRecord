package com.hfut.zhaojiabao.myrecord;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;
import com.hfut.zhaojiabao.myrecord.dialogs.PickDateDialog;
import com.hfut.zhaojiabao.myrecord.dialogs.PickTimeDialog;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;
import com.hfut.zhaojiabao.myrecord.views.DotView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.hfut.zhaojiabao.myrecord.file_operation.BackupTask.verifyStoragePermissions;

public class JayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_CODE_COMPUTE = 0;

    private CheckBox mIncomeBtn;
    private CheckBox mExpendBtn;
    private TextView mCategoryTv;
    private TextView mDateTv;
    private TextView mTimeTv;
    private EditText mSumEdit;
    private EditText mRemarkEdit;
    private DrawerLayout mDrawerLayout;

    private List<Record> mList;
    private List<Category> mCategoryList;
    private String mDefaultCategory;
    private int[] mCategoryColors;

    private Calendar mCalendar;
    private RecordAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jay);
        initToolbarAndDrawer();
        initUI();
        initTime();
        loadRecords();
        //请求读取存储权限
        verifyStoragePermissions(this);
    }

    private void initTime() {
        mCalendar = Calendar.getInstance();
        mDateTv.setText(getDateDescription());
        mTimeTv.setText(getTimeDescription());
    }

    private String getDateDescription() {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
        return sdf.format(mCalendar.getTime());
    }

    private String getTimeDescription() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(mCalendar.getTime());
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
        popLayout.setTips(0, getString(R.string.histogram));
        popLayout.setTips(1, getString(R.string.pie));
        popLayout.setTips(2, getString(R.string.line_chart));

        findViewById(R.id.item_1).setOnClickListener(this);
        findViewById(R.id.item_2).setOnClickListener(this);
        findViewById(R.id.item_3).setOnClickListener(this);

        findViewById(R.id.calculator_img).setOnClickListener(this);
    }

    private void loadRecords() {
        mList = JayDaoManager.getInstance().getDaoSession().getRecordDao().loadAll();
        Collections.sort(mList, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o2.getRecordTime().compareTo(o1.getRecordTime());
            }
        });

        mCategoryList = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
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
                ToastUtil.showToast(JayApp.getInstance(), getString(R.string.account_tips), Toast.LENGTH_SHORT);
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
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, day);
                        mDateTv.setText(getDateDescription());
                    }
                });
                pickDateDialog.show(getFragmentManager(), "pickDateDialog");
                break;
            case R.id.time_container:
                PickTimeDialog pickTimeDialog = new PickTimeDialog();
                pickTimeDialog.setOnTimePickListener(new PickTimeDialog.OnTimePickListener() {
                    @Override
                    public void onTimePick(int hour, int minute) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        mCalendar.set(Calendar.MINUTE, minute);
                        mTimeTv.setText(getTimeDescription());
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
                startActivityForResult(new Intent(this, CalculatorActivity.class), REQUEST_CODE_COMPUTE);
                break;
            case R.id.save_btn:
                save();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_COMPUTE:
                mSumEdit.setText(String.valueOf(data.getDoubleExtra("result", 0)));
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
            ToastUtil.showToast(JayApp.getInstance(), getString(R.string.number_error), Toast.LENGTH_SHORT);
            return;
        }

        String category = mCategoryTv.getText().toString();
        String remark = mRemarkEdit.getText().toString();

        long time = mCalendar.getTimeInMillis();

        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        recordDao.insert(new Record(System.currentTimeMillis(), income, remark, category, time, sumFloat));
        //TODO 这里难道没有异常捕获吗，一定就插入成功了吗？
        ToastUtil.showToast(JayApp.getInstance(), getString(R.string.new_record), Toast.LENGTH_SHORT);

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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
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
            startActivity(new Intent(this, BackupActivity.class));
            return true;
        } else if (id == R.id.action_recovery) {
            startActivity(new Intent(this, RecoveryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initToolbarAndDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.record));
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

        @Override
        public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_record, parent, false);
            return new RecordViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, final int position) {
            final Record record = mList.get(position);

            float sum = record.getSum();
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
            nf.setGroupingUsed(false);

            holder.titleTV.setText(nf.format(sum));
            holder.remarkTv.setText(record.getRemark());
            holder.typeTv.setText(getCategory(record.getCategory()));
            holder.timeTv.setText(TimeFormatter.getInstance().niceFormat(JayActivity.this, record.getConsumeTime()));
            holder.incomeTv.setText(getString(record.getIncome() ? R.string.income : R.string.expend));
            holder.typeDot.setColor(getCategoryColor(record.getCategory()));
            holder.incomeDot.setColor(ContextCompat.getColor(JayActivity.this, record.getIncome() ? R.color.colorAccent : R.color.mint));
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JayDaoManager.getInstance().getDaoSession().delete(record);
                    mList.remove(record);
                    notifyDataSetChanged();
                    ToastUtil.showToast(JayApp.getInstance(), "成功删除一条记录", Toast.LENGTH_SHORT);
                }
            });
            holder.incomeContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommonDialog commonDialog = new CommonDialog();
                    CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder();
                    builder.setTitleText("收入还是支出？")
                            .setLeftTextVisible(false)
                            .setRightTextVisible(false);

                    View content = View.inflate(JayActivity.this, R.layout.layout_select_income, null);
                    content.findViewById(R.id.income_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            record.setIncome(true);
                            JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                            mList.remove(position);
                            mList.add(position, record);
                            notifyDataSetChanged();
                            commonDialog.dismiss();
                        }
                    });
                    content.findViewById(R.id.expend_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            record.setIncome(false);
                            JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                            mList.remove(position);
                            mList.add(position, record);
                            notifyDataSetChanged();
                            commonDialog.dismiss();
                        }
                    });
                    builder.setContent(content);
                    commonDialog.setBuilder(builder);
                    commonDialog.show(getFragmentManager(), "SelectIncomeDialog");
                }
            });
            holder.typeContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryDialog categoryDialog = new CategoryDialog();
                    categoryDialog.setOnCategorySelectedListener(new CategoryDialog.OnCategorySelectedListener() {
                        @Override
                        public void onSelect(String category) {
                            record.setCategory(category);
                            JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                            mList.remove(position);
                            mList.add(position, record);
                            notifyDataSetChanged();
                        }
                    });
                    categoryDialog.show(getFragmentManager(), "categoryDialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        private String getCategory(String categoryStr) {
            for (Category category : mCategoryList) {
                if (category.getCategory().equals(categoryStr)) {
                    return categoryStr;
                }
            }
            return getString(R.string.no_category);
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

    private int getCategoryColor(String categoryStr) {
        if (categoryStr.equals(getString(R.string.no_category))) {
            return Color.BLACK;
        }
        if (mCategoryColors == null) {
            mCategoryColors = new int[]{
                    ContextCompat.getColor(this, R.color.grapefruit),
                    ContextCompat.getColor(this, R.color.bittersweet),
                    ContextCompat.getColor(this, R.color.sunflower),
                    ContextCompat.getColor(this, R.color.grass),
                    ContextCompat.getColor(this, R.color.mint),
                    ContextCompat.getColor(this, R.color.aqua),
                    ContextCompat.getColor(this, R.color.blue_jeans),
                    ContextCompat.getColor(this, R.color.lavender),
                    ContextCompat.getColor(this, R.color.pink_rose),
                    ContextCompat.getColor(this, R.color.light_gray),
                    ContextCompat.getColor(this, R.color.dark_gray)
            };
        }

        List<Category> categories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
        int index = -1;

        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategory().equals(categoryStr)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return Color.BLACK;
        }

        return mCategoryColors[index % mCategoryColors.length];
    }
}
