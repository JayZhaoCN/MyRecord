package com.zhaojiabao.android.baseui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * commonDialog
 * @author zhaojiabao 2017/7/8
 */

public class CommonDialog extends DialogFragment {
    private Builder mBuilder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.dialog_common_dialog, container, false);
        init(content);
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return content;
    }

    private void init(View content) {
        FrameLayout contentContainer = (FrameLayout) content.findViewById(R.id.content_container);
        TextView titleTv = (TextView) content.findViewById(R.id.dialog_title_tv);
        TextView leftTv = (TextView) content.findViewById(R.id.left_tv);
        TextView rightTv = (TextView) content.findViewById(R.id.right_tv);
        TextView contentTv = (TextView) content.findViewById(R.id.content_tv);

        //是否可以取消
        setCancelable(mBuilder.cancelable);

        //标题文字
        if (TextUtils.isEmpty(mBuilder.titleText)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setText(mBuilder.titleText);
        }

        //左按钮
        if (TextUtils.isEmpty(mBuilder.leftText)) {
            leftTv.setVisibility(View.GONE);
        } else {
            leftTv.setText(mBuilder.leftText);
            leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder.leftListener.onClick(CommonDialog.this);
                }
            });
        }

        //右按钮
        if (TextUtils.isEmpty(mBuilder.rightText)) {
            rightTv.setVisibility(View.GONE);
        } else {
            rightTv.setText(mBuilder.rightText);
            rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder.rightListener.onClick(CommonDialog.this);
                }
            });
        }

        if (TextUtils.isEmpty(mBuilder.leftText) && TextUtils.isEmpty(mBuilder.rightText)) {
            content.findViewById(R.id.option_container).setVisibility(View.GONE);
        }

        //这里contentText的优先级是高于contentView的，也就是说，
        //如果同时设置了contentText和contentView，会展示contentText而不展示contentView.
        if (!TextUtils.isEmpty(mBuilder.contentText)) {
            contentContainer.setVisibility(View.GONE);
            contentTv.setVisibility(View.VISIBLE);
            contentTv.setText(mBuilder.contentText);
        } else if (mBuilder.content != null) {
            contentTv.setVisibility(View.GONE);
            if (mBuilder.content.getParent() != null) {
                ((ViewGroup) mBuilder.content.getParent()).removeView(content);
            }
            contentContainer.addView(mBuilder.content);
        } else {
            content.findViewById(R.id.root_container).setVisibility(View.GONE);
        }
    }

    public void setBuilder(Builder builder) {
        mBuilder = builder;
    }

    public static class Builder {
        String titleText;
        String leftText;
        String rightText;
        String contentText;
        public View content;
        boolean titleVisible = true;
        //Dialog默认是可以取消的
        boolean cancelable = true;

        private OnClickListener leftListener;
        private OnClickListener rightListener;

        private Context mContext;

        public Builder(Context context) {
            mContext = context;
        }

        @SuppressWarnings("unused")
        public Builder setTitleVisible(boolean visible) {
            titleVisible = visible;
            return this;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setTitleText(@StringRes int titleTextRes) {
            setTitleText(mContext.getString(titleTextRes));
            return this;
        }

        public Builder setLeftText(String leftText) {
            this.leftText = leftText;
            return this;
        }

        public Builder setLeftText(@StringRes int leftTextRes) {
            setLeftText(mContext.getString(leftTextRes));
            return this;
        }

        public Builder setRightText(String rightText) {
            this.rightText = rightText;
            return this;
        }

        public Builder setRightText(@StringRes int rightTextRes) {
            setRightText(mContext.getString(rightTextRes));
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setContentText(@StringRes int contentTextRes) {
            setContentText(mContext.getString(contentTextRes));
            return this;
        }

        public Builder setContent(View content) {
            this.content = content;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setLeftListener(OnClickListener leftListener) {
            this.leftListener = leftListener;
            return this;
        }

        public Builder setRightListener(OnClickListener rightListener) {
            this.rightListener = rightListener;
            return this;
        }

        public void show(FragmentManager manager) {
            CommonDialog dialog = new CommonDialog();
            dialog.setBuilder(this);
            dialog.show(manager, TextUtils.isEmpty(titleText) ? "commonDialog" : titleText);
        }

        @SuppressWarnings("unused")
        public CommonDialog create() {
            CommonDialog dialog = new CommonDialog();
            dialog.setBuilder(this);
            return dialog;
        }
    }

    public interface OnClickListener {
        void onClick(CommonDialog dialog);
    }
}
