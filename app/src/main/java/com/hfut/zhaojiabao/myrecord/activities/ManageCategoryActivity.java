package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.events.CategoryUpdateEvent;
import com.hfut.zhaojiabao.myrecord.greendao.CategoryDao;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;
import com.zhaojiabao.android.baseui.CommonDialog;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ManageCategoryActivity extends AppCompatActivity {

    private List<Category> mCategories;
    private ManageCategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.manage_catrgory);
        setSupportActionBar(toolbar);

        findViewById(R.id.edit_img).setOnClickListener(v -> showAddCategoryDialog());

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

    private boolean checkCanDelete() {
        return JayDaoManager.getInstance().getDaoSession().getCategoryDao().loadAll().size() > 1;
    }

    private class ManageCategoryAdapter extends RecyclerView.Adapter<ManageCategoryAdapter.ManageViewHolder> {

        @Override
        public ManageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_category_item, parent, false);
            return new ManageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ManageViewHolder holder, int position) {
            holder.titleTv.setText(mCategories.get(position).getCategory());
            holder.deleteImg.setOnClickListener(v ->
                    new CommonDialog.Builder(ManageCategoryActivity.this)
                            .setTitleText(getString(R.string.confirm_delete))
                            .setRightText(getString(R.string.confirm))
                            .setLeftText(getString(R.string.cancel))
                            .setContentText(getString(R.string.delete_category_tips))
                            .setRightListener(dialog -> {
                                if (!checkCanDelete()) {
                                    ToastUtil.showToast(getString(R.string.at_least_one), Toast.LENGTH_SHORT);
                                    return;
                                }
                                JayDaoManager.getInstance().getDaoSession().getCategoryDao().delete(mCategories.get(holder.getAdapterPosition()));
                                updateCategories();
                                notifyDataSetChanged();
                                EventBus.getDefault().post(new CategoryUpdateEvent(CategoryUpdateEvent.STATE_DELETE));
                                dialog.dismiss();
                            })
                            .setLeftListener(DialogFragment::dismiss)
                            .show(getSupportFragmentManager()));
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        class ManageViewHolder extends RecyclerView.ViewHolder {
            TextView titleTv;
            AppCompatImageView deleteImg;

            ManageViewHolder(View itemView) {
                super(itemView);
                titleTv = (TextView) itemView.findViewById(R.id.title_tv);
                deleteImg = (AppCompatImageView) itemView.findViewById(R.id.delete_img);
            }
        }
    }

    private void showAddCategoryDialog() {
        View content = View.inflate(this, R.layout.edit_dialog, null);
        final EditText addEdit = (EditText) content.findViewById(R.id.add_edit);

        new CommonDialog.Builder(this)
                .setTitleText(R.string.add_category)
                .setLeftText(R.string.cancel)
                .setLeftListener(DialogFragment::dismiss)
                .setRightText(R.string.confirm)
                .setRightListener(dialog -> {
                    final String categoryName = addEdit.getText().toString();
                    if (!checkCategoryNameValid(categoryName)) {
                        ToastUtil.showToast(R.string.category_exsit, Toast.LENGTH_SHORT);
                        return;
                    }
                    Category category = new Category();
                    category.setCategory(categoryName);
                    JayDaoManager.getInstance().getDaoSession().getCategoryDao().insert(category);
                    updateCategories();
                    EventBus.getDefault().post(new CategoryUpdateEvent(CategoryUpdateEvent.STATE_ADD));
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                })
                .setContent(content)
                .show(getSupportFragmentManager());
    }

    private boolean checkCategoryNameValid(String name) {
        List<Category> categories =
                JayDaoManager.getInstance().getDaoSession().getCategoryDao().queryBuilder().where(CategoryDao.Properties.Category.eq(name)).list();
        return categories == null || categories.size() == 0;
    }
}
