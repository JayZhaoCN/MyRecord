package com.hfut.zhaojiabao.myrecord.views;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
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
    }

    private void initViews() {
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mSubTitleTv = (TextView) findViewById(R.id.sub_title_tv);
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
}
