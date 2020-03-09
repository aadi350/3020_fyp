package com.helloarbridge4.SizeCheck.MinBoundingBox;

import com.helloarbridge4.Point3F.Point3F;

import java.util.ArrayList;

public class Polygon {
    protected ArrayList<Point3F> points;
    protected Point3F center;

    public Polygon() {
        center = new Point3F();
    }

    public Polygon(ArrayList<Point3F> pointList) {
        this.points = pointList;
        center = new Point3F();
        calculateCenter();
    }

    protected void calculateCenter() {
        double x = 0;
        double z = 0;
        int n = points.size();

        for(int i=0;i<n;i++)
        {
            Point3F p = points.get(i);
            x += p.x;
            z += p.z;
        }

        center.x = (float) x / n;
        center.z = (float) z / n;
    }

    public void addPoint(Point3F p) {
        this.points.add(p);
    }

    public void rotate(double theta) {
        rotate(theta, center);
    }

    public Point3F getPoint(int index) {
        return points.get(index);
    }

    public Point3F getCenter() {
        return this.center;
    }

    public Point3F getEdge(int index) {
        int next = (index+1) % pointCount();
        Point3F p1 = points.get(index);
        Point3F p2 = points.get(next);

        return p2.subtract(p1);
    }

    protected void calcCenter()
    {
        double x = 0;
        double z = 0;
        int n = points.size();

        for(int i = 0; i < n; i++)
        {
            Point3F p = points.get(i);
            x += p.x;
            z += p.z;
        }

        center.x = (float) x / n;
        center.z = (float) z / n;
    }

    public int pointCount()
    {
        return points.size();
    }

    public int edgeCount()
    {
        if(points.size() == 1)
        {
            return 0;
        }
        else if(points.size() == 2)
        {
            return 1;
        }
        else
        {
            return points.size();
        }
    }

    public boolean contains(Point3F p)
    {
        for(int i = 0; i < pointCount(); i++)
        {
            Point3F normal = getEdge(i).normal();
            p = p.subtract(center);
            if(normal.dot(p) > 0)
            {
                return false;
            }
        }
        return true;
    }

    public void rotate(double theta, Point3F pivot)
    {
        for(int i=0;i<points.size();i++)
        {
            Point3F p = points.get(i);
            double x = p.x;
            double z = p.z;
            x -= pivot.x;
            z -= pivot.z;
            double tx = x;
            double sinTheta = Math.sin(theta);
            double cosTheta = Math.cos(theta);
            x = x * cosTheta - z * sinTheta;
            z = tx * sinTheta + z * cosTheta;
            x += pivot.x;
            z += pivot.z;
            p.x = (float) x;
            p.z = (float) z;
        }
    }
}
