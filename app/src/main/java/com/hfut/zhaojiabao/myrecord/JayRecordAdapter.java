package com.hfut.zhaojiabao.myrecord;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.events.RecordUpdateEvent;
import com.hfut.zhaojiabao.myrecord.utils.TimeFormatter;
import com.hfut.zhaojiabao.myrecord.views.DotView;
import com.zhaojiabao.android.baseui.CommonDialog;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author zhaojiabao 2017/8/14
 *         用在首页和详情界面
 */

public class JayRecordAdapter extends RecyclerView.Adapter<JayRecordAdapter.RecordViewHolder> {
    private AppCompatActivity mContext;
    private List<Record> mList;
    private JayDialogManager mRecordManager;
    private List<Category> mCategoryList;
    private static int[] mCategoryColors;

    public JayRecordAdapter(AppCompatActivity context, List<Record> list) {
        mContext = context;
        mList = list;
        invalidateCategoryList();
    }

    public void invalidateCategoryList() {
        mCategoryList = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
        this.notifyDataSetChanged();
    }

    @Override
    public JayRecordAdapter.RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_record, parent, false));
    }

    @Override
    public void onBindViewHolder(final JayRecordAdapter.RecordViewHolder holder, int position) {
        final Record record = mList.get(position);

        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        holder.titleTV.setText(nf.format(record.getSum()));

        String remark = record.getRemark();
        holder.remarkTv.setText(TextUtils.isEmpty(remark) ? mContext.getString(R.string.remark_empty) : remark);

        holder.typeTv.setText(getCategory(record.getCategory()));

        holder.timeTv.setText(TimeFormatter.niceFormat(mContext, record.getConsumeTime()));

        holder.incomeTv.setText(mContext.getString(record.getIncome() ? R.string.income : R.string.expend));

        holder.typeDot.setColor(getCategoryColor(mContext, record.getCategory()));

        holder.titleTV.setOnClickListener(v -> mRecordManager.editSum(holder.getAdapterPosition(), record));

        holder.remarkTv.setOnClickListener(v -> mRecordManager.editRemark(holder.getAdapterPosition(), record));

        holder.incomeDot.setColor(ContextCompat.getColor(mContext, record.getIncome() ? R.color.colorAccent : R.color.mint));

        holder.deleteImg.setOnClickListener(v -> showDeleteConfirmDialog(record));

        holder.incomeContainer.setOnClickListener(v -> mRecordManager.editType(record, holder.getAdapterPosition()));

        holder.typeContainer.setOnClickListener(v -> mRecordManager.showManageCategoryDialog(category -> {
            record.setCategory(category);
            JayDaoManager.getInstance().getDaoSession().getRecordDao().insertOrReplace(record);
            mList.remove(holder.getAdapterPosition());
            mList.add(holder.getAdapterPosition(), record);
            notifyDataSetChanged();
        }));
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
        return mContext.getString(R.string.no_category);
    }

    public void setRecordManager(JayDialogManager recordManager) {
        mRecordManager = recordManager;
    }

    public void setData(List<Record> data) {
        mList = data;
        notifyDataSetChanged();
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

    private static int getCategoryColor(Context context, String categoryStr) {
        if (categoryStr.equals(context.getString(R.string.no_category))) {
            return Color.BLACK;
        }
        if (mCategoryColors == null) {
            mCategoryColors = new int[]{
                    ContextCompat.getColor(context, R.color.grapefruit),
                    ContextCompat.getColor(context, R.color.bittersweet),
                    ContextCompat.getColor(context, R.color.sunflower),
                    ContextCompat.getColor(context, R.color.grass),
                    ContextCompat.getColor(context, R.color.mint),
                    ContextCompat.getColor(context, R.color.aqua),
                    ContextCompat.getColor(context, R.color.blue_jeans),
                    ContextCompat.getColor(context, R.color.lavender),
                    ContextCompat.getColor(context, R.color.pink_rose),
                    ContextCompat.getColor(context, R.color.light_gray),
                    ContextCompat.getColor(context, R.color.dark_gray)
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

    private void showDeleteConfirmDialog(final Record record) {
        new CommonDialog.Builder(mContext)
                .setTitleText(R.string.delete_confirm)
                .setLeftText(R.string.cancel)
                .setLeftListener(DialogFragment::dismiss)
                .setRightText(R.string.confirm)
                .setRightListener(dialog -> {
                    mRecordManager.deleteRecord(record);
                    EventBus.getDefault().post(new RecordUpdateEvent(record, RecordUpdateEvent.STATE_DELETE));
                    dialog.dismiss();
                })
                .show(mContext.getSupportFragmentManager());
    }
}
