package com.helloarbridge4.Point3F;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.ar.sceneform.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class PointFilter {
    private static final float Z_THRESH = 0.2f;
    private static final float POINT_CONFIDENCE_MIN = 0.5f;
    private static final Vector3 REGION_LIMITS = new Vector3(0.5f,0.5f,0.5f);
    private static final String TAG = "PointFilter";

    public static boolean filterSingleByConfidence(Point3F point) {
        if (point == null) throw new NullPointerException();

        return  (point.c > POINT_CONFIDENCE_MIN);
    }

    public static ArrayList<Point3F> getValidPoints(FloatBuffer pointBuffer, Vector3 node) {

        List<Point3F> confPoints = filterByConfidence(pointBuffer);

        ArrayList<Point3F> closePoints = filterByRegion(confPoints, node);
//        Log.d(TAG,"getValidPoints");
        for (Point3F p : closePoints) {
            Log.d(TAG, p.toString());
        }
        ArrayList<Point3F> filteredPoints = filterGround(closePoints,node);
        return filteredPoints;
    }

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

    public static ArrayList<Point3F> filterGround(ArrayList<Point3F> pointList, Vector3 node) {
        if (pointList == null) return new ArrayList<Point3F>();
        ArrayList<Point3F> filteredList = new ArrayList<>();

        for (Point3F point : pointList) {
            if (point.z > Z_THRESH + node.z) {
                filteredList.add(point);
            }
        }
        return  filteredList;
    }

    public static ArrayList<Point3F> filterByRegion (@NonNull List<Point3F> pointList,Vector3 node) {
        ArrayList<Point3F> points = new ArrayList<>();
        for (Point3F point : pointList) {
            if (filterPointByRegion(point, node)) {
                points.add(point);
            }
        }
        return points;
    }

    private static boolean filterPointByRegion(@NonNull Point3F point, Vector3 referencePoint) {

        Float difX = (Float) Math.abs(point.x - referencePoint.x);
        Float difY = (Float) Math.abs(point.y - referencePoint.y);
        Float difZ = (Float) Math.abs(point.z - referencePoint.z);

        if (difX > REGION_LIMITS.x) return false;
        if (difY > REGION_LIMITS.y) return false;
        if (difZ > REGION_LIMITS.z) return false;

        return true;
    }

}
