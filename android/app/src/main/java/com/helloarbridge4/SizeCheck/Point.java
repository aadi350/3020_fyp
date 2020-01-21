package com.helloarbridge4.SizeCheck;

import com.google.ar.sceneform.math.Vector3;

public class Point {
    private static final Float POINT_THRESH = 0.7f;
    Vector3 pointLocation;

    Point (Float x, Float y, Float z) {
        this.pointLocation = new Vector3(x,y,z);
    }

    boolean isValid(Float confidence) {
        return (confidence > POINT_THRESH);
    }
}
