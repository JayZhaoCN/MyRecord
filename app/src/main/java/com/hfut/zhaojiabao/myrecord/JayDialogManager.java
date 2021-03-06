package com.hfut.zhaojiabao.myrecord;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.activities.ManageCategoryActivity;
import com.hfut.zhaojiabao.myrecord.events.RecordUpdateEvent;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;
import com.zhaojiabao.android.baseui.CommonDialog;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author zhaojiabao 2017/8/13
 *         首页对话框管理类
 */

public class JayDialogManager {
    private static final String TAG = "JayRecordManager";

    private AppCompatActivity mContext;
    private List<Record> mList;
    private JayRecordAdapter mAdapter;

    public JayDialogManager(AppCompatActivity context, JayRecordAdapter adapter, List<Record> list) {
        mContext = context;
        mAdapter = adapter;
        mList = list;
    }

    void editRemark(int position, Record record) {
        View content = View.inflate(mContext, R.layout.layout_edit_remark, null);
        EditText editRemark = (EditText) content.findViewById(R.id.remark_edit);
        new CommonDialog.Builder(mContext)
                .setTitleText(mContext.getString(R.string.edit_remark))
                .setLeftText(mContext.getString(R.string.cancel))
                .setRightText(mContext.getString(R.string.confirm))
                .setLeftListener(DialogFragment::dismiss)
                .setRightListener(dialog -> {
                    closeKeyboard(editRemark, mContext);
                    String remark = editRemark.getText().toString();
                    mList.remove(position);
                    record.setRemark(remark);
                    JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                    mList.add(position, record);
                    mAdapter.notifyDataSetChanged();
                    Log.i(TAG, "edit remark: " + remark);
                    dialog.dismiss();
                })
                .setContent(content)
                .show(mContext.getSupportFragmentManager());
    }

    void editSum(final int position, final Record record) {
        View content = View.inflate(mContext, R.layout.layout_edit_sum, null);
        final EditText editSum = (EditText) content.findViewById(R.id.sum_edit);
        new CommonDialog.Builder(mContext)
                .setTitleText(mContext.getString(R.string.edit_sum))
                .setLeftText(mContext.getString(R.string.cancel))
                .setRightText(mContext.getString(R.string.confirm))
                .setLeftListener(DialogFragment::dismiss)
                .setRightListener(dialog -> {
                    closeKeyboard(editSum, mContext);
                    float sum;
                    try {
                        sum = Float.valueOf(editSum.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        showWarningDialog(mContext.getString(R.string.warning_title), mContext.getString(R.string.warning_input_not_match));
                        Log.e(TAG, "please input correct number!");
                        dialog.dismiss();
                        return;
                    }
                    if (sum == 0) {
                        showWarningDialog(mContext.getString(R.string.warning_title), mContext.getString(R.string.warning_input_zero));
                        Log.e(TAG, "sum cannot be zero!");
                        dialog.dismiss();
                        return;
                    }
                    record.setSum(sum);
                    JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
                    mList.remove(position);
                    mList.add(position, record);
                    mAdapter.setData(mList);
                    EventBus.getDefault().post(new RecordUpdateEvent(record, RecordUpdateEvent.STATE_UPDATE));
                    dialog.dismiss();
                })
                .setContent(content)
                .show(mContext.getSupportFragmentManager());
    }

    void deleteRecord(Record record) {
        JayDaoManager.getInstance().getDaoSession().delete(record);
        mList.remove(record);
        mAdapter.setData(mList);
        ToastUtil.showToast(mContext.getString(R.string.delete_succes), Toast.LENGTH_SHORT);
    }

    void editType(final Record record, final int position) {
        final CommonDialog commonDialog = new CommonDialog();
        CommonDialog.Builder builder = new CommonDialog.Builder(mContext);
        builder.setTitleText(mContext.getString(R.string.income_or_expend));

        View content = View.inflate(mContext, R.layout.layout_select_income, null);
        content.findViewById(R.id.income_tv).setOnClickListener(v -> {
            record.setIncome(true);
            JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
            mList.remove(position);
            mList.add(position, record);
            mAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(new RecordUpdateEvent(record, RecordUpdateEvent.STATE_UPDATE));
            commonDialog.dismiss();
        });
        content.findViewById(R.id.expend_tv).setOnClickListener(v -> {
            record.setIncome(false);
            JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
            mList.remove(position);
            mList.add(position, record);
            mAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(new RecordUpdateEvent(record, RecordUpdateEvent.STATE_UPDATE));
            commonDialog.dismiss();
        });
        builder.setContent(content);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectIncomeDialog");
    }

    /**
     * 展示警告框
     */
    private void showWarningDialog(String title, String content) {
        CommonDialog.Builder builder = new CommonDialog.Builder(mContext);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitleText(title);
        }
        builder.setRightText(mContext.getString(R.string.confirm))
                .setRightListener(DialogFragment::dismiss)
                .setContentText(content)
                .show(mContext.getSupportFragmentManager());
    }

    /**
     * 展示选择类别对话框
     */
    public void showManageCategoryDialog(OnCategorySelectedListener listener) {
        final CommonDialog commonDialog = new CommonDialog();

        View contentView = View.inflate(mContext, R.layout.layout_category_dialog, null);
        RecyclerView categoryList = (RecyclerView) contentView.findViewById(R.id.category_list);
        categoryList.setLayoutManager(new LinearLayoutManager(mContext));
        categoryList.setAdapter(new CategoryAdapter(JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll(), commonDialog, listener));

        CommonDialog.Builder builder = new CommonDialog.Builder(mContext);
        builder.setTitleText(R.string.select_category)
                .setRightText(R.string.manage_category)
                .setRightListener(dialog -> {
                    mContext.startActivity(new Intent(mContext, ManageCategoryActivity.class));
                    dialog.dismiss();
                })
                .setContent(contentView);
        commonDialog.setBuilder(builder);
        commonDialog.show(mContext.getSupportFragmentManager(), "selectCategoryDialog");
    }

    /**
     * 选择类别的Adapter
     */
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
        private List<Category> categories;
        private CommonDialog dialog;
        private OnCategorySelectedListener listener;

        CategoryAdapter(List<Category> categories, CommonDialog dialog, OnCategorySelectedListener listener) {
            this.categories = categories;
            this.dialog = dialog;
            this.listener = listener;
        }

        @Override
        public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_category, parent, false);
            return new CategoryAdapter.CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CategoryAdapter.CategoryViewHolder holder, int position) {
            holder.categoryTv.setText(categories.get(position).getCategory());
            holder.categoryTv.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelect(categories.get(holder.getAdapterPosition()).getCategory());
                }
                dialog.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder {
            TextView categoryTv;

            CategoryViewHolder(View itemView) {
                super(itemView);
                categoryTv = (TextView) itemView.findViewById(R.id.category_tv);
            }
        }
    }

    public interface OnCategorySelectedListener {
        void onSelect(String category);
    }

    /**
     * 强制关闭软键盘
     * 详见: http://blog.csdn.net/h7870181/article/details/8332991
     */
    public static void closeKeyboard(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
