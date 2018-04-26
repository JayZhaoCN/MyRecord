package com.zhaojiabao.android.baseui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
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
    private CommonBuilder mBuilder;

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

        setCancelable(mBuilder.cancelable);
        if (!mBuilder.titleVisible) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setText(mBuilder.titleText);
        }
        if (!mBuilder.leftTextVisible) {
            leftTv.setVisibility(View.GONE);
        } else {
            leftTv.setText(mBuilder.leftText);
            leftTv.setOnClickListener(mBuilder.leftListener);
        }
        if (!mBuilder.rightTextVisible) {
            rightTv.setVisibility(View.GONE);
        } else {
            rightTv.setText(mBuilder.rightText);
            rightTv.setOnClickListener(mBuilder.rightListener);
        }
        if (!mBuilder.leftTextVisible && !mBuilder.rightTextVisible) {
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

    public void setBuilder(CommonBuilder builder) {
        mBuilder = builder;
    }

    public static class CommonBuilder {
        String titleText;
        String leftText;
        String rightText;
        String contentText;
        public View content;
        boolean titleVisible = true;
        boolean leftTextVisible = true;
        boolean rightTextVisible = true;
        //Dialog默认是可以取消的
        boolean cancelable = true;

        private View.OnClickListener leftListener;
        private View.OnClickListener rightListener;

        private Context mContext;

        public static void foo() {

        }

        public CommonBuilder(Context context) {
            mContext = context;
        }

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
        public CommonBuilder setContentText(@StringRes int contentTextRes) {
            setContentText(mContext.getString(contentTextRes));
            return this;
        }

        public CommonBuilder setContent(View content) {
            this.content = content;
            return this;
        }

        public CommonBuilder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
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
