package com.helloarbridge4.SizeCheck;

import com.google.ar.sceneform.math.Vector3;

public class Point {
    private static final Float POINT_THRESH = 0.7f;
    private static final float THRESH_HORIZONTAL = 0.8f;
    private static final float THRESH_VERTICAL = 0.8f;
    private Vector3 pointLocation;

    public Point (Float x, Float y, Float z) {
        this.pointLocation = new Vector3(x,y,z);
    }

    public static boolean isValid(Float confidence) {
        return (confidence > POINT_THRESH);
    }

    public Vector3 getXYZ() {
        return pointLocation;
    }

    public float getX() {
        return pointLocation.x;
    }

    public float getY() {
        return pointLocation.y;
    }

    public float getZ() {
        return pointLocation.z;
    }

    public static boolean filterByDistanceTo(Vector3 node, Point point) {
        if (node == null || point == null) {
            return false;
        }
        Vector3 nodeCentre = node;
        Vector3 pointLocation = point.getXYZ();
        Vector3 distanceToCentre = Vector3.subtract(pointLocation,nodeCentre);
        return pointWithinThreshold(distanceToCentre);
    }

    private static boolean pointWithinThreshold(Vector3 dist) {
        if (dist == null) return false;

        return (
                dist.x <= THRESH_HORIZONTAL &&
                        dist.y <= THRESH_HORIZONTAL &&
                        dist.z <= THRESH_VERTICAL
                );
    }


}
