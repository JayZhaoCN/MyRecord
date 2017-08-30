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
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.dialogs.CommonDialog;
import com.hfut.zhaojiabao.myrecord.file_operation.IOManager;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class RecoveryActivity extends AppCompatActivity {

    private List<File> mRecoveryItem;
    private RecoveryItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.action_recovery));
        setSupportActionBar(toolbar);

        initList();
    }

    private void initList() {
        mRecoveryItem = new ArrayList<>();
        RecyclerView recoveryList = (RecyclerView) findViewById(R.id.recovery_list);
        mAdapter = new RecoveryItemAdapter();
        recoveryList.setLayoutManager(new LinearLayoutManager(this));
        recoveryList.setAdapter(mAdapter);

        IOManager.traverseFile(new Action1<List<File>>() {
            @Override
            public void call(List<File> files) {
                mRecoveryItem = files;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private class RecoveryItemAdapter extends RecyclerView.Adapter<RecoveryItemAdapter.RecoveryItemHolder> {

        @Override
        public RecoveryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recovery, parent, false);
            return new RecoveryItemHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecoveryItemHolder holder, int position) {
            final File file = mRecoveryItem.get(position);
            holder.titleTv.setText(mRecoveryItem.get(position).getName());
            holder.recoveryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOManager.recoveryData(RecoveryActivity.this, file.getPath());
                }
            });
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmDialog(holder.getAdapterPosition());
                }
            });
        }

        private void showDeleteConfirmDialog(final int position) {
            final CommonDialog dialog = new CommonDialog();
            CommonDialog.CommonBuilder  builder = new CommonDialog.CommonBuilder(RecoveryActivity.this);
            builder.setTitleText("确认删除该备份文件吗？")
                    .setLeftText(getString(R.string.cancel))
                    .setLeftListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    })
                    .setRightText(getString(R.string.confirm))
                    .setRightListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mRecoveryItem.get(position).delete()) {
                                mRecoveryItem.remove(position);
                                ToastUtil.showToast(getString(R.string.file_delete_success), Toast.LENGTH_SHORT);
                                notifyDataSetChanged();
                            }
                            dialog.dismiss();
                        }
                    });
            dialog.setBuilder(builder);
            dialog.show(getSupportFragmentManager(), "deleteConfirmDialog");
        }

        @Override
        public int getItemCount() {
            return mRecoveryItem.size();
        }

        class RecoveryItemHolder extends  RecyclerView.ViewHolder {
            TextView titleTv;
            TextView recoveryTv;
            View divider;
            AppCompatImageView deleteImg;

            RecoveryItemHolder(View itemView) {
                super(itemView);
                titleTv = (TextView) itemView.findViewById(R.id.recovery_item_tv);
                recoveryTv = (TextView) itemView.findViewById(R.id.recovery_item_check_box);
                divider = itemView.findViewById(R.id.divider);
                deleteImg = (AppCompatImageView) itemView.findViewById(R.id.delete_img);
            }
        }
    }
}
