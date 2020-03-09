package com.helloarbridge4.SizeCheck.MinBoundingBox;

import com.helloarbridge4.Point3F.Point3F;

import java.util.List;

public final class Caliper {
    final static double SIGMA = 0.00000000001;

    final List<Point3F> convexHull;
    int pointIndex;
    float currentAngle;

    Caliper(List<Point3F> convexHull, int pointIndex, float currentAngle) {
        this.convexHull = convexHull;
        this.pointIndex = pointIndex;
        this.currentAngle = currentAngle;
    }

    float getAngleNextPoint() {

        Point3F p1 = convexHull.get(pointIndex);
        Point3F p2 = convexHull.get((pointIndex + 1) % convexHull.size());

        float deltaX = p2.x - p1.x;
        float deltaZ = p2.z - p1.z;

        float angle = (float) (Math.atan2(deltaZ, deltaX) * 180 / Math.PI);

        return angle < 0 ? 360 + angle : angle;
    }

     float getConstant() {

        Point3F p = convexHull.get(pointIndex);

        return p.z - (getSlope() * p.x);
    }

    float getDeltaAngleNextPoint() {

        float angle = getAngleNextPoint();

        angle = angle < 0 ? 360 + angle - currentAngle : angle - currentAngle;

        return angle < 0 ? 360 : angle;
    }

    Point3F getIntersection(Caliper that) {

        // the x-intercept of 'this' and 'that': x = ((c2 - c1) / (m1 - m2))
        float x;
        // the z-intercept of 'this' and 'that', given 'x': (m*x) + c
        float z;

        if(this.isVertical()) {
            x = convexHull.get(pointIndex).x;
        }
        else if(this.isHorizontal()) {
            x = that.convexHull.get(that.pointIndex).x;
        }
        else {
            x = (that.getConstant() -  this.getConstant()) / (this.getSlope() - that.getSlope());
        }

        if(this.isVertical()) {
            z = that.getConstant();
        }
        else if(this.isHorizontal()) {
            z = this.getConstant();
        }
        else {
            z = (this.getSlope() * x) + this.getConstant();
        }

        return new Point3F(x, 0f,z);
    }

    float getSlope() {
        return (float) Math.tan(Math.toRadians(currentAngle));
    }

    boolean isHorizontal() {
        return (Math.abs(currentAngle) < SIGMA) || (Math.abs(currentAngle - 180.0) < SIGMA);
    }

    boolean isVertical() {
        return (Math.abs(currentAngle - 90.0) < SIGMA) || (Math.abs(currentAngle - 270.0) < SIGMA);
    }

    void rotateBy(float angle) {

        if(this.getDeltaAngleNextPoint() == angle) {
            pointIndex++;
        }

        this.currentAngle = (this.currentAngle + angle) % 360;
    }
}
