package com.helloarbridge4.SizeCheck;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Object.SceneFormObject;

import java.nio.FloatBuffer;

public class ObjectDetection {
    private final float OBJ_THRESH = 0.5f;
    private Vector3 objectSize;
    private static final Vector3 REGION_LIMITS = new Vector3(0.8f,0.8f,1.0f);
    private static ObjectDetection objectDetection = new ObjectDetection();
    private PointCloud pointCloud;

    public static ObjectDetection getObjectDetector() {
        return objectDetection;
    }

    private ObjectDetection() {

    }


    public void loadPointCloud(PointCloud pointCloud) {
        this.pointCloud = pointCloud;
    }

    public boolean objectWithinRegion(TransformableNode node) {
        if (pointCloud== null || node == null) {
            return false;
        }
        FloatBuffer points = pointCloud.getPoints();

        float pointsInRegion = 0f;
        float pointsTotal = 0f;


        while (points.hasRemaining()) {
            Point point = new Point(
                    points.get(),
                    points.get(),
                    points.get()
            );

            if (point.isValid(points.get())) {
                pointsTotal++;
                if (point.isInRegion(REGION_LIMITS, node)) {
                    pointsInRegion++;
                }
            }
        }
        System.out.println("In Region: " + pointsInRegion);
        System.out.println("Out of region: " + pointsTotal);

        return (pointsInRegion / pointsTotal) > OBJ_THRESH;
    }

}
