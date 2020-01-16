package com.helloarbridge4.SizeCheck;

import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.TransformableNode;

public class Point {
    private static final Float POINT_THRESH = 0.7f;
    Vector3 pointLocation;
    private Vector3 regionCentre;

    Point (Float x, Float y, Float z) {
        this.pointLocation = new Vector3(x,y,z);
    }

    public boolean isInRegion(Vector3 regionLimits, TransformableNode node) {
        if (node == null) return false;
        regionCentre = node.getWorldPosition();

        return !upperGreater(absoluteDifference(), regionLimits);
    }

    private boolean upperGreater(Vector3 absoluteDifference, Vector3 regionLimits) {
        return (
                        (absoluteDifference.x > regionLimits.x) &&
                        (absoluteDifference.y > regionLimits.y) &&
                        (absoluteDifference.z > regionLimits.z)
                );
    }

    private Vector3 absoluteDifference() {
        Float x = Math.abs(pointLocation.x - regionCentre.x);
        Float y = Math.abs(pointLocation.y - regionCentre.y);
        Float z = Math.abs(pointLocation.z - regionCentre.z);

        return new Vector3(x,y,z);
    }

    boolean isValid(Float confidence) {
        return (confidence > POINT_THRESH);
    }

    float normalisePointVertical(float rotDeg, TransformableNode objectCentre) {

        float angle = (float) Math.toRadians(rotDeg);
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        Vector3 objectCentreLocation = objectCentre.getWorldPosition();

        Float x = this.pointLocation.x - objectCentreLocation.x;
        Float y = this.pointLocation.y - objectCentreLocation.y;
        Float z = this.pointLocation.z;

        return (s*x + c*y);
    }

    float normalisePointHorizontal(float rotDeg, TransformableNode objectCentre) {

        float angle = (float) Math.toRadians(rotDeg);
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        Vector3 objectCentreLocation = objectCentre.getWorldPosition();

        Float x = this.pointLocation.x - objectCentreLocation.x;
        Float y = this.pointLocation.y - objectCentreLocation.y;
        Float z = this.pointLocation.z;

        return (s*x - c*y);
    }

}
