package com.hfut.zhaojiabao.myrecord.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/8/21
 */

public class JayItemView extends FrameLayout {
    private Context mContext;

    private TextView mTitleTv;
    private TextView mSubTitleTv;
    private TextView mSummaryTv;

    private View mDivider;

    public JayItemView(Context context) {
        this(context, null);
    }

    public JayItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JayItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(mContext = context, R.layout.jay_item_view, this);
        initViews();
        obtainAttrs(attrs, defStyleAttr);
    }

    private void initViews() {
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mSubTitleTv = (TextView) findViewById(R.id.sub_title_tv);
        mSummaryTv = (TextView) findViewById(R.id.summary_tv);

        mDivider = findViewById(R.id.divider);
    }

    private void obtainAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.JayItemView, defStyleAttr, 0);
        mTitleTv.setText(ta.getString(R.styleable.JayItemView_itemTitle));
        mSubTitleTv.setText(ta.getString(R.styleable.JayItemView_itemSubTitle));
        mSummaryTv.setText(ta.getString(R.styleable.JayItemView_itemSummary));
        ta.recycle();
    }

    public void setTitle(String titleStr) {
        mTitleTv.setText(titleStr);
    }

    public void setTitle(@StringRes int titleRes) {
        setTitle(mContext.getString(titleRes));
    }

    public void setSubTitle(String subTitleStr) {
        mSubTitleTv.setText(subTitleStr);
    }

    public void setSubTitle(@StringRes int subTitleRes) {
        setSubTitle(mContext.getString(subTitleRes));
    }

    public void setSummaryText(String summaryStr) {
        mSummaryTv.setText(summaryStr);
    }

    public void setSummaryText(@StringRes int summaryRes) {
        setSummaryText(mContext.getString(summaryRes));
    }

    public void setDividerVisible(boolean visible) {
        mDivider.setVisibility(visible ? VISIBLE : GONE);
    }
}
