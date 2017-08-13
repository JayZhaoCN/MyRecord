package com.hfut.zhaojiabao.myrecord;

import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;

import java.util.List;

/**
 * @author zhaojiabao 2017/8/13
 */

public class JayRecordManager {
    private static final String TAG = "JayRecordManager";

    private AppCompatActivity mContext;
    private List<Record> mList;
    private JayActivity.RecordAdapter mAdapter;

    public JayRecordManager(AppCompatActivity context, JayActivity.RecordAdapter adapter, List<Record> list) {
        mContext = context;
        mAdapter = adapter;
        mList = list;
    }

    public void editRemark(final int position, final Record record) {
        final CommonDialog commonDialog = new CommonDialog();
        View content = View.inflate(mContext, R.layout.layout_edit_remark, null);
        final EditText editRemark = (EditText) content.findViewById(R.id.remark_edit);
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder();
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
                        String remark = editRemark.getText().toString();
                        mList.remove(position);
                        mList.add(position, record);
                        mAdapter.notifyDataSetChanged();
                        Log.i(TAG, "edit remark: " + remark);
                        record.setRemark(remark);
                        commonDialog.dismiss();
                    }
                });
        builder.setContent(content);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

    public void editSum(final int position, final Record record) {
        final CommonDialog commonDialog = new CommonDialog();
        View content = View.inflate(mContext, R.layout.layout_edit_sum, null);
        final EditText editSum = (EditText) content.findViewById(R.id.sum_edit);
        editSum.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editSum.setKeyListener(new DigitsKeyListener(false, true));
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder();
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

    private void showWarningDialog(String title, String content) {
        final CommonDialog commonDialog = new CommonDialog();
        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder();
        TextView textView = new TextView(mContext);
        textView.setText(content);
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
                });
        builder.setContent(textView);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

}
