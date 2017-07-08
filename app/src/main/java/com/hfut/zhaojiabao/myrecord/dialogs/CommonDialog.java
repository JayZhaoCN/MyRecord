package com.hfut.zhaojiabao.myrecord.dialogs;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/7/8
 */

public class CommonDialog extends DialogFragment {
    private static final String TAG = "CommonFragment";

    private LayoutInflater mInflater;
    private FrameLayout mContentContainer;
    private TextView mTitleTv;
    private TextView mLeftTv;
    private TextView mRightTv;

    private String mTitleText, mLeftText, mRightText;
    private int mContentRes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.dialog_common_dialog, container, false);
        mInflater = inflater;
        init(content);
        return content;
    }

    private void init(View content) {
        mContentContainer = (FrameLayout) content.findViewById(R.id.content_container);
        mTitleTv = (TextView) content.findViewById(R.id.dialog_title_tv);
        mLeftTv = (TextView) content.findViewById(R.id.left_tv);
        mRightTv = (TextView) content.findViewById(R.id.right_tv);

        mTitleTv.setText(mTitleText);
        mLeftTv.setText(mLeftText);
        mRightTv.setText(mRightText);
        mInflater.inflate(mContentRes, mContentContainer, true);
    }

    public ViewGroup setContent(@LayoutRes int layoutRes) {
        mContentRes = layoutRes;
        if (mInflater == null || mContentContainer == null) {
            return null;
        }
        return (ViewGroup) mInflater.inflate(layoutRes, mContentContainer, true);
    }

    public void setTitle(String title) {
        mTitleText = title;
        if (mTitleTv == null) {
            return;
        }
        mTitleTv.setText(title);
    }

    public void setRightTitle(String rightText) {
        mRightText = rightText;
        if (mRightTv == null) {
            return;
        }
        mRightTv.setText(rightText);
    }

    public void setLeftTitle(String leftText) {
        mLeftText = leftText;
        if (mLeftTv == null) {
            return;
        }
        mLeftTv.setText(leftText);
    }
}
