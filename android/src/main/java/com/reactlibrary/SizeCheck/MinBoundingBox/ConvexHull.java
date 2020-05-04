package com.reactlibrary.SizeCheck.MinBoundingBox;

import com.reactlibrary.Point3F.Point3F;
import com.reactlibrary.SizeCheck.QuickSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConvexHull
{

    // Returns a new list of Point3Fs representing the convex hull of
    // the given set of Point3Fs. The convex hull excludes collinear Point3Fs.
    // This algorithm runs in O(n log n) time.
    public static ArrayList<Point3F> makeHull(List<Point3F> pointList)
    {
        ArrayList<Point3F> newPoints = new ArrayList<>(pointList);
        ArrayList<Point3F> sortedPoints = sort(newPoints);
        return makeHullPresorted(sortedPoints);
    }



    // Returns the convex hull, assuming that each Point3Fs[i] <= Point3Fs[i + 1]. Runs in O(n) time.
    public static ArrayList<Point3F> makeHullPresorted(ArrayList<Point3F> Points)
    {
        if (Points.size() <= 1)
            return new ArrayList<>(Points);

        ArrayList<Point3F> upperHull = new ArrayList<>();
        for (Point3F p : Points)
        {
            while (upperHull.size() >= 2)
            {
                Point3F q = upperHull.get(upperHull.size() - 1);
                Point3F r = upperHull.get(upperHull.size() - 2);
                if ((q.x - r.x) * (p.z - r.z) >= (q.z - r.z) * (p.x - r.x))
                    upperHull.remove(upperHull.size() - 1);
                else
                    break;
            }
            upperHull.add(p);
        }
        upperHull.remove(upperHull.size() - 1);

        ArrayList<Point3F> lowerHull = new ArrayList<>();
        for (int i = Points.size() - 1; i >= 0; i--)
        {
            Point3F p = Points.get(i);
            while (lowerHull.size() >= 2)
            {
                Point3F q = lowerHull.get(lowerHull.size() - 1);
                Point3F r = lowerHull.get(lowerHull.size() - 2);
                if ((q.x - r.x) * (p.z - r.z) >= (q.z - r.z) * (p.x - r.x))
                    lowerHull.remove(lowerHull.size() - 1);
                else
                    break;
            }
            lowerHull.add(p);
        }
        lowerHull.remove(lowerHull.size() - 1);

        if (!(upperHull.size() == 1 && upperHull.equals(lowerHull)))
            upperHull.addAll(lowerHull);
        return upperHull;
    }

    public static ArrayList<Point3F> sort(ArrayList<Point3F> pointList) {
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

    private static void quickSort(Point3F[] pointArray, int low, int high) {
        if (low < high) {
            int pi = partition(pointArray, low, high);

            quickSort(pointArray, low, pi -1);
            quickSort(pointArray, pi + 1, high);
        }
    }

    private static int partition(Point3F[] arr, int low, int high) {
        float pivot = arr[high].x;

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