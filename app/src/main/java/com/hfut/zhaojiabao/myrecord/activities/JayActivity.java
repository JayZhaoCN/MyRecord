package com.hfut.zhaojiabao.myrecord.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.database.User;
import com.hfut.zhaojiabao.myrecord.JayDialogManager;
import com.hfut.zhaojiabao.myrecord.JayRecordAdapter;
import com.hfut.zhaojiabao.myrecord.events.BudgetChangedEvent;
import com.hfut.zhaojiabao.myrecord.utils.NumberUtils;
import com.hfut.zhaojiabao.myrecord.views.PopLayout;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.utils.TimeFormatter;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;
import com.hfut.zhaojiabao.myrecord.dialogs.PickDateDialog;
import com.hfut.zhaojiabao.myrecord.dialogs.PickTimeDialog;
import com.hfut.zhaojiabao.myrecord.events.CategoryUpdateEvent;
import com.hfut.zhaojiabao.myrecord.events.RecordRecoveryEvent;
import com.hfut.zhaojiabao.myrecord.events.RecordUpdateEvent;
import com.hfut.zhaojiabao.myrecord.greendao.RecordDao;
import com.hfut.zhaojiabao.myrecord.greendao.UserDao;
import com.hfut.zhaojiabao.myrecord.file_operation.IOUtils;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;
import com.hfut.zhaojiabao.myrecord.views.CircleImageView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

import static com.hfut.zhaojiabao.myrecord.file_operation.IOManager.verifyStoragePermissions;

