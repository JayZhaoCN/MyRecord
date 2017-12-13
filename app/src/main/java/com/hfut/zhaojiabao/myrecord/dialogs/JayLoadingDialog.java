package com.hfut.zhaojiabao.myrecord.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/7/5
 */

public class JayLoadingDialog extends DialogFragment {
    private static final int STATE_LOADING = 0;
    private static final int STATE_ERROR = 1;
    private static final int STATE_SUCCESS = 2;

    private int mState = STATE_LOADING;

    private ProgressBar mLoadingPb;
    private TextView mLoadingTipsTv;
    private ImageView mLoadingImg;

    private String mLoadingTips = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_jay_loading, container, false);
        initViews(v);
        updateViews();
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return v;
    }

    private void initViews(View v) {
        mLoadingPb = (ProgressBar) v.findViewById(R.id.loading_pb);
        mLoadingTipsTv = (TextView) v.findViewById(R.id.loading_tips_tv);
        mLoadingImg = (ImageView) v.findViewById(R.id.loading_img);
    }

    private void updateViews() {
        mLoadingTipsTv.setText(mLoadingTips);
        switch (mState) {
            case STATE_ERROR:
                showErrorInternal();
                break;
            case STATE_LOADING:
                showLoadingInternal();
                break;
            case STATE_SUCCESS:
                showSuccessInternal();
                break;
            default:
                break;
        }
    }

    public void setLoadingTips(String tips) {
        mLoadingTips = tips;
        if (mLoadingTipsTv != null) {
            mLoadingTipsTv.setText(mLoadingTips);
        }
    }

    public void setLoadingTips(@StringRes int tipsRes) {
        mLoadingTips = getString(tipsRes);
        if (mLoadingTipsTv != null) {
            mLoadingTipsTv.setText(mLoadingTips);
        }
    }

    private void showErrorInternal() {
        mLoadingTipsTv.setText(mLoadingTips);
        if (mLoadingImg == null || mLoadingPb == null) {
            return;
        }
        mLoadingImg.setVisibility(View.VISIBLE);
        mLoadingPb.setVisibility(View.GONE);
        mLoadingImg.setImageResource(R.drawable.loading_error);
    }

    public void showError(String errorTips) {
        setLoadingTips(errorTips);
        mState = STATE_ERROR;
        if (mLoadingImg == null || mLoadingPb == null) {
            return;
        }
        mLoadingImg.setVisibility(View.VISIBLE);
        mLoadingPb.setVisibility(View.GONE);
        mLoadingImg.setImageResource(R.drawable.loading_error);
    }

    private void showLoadingInternal() {
        if (mLoadingImg == null || mLoadingPb == null) {
            return;
        }
        mLoadingImg.setVisibility(View.GONE);
        mLoadingPb.setVisibility(View.VISIBLE);
    }

    public void showLoading(String loadingTips) {
        setLoadingTips(loadingTips);
        mState = STATE_LOADING;
        if (mLoadingImg == null || mLoadingPb == null) {
            return;
        }
        mLoadingImg.setVisibility(View.GONE);
        mLoadingPb.setVisibility(View.VISIBLE);
    }

    private void showSuccessInternal() {
        if (mLoadingImg == null || mLoadingPb == null) {
            return;
        }
        mLoadingImg.setVisibility(View.VISIBLE);
        mLoadingPb.setVisibility(View.GONE);
        mLoadingImg.setImageResource(R.drawable.loading_done);
    }

    public void showSuccess(String successTips) {
        setLoadingTips(successTips);
        mState = STATE_SUCCESS;
        if (mLoadingImg == null || mLoadingPb == null) {
            return;
        }
        mLoadingImg.setVisibility(View.VISIBLE);
        mLoadingPb.setVisibility(View.GONE);
        mLoadingImg.setImageResource(R.drawable.loading_done);
    }

    public void delayClose(long delayMillis) {
        mLoadingImg.postDelayed(() -> dismiss(), delayMillis);
    }
}