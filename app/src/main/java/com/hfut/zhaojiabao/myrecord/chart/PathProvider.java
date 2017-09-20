package com.hfut.zhaojiabao.myrecord.chart;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaojiabao 2017/9/15
 */

public class PathProvider {

    @SuppressWarnings("unused")
    @Deprecated
    public static Path provideBezierPath(List<PointF> points, float a, float b) {
        if (points.size() <= 1) {
            return null;
        }
        Path path = new Path();
        if (points.size() == 2) {
            path.moveTo(points.get(0).x, points.get(0).y);
            path.lineTo(points.get(1).x, points.get(1).y);
            return path;
        }

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

    /**
     * 该方法提供的Path是有极值点的
     */
    public static Path provideBezierPathNew(List<PointF> points, float s) {
        if (points.size() <= 1) {
            return null;
        }

        Path path = new Path();

        List<PointF> controls = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                //第一个点
                //后一个控制点
                PointF point2 = new PointF();
                point2.x = points.get(0).x + (points.get(1).x - points.get(0).x) * s;
                point2.y = points.get(0).y;
                controls.add(point2);
            } else if (i == points.size() - 1) {
                //最后一个点
                //前一个控制点
                PointF point1 = new PointF();
                point1.x = points.get(i).x - (points.get(i).x - points.get(i - 1).x) * s;
                point1.y = points.get(i).y;
                controls.add(point1);
            } else if ((points.get(i - 1).y < points.get(i).y && points.get(i + 1).y < points.get(i).y) ||
                    (points.get(i - 1).y > points.get(i).y && points.get(i + 1).y > points.get(i).y)) {

                System.out.println("JayLog, 第" + (i + 1) + "个点是极值点");

                //极值点
                //前一个控制点
                PointF point1 = new PointF();
                point1.x = points.get(i).x - (points.get(i).x - points.get(i - 1).x) * s;
                point1.y = points.get(i).y;
                controls.add(point1);
                //后一个控制点
                PointF point2 = new PointF();
                point2.x = points.get(i).x + (points.get(i + 1).x - points.get(i).x) * s;
                point2.y = points.get(i).y;
                controls.add(point2);
            } else {
                System.out.println("JayLog, 第" + (i + 1) + "个点是非极值点");
                //非极值点
                float k = (points.get(i + 1).y - points.get(i - 1).y) / (points.get(i + 1).x - points.get(i - 1).x);
                float b = points.get(i).y - k * points.get(i).x;
                PointF point1 = new PointF();
                point1.x = points.get(i).x - (points.get(i).x - (points.get(i - 1).y - b) / k) * s;
                point1.y = point1.x * k + b;
                controls.add(point1);
                PointF point2 = new PointF();
                point2.x = points.get(i).x + (points.get(i + 1).x - points.get(i).x) * s;
                point2.y = point1.x * k + b;
                controls.add(point2);
            }
        }

        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 0; i < points.size() - 1; i++) {
            path.cubicTo(controls.get(i * 2).x, controls.get(i * 2).y, controls.get(i * 2 + 1).x, controls.get(i * 2 + 1).y, points.get(i + 1).x, points.get(i + 1).y);
        }

        return path;
    }

    //TODO 所有写死的数字需要用常量表示，并且注明含义
    public static List<Path> provideLabelPath(List<PointF> points, List<Integer> labelWidths) {
        if (points.size() <= 1) {
            return null;
        }

        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            int labelWidth = (labelWidths.get(i) - 30) / 2;

            PointF point = points.get(i);
            Path path = new Path();
            path.moveTo(point.x, point.y);
            path.lineTo(point.x + 15, point.y - 20);
            path.lineTo(point.x + 15 + labelWidth, point.y - 20);
            path.lineTo(point.x + 15 + labelWidth, point.y - 20 - 35);
            path.lineTo(point.x - 15 - labelWidth, point.y - 20 - 35);
            path.lineTo(point.x - 15 - labelWidth, point.y - 20);
            path.lineTo(point.x - 15, point.y - 20);
            path.close();

            paths.add(path);
        }
        return paths;
    }
}
