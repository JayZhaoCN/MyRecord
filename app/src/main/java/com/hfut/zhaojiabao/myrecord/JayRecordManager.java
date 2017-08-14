package com.hfut.zhaojiabao.myrecord;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import java.util.List;

/**
 * @author zhaojiabao 2017/8/13
 */

class JayRecordManager {
    private static final String TAG = "JayRecordManager";

    private AppCompatActivity mContext;
    private List<Record> mList;
    private JayActivity.RecordAdapter mAdapter;

    JayRecordManager(AppCompatActivity context, JayActivity.RecordAdapter adapter, List<Record> list) {
        mContext = context;
        mAdapter = adapter;
        mList = list;
    }

    void editRemark(final int position, final Record record) {
        final CommonDialog commonDialog = new CommonDialog();
        View content = View.inflate(mContext, R.layout.layout_edit_remark, null);
        final EditText editRemark = (EditText) content.findViewById(R.id.remark_edit);
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(mContext);
        builder.setTitleText(mContext.getString(R.string.edit_sum))
                .setLeftTextVisible(true)
                .setLeftText(mContext.getString(R.string.cancel))
                .setRightText(mContext.getString(R.string.confirm))
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                    }
                })
                .setRightTextVisible(true)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeKeyboard(editRemark);
                        String remark = editRemark.getText().toString();
                        mList.remove(position);
                        record.setRemark(remark);
                        JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                        mList.add(position, record);
                        mAdapter.notifyDataSetChanged();
                        Log.i(TAG, "edit remark: " + remark);
                        commonDialog.dismiss();
                    }
                });
        builder.setContent(content);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

    void editSum(final int position, final Record record) {
        final CommonDialog commonDialog = new CommonDialog();
        View content = View.inflate(mContext, R.layout.layout_edit_sum, null);
        final EditText editSum = (EditText) content.findViewById(R.id.sum_edit);
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(mContext);
        builder.setTitleText(mContext.getString(R.string.edit_sum))
                .setLeftTextVisible(true)
                .setLeftText(mContext.getString(R.string.cancel))
                .setRightText(mContext.getString(R.string.confirm))
                .setLeftListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                    }
                })
                .setRightTextVisible(true)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeKeyboard(editSum);
                        float sum;
                        try {
                            sum = Float.valueOf(editSum.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            showWarningDialog(mContext.getString(R.string.warning_title), mContext.getString(R.string.warning_input_not_match));
                            Log.e(TAG, "please input correct number!");
                            commonDialog.dismiss();
                            return;
                        }
                        if (sum == 0) {
                            showWarningDialog(mContext.getString(R.string.warning_title), mContext.getString(R.string.warning_input_zero));
                            Log.e(TAG, "sum cannot be zero!");
                            commonDialog.dismiss();
                            return;
                        }
                        record.setSum(sum);
                        JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                        mList.remove(position);
                        mList.add(position, record);
                        mAdapter.notifyDataSetChanged();
                        commonDialog.dismiss();
                    }
                });
        builder.setContent(content);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

    void deleteRecord(Record record) {
        JayDaoManager.getInstance().getDaoSession().delete(record);
        mList.remove(record);
        mAdapter.notifyDataSetChanged();
        ToastUtil.showToast(JayApp.getInstance(), mContext.getString(R.string.delete_succes), Toast.LENGTH_SHORT);
    }

    void editType(final Record record, final int position) {
        final CommonDialog commonDialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(mContext);
        builder.setTitleText(mContext.getString(R.string.income_or_expend))
                .setLeftTextVisible(false)
                .setRightTextVisible(false);

        View content = View.inflate(mContext, R.layout.layout_select_income, null);
        content.findViewById(R.id.income_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setIncome(true);
                JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                mList.remove(position);
                mList.add(position, record);
                mAdapter.notifyDataSetChanged();
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
                mAdapter.notifyDataSetChanged();
                commonDialog.dismiss();
            }
        });
        builder.setContent(content);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

    void editCategory(final Record record, final int position) {
        CategoryDialog categoryDialog = new CategoryDialog();
        categoryDialog.setOnCategorySelectedListener(new CategoryDialog.OnCategorySelectedListener() {
            @Override
            public void onSelect(String category) {
                record.setCategory(category);
                JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                mList.remove(position);
                mList.add(position, record);
                mAdapter.notifyDataSetChanged();
            }
        });
        categoryDialog.show(mContext.getFragmentManager(), "categoryDialog");
    }

    private void showWarningDialog(String title, String content) {
        final CommonDialog commonDialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(mContext);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitleText(title);
        }
        builder.setLeftTextVisible(false)
                .setRightText(mContext.getString(R.string.confirm))
                .setRightTextVisible(true)
                .setRightListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                    }
                })
                .setContentText(content);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

    /**
     * 强制关闭软键盘
     * 详见: http://blog.csdn.net/h7870181/article/details/8332991
     * */
    private void closeKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
