package com.helloarbridge4.Point3F;


import androidx.annotation.NonNull;

import com.google.ar.sceneform.math.Vector3;
import com.helloarbridge4.Point3F.Point3F;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class PointFilter {
    private static final float POINT_CONFIDENCE_MIN = 0.7f;
    private static final Vector3 REGION_LIMITS = new Vector3(0.8f,0.8f,0.8f);

    public static List<Point3F> filterByConfidence(FloatBuffer pointBuffer) {
        List<Point3F> pointList = new ArrayList<>();

        if (pointBuffer == null) return new ArrayList<Point3F>();

        while (pointBuffer.hasRemaining()) {
            Point3F point = new Point3F(
                    pointBuffer.get(),
                    pointBuffer.get(),
                    pointBuffer.get(),
                    pointBuffer.get()
            );

            if (point.c > POINT_CONFIDENCE_MIN) {
                pointList.add(point);
            }
        }
        return pointList;
    }

    public static List<Point3F> filterByRegion (@NonNull List<Point3F> pointList) {
        List<Point3F> points = new ArrayList<>();
        for (Point3F point : pointList) {
            if (filterPointByRegion(point)) {
                points.add(point);
            }
        }
        return points;
    }

    private static boolean filterPointByRegion(@NonNull Point3F point) {
        float x = Math.abs(point.x);
        float y = Math.abs(point.y);
        float z = Math.abs(point.z);

        if (x > REGION_LIMITS.x) return false;
        if (y > REGION_LIMITS.y) return false;
        if (z > REGION_LIMITS.z) return false;

        return true;
    }

}
