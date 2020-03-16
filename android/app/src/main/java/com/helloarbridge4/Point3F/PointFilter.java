package com.helloarbridge4.Point3F;

import androidx.annotation.NonNull;

import com.google.ar.core.Pose;
import com.google.ar.sceneform.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public class PointFilter {
    private static final float POINT_CONFIDENCE_MIN = 0.7f;

    private static final float GROUND_THRESH_LIMIT = 0.05f;
    private static final float REGION_LIMITS = 0.5f;
    private static final float CAMERA_DISTANCE_LIMIT = 0.5f;

    public static ArrayList<Point3F> getValidPoints(FloatBuffer pointBuffer, Vector3 node, Pose planePose) {
    //parent method to filter FloatBuffer of raw point data
        ArrayList<Point3F> confPoints = filterByConfidence(pointBuffer);
        ArrayList<Point3F> closePoints = filterByRegion(confPoints, node);
        ArrayList<Point3F> filteredPoints = filterGround(closePoints,planePose);
        return filteredPoints;
    }

    public static ArrayList<Point3F> filterByConfidence(ArrayList<Float[]> pointArrayList) {
        ArrayList<Point3F> pointList = new ArrayList<>();
        if (pointArrayList == null) return pointList;

        for (Float[] f : pointArrayList) {
            if (f[3] > POINT_CONFIDENCE_MIN) {
                    pointList.add(new Point3F(
                            f[0],
                            f[1],
                            f[2],
                            f[3]
                        )
                    );
            }
        }
        return pointList;
    }

    public static ArrayList<Point3F> filterByConfidence(FloatBuffer pointBuffer) {
        ArrayList<Point3F> pointList = new ArrayList<>();

        if (pointBuffer == null) return new ArrayList<Point3F>();

        for (int i = 0; i < pointBuffer.remaining(); i+=4) {
            if (pointBuffer.get(i+3) > POINT_CONFIDENCE_MIN)
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

    public static ArrayList<Point3F> filterByDistanceToCamera(ArrayList<Point3F> pointList, Pose cameraPose) {
        if ((pointList == null) || (cameraPose == null)) return null;
        ArrayList<Point3F> filteredList = new ArrayList<>();
        for (Point3F point : pointList) {
            float dx = point.x - cameraPose.tx();
            float dy = point.y - cameraPose.ty();
            float dz = point.z - cameraPose.tz();

            float dist = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (dist < CAMERA_DISTANCE_LIMIT) {
                filteredList.add(point);
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

    public static ArrayList<Point3F> filterGround(ArrayList<Point3F> pointList, Pose planePose) {
        ArrayList<Point3F> filteredList = new ArrayList<>();
        if (pointList == null) return filteredList;

        for (Point3F point : pointList) {
            if (point.y > GROUND_THRESH_LIMIT + planePose.ty()) {
                filteredList.add(point);
            }
        }
        return  filteredList;
    }

    private static ArrayList<Point3F> convertToPointList(ArrayList<float[]> floatArray) {
        ArrayList<Point3F> pointList = new ArrayList<>();
        if (floatArray == null) return pointList;
        for (int i = 0; i < floatArray.size(); i++) {
            pointList.add(
                    new Point3F(
                            floatArray.get(i)[0],
                            floatArray.get(i)[1],
                            floatArray.get(i)[2],
                            floatArray.get(i)[3]
                    )
            );
        }
        return pointList;
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
}
