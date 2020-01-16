package com.helloarbridge4.SizeCheck;

import com.google.ar.core.PointCloud;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;
import com.helloarbridge4.Object.ObjectCodes;

import java.nio.FloatBuffer;

public class SizeCheckHandler {
    private static final Vector3 REGION_LIMITS = new Vector3(0.8f,0.8f,1.0f);
    private static final float OBJECT_THRESH = 0.5f;
    private final Float REGION_THRESH = 0.3f;
    ObjectCodes objectCode;
    PointCloud pointCloud;

    Quaternion objectRotation;
    Vector3 objectSize;
    TransformableNode objectCentre;

    public void loadPointCloud(PointCloud pointCloud) {
        this.pointCloud = pointCloud;
    }

    public void setObjectCentre(TransformableNode transformableNode) {
        this.objectCentre = transformableNode;
        this.objectRotation = objectCentre.getWorldRotation();
    }

    public void setObjectType(ObjectCodes objectCode) {
        if (objectCode == null) return;
        this.objectCode = objectCode;
        switch(objectCode) {
            case CARRYON:
                    objectSize = ObjectSizes.getCarryOnSize();
                break;
            case DUFFEL:
                    objectSize = ObjectSizes.getDuffelSize();
                break;
            case PERSONAL:
                    objectSize = ObjectSizes.getPersonalItemSize();
                break;
        }
    }

    public FitCodes getFitsCode() {
        if (pointCloud == null || objectCentre == null) {
            return  FitCodes.NONE;
        }

        FitCodes fitCodes = FitCodes.NONE;

        FloatBuffer points = pointCloud.getPoints();
        float pointsInRegion = 0f;
        float pointsInObject = 0f;
        float pointsTotal = 0f;

        while (points.hasRemaining()) {
            Point point = new Point(
                    points.get(),
                    points.get(),
                    points.get()
            );
            Float pointConfidence = points.get();
            if (point.isValid(pointConfidence)) {
                pointsTotal++;
                if (point.isInRegion(REGION_LIMITS, objectCentre))
                    pointsInRegion++;
                    if (inObject(point)) {
                        pointsInObject++;
                    }
                }
            }



        boolean objectDetected = pointsInRegion / pointsTotal > REGION_THRESH;
        boolean objectFits = pointsInObject / pointsInRegion > OBJECT_THRESH;

        if (objectDetected) {
            fitCodes = (objectFits) ? FitCodes.FIT : FitCodes.LARGE;
            return fitCodes;
        }
        return fitCodes;
    }

    private boolean inObject(Point point) {
        if (point == null) {
            return false;
        }

        Vector3 pointV3 = new Vector3(
                point.pointLocation.x,
                point.pointLocation.y,
                point.pointLocation.z
        );

        boolean inObject = false;

        Float objectRotationDeg = getRotationInDeg(objectRotation);
        float distanceVertical = point.normalisePointVertical(objectRotationDeg,objectCentre);
        float distanceHorizontal = point.normalisePointHorizontal(objectRotationDeg,objectCentre);

//        if (distanceVertical >  )

        return inObject;
    }

    private Float getRotationInDeg(Quaternion objectRotation) {
        //TODO write function to convert to degrees
        return 0f;
    }



}
