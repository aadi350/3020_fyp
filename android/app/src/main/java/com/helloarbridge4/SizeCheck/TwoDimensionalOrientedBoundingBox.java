package com.helloarbridge4.SizeCheck;

import android.gesture.GestureUtils;
import android.gesture.OrientedBoundingBox;
import android.graphics.PointF;

import com.helloarbridge4.Point3F.Point3F;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TwoDimensionalOrientedBoundingBox {
    public static PointF computeBox(@NotNull ArrayList<Point3F> pointList) {
        OrientedBoundingBox boundingBox;

        int arraySize = doubleInt(pointList.size());
        float[] pointArray = new float[arraySize];
        int listIterator = 0;
        for (Point3F point : pointList) {
            pointArray[listIterator] = point.x;
            listIterator++;
            pointArray[listIterator] = point.y;
            listIterator++;
        }
        boundingBox = GestureUtils.computeOrientedBoundingBox(pointArray);

        return new PointF(
                boundingBox.height,
                boundingBox.width
        );
    }

    private static int doubleInt(int i) {
        return i*2;
    }

}
