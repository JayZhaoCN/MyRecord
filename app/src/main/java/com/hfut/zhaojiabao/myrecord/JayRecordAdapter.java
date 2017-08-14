package com.hfut.zhaojiabao.myrecord;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.database.Record;
import com.hfut.zhaojiabao.myrecord.views.DotView;

import java.util.List;

/**
 * @author zhaojiabao 2017/8/14
 *         用在首页和详情界面
 */

public class JayRecordAdapter extends RecyclerView.Adapter<JayRecordAdapter.RecordViewHolder> {
    private Context mContext;
    private List<Record> mList;
    private JayRecordManager mRecordManager;
    private List<Category> mCategoryList;
    private static int[] mCategoryColors;

    public JayRecordAdapter(Context context, List<Record> list) {
        mContext = context;
        mList = list;
        mCategoryList = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
    }

    @Override
    public JayRecordAdapter.RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_record, parent, false);
        return new RecordViewHolder(v);
    }

    @Override
    public void onBindViewHolder(JayRecordAdapter.RecordViewHolder holder, final int position) {
        final Record record = mList.get(position);

        float sum = record.getSum();
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);

        holder.titleTV.setText(nf.format(sum));
        String remark = record.getRemark();
        if (TextUtils.isEmpty(remark)) {
            remark = mContext.getString(R.string.remark_empty);
        }
        holder.remarkTv.setText(remark);
        holder.typeTv.setText(getCategory(record.getCategory()));
        holder.timeTv.setText(TimeFormatter.getInstance().niceFormat(mContext, record.getConsumeTime()));
        holder.incomeTv.setText(mContext.getString(record.getIncome() ? R.string.income : R.string.expend));
        holder.typeDot.setColor(getCategoryColor(mContext, record.getCategory()));

        holder.titleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordManager.editSum(position, record);
            }
        });

        holder.remarkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordManager.editRemark(position, record);
            }
        });

        holder.incomeDot.setColor(ContextCompat.getColor(mContext, record.getIncome() ? R.color.colorAccent : R.color.mint));

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordManager.deleteRecord(record);
            }
        });

        holder.incomeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordManager.editType(record, position);
            }
        });

        holder.typeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordManager.editCategory(record, position);
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
        return mContext.getString(R.string.no_category);
    }

    public void setRecordManager(JayRecordManager recordManager) {
        mRecordManager = recordManager;
    }

    public void setData(List<Record> data) {
        mList = data;
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

    public static int getCategoryColor(Context context, String categoryStr) {
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
}
