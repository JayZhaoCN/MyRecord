package com.hfut.zhaojiabao.myrecord.dialogs;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.hfut.zhaojiabao.myrecord.R;

/**
 * @author zhaojiabao 2017/5/21
 */

public class PickTimeDialog extends DialogFragment implements View.OnClickListener {

    private int mHour, mMinute;

    private OnTimePickListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pick_time_dialog, container, false);

        v.findViewById(R.id.cancel_tv).setOnClickListener(this);
        v.findViewById(R.id.confirm_tv).setOnClickListener(this);

        initTimePicker(v);

        return v;
    }

    private void initTimePicker(View v) {
        TimePicker timePicker = (TimePicker) v.findViewById(R.id.time_picker);

        mHour = timePicker.getCurrentHour();
        mMinute = timePicker.getCurrentMinute();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
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
                    mListener.onTimePick(mHour, mMinute);
                }
                dismiss();
                break;
        }
    }

    public void setOnTimePickListener(OnTimePickListener listener) {
        mListener = listener;
    }

    public interface OnTimePickListener {
        void onTimePick(int hour, int minute);
    }
}
