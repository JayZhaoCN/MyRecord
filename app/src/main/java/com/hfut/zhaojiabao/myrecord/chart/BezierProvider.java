package com.hfut.zhaojiabao.myrecord.chart;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao 2017/9/15
 */

public class BezierProvider {

    //TODO 有待优化
    public static Path provideBezierPath(List<PointF> points, float a, float b) {
        Path path = new Path();

        List<PointF> controls = new ArrayList<>();

        for (int i = 0; i < points.size() - 1; i++) {
            if (i == 0) {
                controls.add(new PointF(points.get(0).x + a * (points.get(1).x - points.get(0).x), points.get(0).y + a * (points.get(1).y - points.get(0).y)));
                controls.add(new PointF(points.get(1).x - b * (points.get(2).x - points.get(0).x), points.get(1).y - b * (points.get(2).y - points.get(0).y)));
                continue;
            }

            if (i == points.size() - 2) {
                controls.add(new PointF(points.get(i).x + a * (points.get(i + 1).x - points.get(i - 1).x), points.get(i).y + a * (points.get(i + 1).y - points.get(i - 1).y)));
                controls.add(new PointF(points.get(i + 1).x - b * (points.get(i + 1).x - points.get(i).x), points.get(i + 1).y - b * (points.get(i + 1).y - points.get(i).y)));
                continue;
            }

            controls.add(new PointF(points.get(i).x + a * (points.get(i + 1).x - points.get(i - 1).x), points.get(i).y + a * (points.get(i + 1).y - points.get(i - 1).y)));
            controls.add(new PointF(points.get(i + 1).x - b * (points.get(i + 2).x - points.get(i).x), points.get(i + 1).y - b * (points.get(i + 2).y - points.get(i).y)));
        }

        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 0; i < points.size() - 1; i++) {
            path.cubicTo(controls.get(i * 2).x, controls.get(i * 2).y, controls.get(i * 2 + 1).x, controls.get(i * 2 + 1).y, points.get(i + 1).x, points.get(i + 1).y);
        }

        return path;
    }


}
