package com.reactlibrary.SizeCheck.MinBoundingBox;

import com.reactlibrary.Point3F.Point3F;

import java.util.ArrayList;

public final class TwoDimensionalOrientedBoundingBox {
    public static Point3F[] getOBB(ArrayList<Point3F> pointList) {
        ArrayList<Point3F> tempList = new ArrayList<>(pointList);
        Polygon polygon =  new Polygon(tempList);
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
        float minX = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

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

        double lengthAB = Math.sqrt(Math.abs((deltaXAB * deltaXAB) + (deltaZAB * deltaZAB)));
        double lengthBC = Math.sqrt(Math.abs((deltaXBC * deltaXBC) + (deltaZBC * deltaZBC)));

        return lengthAB * lengthBC;
    }

}


