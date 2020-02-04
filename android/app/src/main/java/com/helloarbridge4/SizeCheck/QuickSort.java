package com.helloarbridge4.SizeCheck;

import com.helloarbridge4.Point3F.Point3F;

import java.util.ArrayList;
import java.util.Collections;

public class QuickSort {
    public float getHighestZ(ArrayList<Point3F> pointList) {
        ArrayList<Point3F> sortedList = sortByHeight(pointList);
        return sortedList.get(sortedList.size() - 1).z;
    }

    public ArrayList<Point3F> sortByHeight(ArrayList<Point3F> pointList) {
        if (pointList == null) return new ArrayList<>();
        if (pointList.size() <= 1) return pointList;

        Point3F[] pointArray = new Point3F[pointList.size()];
        int i = 0;
        for (Point3F point : pointList) {
            pointArray[i] = point;
            i++;
        }

        quickSort(pointArray,0, pointList.size() - 1);

        ArrayList<Point3F> sortedList = new ArrayList<>();
        Collections.addAll(sortedList, pointArray);
        return sortedList;
    }

    private void quickSort(Point3F[] pointArray, int low, int high) {
        if (low < high) {
            int pi = partition(pointArray, low, high);

            quickSort(pointArray, low, pi -1);
            quickSort(pointArray, pi + 1, high);
        }
    }

    private int partition(Point3F[] arr, int low, int high) {
        float pivot = arr[high].z;

        int i = (low - 1);

        for (int j = low; j <= high; j++) {
            if (arr[j].z < pivot) {
                i++;
                Point3F temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Point3F temp = arr[i + 1];
        arr[i+1] = arr[high];
        arr[high] = temp;
        return (i + 1);
    }
}

