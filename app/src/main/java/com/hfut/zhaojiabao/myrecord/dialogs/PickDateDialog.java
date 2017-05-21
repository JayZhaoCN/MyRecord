package com.hfut.zhaojiabao.myrecord.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.hfut.zhaojiabao.myrecord.R;

import java.util.Calendar;

/**
 * @author zhaojiabao 2017/5/21
 */

public class PickDateDialog extends DialogFragment implements View.OnClickListener {

    private OnDatePickListener mListener;
    private String mYear, mMonth, mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_pick_date, container, false);

        v.findViewById(R.id.cancel_tv).setOnClickListener(this);
        v.findViewById(R.id.confirm_tv).setOnClickListener(this);

        initDatePicker(v);
        return v;
    }

    private void initDatePicker(View v) {
        int year;
        int month;
        int day;

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        mYear = String.valueOf(year).substring(2);
        mMonth = String.valueOf(month);
        mDay = String.valueOf(day);

        final DatePicker datePicker = (DatePicker) v.findViewById(R.id.date_picker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = String.valueOf(year).substring(2);
                mMonth = String.valueOf(monthOfYear);
                mDay = String.valueOf(dayOfMonth);
            }
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
        void onDatePick(String year, String month, String day);
    }
}
