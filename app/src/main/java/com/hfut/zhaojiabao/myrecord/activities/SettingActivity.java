package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.User;
import com.hfut.zhaojiabao.myrecord.JayApp;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;
import com.hfut.zhaojiabao.myrecord.events.BudgetChangedEvent;
import com.hfut.zhaojiabao.myrecord.utils.NumberUtils;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;
import com.hfut.zhaojiabao.myrecord.views.JayItemView;

import de.greenrobot.event.EventBus;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private JayItemView mBalanceItem;
    private JayItemView mBudgetItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.setting);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        mBalanceItem = (JayItemView) findViewById(R.id.balance_item);
        //mBalanceItem.setTitle(R.string.set_balance);
        mBalanceItem.setDividerVisible(true);
        mBalanceItem.setOnClickListener(this);

        mBudgetItem = (JayItemView) findViewById(R.id.budget_item);
        //mBudgetItem.setTitle(R.string.set_budget);
        mBudgetItem.setDividerVisible(true);
        mBudgetItem.setOnClickListener(this);

        updateItemSummary();
    }

    private void updateItemSummary() {
        User user = JayDaoManager.getInstance().getDaoSession().getUserDao().loadAll().get(0);
        mBalanceItem.setSummaryText(NumberUtils.getFormattedNumber(user.getBalance()) + "元");
        mBudgetItem.setSummaryText(NumberUtils.getFormattedNumber(user.getBudget()) + "元");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balance_item:
                showEditBalanceDialog();
                break;
            case R.id.budget_item:
                showEditBudgetDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 修改初始值
     */
    private void showEditBalanceDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        final CommonDialog dialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        builder.setTitleText(R.string.edit_balance)
                .setCancelable(false)
                .setLeftTextVisible(true)
                .setLeftText(R.string.cancel)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setRightTextVisible(true)
                .setRightText(R.string.confirm)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float budget = 0;
                        try {
                            budget = Float.valueOf(editText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //输入有误(0或非数字)
                        if (budget == 0) {
                            ToastUtil.showToast(JayApp.getInstance(), getString(R.string.balance_cannot_be_zero), Toast.LENGTH_SHORT);
                            dialog.dismiss();
                            return;
                        }
                        User user = JayDaoManager.getInstance().getDaoSession().getUserDao().loadAll().get(0);
                        user.setBalance(budget);
                        JayDaoManager.getInstance().getDaoSession().getUserDao().insertOrReplace(user);
                        updateItemSummary();
                        dialog.dismiss();
                    }
                })
                .setContent(editText);
        dialog.setBuilder(builder);
        dialog.show(getSupportFragmentManager(), "editBudgetDialog");
    }

    /**
     * 修改预算
     */
    private void showEditBudgetDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        final CommonDialog dialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        builder.setTitleText(R.string.edit_budget)
                .setCancelable(false)
                .setLeftTextVisible(true)
                .setLeftText(R.string.cancel)
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setRightTextVisible(true)
                .setRightText(R.string.confirm)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float budget = 0;
                        try {
                            budget = Float.valueOf(editText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //输入有误(0或非数字)
                        if (budget == 0) {
                            ToastUtil.showToast(JayApp.getInstance(), getString(R.string.budget_cannot_be_zero), Toast.LENGTH_SHORT);
                            dialog.dismiss();
                            return;
                        }
                        User user = JayDaoManager.getInstance().getDaoSession().getUserDao().loadAll().get(0);
                        user.setBudget(budget);
                        JayDaoManager.getInstance().getDaoSession().getUserDao().insertOrReplace(user);
                        EventBus.getDefault().post(new BudgetChangedEvent());
                        dialog.dismiss();
                        updateItemSummary();
                    }
                })
                .setContent(editText);
        dialog.setBuilder(builder);
        dialog.show(getSupportFragmentManager(), "editBudgetDialog");
    }
}
