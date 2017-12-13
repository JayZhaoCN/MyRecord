package com.hfut.zhaojiabao.myrecord.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;

import com.hfut.zhaojiabao.myrecord.R;

import java.util.Calendar;

/**
 * @author zhaojiabao 2017/5/21
 */

public class PickDateDialog extends DialogFragment implements View.OnClickListener {

    private OnDatePickListener mListener;
    private int mYear, mMonth, mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_pick_date, container, false);

        v.findViewById(R.id.cancel_tv).setOnClickListener(this);
        v.findViewById(R.id.confirm_tv).setOnClickListener(this);

        initDatePicker(v);
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return v;
    }

    private void initDatePicker(View v) {

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePicker datePicker = (DatePicker) v.findViewById(R.id.date_picker);
        datePicker.init(mYear, mMonth, mDay, (view, year, monthOfYear, dayOfMonth) -> {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv:
                dismiss();
                break;
            case R.id.confirm_tv:
                if(mListener != null) {
                    mListener.onDatePick(mYear, mMonth, mDay);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setOnDatePickListener(OnDatePickListener listener) {
        mListener = listener;
    }

    public interface OnDatePickListener {
        void onDatePick(int year, int month, int day);
    }
}
