package com.hfut.zhaojiabao.myrecord.dialogs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
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
    private TextView mContentTv;

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
        mContentTv = (TextView) content.findViewById(R.id.content_tv);

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
        //这里contentText的优先级是高于contentView的，也就是说，
        //如果同时设置了contentText和contentView，会展示contentText而不展示contentView.
        if (!TextUtils.isEmpty(mBuilder.contentText)) {
            mContentTv.setVisibility(View.VISIBLE);
            mContentTv.setText(mBuilder.contentText);
        } else {
            mContentTv.setVisibility(View.GONE);
            if (mBuilder.content != null) {
                if (mBuilder.content.getParent() != null) {
                    ((ViewGroup) mBuilder.content.getParent()).removeView(content);
                }
                mContentContainer.addView(mBuilder.content);
            }
        }
    }

    public void setBuilder(CommonBuilder builder) {
        mBuilder = builder;
    }

    public static class CommonBuilder {
        public String titleText;
        public String leftText;
        public String rightText;
        public String contentText;
        public View content;
        public CommonDialog dialog;
        public boolean titleVisible = true;
        public boolean leftTextVisible = true;
        public boolean rightTextVisible = true;

        private View.OnClickListener leftListener;
        private View.OnClickListener rightListener;

        private Context mContext;

        public CommonBuilder(Context context) {
            mContext = context;
        }

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

        public CommonBuilder setTitleText(@StringRes int titleTextRes) {
            setTitleText(mContext.getString(titleTextRes));
            return this;
        }

        public CommonBuilder setLeftText(String leftText) {
            this.leftText = leftText;
            return this;
        }

        public CommonBuilder setLeftText(@StringRes int leftTextRes) {
            setLeftText(mContext.getString(leftTextRes));
            return this;
        }

        public CommonBuilder setRightText(String rightText) {
            this.rightText = rightText;
            return this;
        }

        public CommonBuilder setRightText(@StringRes int rightTextRes) {
            setRightText(mContext.getString(rightTextRes));
            return this;
        }

        public CommonBuilder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public CommonBuilder setContentText(@StringRes int contentTextRes) {
            setContentText(mContext.getString(contentTextRes));
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
