package com.hfut.zhaojiabao.myrecord.dialogs;


import android.app.DialogFragment;
import android.os.Bundle;
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

    private CommonBuilder mBuilder;

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

        if (!mBuilder.titleVisible) {
            mTitleTv.setVisibility(View.GONE);
        } else {
            mTitleTv.setText(mBuilder.titleText);
        }
        if (!mBuilder.leftTextVisible) {
            mLeftTv.setVisibility(View.GONE);
        } else {
            mLeftTv.setText(mBuilder.leftText);
            mLeftTv.setOnClickListener(mBuilder.leftListener);
        }
        if (!mBuilder.rightTextVisible) {
            mRightTv.setVisibility(View.GONE);
        } else {
            mRightTv.setText(mBuilder.rightText);
            mRightTv.setOnClickListener(mBuilder.rightListener);
        }
        if (mBuilder.content != null) {
            if (mBuilder.content.getParent() != null) {
                ((ViewGroup) mBuilder.content.getParent()).removeView(content);
            }
            mContentContainer.addView(mBuilder.content);
        }
    }

    public void setBuilder(CommonBuilder builder) {
        mBuilder = builder;
    }

    public static class CommonBuilder {
        public String titleText;
        public String leftText;
        public String rightText;
        public View content;
        public CommonDialog dialog;
        public boolean titleVisible = true;
        public boolean leftTextVisible = true;
        public boolean rightTextVisible = true;

        private View.OnClickListener leftListener;
        private View.OnClickListener rightListener;

        public CommonBuilder setTitleVisible(boolean visible) {
            titleVisible = visible;
            return this;
        }

        public CommonBuilder setLeftTextVisible(boolean visible) {
            leftTextVisible = visible;
            return this;
        }

        public CommonBuilder setRightTextVisible(boolean visible) {
            rightTextVisible = visible;
            return this;
        }

        public CommonBuilder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public CommonBuilder setLeftText(String leftText) {
            this.leftText = leftText;
            return this;
        }

        public CommonBuilder setRightText(String rightText) {
            this.rightText = rightText;
            return this;
        }

        public CommonBuilder setContent(View content) {
            this.content = content;
            return this;
        }

        public CommonBuilder setLeftListener(View.OnClickListener leftListener) {
            this.leftListener = leftListener;
            return this;
        }

        public CommonBuilder setRightListener(View.OnClickListener rightListener) {
            this.rightListener = rightListener;
            return this;
        }
    }
}
