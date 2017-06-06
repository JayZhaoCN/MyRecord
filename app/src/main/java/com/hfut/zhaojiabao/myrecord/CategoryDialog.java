package com.hfut.zhaojiabao.myrecord;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;

import java.util.List;

/**
 * @author zhaojiabao 2017/5/19
 */

public class CategoryDialog extends DialogFragment {

    private RecyclerView mCategoryList;
    private List<Category> mCategories;
    private OnCategorySelectedListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_category_dialog, container, false);

        mCategories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();

        mCategoryList = (RecyclerView) v.findViewById(R.id.category_list);
        mCategoryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCategoryList.setAdapter(new CategoryAdapter());

        v.findViewById(R.id.manage_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ManagerCategoryActivity.class));
                dismiss();
            }
        });

        return v;
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_category, parent, false);
            return new CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, final int position) {
            holder.categoryTv.setText(mCategories.get(position).getCategory());
            holder.categoryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onSelect(mCategories.get(position).getCategory());
                    }
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder {
            TextView categoryTv;

            CategoryViewHolder(View itemView) {
                super(itemView);
                categoryTv = (TextView) itemView.findViewById(R.id.category_tv);
            }
        }
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        mListener = listener;
    }

    interface OnCategorySelectedListener {
        void onSelect(String category);
    }
}
