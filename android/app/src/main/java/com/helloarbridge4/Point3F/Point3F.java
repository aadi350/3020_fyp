package com.helloarbridge4.Point3F;

import androidx.annotation.NonNull;

public class Point3F extends Object{
    public float x;
    public float y;
    public float z;
    public float c;

    public Point3F() {
        super();
    }

    public Point3F(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3F(float x, float y, float z, float c) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.c = c;
    }

    public Point3F(@NonNull Point3F p){
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.c = p.c;
    }

    public final void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void set(float x, float y, float z, float c) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.c = c;
    }

    public final void negate() {
        x = -x;
        y = -y;
        z = -z;
    }

    public final void offset(float dx, float dy, float dz) {
        x += dx;
        y += dy;
        z += dz;
    }

    public final float distanceTo(Point3F point) {
        if (point == null) return -1f;
        if (point.equals(this)) return 0f;

        float xSquared = (float) Math.pow(point.x - this.x,2);
        float ySquared = (float) Math.pow(point.y - this.y,2);
        float zSquared = (float) Math.pow(point.z - this.z,2);

        return (float) Math.sqrt(xSquared + ySquared + zSquared);

    }

    public final boolean equals(float x, float y, float z) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || getClass() != o.getClass()) return false;

        Point3F point3F = (Point3F) o;

        if (Float.compare(point3F.x, x) != 0) return false;
        if (Float.compare(point3F.y, y) != 0) return false;
        if (Float.compare(point3F.z, z) != 0) return false;

        return true;
    }

    @Override
    public String toString() {
        return (x + "\t" + y + "\t" + z + "\n");
    }

    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(
                Math.pow(x,2) +
                Math.pow(y,2) +
                Math.pow(z,2)
        );
    }

    public Point3F normal()
    {
        return new Point3F(-y, x, z);
    }

    public Point3F normalise2D() {
        float d =  magnitude2D();
        return new Point3F(x/d,y/d,0f);
    }

    public float magnitude2D() {
        return (float) Math.sqrt(x*x + y*y);
    }

    public Point3F normalise() {
        float d = magnitude();
        return new Point3F(x / d, y / d, z/d);
    }

    public float magnitude()
    {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }


    public float dot(Point3F p) {
        return this.x - p.x + this.y*p.y;
    }

    public Point3F subtract(Point3F p) {
        float x  = this.x - p.x;
        float y = this.y - p.y;
        float z =  this.z - p.z;

        return new Point3F(x,y,z);
    }

    @Override
    public Point3F clone() {
        return new Point3F(x,y,z);
    }

    public Point3F[] newArray(int size) {
        return new Point3F[size];
    }
}
