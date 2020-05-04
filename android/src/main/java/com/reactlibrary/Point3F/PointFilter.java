package com.reactlibrary.Point3F;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class PointFilter {
    private static final float POINT_CONFIDENCE_MIN = 0.5f;

    private static final float GROUND_THRESH_LIMIT = 0.05f;
    private static final float REGION_LIMITS = 0.5f;

    private static final String TAG = "POINT_FILTER";

    public static ArrayList<Point3F> filterPoints(ArrayList<Point3F> arrayList, Vector3 nodePosition) {
        assert (arrayList != null && nodePosition != null);

        ArrayList<Point3F> groundRemoved = PointFilter.filterGround(arrayList, nodePosition);
        ArrayList<Point3F> closePoints = PointFilter.filterByRegion(groundRemoved,nodePosition);
        ArrayList<Point3F> confPoints = PointFilter.filterByConfidence(closePoints);
        Log.d(TAG,"filterPoints: " + groundRemoved.size() + " "  + closePoints.size() + " " + confPoints.size());

        return confPoints;
    }


    public static ArrayList<Point3F> convertBufferToList(FloatBuffer pointBuffer) {
        ArrayList<Point3F> pointList = new ArrayList<>();

        if (pointBuffer == null) return new ArrayList<Point3F>();

        for (int i = 0; i < pointBuffer.remaining(); i+=4) {
                pointList.add(
                        new Point3F(
                                pointBuffer.get(i),
                                pointBuffer.get(i+1),
                                pointBuffer.get(i+2),
                                pointBuffer.get(i+3)
                        )
                );
        }
        return pointList;
    }

    public static ArrayList<Point3F> convertCloudToArrayList(@NonNull PointCloud pointCloud) {
        ArrayList<Point3F> pointList = new ArrayList<>();
        FloatBuffer pointBuffer = pointCloud.getPoints();
        while (pointBuffer.hasRemaining()) {
            pointList.add(
                    new Point3F(
                            pointBuffer.get(),
                            pointBuffer.get(),
                            pointBuffer.get(),
                            pointBuffer.get()
                    )
            );
        }
        return pointList;
    }


    public static ArrayList<Point3F> filterByConfidence(ArrayList<Point3F> pointList) {
        ArrayList<Point3F> filteredList = new ArrayList<>();

        if (pointList == null) return new ArrayList<Point3F>();

        for (Point3F p : pointList) {
            if (p.c > POINT_CONFIDENCE_MIN) {
                filteredList.add(p);
            }
        }

        return filteredList;
    }


    public static ArrayList<Point3F> filterByRegion (@NonNull ArrayList<Point3F> pointList,Vector3 referencePoint) {
        ArrayList<Point3F> points = new ArrayList<>();
        for (Point3F point : pointList) {
            if (filterPointByRegion(point, referencePoint)) {
                points.add(point);
            }
        }
        return points;
    }

    private static boolean filterPointByRegion(@NonNull Point3F point, Vector3 referencePoint) {

        Float difX = Math.abs(point.x - referencePoint.x);
        Float difZ = Math.abs(point.z - referencePoint.z);

        float dist = (float) Math.sqrt(difX*difX + difZ*difZ);

        return (dist < REGION_LIMITS);
    }

    public static ArrayList<Point3F> filterGround(ArrayList<Point3F> pointList, Vector3 anchorNodePosition) {
        ArrayList<Point3F> filteredList = new ArrayList<>();
        if (pointList == null) return filteredList;

        for (Point3F point : pointList) {
            if ((point.y - anchorNodePosition.y) >= GROUND_THRESH_LIMIT) {
                filteredList.add(point);
            }
        }
        return  filteredList;
    }


    private static ArrayList<Point3F> convertToPointList(FloatBuffer floatBuffer) {
        ArrayList<Point3F> pointList = new ArrayList<>();
        if (floatBuffer == null) return pointList;

        for (int i = 0; i < floatBuffer.limit(); i+=4) {
            pointList.add(
                    new Point3F(
                            floatBuffer.get(i),
                            floatBuffer.get(i),
                            floatBuffer.get(i),
                            floatBuffer.get(i)
                    )
            );
        }
        return pointList;
    }

    public static float getPointConfidenceMin() {
        return POINT_CONFIDENCE_MIN;
    }
}
