package com.hfut.zhaojiabao.myrecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;

import java.util.List;

public class ManagerCategoryActivity extends AppCompatActivity {

    private List<Category> mCategories;
    private ManageCategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("管理类别");
        setSupportActionBar(toolbar);

        findViewById(R.id.edit_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCategoryDialog dialog = new EditCategoryDialog();
                dialog.show(getFragmentManager(), "EditCategoryDialog");
                dialog.setOnCategoryAddedListener(new EditCategoryDialog.OnCategoryAddedListener() {
                    @Override
                    public void onCategoryAdded() {
                        updateCategories();
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        initCategories();
    }

    private void initCategories() {
        updateCategories();
        RecyclerView categoryList = (RecyclerView) findViewById(R.id.category_list);
        categoryList.setLayoutManager(new LinearLayoutManager(this));
        categoryList.setAdapter(mAdapter = new ManageCategoryAdapter());

    }

    private void updateCategories() {
        mCategories = JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll();
    }


    private class ManageCategoryAdapter extends RecyclerView.Adapter<ManageCategoryAdapter.ManageViewHolder> {

        @Override
        public ManageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_category_item, parent, false);
            return new ManageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ManageViewHolder holder, final int position) {
            holder.titleTv.setText(mCategories.get(position).getCategory());
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("JayTest", "item delete: " + mCategories.get(position));
                    JayDaoManager.getInstance().getDaoSession().getCategoryDao().delete(mCategories.get(position));
                    updateCategories();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        class ManageViewHolder extends RecyclerView.ViewHolder {
            TextView titleTv;
            ImageView deleteImg;

            ManageViewHolder(View itemView) {
                super(itemView);
                titleTv = (TextView) itemView.findViewById(R.id.title_tv);
                deleteImg = (ImageView) itemView.findViewById(R.id.delete_img);
            }
        }
    }
}
