package com.helloarbridge4.SizeCheck;

import com.helloarbridge4.Point3F.Point3F;

import java.util.ArrayList;

public final class TwoDimensionalOrientedBoundingBox {

    protected enum Corner { UPPER_RIGHT, UPPER_LEFT, LOWER_LEFT, LOWER_RIGHT }

    public static Point3F[] getOBB(ArrayList<Point3F> pointList) {
        Polygon polygon =  new Polygon(pointList);
        return getMinimumBoundingRectangle(polygon);
    }

    public static Point3F[] getMinimumBoundingRectangle(Polygon polygon) {
        Rectangle[] rects = new Rectangle[polygon.edgeCount()];

        for(int i = 0; i < polygon.edgeCount(); i++)
        {
            Point3F edge = polygon.getEdge(i);
            //Rotate the polygon so that the current edge is parallel to a major axis
            //The z-Axis in this use case
            double theta = Math.acos(edge.normalise2D().z);
            polygon.rotate(theta);
            //Calculate a bounding box
            rects[i] = boundingBox(polygon);
            polygon.rotate(-theta);
            rects[i].rotate(-theta, polygon.getCenter());
        }

        double minArea = Double.MAX_VALUE;
        Rectangle box = rects[0];

        //Find the bounding box with the smallest area, this is the minimum bounding box
        for(int i = 0 ;i < rects.length; i++)
        {
            double area = rects[i].area();
            if(area < minArea)
            {
                minArea = area;
                box = rects[i];
            }
        }
        Point3F[]  boxPoints  = new Point3F[4];

        for (int i = 0;  i < 4;  i++) {
            boxPoints[i] =  box.getPoint(i);
        }
        return boxPoints;

    }

    public static Rectangle boundingBox(Polygon polygon)
    {
        float minX = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxZ = Float.MIN_VALUE;

        for(int i=0;i<polygon.pointCount();i++)
        {
            Point3F p = polygon.getPoint(i);
            if(minX > p.x)
                minX = p.x;
            if(maxX < p.x)
                maxX = p.x;
            if(minZ > p.z)
                minZ = p.z;
            if(maxZ < p.z)
                maxZ = p.z;
        }
        return new Rectangle(minX, minZ, maxX - minX, maxZ - minZ);
    }

    public static double getArea(Point3F[] rectangle) {

        double deltaXAB = rectangle[0].x - rectangle[1].x;
        double deltaZAB = rectangle[0].z - rectangle[1].z;

        double deltaXBC = rectangle[1].x - rectangle[2].x;
        double deltaZBC = rectangle[1].z - rectangle[2].z;

        double lengthAB = Math.sqrt((deltaXAB * deltaXAB) + (deltaZAB * deltaZAB));
        double lengthBC = Math.sqrt((deltaXBC * deltaXBC) + (deltaZBC * deltaZBC));

        return lengthAB * lengthBC;
    }

}