public class JayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "JayActivity";

    private static final int REQUEST_CODE_COMPUTE = 0;
    private static final int REQUEST_CODE_CAPTURE = 1;

    @BindView(R.id.income_btn) CheckBox mIncomeBtn;
    @BindView(R.id.expend_btn) CheckBox mExpendBtn;
    @BindView(R.id.category_tv) TextView mCategoryTv;
    @BindView(R.id.date_tv) TextView mDateTv;
    @BindView(R.id.time_tv) TextView mTimeTv;
    @BindView(R.id.sum_edit) EditText mSumEdit;
    @BindView(R.id.remark_edit) EditText mRemarkEdit;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.income_sum_tv) TextView mIncomeSumTv;
    @BindView(R.id.expend_sum_tv) TextView mExpendSumTv;
    @BindView(R.id.balance_warning_tv) TextView mBalanceWarningTv;

    @OnClick(R.id.income_btn)
    void income() {
        mExpendBtn.setChecked(!mIncomeBtn.isChecked());
    }

    @OnClick(R.id.expend_btn)
    void expend() {
        mIncomeBtn.setChecked(!mExpendBtn.isChecked());
    }

    @OnClick(R.id.account_container)
    void account() {
        ToastUtil.showToast(getString(R.string.account_tips), Toast.LENGTH_SHORT);
    }

    @OnClick(R.id.type_container)
    void type() {
        mRecordManager.showManageCategoryDialog(new JayDialogManager.OnCategorySelectedListener() {
            @Override
            public void onSelect(String category) {
                mCategoryTv.setText(category);
            }
        });
    }

    @OnClick(R.id.date_container)
    void date() {
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
        pickDateDialog.show(getSupportFragmentManager(), "pickDateDialog");
    }

    @OnClick(R.id.time_container)
    void time() {
        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setOnTimePickListener(new PickTimeDialog.OnTimePickListener() {
            @Override
            public void onTimePick(int hour, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                mCalendar.set(Calendar.MINUTE, minute);
                mTimeTv.setText(getTimeDescription());
            }
        });
        pickTimeDialog.show(getSupportFragmentManager(), "pickTimeDialog");
    }

    @OnClick(R.id.calculator_img)
    void calculator() {
        startActivityForResult(new Intent(this, CalculatorActivity.class), REQUEST_CODE_COMPUTE);
    }

    @OnClick(R.id.item_1)
    void item1() {
        startActivity(new Intent(this, RecordChartActivity.class));
    }

    @OnClick(R.id.item_2)
    void item2() {
        startActivity(new Intent(this, SectorActivity.class));
    }

    @OnClick(R.id.save_btn)
    void saveBtn() {
        save();
    }

    private CircleImageView mUserIcon;
    private TextView mUserNameTv;

    private List<Record> mList;
    private String mDefaultCategory;

    private Calendar mCalendar;
    private JayRecordAdapter mAdapter;

    private File mCaptureFile;
    //存放裁剪后临时图片的Uri
    private Uri mDestinationUri;

    private JayDialogManager mRecordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jay);
        ButterKnife.bind(this);
        initToolbarAndDrawer();
        loadRecords();
        initUI();
        initTime();
        //请求读取存储权限
        verifyStoragePermissions(this);

        EventBus.getDefault().registerSticky(this);
    }

    public void onEventMainThread(RecordRecoveryEvent event) {
        Log.i(TAG, "recovery success: " + event.success);
        loadRecords();
        updateTodaySummary();
    }

    public void onEventMainThread(CategoryUpdateEvent event) {
        if (mAdapter != null) {
            mAdapter.invalidateCategoryList();
        }
    }

    public void onEventMainThread(RecordUpdateEvent event) {
        Log.i(TAG, "RecordUpdateEvent");
        long[] todayBounds = TimeFormatter.getTodayBounds();
        //如果不是今日的记录变化，则无需更新今日Summary
        if (todayBounds[0] > event.record.getConsumeTime() || todayBounds[1] < event.record.getConsumeTime()) {
            Log.i(TAG, "not today's record change, so return.");
            return;
        }
        loadRecords();
        updateTodaySummary();
    }

    public void onEventMainThread(BudgetChangedEvent event) {
        updateTodaySummary();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        mCategoryTv.setText(mDefaultCategory = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll().get(0).getCategory());

        RecyclerView recordList = (RecyclerView) findViewById(R.id.today_record);
        recordList.setLayoutManager(new LinearLayoutManager(this));
        recordList.setAdapter(mAdapter = new JayRecordAdapter(this, mList));
        mRecordManager = new JayDialogManager(this, mAdapter, mList);
        mAdapter.setRecordManager(mRecordManager);
        recordList.setNestedScrollingEnabled(false);

        PopLayout popLayout = (PopLayout) findViewById(R.id.pop_Layout);
        popLayout.setTips(0, getString(R.string.histogram));
        popLayout.setTips(1, getString(R.string.pie));
        popLayout.setTips(2, getString(R.string.line_chart));

        updateTodaySummary();
    }

    /**
     * 更新今日Summary
     */
    private void updateTodaySummary() {
        float incomeSummary = 0, expendSummary = 0;
        for (Record record : mList) {
            if (record.getIncome()) {
                incomeSummary += record.getSum();
            } else {
                expendSummary += record.getSum();
            }
        }
        mIncomeSumTv.setText(NumberUtils.getFormattedNumber(incomeSummary));
        mExpendSumTv.setText(NumberUtils.getFormattedNumber(expendSummary));

        float remain = JayDaoManager.getInstance().getDaoSession().getUserDao()
                .loadAll().get(0).getBudget() - expendSummary;
        mBalanceWarningTv.setText(getString(R.string.remain_summary, NumberUtils.getFormattedNumber(remain)));
    }

    private void loadRecords() {
        long[] bounds = TimeFormatter.getTodayBounds();
        mList = JayDaoManager.getInstance().getDaoSession().getRecordDao()
                .queryBuilder()
                .where(RecordDao.Properties.ConsumeTime.ge(bounds[0]))
                .where(RecordDao.Properties.ConsumeTime.le(bounds[1]))
                .list();
        if (mAdapter != null) {
            mAdapter.setData(mList);
        }
        Collections.sort(mList, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o2.getRecordTime().compareTo(o1.getRecordTime());
            }
        });
    }

    //调用相机，准备拍照
    public static void pickCapture(Activity activity, Uri path, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        activity.startActivityForResult(intent, requestCode);
    }

    private void showPickImgDialog() {
        final CommonDialog dialog = new CommonDialog();
        View content = View.inflate(this, R.layout.dialog_pick_img, null);
        content.findViewById(R.id.pick_img_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(JayActivity.this);
                dialog.dismiss();
            }
        });
        content.findViewById(R.id.capture_img_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCapture(JayActivity.this, Uri.fromFile
                        (mCaptureFile = IOUtils.getCropImgFile(IOUtils.CAPTURE_IMG_FOLDER_NAME)), REQUEST_CODE_CAPTURE);
                dialog.dismiss();
            }
        });
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        builder.setTitleText(getString(R.string.select_img))
                .setLeftTextVisible(false)
                .setRightTextVisible(false)
                .setContent(content);
        dialog.setBuilder(builder);
        dialog.show(getSupportFragmentManager(), "pickImgDialog");
    }

    private void showModifyNameDialog() {
        final CommonDialog dialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        View content = View.inflate(this, R.layout.dialog_modify_name, null);
        final EditText nameEdit = (EditText) content.findViewById(R.id.edit_name);
        builder.setContent(content)
                .setTitleText(getString(R.string.modify_name_title))
                .setLeftText(getString(R.string.cancel))
                .setRightText(getString(R.string.confirm))
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = nameEdit.getText().toString();
                        if (!userName.equals("")) {
                            mUserNameTv.setText(userName);
                            UserDao userDao = JayDaoManager.getInstance().getDaoSession().getUserDao();
                            User user = userDao.loadAll().get(0);
                            user.setUserName(userName);
                            userDao.insertOrReplace(user);
                            closeKeyboard(nameEdit);
                        }
                        dialog.dismiss();
                    }
                });
        dialog.setBuilder(builder);
        dialog.show(getSupportFragmentManager(), "modifyNameDialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        onSelectImg(requestCode, data);
        onCropImg(requestCode);
        onComputeDone(requestCode, data);
        onCaptureImg(requestCode);
    }

    //拍照完成，准备裁剪图片
    private void onCaptureImg(int requestCode) {
        if (requestCode != REQUEST_CODE_CAPTURE) {
            return;
        }

        Uri source = Uri.fromFile(mCaptureFile);
        mDestinationUri = Uri.fromFile(IOUtils.getCropImgFile(IOUtils.CROP_IMG_FOLDER_NAME));
        Log.i(TAG, "source uri: " + source);
        Log.i(TAG, "destination uri: " + mDestinationUri);
        Crop.of(source, mDestinationUri).asSquare().start(this);
    }

    private void onComputeDone(int requestCode, Intent intent) {
        if (intent == null) {
            Log.i(TAG, "data is null, so return.");
            return;
        }
        if (requestCode != REQUEST_CODE_COMPUTE) {
            return;
        }
        mSumEdit.setText(NumberUtils.getFormattedNumber(intent.getDoubleExtra("result", 0)));
        mSumEdit.setSelection(mSumEdit.getText().toString().length());
    }

    //获取裁剪完的图片，更新到UI
    //这里裁剪的图片可能来自图库选择，也可能来自拍照
    private void onCropImg(int requestCode) {
        if (requestCode != Crop.REQUEST_CROP) {
            return;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mDestinationUri);
            mUserIcon.setImageBitmap(bitmap);
            //这个等下新开线程去做
            IOUtils.saveAvatar(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //从图库选择完图片，调用该方法开始图片裁剪
    private void onSelectImg(int requestCode, Intent intent) {
        if (intent == null) {
            Log.i(TAG, "data is null, so return.");
            return;
        }
        try {
            if (requestCode != Crop.REQUEST_PICK) {
                return;
            }
            //被选中的图片的Uri
            Uri pickedUri = intent.getData();
            //裁剪完的图片放在该目录下
            mDestinationUri = Uri.fromFile(IOUtils.getCropImgFile(IOUtils.CROP_IMG_FOLDER_NAME));
            Log.i(TAG, "picked img uri: " + pickedUri);
            Log.i(TAG, "mDestination img uri: " + mDestinationUri);
            //开始裁剪
            Crop.of(pickedUri, mDestinationUri).asSquare().start(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        boolean income = mIncomeBtn.isChecked();
        float sumFloat;
        String sum = mSumEdit.getText().toString();
        try {
            sumFloat = Float.valueOf(sum);
        } catch (Exception e) {
            ToastUtil.showToast(getString(R.string.number_error), Toast.LENGTH_SHORT);
            return;
        }

        String category = mCategoryTv.getText().toString();
        String remark = mRemarkEdit.getText().toString();

        long time = mCalendar.getTimeInMillis();

        Record record;
        RecordDao recordDao = JayDaoManager.getInstance().getDaoSession().getRecordDao();
        recordDao.insert(record = new Record(System.currentTimeMillis(), income, remark, category, time, sumFloat));
        //TODO 这里难道没有异常捕获吗，一定就插入成功了吗？
        ToastUtil.showToast(getString(R.string.new_record), Toast.LENGTH_SHORT);

        EventBus.getDefault().post(new RecordUpdateEvent(record, RecordUpdateEvent.STATE_ADD));

        //保存完成后将所有信息还原
        mSumEdit.setText("");
        mRemarkEdit.setText("");
        initTime();
        mCategoryTv.setText(mDefaultCategory);
        JayDialogManager.closeKeyboard(mSumEdit, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCategoryTv.setText(JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll().get(0).getCategory());
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_backup:
                startActivity(new Intent(this, BackupActivity.class));
                break;
            case R.id.nav_recovery:
                startActivity(new Intent(this, RecoveryActivity.class));
                break;
            case R.id.nav_pie_chart:
                startActivity(new Intent(this, SectorActivity.class));
                break;
            case R.id.nav_curve:
                startActivity(new Intent(this, CurveChartActivity.class));
                break;
            case R.id.nav_detail:
                startActivity(new Intent(this, DetailActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.nav_about:
                showAboutDialog();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutDialog() {
        final CommonDialog aboutDialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        builder.setContentText(getString(R.string.about_content))
                .setTitleText(getString(R.string.about))
                .setLeftTextVisible(false)
                .setRightText(getString(R.string.confirm))
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutDialog.dismiss();
                    }
                });
        aboutDialog.setBuilder(builder);
        aboutDialog.show(getSupportFragmentManager(), "aboutDialog");

    }

    private void initToolbarAndDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.record));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mUserIcon = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.user_img);
        mUserNameTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name_tv);
        mUserNameTv.setText(JayDaoManager.getInstance().getDaoSession().getUserDao().loadAll().get(0).getUserName());
        mUserNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyNameDialog();
            }
        });
    }

    private void closeKeyboard(EditText editText) {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                (editText.getWindowToken(), 0);
    }
}