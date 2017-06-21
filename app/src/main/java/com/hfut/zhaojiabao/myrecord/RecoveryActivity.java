package com.hfut.zhaojiabao.myrecord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

        new TraverseTask(this).execute();
    }

    private class RecoveryItemAdapter extends RecyclerView.Adapter<RecoveryItemAdapter.RecoveryItemHolder> {

        @Override
        public RecoveryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recovery, parent, false);
            return new RecoveryItemHolder(view);
        }

        @Override
        public void onBindViewHolder(RecoveryItemHolder holder, int position) {
            holder.titleTv.setText(mRecoveryItem.get(position).toString());
            if (position == mRecoveryItem.size() - 1) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mRecoveryItem.size();
        }

        class RecoveryItemHolder extends  RecyclerView.ViewHolder {
            TextView titleTv;
            CheckBox checkBox;
            View divider;

            RecoveryItemHolder(View itemView) {
                super(itemView);
                titleTv = (TextView) itemView.findViewById(R.id.recovery_item_tv);
                checkBox = (CheckBox) itemView.findViewById(R.id.recovery_item_check_box);
                divider = itemView.findViewById(R.id.divider);
            }
        }
    }


    private static class TraverseTask extends AsyncTask<Void, Void, List<File>> {
        private WeakReference<RecoveryActivity> reference;

        TraverseTask(RecoveryActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        protected List<File> doInBackground(Void... params) {
            RecoveryActivity activity = reference.get();
            //如果Activity被回收了，再去读文件也没有意义了
            if (activity == null) {
                return null;
            }

            File file = Environment.getExternalStorageDirectory();
            File[] children = file.listFiles();
            List<File> files = new ArrayList<>();

            for (File child : children) {
                if (child.getPath().endsWith(".jay")) {
                    files.add(child);
                }
            }

            return files;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            RecoveryActivity activity = reference.get();
            //如果Activity被回收了，再去读文件也没有意义了
            if (activity == null) {
                return;
            }

            activity.mRecoveryItem = files;
            activity.mAdapter.notifyDataSetChanged();
        }
    }

}
