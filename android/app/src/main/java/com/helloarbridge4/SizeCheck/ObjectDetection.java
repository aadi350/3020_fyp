package com.helloarbridge4.SizeCheck;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectDetection {
    private final float OBJ_THRESH = 0.5f;
    private Vector3 objectSize;
    private static final Vector3 REGION_LIMITS = new Vector3(0.8f,0.8f,1.0f);
    private static ObjectDetection objectDetection = new ObjectDetection();
    private PointCloud pointCloud;
    FloatBuffer points;
    private TransformableNode node;
    private List<Point> pointList = new ArrayList<Point>();

    public static ObjectDetection getObjectDetector() {
        return objectDetection;
    }

    private ObjectDetection() {

    }

    public void attachTransformableNode(TransformableNode node) {
        this.node = node;
    }

    public void loadPointCloud(PointCloud pointCloud) {
        this.pointCloud = pointCloud;
        this.points = pointCloud.getPoints();
    }

    public void loadValidPoints() throws NullPointerException{
        if (pointCloud== null) {
            throw new NullPointerException("PointCloud null");
        }

        if (node == null) {
            throw new NullPointerException("TransformableNode null");
        }

        while (points.hasRemaining()) {
            Point point = new Point(
                    points.get(),
                    points.get(),
                    points.get()
            );

            if (Point.isValid(points.get()) && Point.filterByDistanceTo(node, point)) {
                pointList.add(point);
            }
        }
    }

    public boolean isObjectDetected(TransformableNode node) {
        if (node == null) {
            return false;
        }

        float pointsInRegion = 0.0f;
        float pointsTotal = 0.0f;
        Vector3 nodeLocation = node.getWorldPosition();
        while (points.hasRemaining()) {
            Point point  = new Point(
                    points.get(),
                    points.get(),
                    points.get()
            );
            boolean isPointValid = point.isValid(points.get());

            if (isPointValid) {
                if (
                        (point.getXYZ().x - nodeLocation.x < REGION_LIMITS.x) &&
                                (point.getXYZ().y - nodeLocation.x < REGION_LIMITS.y) &&
                                (point.getXYZ().z - nodeLocation.z < REGION_LIMITS.z)
                ) {
                    pointsInRegion++;
                }
                pointsTotal++;
                pointList.add(point);
            }
        }
        return (pointsInRegion/pointsTotal) > OBJ_THRESH;
    }

    public void generateHull() {
        List<Vector3> cvHull = new ArrayList<Vector3>();
    }

    private List<Point> getLowestY() {

        List<Point> upperHull = new ArrayList<Point>();
        Point[] arr = new Point[pointList.size()];
        List<Point> sortedList = new ArrayList<Point>();

        int iterator = 0;
        for (Point temp : pointList) {
            arr[iterator] = temp;
            iterator++;
        }
        Collections.addAll(sortedList, arr);
        sortAscendingY(arr);

        for (Point temp : arr) {
            sortedList.add(temp);
        }

        for (Point point : sortedList) {
            while (sortedList.size() >= 2) {
                Point q = upperHull.get(upperHull.size() - 1);
                Point r = upperHull.get(upperHull.size() - 2);

                if ((q.getX() - r.getX())*(point.getY() - r.getY())
                        >= ((q.getY() - r.getY())*(point.getX() - r.getX()))) {
                    sortedList.remove(point);
                } else {
                    break;
                }
                upperHull.add(point);
            }
        }
        //TODO implement sorting algorithm
//        for (Point point : sortedArray) {
//            sortedList.add(point);
//        }
        return sortedList;
    }

    private void sortAscendingY(Point[] arr) {
        int low = 0;
        int high = arr.length - 1;
        sortAscending(arr,low, high);
    }

    private int getPartition(Point[] arr, int low, int high) {
        Point pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (arr[j].getY() < pivot.getY()) {
                i++;

                //Swap
                Point temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Point temp = arr[i+1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return (i + 1);
    }

    private void sortAscending(Point[] arr, int low, int high) {
        if (low < high) {
            int pi = getPartition(arr, low, high);

            sortAscending(arr, low, pi - 1);
            sortAscending(arr, pi + 1, high);
        }
    }


}
