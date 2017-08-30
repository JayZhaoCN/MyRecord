package com.hfut.zhaojiabao.myrecord.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hfut.zhaojiabao.myrecord.utils.NumberUtils;
import com.hfut.zhaojiabao.myrecord.R;
import com.hfut.zhaojiabao.myrecord.calculator.ArithmeticHelper;
import com.hfut.zhaojiabao.myrecord.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.screen_tv) TextView mScreenTv;
    private String mScreenContentStr = "";
    private boolean mCanInputDot = true;
    private boolean mCanInputOperator = true;
    //是否可以输入'-'
    private boolean mCanInputSub = true;
    private boolean mHasResult = false;
    private double mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        ButterKnife.bind(this);
        initToolbar();
        initUI();
    }

    private void initUI() {
        findViewById(R.id.num_0).setOnClickListener(this);
        findViewById(R.id.num_1).setOnClickListener(this);
        findViewById(R.id.num_2).setOnClickListener(this);
        findViewById(R.id.num_3).setOnClickListener(this);
        findViewById(R.id.num_4).setOnClickListener(this);
        findViewById(R.id.num_5).setOnClickListener(this);
        findViewById(R.id.num_6).setOnClickListener(this);
        findViewById(R.id.num_7).setOnClickListener(this);
        findViewById(R.id.num_8).setOnClickListener(this);
        findViewById(R.id.num_9).setOnClickListener(this);
        findViewById(R.id.equal).setOnClickListener(this);
        findViewById(R.id.num_dot).setOnClickListener(this);
        findViewById(R.id.operator_add).setOnClickListener(this);
        findViewById(R.id.operator_multi).setOnClickListener(this);
        findViewById(R.id.operator_division).setOnClickListener(this);
        findViewById(R.id.operator_subtraction).setOnClickListener(this);

        //control UI
        findViewById(R.id.clean_btn).setOnClickListener(this);
        findViewById(R.id.delete_btn).setOnClickListener(this);
        findViewById(R.id.confirm_btn).setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("计算器");
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.num_0:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "0";
                break;
            case R.id.num_1:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "1";
                break;
            case R.id.num_2:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "2";
                break;
            case R.id.num_3:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "3";
                break;
            case R.id.num_4:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "4";
                break;
            case R.id.num_5:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "5";
                break;
            case R.id.num_6:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "6";
                break;
            case R.id.num_7:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "7";
                break;
            case R.id.num_8:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "8";
                break;
            case R.id.num_9:
                mCanInputOperator = true;
                mCanInputSub = true;
                if (mHasResult) {
                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += "9";
                break;
            case R.id.operator_add:
                if (!mCanInputOperator) {
                    return;
                }
                mHasResult = false;
                mCanInputOperator = false;
                mScreenContentStr += "+";
                mCanInputDot = true;
                mCanInputSub = true;
                break;
            case R.id.operator_subtraction:
                if (!mCanInputSub) {
                    return;
                }
                mHasResult = false;
                mCanInputOperator = false;
                mScreenContentStr += "-";
                mCanInputDot = true;

                int length = mScreenContentStr.length();
                if (length == 1) {
                    mCanInputSub = false;
                    break;
                }
                char aChar = mScreenContentStr.charAt(length - 2);
                mCanInputSub = !(ArithmeticHelper.isOperator(aChar) && aChar != '-');

                break;
            case R.id.operator_multi:
                if (!mCanInputOperator) {
                    return;
                }
                mHasResult = false;
                mCanInputOperator = false;
                mScreenContentStr += "*";
                mCanInputDot = true;
                mCanInputSub = true;
                break;
            case R.id.operator_division:
                if (!mCanInputOperator) {
                    return;
                }
                mHasResult = false;
                mCanInputOperator = false;
                mScreenContentStr += "/";
                mCanInputDot = true;
                mCanInputSub = true;
                break;
            case R.id.num_dot:
                if (!mCanInputDot) {
                    return;
                }
                if (mScreenContentStr.length() != 0 && mScreenContentStr.charAt(mScreenContentStr.length() - 1) == '.') {
                    return;
                }
                if (mHasResult) {

                    mScreenContentStr = "";
                    mHasResult = false;
                }
                mScreenContentStr += ".";
                mCanInputDot = false;
                mCanInputOperator = false;
                mCanInputSub = false;
                break;
            case R.id.equal:
                try {
                    mResult = ArithmeticHelper.calculate(mScreenContentStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast("输入有误", Toast.LENGTH_SHORT);
                    mScreenContentStr = "";
                    mScreenTv.setText(mScreenContentStr);
                    mResult = 0;
                    break;
                }
                mScreenContentStr = NumberUtils.getFormattedNumber((float) mResult);
                mHasResult = true;
                break;
            case R.id.clean_btn:
                mScreenContentStr = "";
                break;
            case R.id.delete_btn:
                if (mScreenContentStr == null || mScreenContentStr.length() == 0) {
                    return;
                }
                mScreenContentStr = mScreenContentStr.substring(0, mScreenContentStr.length() - 1);
                break;
            case R.id.confirm_btn:
                if (mHasResult) {
                    Intent intent = new Intent();
                    intent.putExtra("result", mResult);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                mHasResult = false;
                break;
            default:
                break;
        }
        mScreenTv.setText(mScreenContentStr);
    }
}
