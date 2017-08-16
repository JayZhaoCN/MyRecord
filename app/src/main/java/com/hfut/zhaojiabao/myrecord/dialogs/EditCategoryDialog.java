package com.hfut.zhaojiabao.myrecord.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hfut.zhaojiabao.JayDaoManager;
import com.hfut.zhaojiabao.database.Category;
import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/5/20
 */

public class EditCategoryDialog extends DialogFragment implements View.OnClickListener {

    private EditText mAddEdit;
    private OnCategoryAddedListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_dialog, container, false);

        init(v);

        return v;
    }

    private void init(View v) {
        v.findViewById(R.id.cancel_tv).setOnClickListener(this);
        v.findViewById(R.id.confirm_tv).setOnClickListener(this);

        mAddEdit = (EditText) v.findViewById(R.id.add_edit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv:
                dismiss();
                break;
            case R.id.confirm_tv:
                addCategory();
                if (mListener != null) {
                    mListener.onCategoryAdded();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    private void addCategory() {
        Category category = new Category();
        category.setCategory(mAddEdit.getText().toString());

        JayDaoManager.getInstance().getDaoSession().getCategoryDao().insert(category);
    }

    public void setOnCategoryAddedListener(OnCategoryAddedListener listener) {
        mListener = listener;
    }

    interface OnCategoryAddedListener {
        void onCategoryAdded();
    }
}
