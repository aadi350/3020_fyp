package com.helloarbridge4.SizeCheck;


import android.graphics.PointF;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Builder.CarryOnBuilder;
import com.helloarbridge4.Builder.DuffelBuilder;
import com.helloarbridge4.Builder.PersonalItemBuilder;
import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Point3F.Point3F;
import com.helloarbridge4.Point3F.PointFilter;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SizeCheckHandler {

    private Vector3 objectSize = new Vector3();
    private TransformableNode node;
    private PointCloud pointCloud;
    private FloatBuffer pointBuffer;
    private ArrayList<Point3F> pointList = new ArrayList<>();





    public void loadPointCloud(PointCloud pointCloud) {
        if (pointCloud == null) return;
        this.pointCloud = pointCloud;
    }

    public void loadObjectPosition(TransformableNode node) {
        this.node= node;
    }

    public boolean checkIfFits(ObjectCodes objectCode) {
        QuickSort q = new QuickSort();
        getValidPoints();
        PointF dim =  TwoDimensionalOrientedBoundingBox.computeBox(this.pointList);
        float highZ = q.getHighestZ(this.pointList);
        Vector3 actualSize = new Vector3(dim.x, dim.y, highZ);
        switch (objectCode) {
            case CARRYON:
                objectSize = CarryOnBuilder.getObjectSize();
            case DUFFEL:
                objectSize = DuffelBuilder.getObjectSize();
            case PERSONAL:
                objectSize = PersonalItemBuilder.getObjectSize();
        }
        return compareLimits(objectSize, actualSize);
    }


    private void getValidPoints() {
        pointBuffer = pointCloud.getPoints();
        pointList = PointFilter.getValidPoints(pointBuffer,node);
    }


    private boolean compareLimits(Vector3 ref, Vector3 actual) {
        return (
                        (ref.x >= actual.x) &&
                        (ref.y >= actual.y) &&
                        (ref.z >= actual.z)
                );
    }

}
