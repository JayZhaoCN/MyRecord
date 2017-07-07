package com.hfut.zhaojiabao.myrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hfut.zhaojiabao.myrecord.calculator.ArithmeticHelper;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mScreenTv;
    private String mScreenContentStr = "";
    private boolean mCanInputDot = true;
    private boolean mCanInputOperator = true;
    private boolean mHasResult = false;
    private double mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

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

        mScreenTv = (TextView) findViewById(R.id.screen_tv);
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
                mScreenContentStr += "0";
                break;
            case R.id.num_1:
                mCanInputOperator = true;
                mScreenContentStr += "1";
                break;
            case R.id.num_2:
                mCanInputOperator = true;
                mScreenContentStr += "2";
                break;
            case R.id.num_3:
                mCanInputOperator = true;
                mScreenContentStr += "3";
                break;
            case R.id.num_4:
                mCanInputOperator = true;
                mScreenContentStr += "4";
                break;
            case R.id.num_5:
                mScreenContentStr += "5";
                break;
            case R.id.num_6:
                mCanInputOperator = true;
                mScreenContentStr += "6";
                break;
            case R.id.num_7:
                mCanInputOperator = true;
                mScreenContentStr += "7";
                break;
            case R.id.num_8:
                mCanInputOperator = true;
                mScreenContentStr += "8";
                break;
            case R.id.num_9:
                mCanInputOperator = true;
                mScreenContentStr += "9";
                break;
            case R.id.operator_add:
                if (!mCanInputOperator) {
                    return;
                }
                mCanInputOperator = false;
                mScreenContentStr += "+";
                mCanInputDot = true;
                break;
            case R.id.operator_subtraction:
                if (!mCanInputOperator) {
                    return;
                }
                mCanInputOperator = false;
                mScreenContentStr += "-";
                mCanInputDot = true;
                break;
            case R.id.operator_multi:
                if (!mCanInputOperator) {
                    return;
                }
                mCanInputOperator = false;
                mScreenContentStr += "*";
                mCanInputDot = true;
                break;
            case R.id.operator_division:
                if (!mCanInputOperator) {
                    return;
                }
                mCanInputOperator = false;
                mScreenContentStr += "/";
                mCanInputDot = true;
                break;
            case R.id.num_dot:
                if (!mCanInputDot) {
                    return;
                }
                if (mScreenContentStr.length() != 0 && mScreenContentStr.charAt(mScreenContentStr.length() - 1) == '.') {
                    return;
                }
                mScreenContentStr += ".";
                mCanInputDot = false;
                break;
            case R.id.equal:
                mResult = ArithmeticHelper.calculate(mScreenContentStr);
                mScreenTv.setText(String.valueOf(mResult));
                mScreenContentStr = String.valueOf(mResult);
                mHasResult = true;
                break;
            case R.id.clean_btn:
                mScreenContentStr = "";
                break;
            case R.id.delete_btn:
                mScreenContentStr = mScreenContentStr.substring(0, mScreenContentStr.length() - 1);
                break;
            case R.id.confirm_btn:
                if (mHasResult) {
                    Intent intent = new Intent();
                    intent.putExtra("result", mResult);
                    setResult(0, intent);
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
