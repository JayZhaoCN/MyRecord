package com.hfut.zhaojiabao.myrecord.activities;

import android.os.Bundle;
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
import com.hfut.zhaojiabao.myrecord.JayApp;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;
import com.hfut.zhaojiabao.myrecord.events.CategoryUpdateEvent;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

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

        findViewById(R.id.edit_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
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
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CommonDialog dialog = new CommonDialog();
                    CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(ManageCategoryActivity.this);
                    builder.setTitleText(getString(R.string.confirm_delete))
                            .setRightText(getString(R.string.confirm))
                            .setLeftText(getString(R.string.cancel))
                            .setContentText(getString(R.string.delete_category_tips))
                            .setRightListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!checkCanDelete()) {
                                        ToastUtil.showToast(JayApp.getInstance(), getString(R.string.at_least_one), Toast.LENGTH_SHORT);
                                        return;
                                    }
                                    JayDaoManager.getInstance().getDaoSession().getCategoryDao().delete(mCategories.get(holder.getAdapterPosition()));
                                    updateCategories();
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new CategoryUpdateEvent(CategoryUpdateEvent.STATE_DELETE));
                                    dialog.dismiss();
                                }
                            })
                            .setLeftListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.setBuilder(builder);
                    dialog.show(getSupportFragmentManager(), "confirmDeleteCategoryDialog");
                }
            });
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
        final CommonDialog dialog = new CommonDialog();

        View content = View.inflate(this, R.layout.edit_dialog, null);
        final EditText addEdit = (EditText) content.findViewById(R.id.add_edit);

        CommonDialog.CommonBuilder builder = new CommonDialog.CommonBuilder(this);
        builder.setTitleText(R.string.add_category)
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
                        Category category = new Category();
                        category.setCategory(addEdit.getText().toString());
                        JayDaoManager.getInstance().getDaoSession().getCategoryDao().insert(category);
                        updateCategories();
                        EventBus.getDefault().post(new CategoryUpdateEvent(CategoryUpdateEvent.STATE_DELETE));
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setContent(content);
        dialog.setBuilder(builder);
        dialog.show(getSupportFragmentManager(), "addCategoryDialog");
    }
}
