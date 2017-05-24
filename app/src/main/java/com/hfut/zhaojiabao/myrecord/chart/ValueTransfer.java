package com.hfut.zhaojiabao.myrecord.chart;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao 2017/5/24
 */

public class ValueTransfer {
    public static List<Float> transform(List<RecordChart.ChartItem> data, float height) {

        List<Float> output = new ArrayList<>();

        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;

        RecordChart.ChartItem item;
        for (int i = 0; i < data.size(); i++) {
            item = data.get(i);
            if (item.value > max) {
                max = item.value;
            }
            if (item.value < min) {
                min = item.value;
            }
        }

        Log.i("JayTest", "max: " + max + " min: " + min);

        float sub = max - min;

        for (int i = 0; i < data.size(); i++) {
            item = data.get(i);
            float x = (item.value - min) / sub;
            //+30是为了给一个最低高度。
            data.get(i).value = (float) (height * Math.sin(Math.PI / 2 * x)) + 30;
            //data.get(i).value = (float) 0.01 * height;
            Log.i("JayTest", "value: " + data.get(i).value);
        }

        return output;
    }
}
